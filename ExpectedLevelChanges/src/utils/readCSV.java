package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class readCSV {
	private static String source = "C:/Users/mohse/git/thesis/ExpectedLevelChanges/src/files/";
	
	public static List<String[]> readFile(String fileName){
		String path = source+fileName+".csv";
		List<String[]> lines = new ArrayList<String[]>();
		BufferedReader br = null;
		String line;
		String cvsSplitBy = ",";

		try {
			br = new BufferedReader(new FileReader(path));
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] lineArray = line.split(cvsSplitBy);
				lines.add(lineArray);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		
		return lines;
	}
}
