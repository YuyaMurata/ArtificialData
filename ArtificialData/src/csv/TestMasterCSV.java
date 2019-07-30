/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csv;

import ec.util.MersenneTwisterFast;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ZZ17807
 */
public class TestMasterCSV {
    
    private static String path = "metaset\\test\\test_master.csv";
    private static List<String> mheader = new ArrayList();
    private static Map<String, List<String>> master = new HashMap();

    private static MersenneTwisterFast rand = new MersenneTwisterFast();
    private static TestMasterCSV instance = new TestMasterCSV();
    
    public Map<String, String> headers;
    
    private TestMasterCSV() {
    }
    
    public void settings(){
        List<String> l = ListToCSV.toList(path);

        //header
        mheader = Arrays.asList(l.get(0).split(","));
        l.remove(0);

        //data
        l.stream().map(s -> s.split(",")).forEach(s -> {
            master.put(s[0], Arrays.asList(s));
        });
        
        //全ファイルのヘッダ情報
        headers = ListToCSV.toMap("metaset\\データ項目匿名化ファイル.csv", 2, 1);
        //headers = ListToCSV.toMap("metaset\\国内データ項目匿名化案20190704.csv", 2, 1);
    }

    public static TestMasterCSV getInstance() {
        return instance;
    }

    //行の抽出
    public List<String> get() {
        return master.get(String.valueOf(rand.nextInt(master.size())));
    }

    //列項目の抽出
    public String extract(String key, List<String> m) {
        int i = mheader.indexOf(key.split("\\.")[1]);
        if (i < 0) {
            return null;
        } else if (i == m.size()) {
            return "";
        }

        try {
            return m.get(i);
        } catch (Exception e) {
            System.err.println(i+":"+key);
            System.err.println(m.size()+":"+m);
            System.exit(0);
            return null;
        }
    }
}
