/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package valid;

import id.ID;

/**
 *
 * @author kaeru
 */
public class MetaDataAnonymous {
    public static void main(String[] args) {
        //機種
        kisy();
        //機番
        //会社コード
        //作番
        //顧客コード
    }
    
    private static void kisy(){
        //test
        MetaDataSet.setFiles();
        MetaDataDefine meta = new MetaDataDefine(MetaDataSet.files.get("test_syaryo.json"));
        
        meta.getData().get("syaryo.kisy").keySet().stream().forEach(k ->{
            String s = k.replaceAll("[A-Z]", "");
            if(s.length() == k.length()){
                System.out.println(k+" -> "+s);
            }
                //System.out.println(k+" -> "+ID.A(2)+s);
        });
    }
}
