package nl.siegmann.epublib.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ParseUnicode {

	private static class CharDesc {
		public int number;
		public String numberString;
		public String description;
	}
	
	public static void main(String[] args) throws Exception {
		String input = "/home/paul/project/private/library/font_poems/cuneiform.txt";
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line = reader.readLine();
		int skipLines = 0;
		List<CharDesc> resultList = new ArrayList<CharDesc>();
		CharDesc cd1 = new CharDesc();
		CharDesc cd2 = new CharDesc();
		while(line != null) {
			if(line.indexOf("The Unicode Standard 5.2") > 0) {
				skipLines = 1;
				continue;
			} else if(skipLines > 0) {
				skipLines --;
				continue;
			}
			String line1 = line;
			String line2 = "";
			if(line.length() > 50) {
				line2 = line1.substring(50);
				line1 = line1.substring(0, 50);
			}
			processLine(line1, cd1, resultList);
			processLine(line2, cd2, resultList);
			line = reader.readLine();
		}
	}

	private static void processLine(String line, CharDesc cd, List<CharDesc> resultList) {
		line = line.trim();
		if(line.length() == 0) {
			return;
		}
		if(line.charAt(0) > 256) {
			line = line.substring(1);
		}
		if(StringUtils.isNumeric(line)) {
			if(StringUtils.isBlank(cd.numberString)) {
				
			}
		}
		
	}
}
