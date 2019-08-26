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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author ZZ17807
 */
public class CreateKMRecord {

    private static TestMasterCSV TEST;
    public static Map<String, Map<String, String>> KMLAYOUT = new HashMap() {
        {
            put("SMR", ListToCSV.toMap("metaset\\Layout_SMR.csv", 2, 1));
            put("ACT", ListToCSV.toMap("metaset\\Layout_ACT.csv", 2, 1));
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
            String[] status = new String[]{(TEST.extract("nny_ymd", m)), String.valueOf(rand.nextInt(MAX_TERM))};
            map.put(m.toString(), status);
        }

        String[] status = map.get(m.toString());
        List<List<String>> rec = IntStream.range(0, Integer.valueOf(status[1])).boxed()
                .map(i -> KMLAYOUT.get(data).keySet().stream()
                .filter(k -> !k.contains("name"))
                .map(k -> selector(data, k, status[0], i, m))
                .collect(Collectors.toList()))
                .collect(Collectors.toList());

        return rec;
    }

    public void create(int n, String outpath, String data) {
        long start = System.currentTimeMillis();
        String f = "TEST_KOMTRAX_" + data + ".csv";
        try (PrintWriter pw = CSVFileReadWrite.writer(outpath + f)) {
            //header
            List<String> header = KMLAYOUT.get(data).values().stream()
                    .map(k -> k.toUpperCase())
                    .collect(Collectors.toList());
            pw.println(String.join(",", header));
            int total = 0;
            while (total < n) {
                List<List<String>> rec = get(data);
                total += rec.size();
                rec.stream().map(r -> String.join(",", r)).forEach(pw::println);
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

    private static Map<Object, Integer> acmsmr = new HashMap<>();
    private static String selector(String data, String k, String st, int i, List<String> m) {
        String s = "";

        System.out.println(data + "," + k + "," + st + "," + i + "," + TEST.extract(k.toLowerCase(), m) + "," + KMLAYOUT.get(data).get(k));

        if (TEST.extract(k.toLowerCase(), m) != null) {
            s = TEST.extract(k.toLowerCase(), m);
        } else if (KMLAYOUT.get(data).get(k).contains("時間")) {
            s = date(st, i);
        } else if (KMLAYOUT.get(data).get(k).contains("フラグ")
                || KMLAYOUT.get(data).get(k).contains("ステータス")
                || KMLAYOUT.get(data).get(k).contains("エンジン")
                || KMLAYOUT.get(data).get(k).contains("区分")) {
            s = String.valueOf(rand.nextInt(2));
        } else if (KMLAYOUT.get(data).get(k).equals("メーカーコード")) {
            s = "0001";
        } else if (KMLAYOUT.get(data).get(k).contains("SMR進捗")) {
            //進捗を増やす仕組み
            if(acmsmr.get(m) == null)
                acmsmr.put(m, rand.nextInt(1000)*60);
            else
                acmsmr.put(m, acmsmr.get(m)+rand.nextInt(36)*60);
            
            s = acmsmr.get(m).toString();
            
        } else if (KMLAYOUT.get(data).get(k).contains("カウント")) {
            s = String.valueOf(rand.nextInt(36) * 16 * 60 * 2);
        } else if (KMLAYOUT.get(data).get(k).contains("ユニット")) {
            s = String.valueOf(2);
        } else if (KMLAYOUT.get(data).get(k).contains("マップ")) {
            s = String.valueOf(rand.nextInt(36) / 4 * 16 * 60 * 2);
        } else if (KMLAYOUT.get(data).get(k).contains("経度")) {
            s = latlong(k);
        } else if (KMLAYOUT.get(data).get(k).contains("緯度")) {
            s = latlong(k);
        } else if (KMLAYOUT.get(data).get(k).contains("場所")) {
            s = "";
        }

        System.out.println(k.toLowerCase() + ":" + s);

        return s;
    }

    private static String latlong(String k) {
        String l;

        if (k.equals("緯度")) {
            l = String.format("%d.%d.%d.%d", new Object[]{rand.nextInt(90), rand.nextInt(90), rand.nextInt(90), rand.nextInt(360)});
            l = rand.nextBoolean() ? "N" + l : "S" + l;
        } else {
            l = String.format("%d.%d.%d.%d", new Object[]{rand.nextInt(180), rand.nextInt(90), rand.nextInt(90), rand.nextInt(360)});
            l = rand.nextBoolean() ? "E" + l : "W" + l;
        }

        return l;
    }
}
