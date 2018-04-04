package org.sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public class MnemonicViewer {
    public static void main(String[] args) {
        MnemonicViewer me = new MnemonicViewer();
        me.validateArgs(args);
        try {
            me.printMnemonic(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private void printMnemonic(String path) throws IOException {
        try {
            JavaClass javaClass = new ClassParser(path).parse();
            printClass(javaClass);
            Field[] fields = javaClass.getFields();
            printFileds(fields);
            Method[] methods = javaClass.getMethods();
            printMehtods(methods);
        } catch (ClassFormatException e) {
            throw new IllegalArgumentException("第1引数で指定されたファイルはクラスファイルではありません。", e);
        }
    }

    private void printClass(JavaClass javaClass) {
        StringBuilder sb = new StringBuilder();
        sb.append("Class").append("\r\n")
            .append("\tName : ").append(javaClass.getClassName()).append("\r\n")
            .append("\tSuper Class : ").append(javaClass.getSuperclassName()).append("\r\n");
        AnnotationEntry[] annotationEntries = javaClass.getAnnotationEntries();
        if (annotationEntries.length > 0) {
            sb.append("\tAnnotations : ");
            for (int i = 0; i < annotationEntries.length; i++) {
                AnnotationEntry annotationEntry = annotationEntries[i];
                sb.append(annotationEntry.toShortString());
                if (i != annotationEntries.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("\r\n");
        }
        System.out.print(sb.toString());
    }

    private void printMehtods(Method[] methods) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Method method : methods) {
            sb.append("\r\nMethod\r\n")
                .append("\tName : ").append(method.getName()).append("\r\n")
                .append("\tSignature : ").append(method.getSignature()).append("\r\n");
            if (method.getExceptionTable() != null) {
                sb.append("\tException : ").append(method.getExceptionTable().toString().replaceAll("^Exceptions:", ""))
                    .append("\r\n");
            }
            AnnotationEntry[] annotationEntries = method.getAnnotationEntries();
            if (annotationEntries.length > 0) {
                sb.append("\tAnnotations : ");
                for (int i = 0; i < annotationEntries.length; i++) {
                    AnnotationEntry annotationEntry = annotationEntries[i];
                    sb.append(annotationEntry.toShortString());
                    if (i != annotationEntries.length - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("\r\n");
            }
            Code code = method.getCode();
            if(code != null){
                sb.append("\tCode : \r\n");
                try(BufferedReader reader = new BufferedReader(new StringReader(code.toString(false).replaceAll("\t", " ")))){
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append("\t\t").append(line).append("\r\n");
                    }
                }
            }
        }
        System.out.print(sb.toString());
    }

    private void printFileds(Field[] fields) {
        StringBuilder sb = new StringBuilder();
        for (Field field : fields) {
            sb.append("Field").append("\r\n")
                .append("\tName : " + field.getName()).append("\r\n")
                .append("\tType : " + field.getType()).append("\r\n");
            AnnotationEntry[] annotationEntries = field.getAnnotationEntries();
            if (annotationEntries.length > 0) {
                sb.append("\tAnnotations : ");
                for (int i = 0; i < annotationEntries.length; i++) {
                    AnnotationEntry annotationEntry = annotationEntries[i];
                    sb.append(annotationEntry.toShortString());
                    if (i != annotationEntries.length - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("\r\n");
            }
        }
        if (sb.length() != 0) {
            System.out.print(sb.toString());
        }
    }

    private void validateArgs(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("引数が不足しています。");
        }
        File f = new File(args[0]);
        if (!f.exists() || f.isDirectory()) {
            throw new IllegalArgumentException("第1引数で指定されたファイルが存在しない、"
                + "またはディレクトリが指定されています。");
        }
    }
}
