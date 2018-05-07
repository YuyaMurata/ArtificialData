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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author kaeru_yuya
 */
public class OrderTable {
    
    public static String orderFile = "TEST_ORDER.csv";
    private Integer numOrder = 10000000;
    private static Map indexMap = new HashMap();

    public OrderTable() {
    }
   
    public OrderTable(Integer n) {
        this.numOrder = n;
    } 
    
    public void createOrderTable(DataGenerator dataGen, List<String> layout, Map syaryoToCustomer) {
        int i = 0;

        try (PrintWriter out = CSVFileReadWrite.writer(InfoTable.filepath+orderFile)) {
            //Header Name
            List<String> header = layout.stream().map(l -> l.split(",")[1]).collect(Collectors.toList());
            out.println(String.join(",", header));

            //Data Generate
            while (true) {
                List<String> csvLine = new ArrayList();
                for (String field : layout) {
                    String[] l = field.split(",");
                    String d = dataGen.getData(l[1], l[3], Integer.valueOf(l[4]));
                    if(l[1].equals("作番")){
                        while(indexMap.get(d) != null)
                            d = dataGen.getData(l[1], l[3], Integer.valueOf(l[4]));
                        indexMap.put(d, "1");
                    }
                    csvLine.add(d);
                }
                //機種の規則化 機番から機種が決まるようにする
                csvLine.set(header.indexOf("型式"), dataGen.getType(csvLine.get(header.indexOf("機番")).hashCode()));
                csvLine.set(header.indexOf("小変形"), dataGen.getSyhk(csvLine.get(header.indexOf("機番")).hashCode()));
                
                //会社コードの規則化 顧客コードから会社コードが決まるようにする
                //csvLine.set(0, dataGen.getCompany(csvLine.get(9).hashCode()));
                csvLine.set(header.indexOf("会社コード"), dataGen.getCompany(csvLine.get(header.indexOf("作番")).hashCode()));
                
                String key = csvLine.get(header.indexOf("機種"))+csvLine.get(header.indexOf("型式"))+csvLine.get(header.indexOf("小変形"))+csvLine.get(header.indexOf("機番"));
                csvLine.set(header.indexOf("保有顧客コード"), (String) syaryoToCustomer.get(key));
                
                out.println(String.join(",", csvLine));
                if (++i > numOrder) {
                    System.out.println("Order Table Generate!");
                    break;
                }
            }
        }
    }
}
