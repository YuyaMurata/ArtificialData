/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table;

import csv.CSVFileReadWrite;
import csv.TestMasterCSV;
import id.DataGenerator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SMRテーブル生成
 * @author murata
 */
public class SMRTable {
    public static String smrFile = "TEST_KOMTRAX_SMR.csv";
    private Integer numSMR = 250000000;
    private static String gpsFile = "TEST_KOMTRAX_GPS.csv";

    public SMRTable() {
    }
    
    public SMRTable(Integer n) {
        this.numSMR = n;
	gpsFile = GPSTable.gpsFile;
    }
    
    public void createSMRTable(DataGenerator dataGen, List<String> layout) {
        int i = 0;
        TestMasterCSV TEST = TestMasterCSV.getInstance();
        
        try (PrintWriter out = CSVFileReadWrite.writer(InfoTable.filepath+smrFile)) {
            //Header Name
            out.println(layout.stream().map(l -> l.split(",")[1]).collect(Collectors.joining(",")));
            
            try (BufferedReader br = CSVFileReadWrite.reader(InfoTable.filepath+gpsFile)) {
                String line = br.readLine();
                int smr = 0;
                while((line = br.readLine()) != null){
                    //マスタデータの切り出し
                    List<String> m = TEST.get();
                                
                    List<String> csvLine = new ArrayList();
                    String[] l = line.split(",");
                    csvLine.add(l[0]);
                    csvLine.add(l[1]);
                    csvLine.add(l[2]);
                    csvLine.add(l[3]);
                    csvLine.add(TEST.extract("gps.kisy", m)); // l[4]
                    csvLine.add(TEST.extract("gps.typ", m)+TEST.extract("gps.syhk", m)); //l[5]
                    csvLine.add(" "); //l[6]
                    csvLine.add(TEST.extract("gps.kiban", m)); //l[7]
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
                        System.out.println("Finished Parts Table!");
                        break;
                    }
                }
                
            } catch (IOException ex) {
            }
        }
    }
}
