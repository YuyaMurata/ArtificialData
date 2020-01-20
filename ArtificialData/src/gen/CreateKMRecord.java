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
    private static int MAX_TERM = 2000;

    public CreateKMRecord() {
        TEST = TestMasterCSV.getInstance();
    }

    static Map<String, String[]> map = new HashMap();

    public List<List<String>> get(List<String> m, int num, String data) {
        //masterの選択
        if (map.get(m.toString()) == null) {
            //KOMTRAXの初期値設定
            String[] status = new String[]{(TEST.extract("nny_ymd", m)), String.valueOf(num)};
            map.put(m.toString(), status);
        }

        String[] status = map.get(m.toString());
        List<List<String>> rec = IntStream.range(0, Integer.valueOf(status[1])).boxed()
                .map(i -> KMLAYOUT.get(data).keySet().stream()
                .map(k -> selector(data, k, status[0], i, m))
                .collect(Collectors.toList()))
                .collect(Collectors.toList());

        return rec;
    }

    public void create(String outpath, String data) {
        long start = System.currentTimeMillis();
        String f = "TEST_KOMTRAX_" + data + ".csv";
        try (PrintWriter pw = CSVFileReadWrite.writer(outpath + f)) {
            //header
            List<String> header = KMLAYOUT.get(data).values().stream()
                    .map(k -> k.toUpperCase())
                    .collect(Collectors.toList());
            pw.println(String.join(",", header));
            List<String> m = new ArrayList();
            int total = 0;
            while ((m = TEST.get(total)) != null) {
                int num = rand.nextInt(MAX_TERM);
                List<List<String>> rec = get(m, num, data);
                rec.stream().map(r -> String.join(",", r)).forEach(pw::println);
                
                total++;
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

        //System.out.println(data + "," + k + "," + st + "," + i + "," + TEST.extract(k.toLowerCase(), m) + "," + KMLAYOUT.get(data).get(k));
        if (TEST.extract(k.toLowerCase(), m) != null) {
            s = TEST.extract(k.toLowerCase(), m);
        } else if (KMLAYOUT.get(data).get(k).contains("_TIME")
                || KMLAYOUT.get(data).get(k).contains("_DATE")) {
            s = date(st, i);
        } else if (KMLAYOUT.get(data).get(k).contains("FLAG")
                || KMLAYOUT.get(data).get(k).contains("STATUS")
                || KMLAYOUT.get(data).get(k).contains("ENGINE")
                || KMLAYOUT.get(data).get(k).contains("EVENT")) {
            s = String.valueOf(rand.nextInt(2));
        } else if (KMLAYOUT.get(data).get(k).equals("MAKER_CODE")) {
            s = "0001";
        } else if (KMLAYOUT.get(data).get(k).contains("SMR_VALUE")) {
            //進捗を増やす仕組み
            if (acmsmr.get(m) == null) {
                acmsmr.put(m, rand.nextInt(1000) * 60);
            } else {
                acmsmr.put(m, acmsmr.get(m) + rand.nextInt(36) * 60);
            }

            s = acmsmr.get(m).toString();
        } else if (KMLAYOUT.get(data).get(k).contains("COUNT")) {
            s = String.valueOf(rand.nextInt(36) * 16 * 60 * 2);
        } else if (KMLAYOUT.get(data).get(k).contains("UNIT")) {
            s = String.valueOf(2);
        } else if (KMLAYOUT.get(data).get(k).contains("MAP")) {
            s = String.valueOf(rand.nextInt(36) / 4 * 16 * 60 * 2);
        } else if (KMLAYOUT.get(data).get(k).contains("LATITUDE")) {
            s = latlong(k);
        } else if (KMLAYOUT.get(data).get(k).contains("LONGITUDE")) {
            s = latlong(k);
        } else if (KMLAYOUT.get(data).get(k).contains("PLACE")) {
            s = "";
        }
        
        //空白追加
        /*if (s.equals("")) {
            s = " ";
        }*/
        
        //System.out.println(k.toLowerCase() + ":" + s);
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
