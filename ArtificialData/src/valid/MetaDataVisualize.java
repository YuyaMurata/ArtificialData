/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package valid;

import csv.CSVFileReadWrite;
import csv.ListToCSV;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author ZZ17807
 */
public class MetaDataVisualize {
    public static void main(String[] args) {
        MetaDataSet.setFiles("metaset\\anonymous\\");
        MetaDataSet.files.keySet().stream().forEach(f ->{
            MetaDataDefine meta = new MetaDataDefine(MetaDataSet.files.get(f));
            Map<String, Map<String, Double>> data = meta.getData();
            
            System.out.println(f);
            Map<String, String> layout = ListToCSV.toMap("resource\\kmresource\\layout\\"+f.replace("test", "Layout").replace(".json", ".csv"), 2, 1);
            
            try(PrintWriter pw = CSVFileReadWrite.writerSJIS(f+".csv")){
                pw.println("公開キー,"+data.keySet().stream().collect(Collectors.joining(",")));
                pw.println("非公開キー,"+data.keySet().stream().map(k -> layout.get(k.toUpperCase().split("\\.")[1])).collect(Collectors.joining(",")));
                pw.println("データ匿名化の有無,");
                Boolean eof = false;
                int i = 0;
                while(!eof){
                    final int idx = i;
                    String str = data.values().stream()
                                    .map(d -> d.keySet())
                                    .map(dk -> idx > dk.size()-1 ? "" : dk.toArray(new String[dk.size()])[idx])
                                    .collect(Collectors.joining(","));
                    
                    eof = !Arrays.asList(str.split(",")).stream().filter(s -> !s.equals("")).findFirst().isPresent();
                    
                    pw.println(","+str);
                    i += 1;
                    
                    if(i % 1000 == 0){
                        System.out.println(i+"件 "+str);
                        eof = true;
                    }
                }
            }
        });
    }
}
