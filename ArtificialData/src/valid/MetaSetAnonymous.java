/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package valid;

import csv.ListToCSV;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author ZZ17807
 */
public class MetaSetAnonymous {

    //
    static Map<String, String> rule = ListToCSV.toMap("metaset\\国内データ項目匿名化案20190704.csv", 2, 29);

    public static void main(String[] args) {
        System.out.println(rule);
        MetaDataSet.setFiles();
        
        /*
        MetaDataSet.files.keySet().stream().forEach(f -> {
            MetaDataDefine meta = new MetaDataDefine(MetaDataSet.files.get(f));
            Map<String, Map<String, Double>> data = meta.getData();
        });*/
        
        MetaDataDefine meta = new MetaDataDefine(MetaDataSet.files.get("test_syaryo.json"));
        Map<String, Map<String, Double>> data = meta.getData();
        Map<String, Map<String, Double>> anymdata = new LinkedHashMap<>();
        data.entrySet().stream().limit(data.size()-1).forEach(d ->{
            String key = d.getKey();
            String field = key.split("\\.")[1];
            String ruleField = field.toUpperCase();
            
            anymdata.put(field, new LinkedHashMap());
            
            d.getValue().entrySet().stream().forEach(df ->{
                anymdata.get(field).put(
                        Anonymous.A(rule.get(field.toUpperCase()), 
                        field, df.getKey()), 
                        df.getValue()
                );
            });
        });
        
        new MapToJSON().toJSON("test_syaryo.json", anymdata);
    }
}
