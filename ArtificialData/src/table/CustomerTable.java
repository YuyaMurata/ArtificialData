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
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author kaeru_yuya
 */
public class CustomerTable {

    private String cutomerFile = "TEST_CUSTOMER.csv";
    private Integer numCustomer = 100000;
    private static DecimalFormat df = new DecimalFormat("00000");
    private static DecimalFormat dm = new DecimalFormat("00");

    public CustomerTable() {
    }
    
    public CustomerTable(Integer n) {
        this.numCustomer = n;
        DataGenerator.NUM_CUSTOMER = n;
    }
    
    public void createCustTable(DataGenerator dataGen, String file, List<String> layout) {
        int i = 0;

        try (PrintWriter out = CSVFileReadWrite.writer(cutomerFile)) {
            //Header Name
            out.println(layout.stream().map(l -> l.split(",")[1]).collect(Collectors.joining(",")));
            try (BufferedReader csv = CSVFileReadWrite.reader(new File(file))) {
                String line = csv.readLine();

                //Data Generate
                while ((line = csv.readLine()) != null) {
                    String[] h = line.split(",");
                    List csvLine = new ArrayList();
                    for (String field : layout) {
                        String[] l = field.split(",");
                        if (l[1].equals("顧客コード")) {
                            String cid = "C00" + df.format(i++); 
                            csvLine.add(cid);
                            //csvLine.set(0, dataGen.getCompany(cid.hashCode()));
                        } else if (l[1].equals("顧客名称１")) {
                            csvLine.add(h[0]);
                        } else if (l[1].equals("顧客名称２")) {
                            csvLine.add(h[1]);
                        } else if (l[1].equals("都道府県名称")) {
                            csvLine.add(h[5]);
                        } else if (l[1].contains("年月")) {
                            csvLine.add(h[4].split("/")[0] + dm.format(Integer.valueOf(h[4].split("/")[1])));
                        } else {
                            csvLine.add(dataGen.getData(l[1], l[3], Integer.valueOf(l[4])));
                        }
                    }
                    out.println(String.join(",", csvLine));
                    if (i > numCustomer) {
                        System.out.println("Customer Table Generate!");
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
