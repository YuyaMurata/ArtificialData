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
 *
 * @author kaeru_yuya
 */
public class PartsTable {
    private String partsFile = "TEST_PARTS.csv";
    private Integer numParts = 30000000;

    public PartsTable() {
    }
    
    public PartsTable(Integer n) {
        this.numParts = n;
    }
    
    public void createPartsTable(DataGenerator dataGen, List<String> layout) {
        int i = 0;

        try (PrintWriter out = CSVFileReadWrite.writer(partsFile)) {
            //Header Name
            out.println(layout.stream().map(l -> l.split(",")[1]).collect(Collectors.joining(",")));

            //Data Generate
            while (true) {
                List<String> csvLine = new ArrayList();
                for (String field : layout) {
                    String[] l = field.split(",");
                    csvLine.add(dataGen.getData(l[1], l[3], Integer.valueOf(l[4])));
                }
                out.println(String.join(",", csvLine));

                if (++i > numParts) {
                    System.out.println("Parts Table Generate!");
                    break;
                }
            }
        }
    }
}
