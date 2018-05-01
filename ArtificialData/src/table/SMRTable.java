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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author kaeru_yuya
 */
public class SMRTable {

    private String smrFile = "TEST_SMR.csv";
    private Integer numSMR = 250000000;
    private static String gpsFile = "TEST_GPS.csv";

    public SMRTable() {
    }
    
    public SMRTable(Integer n) {
        this.numSMR = n;
    }
    
    public void createSMRTable(DataGenerator dataGen, List<String> layout) {
        int i = 0;

        try (PrintWriter out = CSVFileReadWrite.writer(smrFile)) {
            //Header Name
            out.println(layout.stream().map(l -> l.split(",")[1]).collect(Collectors.joining(",")));

            try (BufferedReader br = CSVFileReadWrite.reader(new File(gpsFile))) {
                String line = br.readLine();
                int smr = 0;
                while((line = br.readLine()) != null){
                    List<String> csvLine = new ArrayList();
                    String[] l = line.split(",");
                    csvLine.add(l[0]);
                    csvLine.add(l[1]);
                    csvLine.add(l[2]);
                    csvLine.add(l[3]);
                    csvLine.add(l[4]);
                    csvLine.add(l[5]);
                    csvLine.add(l[6]);
                    csvLine.add(l[7]);
                    csvLine.add(l[8]);
                    csvLine.add(l[9]);
                    csvLine.add(String.valueOf(smr));
                    csvLine.add(l[15]);
                    csvLine.add(l[18]);
                    
                    out.println(String.join(",", csvLine));
                    smr += DataGenerator.genLogic(24);
                    i++;
                    
                    if(i%100000 == 0)
                        System.out.println(i+" SMRレコード 生成");
                    if (i > numSMR) {
                        System.out.println("Parts Table Generate!");
                        break;
                    }
                }
                
            } catch (IOException ex) {
            }
        }
    }
}
