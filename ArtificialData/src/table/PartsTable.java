/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table;

import csv.CSVFileReadWrite;
import id.DataGenerator;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部品テーブルの生成
 * @author murata
 */
public class PartsTable {

	public static String partsFile = "TEST_PARTS.csv";
	private Integer numParts = 30000000;

	public PartsTable() {
	}

	public PartsTable(Integer n) {
		this.numParts = n;
	}

	public void createPartsTable(DataGenerator dataGen, List<String> layout, List<String> sbns) {
		int i = 0;

		try (PrintWriter out = CSVFileReadWrite.writer(InfoTable.filepath + partsFile)) {
			//Header Name
			List<String> header = layout.stream().map(l -> l.split(",")[1]).collect(Collectors.toList());
			out.println(String.join(",", header));

			//Data Generate
			while (true) {
				List<String> csvLine = new ArrayList();
				for (String field : layout) {
					String[] l = field.split(",");
					csvLine.add(dataGen.getData(l[1], l[3], Integer.valueOf(l[4])));
				}

				//作番
				csvLine.set(header.indexOf("作番"), sbns.get(DataGenerator.genLogic(sbns.size())));

				//会社コードの規則化 顧客コードから会社コードが決まるようにする
				csvLine.set(header.indexOf("会社コード"), dataGen.getCompany(csvLine.get(header.indexOf("作番")).hashCode()));

				out.println(String.join(",", csvLine));
                i++;
                if (i % 100000 == 0) {
						System.out.println(i + " 部品レコード 生成");
					}
				if (i > numParts) {
					System.out.println("Finished Parts Table!");
					break;
				}
			}
		}
	}
}
