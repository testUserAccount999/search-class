package org.sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public class Searcher {

    private static final String DELIMITER = "\t";
    private static final String LINE_SEPARATOR = "\r\n";

    public static void main(String[] args) {
        try {
            validateArgs(args);
            new Searcher().searchKeyWord(args[0], args[1], args[2]);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void validateArgs(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("引数が不足しています。");
        }
        File keyword = new File(args[0]);
        if (!keyword.exists() || keyword.isDirectory()) {
            throw new IllegalArgumentException("第1引数で指定されたファイルが存在しない、"
                + "またはディレクトリが指定されています。");
        }
        File dir = new File(args[1]);
        if (!dir.exists() || dir.isFile()) {
            throw new IllegalArgumentException("第2引数で指定されたディレクトリが存在しない、"
                + "またはファイルが指定されています。");
        }
    }

    public void searchKeyWord(String keywordPath, String jarDirPath, String resultPath) throws IOException {
        long start = System.currentTimeMillis();
        File text = new File(keywordPath);
        List<String> keywords = getKeyword(text);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultPath)),
            8192);) {
            writeHeader(writer);
            File input = new File(jarDirPath);
            List<File> jars = searchJarFile(input);
            for (File jar : jars) {
                searchKeywordFromJar(jar, keywords, writer);
            }
            List<File> classFiles = searchClassFile(input);
            for (File classFile : classFiles) {
                JavaClass javaClass = new ClassParser(classFile.getCanonicalPath()).parse();
                seachKeyword(javaClass, keywords, "-", writer);
            }
            writer.flush();
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start) + " ミリ秒");
    }

    private void writeHeader(Writer writer) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("jar name").append(DELIMITER)
            .append("class name").append(DELIMITER)
            .append("method name").append(DELIMITER)
            .append("keyword").append(DELIMITER)
            .append("mnemonic").append(LINE_SEPARATOR);
        writer.write(sb.toString());
    }

    private void searchKeywordFromJar(File jar, List<String> keywords, Writer writer) throws IOException {
        ZipFile zip = new ZipFile(jar);
        Enumeration<? extends ZipEntry> entries = zip.entries();
        String jarName = jar.getName();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                InputStream inputStream = zip.getInputStream(entry);
                JavaClass javaClass = new ClassParser(inputStream, entry.getName()).parse();
                seachKeyword(javaClass, keywords, jarName, writer);
            }
        }
        zip.close();
    }

    private void seachKeyword(JavaClass javaClass, List<String> keywords, String jarName, Writer writer)
        throws IOException {
        if (javaClass.isInterface()) {
            return;
        }
        String className = javaClass.getClassName();
        StringBuilder sb = new StringBuilder();
        for (Method method : javaClass.getMethods()) {
            String methodName = method.getName();
            for (String line : getLines(method)) {
                for (String keyword : keywords) {
                    if (line.contains(keyword)) {
                        sb.append(jarName).append(DELIMITER)
                            .append(className).append(DELIMITER)
                            .append(methodName).append(DELIMITER)
                            .append(keyword).append(DELIMITER)
                            .append(line).append(LINE_SEPARATOR);
                    }
                }
            }
        }
        writer.write(sb.toString());
    }

    private List<String> getKeyword(File textFile) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() != 0) {
                    words.add(line);
                }
            }
        }
        return words;
    }

    private List<File> searchClassFile(File dir) {
        List<File> jars = new ArrayList<>();
        for (File f : dir.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".class")) {
                jars.add(f);
            } else if (f.isDirectory()) {
                jars.addAll(searchClassFile(f));
            }
        }
        return jars;
    }

    private List<File> searchJarFile(File dir) {
        List<File> jars = new ArrayList<>();
        for (File f : dir.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".jar")) {
                jars.add(f);
            } else if (f.isDirectory()) {
                jars.addAll(searchJarFile(f));
            }
        }
        return jars;
    }

    private String[] getLines(Method method) {
        String[] lines = new String[0];
        Code code = method.getCode();
        try {
            if (code != null) {
                String all = code.toString(false);
                lines = all.replaceAll(DELIMITER, " ").split("\n");
            }
        } catch (Exception e) {
            System.out.println(method.getName() + "でエラー！！");
            e.printStackTrace();
        }
        return lines;
    }

}
