/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package valid;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zz17807
 */
public class MetaDataSet {
    public static final String PATH = "metaset\\kompas\\";
    public static Map<String, File> files = new HashMap();
    
    public static void setFiles(String path){
        File p = new File(path);
        int idx = 0;
        
        System.out.println("Read Metafiles:");
        for(File f : p.listFiles()){
            if(f.getAbsoluteFile().toString().contains(".txt") || f.getAbsoluteFile().toString().contains("test_km_all.json"))
                continue;
            files.put(f.getName(), f);
            System.out.println("  "+idx+":"+f.getName());
            idx++;
        }
    }
    
    public static void main(String[] args) {
        setFiles(PATH);
        
        //Test
        MetaDataDefine def = new MetaDataDefine(files.get("test_syaryo.json"));
        def.getData().entrySet().stream().map(d -> d.getKey()+":"+d.getValue().size()+" u="+def.getUnique(d.getKey())).forEach(System.out::println);
    }
}
