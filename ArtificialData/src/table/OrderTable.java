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
    
    private String orderFile = "TEST_ORDER.csv";
    private Integer numOrder = 10000000;
    private static Map indexMap = new HashMap();

    public OrderTable() {
    }
   
    public OrderTable(Integer n) {
        this.numOrder = n;
    } 
    
    public void createOrderTable(DataGenerator dataGen, List<String> layout, Map syaryoToCustomer) {
        int i = 0;

        try (PrintWriter out = CSVFileReadWrite.writer(orderFile)) {
            //Header Name
            out.println(layout.stream().map(l -> l.split(",")[1]).collect(Collectors.joining(",")));

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
                //csvLine.set(0, dataGen.getCompany(csvLine.get(9).hashCode()));
                String key = csvLine.get(29)+csvLine.get(30)+csvLine.get(31)+csvLine.get(32);
                csvLine.set(41, (String) syaryoToCustomer.get(key));
                
                out.println(String.join(",", csvLine));
                if (++i > numOrder) {
                    System.out.println("Order Table Generate!");
                    break;
                }
            }
        }
    }
}
