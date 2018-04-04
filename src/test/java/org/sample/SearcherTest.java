package org.sample;

import java.io.File;

import org.junit.Test;

public class SearcherTest {

	@Test
	public void execute() throws Exception {
		File targetDir = new File("./target/test-classes/keyword.txt");
		File keyword = new File("./target/classes");
		File output = new File("./target/seachResult.txt");
		String[] args = { targetDir.getCanonicalPath(), keyword.getCanonicalPath(), output.getCanonicalPath() };
		Searcher.main(args);
	}
}
