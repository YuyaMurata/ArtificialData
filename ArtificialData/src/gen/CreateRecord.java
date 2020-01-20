/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

import csv.CSVFileReadWrite;
import csv.TestMasterCSV;
import ec.util.MersenneTwisterFast;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import valid.MetaDataDefine;

/**
 *
 * @author zz17807
 */
public class CreateRecord {

    private String name;
    private MetaDataDefine def;
    public int origin;
    private static MersenneTwisterFast rand = new MersenneTwisterFast();
    private static TestMasterCSV TEST;

    public CreateRecord(File meta) {
        name = meta.getName();
        def = new MetaDataDefine(meta);
        origin = def.total;
        TEST = TestMasterCSV.getInstance();
    }

    public String get() {
        //masterの選択
        List<String> m = TEST.get();

        List<String> rec = def.getData().keySet().stream()
                .map(k -> TEST.extract(k, m) == null ? def.getData(k, rand.nextInt(def.getRange(k))) : TEST.extract(k, m))
                .map(rd -> rd == null ? "" : rd)
                //.map(rd -> rd.equals("") || rd.equals("null") ? " " : rd) //空白追加
                .map(rd -> rd.equals("") || rd.equals("null") ? "" : rd)
                .collect(Collectors.toList());

        return String.join(",", rec);
    }

    //作番用
    static List<String> svid = new ArrayList<>();
    static Boolean flg = true;

    public String svget(int i, int j) {
        //masterの選択
        List<String> m = TEST.get();

        List<String> rec = def.getData().keySet().stream()
                .map(k -> TEST.extract(k, m) == null ? def.getData(k, rand.nextInt(def.getRange(k))) : TEST.extract(k, m))
                .map(rd -> rd == null ? "" : rd)
                //.map(rd -> rd.equals("") || rd.equals("null") ? " " : rd) //空白追加
                .map(rd -> rd.equals("") || rd.equals("null") ? "" : rd)
                .collect(Collectors.toList());

        //会社コード+作番の登録
        if (flg) {
            svid.add(rec.get(i) + "," + rec.get(j));
        } else {
            if (rand.nextBoolean(0.7d)) {
                String sv = svid.get(rand.nextInt(svid.size()));
                rec.set(i, sv.split(",")[0]);
                rec.set(j, sv.split(",")[1]);
            }
        }

        return String.join(",", rec);
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
            if(header.indexOf("作番") > 0 || header.indexOf("サービス経歴管理番号") > 0){
                int idx1 = header.indexOf("会社コード");
                int idx2 = header.indexOf("作番") > 0 ? header.indexOf("作番") : header.indexOf("サービス経歴管理番号");
                IntStream.range(0, n).boxed().map(i -> svget(idx1, idx2)).forEach(pw::println);
            }else
                IntStream.range(0, n).boxed().map(i -> get()).forEach(pw::println);
            
            //作番管理用
            if(!svid.isEmpty())
                flg = false;
        }
        long stop = System.currentTimeMillis();
        System.out.println(f + " 生成 time=" + (stop - start) + "[ms]");
    }
}
