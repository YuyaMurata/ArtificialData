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
    private static final String PATH = "metaset\\kompas\\";
    public static Map<String, File> files = new HashMap();
    
    public static void setFiles(){
        File path = new File(PATH);
        int idx = 0;
        
        System.out.println("Read Metafiles:");
        for(File f : path.listFiles()){
            files.put(f.getName(), f);
            System.out.println("  "+idx+":"+f.getName());
            idx++;
        }
    }
    
    public static void main(String[] args) {
        setFiles();
        
        //Test
        MetaDataDefine def = new MetaDataDefine(files.get("test_syaryo.json"));
        def.getData().entrySet().stream().map(d -> d.getKey()+":"+d.getValue().size()+" u="+def.getUnique(d.getKey())).forEach(System.out::println);
    }
}
