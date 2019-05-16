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
import java.util.Map;
import py.PythonCommand;

/**
 *
 * @author zz17807
 */
public class MetaFileDefine {
    private static String TEMP = "py\\csv\\metahist_temp.csv";
    private static String VISUL_BAR = "py\\metahistgram.py";
    public String name;
    public Integer total;
    private Map<String, Map<String, Double>> data;
    private Map<String, Double> unique;
    
    public MetaFileDefine(File f){
        this.name = f.getName();
        setData(f);
    }
    
    private void setData(File f){
        this.data = new MapToJSON().toMap(f.getAbsolutePath());
        total = data.get("Total").get("_").intValue();
        
        //uniqu value
        unique = new HashMap<>();
        this.data.entrySet().stream()
                .forEach(d -> unique.put(d.getKey(), d.getValue().size() / total.doubleValue()));
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
