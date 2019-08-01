/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

import csv.CSVFileReadWrite;
import csv.ListToCSV;
import csv.TestMasterCSV;
import ec.util.MersenneTwisterFast;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author ZZ17807
 */
public class CreateKMRecord {

    private static TestMasterCSV TEST;
    private static Map<String, Map<String, String>> KMLAYOUT = new HashMap() {
        {
            put("SMR", ListToCSV.toMap("metaset\\Layout_SMR.csv", 2, 1));
            put("ACT_DATA", ListToCSV.toMap("metaset\\Layout_ACT.csv", 2, 1));
            put("GPS", ListToCSV.toMap("metaset\\Layout_GPS.csv", 2, 1));
        }
    };
    private static MersenneTwisterFast rand = new MersenneTwisterFast();
    private static int MAX_TERM = 3000;
    
    public CreateKMRecord() {
        TEST = TestMasterCSV.getInstance();
    }
    
    static Map<String, String[]> map = new HashMap();

    public List<List<String>> get(String data) {
        //masterの選択
        List<String> m = TEST.get();
        if (map.get(m.toString()) == null) {
            String[] status = new String[]{(TEST.extract("NNY_YMD", m)), String.valueOf(rand.nextInt(MAX_TERM))};
            map.put(m.toString(), status);
        }
        
        String[] status = map.get(m.toString());
        List rec = IntStream.range(0, Integer.valueOf(status[1])).boxed()
                .map(i -> KMLAYOUT.get(data).keySet().stream()
                        .map(k -> selector(data, k, status[0], i, m))
                        .collect(Collectors.toList())
                ).collect(Collectors.toList());
        
        return rec;
    }
    
    public void create(int n, String outpath) {
        String f = def.name.toUpperCase().replace("JSON", "csv");
        long start = System.currentTimeMillis();
        try (PrintWriter pw = CSVFileReadWrite.writer(outpath + f)) {
            //header
            List<String> header = def.getData().keySet().stream()
                    .map(k -> k.split("\\.")[1].toUpperCase())
                    .map(k -> TEST.headers.get(k)) // ヘッダーの日本語変換
                    .collect(Collectors.toList());
            pw.println(String.join(",", header));

            //data
            if (header.indexOf("作番") > 0 || header.indexOf("サービス経歴管理番号") > 0) {
                int idx1 = header.indexOf("会社コード");
                int idx2 = header.indexOf("作番") > 0 ? header.indexOf("作番") : header.indexOf("サービス経歴管理番号");
                IntStream.range(0, n).boxed().map(i -> svget(idx1, idx2)).forEach(pw::println);
            } else {
                IntStream.range(0, n).boxed().map(i -> get()).forEach(pw::println);
            }

            //作番管理用
            if (!svid.isEmpty()) {
                flg = false;
            }
        }
        long stop = System.currentTimeMillis();
        System.out.println(f + " 生成 time=" + (stop - start) + "[ms]");
    }
    
    static SimpleDateFormat nsdf = new SimpleDateFormat("yyyyMMdd");
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    static Calendar cal = Calendar.getInstance();

    private static String date(String st, int t) {
        try {
            Date d = nsdf.parse(st);
            cal.setTime(d);
            cal.add(Calendar.DATE, t);
            return sdf.format(cal.getTime());
        } catch (ParseException ex) {
            return null;
        }
    }
    
    static Integer smr = 0;
    private static String selector(String data, String k, String st, int i, List<String> m){
        String s = "";
        if(TEST.extract(k, m) != null)
            s = TEST.extract(k, m);
        else if(KMLAYOUT.get(data).get(k).contains("時間"))
            s = date(st, i);
        else if(KMLAYOUT.get(data).get(k).contains("フラグ") 
                || KMLAYOUT.get(data).get(k).contains("ステータス")
                || KMLAYOUT.get(data).get(k).contains("エンジン")
                || KMLAYOUT.get(data).get(k).contains("区分"))
            s = String.valueOf(rand.nextInt(2));
        else if(KMLAYOUT.get(data).get(k).equals("メーカーコード"))
            s = "0001";
        else if(KMLAYOUT.get(data).get(k).contains("カウント"))
            s = String.valueOf(rand.nextInt(36)*16*60*2);
        else if(KMLAYOUT.get(data).get(k).contains("ユニット"))
            s = String.valueOf(2);
        else if(KMLAYOUT.get(data).get(k).contains("マップ"))
            s = String.valueOf(rand.nextInt(36)/4*16*60*2);
        else if(KMLAYOUT.get(data).get(k).contains("経度"))
            s = latlong(k);
        else if(KMLAYOUT.get(data).get(k).contains("緯度"))
            s = latlong(k);
        else if(KMLAYOUT.get(data).get(k).contains("場所"))
            s = "";
        
        return s;
    }
    
    private static String latlong(String k){
        String l;
        
        if(k.equals("緯度")){
            l = String.format("02d.02d.02d.03d", new Object[]{rand.nextInt(90),rand.nextInt(90),rand.nextInt(90),rand.nextInt(360)});
            l = rand.nextBoolean()?"N"+l:"S"+l;
        }else{
            l = String.format("03d.02d.02d.03d", new Object[]{rand.nextInt(180),rand.nextInt(90),rand.nextInt(90),rand.nextInt(360)});
            l = rand.nextBoolean()?"E"+l:"W"+l;
        }
            
        return l;
    }
}
