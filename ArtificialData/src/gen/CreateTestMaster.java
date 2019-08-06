/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

import csv.CSVFileReadWrite;
import csv.ListToCSV;
import ec.util.MersenneTwisterFast;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import valid.MetaDataDefine;
import valid.MetaDataSet;

/**
 * テストデータ生成ためのマスター作成
 * ※生成前に作成しないと動かない
 *
 * @author ZZ17807
 */
public class CreateTestMaster {

    //
    static Map<String, String> rule = ListToCSV.toMap("metaset\\データ項目匿名化ファイル.csv", 2, 7);
    //static Map<String, String> rule = ListToCSV.toMap("metaset\\国内データ項目匿名化案20190704.csv", 2, 29);
    static String path = "metaset\\anonymous\\";
    static String outpath = "metaset\\test\\";

    public static void generate(int n) {
        //集約データチェック
        Map<String, TreeMap<Double, String>> roullet = aggregate();
        /*roullet.entrySet().stream()
                .map(a -> a.getKey() + ":" + a.getValue().size()
                + " - sample(" + a.getValue().entrySet().stream().limit(10).map(av -> av.getValue() + "(" + String.format("%.1f", av.getKey()) + ")").collect(Collectors.toList()) + ")")
                .forEach(System.out::println);
        */
        
        //マスタデータ生成
        create(roullet, n);
    }

    //ID処理されているデータの集約
    private static Map<String, TreeMap<Double, String>> aggregate() {
        Map<String, Map<String, Double>> agg = new ConcurrentHashMap();

        //マスタに載せたいデータ項目
        rule.put("SYHK", "ID");
        rule.put("TYP", "ID");
        rule.put("NNY_YMD", "ID");

        MetaDataSet.setFiles(path);
        MetaDataSet.files.values().stream().forEach(f -> {
            MetaDataDefine meta = new MetaDataDefine(f); //MetaDataSet.files.get("test_parts.json")
            System.out.println(meta.name);

            Map<String, Map<String, Double>> data = meta.getData();
            data.entrySet().parallelStream().filter(d -> idfilter(d.getKey())).forEach(d -> {
                String field = d.getKey().split("\\.")[1];

                if (agg.get(field) == null) {
                    agg.put(field, new ConcurrentHashMap());
                    agg.get(field).putAll(d.getValue());
                } else {
                    d.getValue().entrySet().parallelStream().forEach(dv -> {
                        try {
                            if (agg.get(field).get(dv.getKey()) == null) {
                                agg.get(field).put(dv.getKey(), dv.getValue());
                            } else {
                                agg.get(field).put(dv.getKey(), agg.get(field).get(dv.getKey()) + dv.getValue());
                            }
                        } catch (Exception e) {
                            System.err.println(dv);
                            System.err.println(agg.get(field));
                            System.exit(0);
                        }
                    });
                }
            });
        });

        //不都合があるため機番のみ空白削除
        agg.get("kiban").remove("");

        //累積比率に変換し並び替え
        Map<String, TreeMap<Double, String>> acm = new ConcurrentHashMap();
        agg.entrySet().parallelStream().forEach(ag -> {
            Map<String, Double> sort = ag.getValue().entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(agv -> agv.getKey(), agv -> agv.getValue(), (v1, v2) -> v2, LinkedHashMap::new));

            acm.put(ag.getKey(), new TreeMap<>());

            //累積比率マップ
            Double total = 0d;
            Double sum = sort.values().stream().mapToDouble(sv -> sv).sum();
            for (String key : sort.keySet()) {
                total += sort.get(key);
                acm.get(ag.getKey()).put(total / sum, key);
            }
        });

        return acm;
    }

    //匿名化タイプチェック
    private static Boolean idfilter(String dkey) {
        String field = dkey.split("\\.")[1];
        String anonym = rule.get(field.toUpperCase());
        if (anonym == null) {
            return false;
        }

        return (anonym.contains("ID") || anonym.contains("担当名") || anonym.contains("機種・機番"));
    }
    
    private static MersenneTwisterFast rand = new MersenneTwisterFast();
    
    public static void create(Map<String, TreeMap<Double, String>> d, int n) {
        Map<List<String>, String> dupCheck = new HashMap<>();

        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(outpath+"test_master.csv")) {
            //header
            pw.println("No.," + String.join(",", d.keySet()));

            IntStream.range(0, n).forEach(i -> {
                //record
                List<String> rec = new ArrayList<>();
                rec.add(String.valueOf(i));
                rec.addAll(d.values().stream()
                        .map(r -> r.ceilingEntry(Math.abs(rand.nextDouble())).getValue())
                        .map(rv -> rv.equals("") ? " " : rv)
                        .collect(Collectors.toList()));

                //重複しない場合のみ
                if (dupCheck.get(rec) == null) {
                    pw.println(String.join(",", rec));
                    dupCheck.put(rec, "");
                }
            });
        }
    }
    
}
