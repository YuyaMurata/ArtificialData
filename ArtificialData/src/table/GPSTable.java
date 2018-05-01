/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table;

import csv.CSVFileReadWrite;
import id.DataGenerator;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * @author kaeru_yuya
 */
public class GPSTable {
    private String gpsFile = "TEST_GPS.csv";
    private Integer numGPS = 250000000;
    private static String path = "resource\\root\\";

    public GPSTable() {
    }
    
    public GPSTable(Integer n) {
        this.numGPS = n;
    }
    
    public void createGPSTable(DataGenerator dataGen, List<String> layout, List syaryos) {
        int i = 0;

        try (PrintWriter out = CSVFileReadWrite.writer(gpsFile)) {
            //Header Name
            out.println(layout.stream().map(l -> l.split(",")[1]).collect(Collectors.joining(",")));

            //Data Generate
            Boolean flg = true;
            while (flg) {
                int index = DataGenerator.genLogic(numGPS);
                Map<String, String> rootMap = getRoot(index);
                String syaryo = (String) syaryos.get(index % syaryos.size());
                for (String date : rootMap.keySet()) {
                    List<String> csvLine = new ArrayList();
                    for (String field : layout) {
                        String[] l = field.split(",");
                        if (l[3].equals("DATE")) {
                            csvLine.add(date);
                        } else if (l[1].equals("経度")) {
                            csvLine.add(rootMap.get(date).split(",")[0]);
                        } else if (l[1].equals("緯度")) {
                            csvLine.add(rootMap.get(date).split(",")[1]);
                        } else if (l[1].equals("機種")) {
                            csvLine.add(syaryo.split("_")[0]);
                        } else if (l[1].equals("型式")) {
                            csvLine.add(syaryo.split("_")[1]);
                        } else if (l[1].equals("小変形")) {
                            csvLine.add(syaryo.split("_")[2]);
                        } else if (l[1].equals("機番")) {
                            csvLine.add(syaryo.split("_")[3]);
                        } else {
                            csvLine.add(dataGen.getData(l[1], l[3], Integer.valueOf(l[4])));
                        }
                    }

                    out.println(String.join(",", csvLine));
                    i++;
                    
                    if(i%100000 == 0)
                        System.out.println(i+" GPSレコード 生成");
                    if (i > numGPS) {
                        System.out.println("GPS Table Generate!");
                        flg = false;
                        break;
                    }
                }
            }
        }
    }

    private File[] files = (new File(path)).listFiles();

    private Map<String, String> getRoot(int index) {
        Map rootMap = new TreeMap();
        index = index % files.length;
        File f = files[index];

        try (BufferedReader csv = CSVFileReadWrite.reader(f)) {
            List rt = root(csv);
            List dates = DataGenerator.randomDates(rt.size());

            for (int i = 0; i < rt.size(); i++) {
                rootMap.put(dates.get(i).toString(), rt.get(i));
            }
        } catch (IOException ex) {
        }

        return rootMap;
    }

    private static List root(BufferedReader csv) throws IOException {
        Boolean stflg = false;
        String line = "";
        List<String> rootList = new ArrayList();
        String name = "";
        while ((line = csv.readLine()) != null) {
            if (line.contains("<name>")) {
                name = line.substring(6, line.length() - 7);
            }
            if (!stflg) {
                if (line.contains("<coordinates>")) {
                    stflg = true;
                }
                continue;
            } else if (line.contains("</coordinates>")) {
                stflg = false;
                continue;
            }

            rootList.add(line + "," + name);

            //各ID一つずつ
            //break;
        }

        return rootList;
    }
}
