package org.sample;

import java.io.File;

import org.junit.Test;

public class MnemonicViewerTest {
	
	@Test
	public void execute() throws Exception {
		File classFile = new File("./target/classes/org/sample/Searcher.class");
		String[] args = {classFile.getCanonicalPath()};
		MnemonicViewer.main(args);
	}
}
