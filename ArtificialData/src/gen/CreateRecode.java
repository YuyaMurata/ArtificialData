/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

import csv.CSVFileReadWrite;
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
public class CreateRecode {

    private String name;
    private MetaDataDefine def;
    private static MersenneTwisterFast rand = new MersenneTwisterFast();

    public CreateRecode(File meta) {
        name = meta.getName();
        def = new MetaDataDefine(meta);
    }

    public String get() {
        List<String> rec = def.getData().keySet().stream()
                .map(k -> def.getData(k, rand.nextInt(def.getRange(k))))
                .collect(Collectors.toList());

        return String.join(",", rec);
    }

    public void create(int n) {
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(def.name.replace("json", "csv"))) {
            //header
            pw.println(def.getData().keySet().stream().collect(Collectors.joining(",")));
            
            //data
            IntStream.range(0, n).boxed().map(i -> get()).forEach(pw::println);
        }
    }
}
