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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import valid.MetaDataDefine;

/**
 *
 * @author zz17807
 */
public class CreateRecode {
    private String name;
    private MetaDataDefine def;
    public int origin;
    private static MersenneTwisterFast rand = new MersenneTwisterFast();
    private static TestMasterCSV TEST;
    
    public CreateRecode(File meta) {
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
                .collect(Collectors.toList());

        return String.join(",", rec);
    }

    public void create(int n, String outpath) {
        String f = def.name.toUpperCase().replace("JSON", "csv");
        long start = System.currentTimeMillis();
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(outpath+f)) {
            //header
            pw.println(def.getData().keySet().stream()
                                .map(k -> k.split("\\.")[1].toUpperCase())
                                .map(k -> TEST.headers.get(k))  // ヘッダーの日本語変換
                                .collect(Collectors.joining(",")));
            
            //data
            IntStream.range(0, n).boxed().map(i -> get()).forEach(pw::println);
        }
        long stop = System.currentTimeMillis();
        System.out.println(f+" 生成 time="+(stop-start)+"[ms]");
    }
}
