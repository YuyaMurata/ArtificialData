/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package valid;

import csv.CSVFileReadWrite;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import py.PythonCommand;

/**
 *
 * @author zz17807
 */
public class MetaDataDefine {
    private static String TEMP = "py\\csv\\metahist_temp.csv";
    private static String VISUL_BAR = "py\\metahistgram.py";
    public String name;
    public Integer total;
    private Map<String, TreeMap<Integer, String>> acm;
    private Map<String, Map<String, Double>> data;
    private Map<String, Double> unique;
    private Map<String, Integer> range;
    
    public MetaDataDefine(File f){
        this.name = f.getName();
        setData(f);
    }
    
    private void setData(File f){
        this.data = new MapToJSON().toMap(f.getAbsolutePath());
        total = data.get("Total").get("_").intValue();
        //remove total info
        this.data.remove("Total");
        
        //uniqu value
        unique = new LinkedHashMap<>();
        acm = new LinkedHashMap<>();
        range = new LinkedHashMap<>();
        this.data.entrySet().stream()
                .forEach(d -> {
                    unique.put(d.getKey(), d.getValue().size() / total.doubleValue());
                    
                    //ルーレット用
                    acm.put(d.getKey(), new TreeMap<>());
                    Double n = 0d;
                    for(String key : d.getValue().keySet()){
                        n += d.getValue().get(key);
                        acm.get(d.getKey()).put(n.intValue(), key);
                    }
                    range.put(d.getKey(), n.intValue());
                });
    }
    
    public String getData(String field, Integer r){
        return acm.get(field).ceilingEntry(r).getValue();
    }
    
    public Integer getRange(String field){
        return range.get(field);
    }
    
    public Double getUnique(String field){
        return unique.get(field);
    }
    
    public Map<String, Double> getData(String field){
        return data.get(field);
    }
    
    public Map<String, Map<String, Double>> getData(){
        return data;
    }
    
    public void print(String field){
        System.out.println(field+" : u="+getUnique(field));
        data.get(field).entrySet().stream().forEach(System.out::println);
    }
    
    public void visualize(String field){
        try(PrintWriter pw = CSVFileReadWrite.writer(TEMP)){
            //header
            pw.println(name+" - "+field);
            pw.println("List,N");
            
            Map<String, Double> dl = getData(field);
            dl.entrySet().stream()
                    .map(d -> d.getKey()+","+d.getValue().intValue())
                    .forEach(pw::println);
        }
        
        String[] args = new String[]{TEMP};
        PythonCommand.py(VISUL_BAR, args);
    }
}
