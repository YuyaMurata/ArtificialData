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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import valid.MetaDataDefine;
import valid.MetaDataSet;

/**
 * テストデータ生成ためのマスター作成 ※生成前に作成しないと動かない
 *
 * @author ZZ17807
 */
public class CreateTestMaster {

    //
    static Map<String, String> rule;
    static String path;
    static String outpath;
    
    public static String getFile(){
        return outpath + "TEST_MASTER.csv";
    }
    
    public static void generate(String rulefile, String metapath, String out, int n) {
        //パス設定
        rule = ListToCSV.toMap(rulefile, 2, 7);
        path = metapath;
        outpath = out;

        //集約データチェック
        Map<String, TreeMap<Double, String>> roullet = aggregate();
        
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

    static Map<List<String>, String> dupCheck = new HashMap<>();
    private static Boolean duplicateCheck(List<String> rec, List<String> header) {
        //重複排除項目
        List<Integer> duplist = Arrays.asList(new Integer[]{
            header.indexOf("kisy"),
            header.indexOf("typ"),
            header.indexOf("syhk"),
            header.indexOf("kiban"),
            header.indexOf("hy_kkykcd")
        });
        List<String> dupkey = duplist.stream().map(i -> rec.get(i)).collect(Collectors.toList());

        if (dupCheck.get(dupkey) == null) {
            dupCheck.put(dupkey, "1");
            return true;
        } else {
            return false;
        }
    }

    private static MersenneTwisterFast rand = new MersenneTwisterFast();

    public static void create(Map<String, TreeMap<Double, String>> d, int n) {

        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(outpath + "test_master.csv")) {
            //header
            List<String> header = new ArrayList<>();
            header.add("No.");
            header.addAll(d.keySet());
            pw.println(String.join(",", header));
            
            long i = 0L;
            while(i < n){
                //record
                List<String> rec = new ArrayList<>();
                rec.add(String.valueOf(i));
                rec.addAll(d.values().stream()
                        .map(r -> r.ceilingEntry(Math.abs(rand.nextDouble())).getValue())
                        .map(rv -> rv.equals("") ? " " : rv)
                        .collect(Collectors.toList()));

                //重複しない場合のみ
                if (duplicateCheck(rec, header)) {
                    pw.println(String.join(",", rec));
                    i++;
                }
            };
        }
    }

}
