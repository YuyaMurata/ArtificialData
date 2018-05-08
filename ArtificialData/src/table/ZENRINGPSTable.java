/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table;

import csv.CSVFileReadWrite;
import id.DataGenerator;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ゼンリン用テストデータ生成
 *
 * @author murata
 */
public class ZENRINGPSTable {

	private static int numRec = 100000;
	private static String path = "E:\\root\\";
	private static String filename = "TESTDATA_複ID_10万rec1_0409.csv";

	public static void main(String[] args) {
		File[] files = (new File(path)).listFiles();
		System.out.println("Num Files:" + files.length);
		try (PrintWriter pw = CSVFileReadWrite.writer(filename)) {
			//Header
			pw.println("uid,lon,lat,label");
			for (File f : files) {
				System.out.println(f.getName());
				try (BufferedReader csv = CSVFileReadWrite.reader(f)) {
					List rt = root(csv);
					List dates = DataGenerator.randomDates(rt.size());

					writeRoot(pw, rt, dates);
				} catch (IOException ex) {
				}
			}
		}

		System.out.println("Finish:" + files.length + " rec=" + cnt);
	}

	private static List root(BufferedReader csv) throws IOException {
		Boolean stflg = false;
		String line = "";
		List<String> rootList = new ArrayList();
		String name = "";
		while ((line = csv.readLine()) != null) {
			if (line.contains("<name>")) {
				name = line.substring(6, line.length() - 7);
			}
			if (!stflg) {
				if (line.contains("<coordinates>")) {
					stflg = true;
				}
				continue;
			} else if (line.contains("</coordinates>")) {
				stflg = false;
				continue;
			}

			rootList.add(line + "," + name);

			//各ID一つずつ
			break;
		}

		return rootList;
	}

	private static int cnt = 1;
	private static Integer id = 0;

	private static void writeRoot(PrintWriter pw, List<String> root, List<LocalDate> date) {
		id += 100;
		for (int i = 0; i < root.size(); i++) {
			String[] latLong = root.get(i).split(",");

			//軌跡
			//pw.println(id + "," + latLong[0] + "," + latLong[1] + "," + date.get(i));
			//地点
			pw.println(id + "," + latLong[0] + "," + latLong[1] + "," + latLong[3]);

			++cnt;
			if (cnt > numRec - 1) {
				pw.flush();
				System.out.println("Limit of Records:" + numRec);
				System.exit(0);
			}
		}
	}
}
