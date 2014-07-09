package utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class FileUtilities {
	
	public static int getNumberOfLines(String fileName) throws IOException {
		LineNumberReader lnr;
		lnr = new LineNumberReader(new FileReader(fileName));
		lnr.skip(Long.MAX_VALUE);
		int numberOfLines = lnr.getLineNumber() + 1;
		lnr.close();
		return numberOfLines;
	}
	
	public static boolean doesFileExist(String filePath) {
		return new File(filePath).exists();
	}

}
