/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json.create;

import csv.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import json.file.SyaryoObjectToJSON;
import json.obj.SyaryoObject;

/**
 *
 * @author 産総研・東工大OIL_2-2
 */
public class JSONCreator {

    private String filename = "test_syaryo.json";

    public void create(String filepath) {
        //Initialize Map
        Map<String, SyaryoObject> syaryoMap = new HashMap();

        syaryo(new File(filepath + "TEST_SYARYO.csv"), syaryoMap);
        customer(new File(filepath + "TEST_CUSTOMER.csv"), syaryoMap);
        
        SyaryoObjectToJSON json = new SyaryoObjectToJSON();
        json.write(filename, syaryoMap);
        json.pretty(filename);
    }

    private void syaryo(File file, Map<String, SyaryoObject> map) {
        try (BufferedReader br = CSVFileReadWrite.reader(file)) {
            String line = br.readLine();
            List<String> header = Arrays.asList(line.split(","));
            List<Integer> keys = new ArrayList();
            keys.add(header.indexOf("機種"));
            keys.add(header.indexOf("型式"));
            keys.add(header.indexOf("小変形"));
            keys.add(header.indexOf("機番"));

            int subKey = header.indexOf("納入年月日");
            List<Integer> custkeys = new ArrayList();
            custkeys.add(header.indexOf("会社コード"));
            custkeys.add(header.indexOf("保有顧客コード"));

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String name = keys.stream()
                        .map(key -> data[key])
                        .collect(Collectors.joining("-"));

                //Customert
                String cust = custkeys.stream()
                        .map(key -> data[key])
                        .collect(Collectors.joining("-"));
                if (custMap.get(cust) == null) {
                    custMap.put(cust, new ArrayList());
                }
                custMap.get(cust).add(name);

                //Syaryo
                SyaryoObject syaryo;
                if ((syaryo = map.get(name)) == null) {
                    syaryo = new SyaryoObject(name);
                }

                syaryo.add("車両", data[subKey], Arrays.asList(line.split(",")));

                //Register
                map.put(name, syaryo);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    Map<String, List<String>> custMap = new HashMap();

    private void customer(File file, Map<String, SyaryoObject> map) {
        try (PrintWriter pw = CSVFileReadWrite.writer("error_"+file.getName());
                BufferedReader br = CSVFileReadWrite.reader(file)) {
            String line = br.readLine();
            List<String> header = Arrays.asList(line.split(","));
            List<Integer> keys = new ArrayList();
            keys.add(header.indexOf("会社コード"));
            keys.add(header.indexOf("顧客コード"));
            
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String cust = keys.stream()
                        .map(key -> data[key])
                        .collect(Collectors.joining("-"));
                
                if(custMap.get(cust) == null){
                    pw.println(cust+":"+line);
                    continue;
                }
                
                for (String name : custMap.get(cust)) {
                    SyaryoObject syaryo = map.get(name);
                    syaryo.add("顧客", cust, Arrays.asList(line.split(",")));

                    //Register
                    map.put(name, syaryo);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
