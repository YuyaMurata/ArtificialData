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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author kaeru_yuya
 */
public class SyaryoTable {

    private String syaryoFile = "TEST_SYARYO.csv";
    private Integer numSyaryo = 1000000;
    private static Map indexMap = new HashMap();

    public SyaryoTable() {
    }
    
    public SyaryoTable(Integer n) {
        this.numSyaryo = n;
        DataGenerator.NUM_SYARYO = n;
    }
    
    
    public void createSyaryoTable(DataGenerator dataGen, List<String> layout) {
        int i = 0;

        try (PrintWriter out = CSVFileReadWrite.writer(syaryoFile)) {
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
                //型・小変形の規則化 機番から決まるようにする
                csvLine.set(header.indexOf("型式"), dataGen.getType(csvLine.get(header.indexOf("機番")).hashCode()));
                csvLine.set(header.indexOf("小変形"), dataGen.getSyhk(csvLine.get(header.indexOf("機番")).hashCode()));
                //会社コードの規則化 顧客コードから会社コードが決まるようにする
                //csvLine.set(0, dataGen.getCompany(csvLine.get(13).hashCode()));
                
                String key = csvLine.get(header.indexOf("機種"))+csvLine.get(header.indexOf("機番"));
                if(indexMap.get(key) == null){
                    indexMap.put(key, "1");
                    out.println(String.join(",", csvLine));
                }else
                    i--;
                
                if (++i > numSyaryo) {
                    System.out.println("Syaryo Table Generate!");
                    break;
                }
            }
        }
    }

}
