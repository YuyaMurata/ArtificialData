/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author ZZ17390
 */
public class ListToCSV {
    public static List<String> toListSJIS(String csv){
        List<String> list = new ArrayList<>();
        try(BufferedReader br = CSVFileReadWrite.readerSJIS(csv)){
            String line;
            while((line = br.readLine()) != null){
                //コメント除外
                if(line.charAt(0) == '#')
                    continue;
                
                list.add(line);
            }
            
            return list;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static List<String> toList(String csv){
        List<String> list = new ArrayList<>();
        try(BufferedReader br = CSVFileReadWrite.reader(csv)){
            String line;
            while((line = br.readLine()) != null){
                //コメント除外
                if(line.charAt(0) == '#')
                    continue;
                
                list.add(line);
            }
            
            return list;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static Map<String, String> toMap(String csv, int k, int v){
        List<String> l = toListSJIS(csv);
        
        //ヘッダ削除
        l.remove(0);
        
        Map<String, String> map = l.stream()
                                    .map(s -> s.split(","))
                                    .filter(s -> !s[k].equals(""))
                                    .collect(Collectors.toMap(
                                            s -> s[k], 
                                            s -> s[v],
                                            (e1, e2) -> e2,
                                            LinkedHashMap::new
                                    ));
        
        return map;
    }
    
    public static void toCSV(String csv, List list){
        try(PrintWriter pw = CSVFileReadWrite.writer(csv)){
            list.stream().forEach(pw::println);   
        }
    }
}
