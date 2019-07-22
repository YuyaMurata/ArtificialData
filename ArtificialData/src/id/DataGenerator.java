/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id;

import csv.CSVFileReadWrite;
import csv.TestMasterCSV;
import ec.util.MersenneTwisterFast;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * テーブルのフィールド値を設定するクラス
 *
 * @author murata
 */
public class DataGenerator {

    private static TestMasterCSV TEST = TestMasterCSV.getInstance();
    private static final int ALPHABET_SIZE = 'Z' - 'A';
    private static int NUM_COMPANY = 9;
    public static int NUM_KISY = 10; //TEST
    public static int NUM_SYARYO = 1000000;
    public static int NUM_CUSTOMER = 100000;
    private static DecimalFormat custmerDF = new DecimalFormat("00000");
    private static DecimalFormat syaryoDF = new DecimalFormat("000000");
    private static DecimalFormat yubinDF = new DecimalFormat("0000000");
    private static DecimalFormat telDF = new DecimalFormat("00000000000");
    private List<String> kisyList = new ArrayList();
    private List<String> companyList = new ArrayList();
    private List<String> prefList = new ArrayList();
    private List<String> typeList = new ArrayList();
    private List<String> syList = new ArrayList();
    private static MersenneTwisterFast rand = new MersenneTwisterFast();

    public DataGenerator() {
        initialize();
    }

    private void initialize() {
        //会社リスト
        companyList.add("ABC");
        companyList.add("DEF");
        companyList.add("GHI");
        companyList.add("JKL");
        companyList.add("MNO");
        companyList.add("PQR");
        companyList.add("STU");
        companyList.add("VWX");
        companyList.add("YZ.");
        NUM_COMPANY = companyList.size();
        //System.out.print("会社リスト\n  ");
        //System.out.println(companyList);

        //型リスト
        typeList.add("2");
        typeList.add("3");
        typeList.add("5");
        typeList.add("6");
        typeList.add("7");
        typeList.add("8");
        typeList.add("10");

        //小変形
        syList.add("");
        syList.add("LC");
        syList.add("C2");
        syList.add("X");
        syList.add("KE");

        //都道府県
        try (BufferedReader br = CSVFileReadWrite.reader("resource\\都道府県.csv")) {
            prefList = br.lines().collect(Collectors.toList());
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    public void initKisy() {
        char alphabet1 = 'A';
        DecimalFormat df = new DecimalFormat("00");

        //機種リスト
        for (int i = 0; i <= ALPHABET_SIZE; i++) {
            String a1 = String.valueOf(alphabet1++);
            char alphabet2 = 'A';
            for (int j = 0; j <= ALPHABET_SIZE; j++) {
                String kisy = a1 + String.valueOf(alphabet2++) + df.format(i % 11) + (j % 7);
                kisyList.add(kisy);
            }
        }
        Collections.shuffle(kisyList);
        kisyList = kisyList.subList(0, NUM_KISY);

        //欠損値
        kisyList.add("XXXXX");
        kisyList.add(" ");
        kisyList.add("--");

        System.out.print("機種リスト(" + kisyList.size() + ")\n  ");
        System.out.println(kisyList);
    }

    public static Integer genLogic(int limit) {
        return rand.nextInt(limit);
    }

    private static LocalDate randomDate() {
        int minDay = (int) LocalDate.of(1990, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.of(2017, 1, 1).toEpochDay();
        long randomDay = minDay + genLogic(maxDay - minDay);

        LocalDate randomDate = LocalDate.ofEpochDay(randomDay);

        return randomDate;
    }

    public static List<LocalDate> randomDates(int term) {
        int day = (int) randomDate().toEpochDay();
        List dates = new ArrayList();
        for (int i = 0; i < term; i++) {
            dates.add(LocalDate.ofEpochDay(day + i));
        }

        return dates;
    }

    public String getData(String name, String type, int length) {
        //フラグ
        if (length == 1) {
            return String.valueOf(genLogic(2));
        }

        //フィールド名に依存したデータの生成
        switch (name) {
            case "会社コード":
                return companyList.get(genLogic(NUM_COMPANY));
            case "機種":
                return kisyList.get(genLogic(kisyList.size()));
            case "型式":
                return typeList.get(genLogic(typeList.size() - 1));
            case "小変形":
                return syList.get(genLogic(syList.size() - 1));
            case "機番":
                return syaryoDF.format(genLogic(NUM_SYARYO / kisyList.size()));
            case "作番":
                return randomString(1) + yubinDF.format(genLogic((int) Math.pow(10, length - 1)));
        }

        if (name.contains("顧客コード")) {
            return "C00" + custmerDF.format(genLogic(NUM_CUSTOMER));
        }

        //データ名のフォーマットに依存した生成
        if (name.contains("コード") || name.contains("番号")) {
            if (name.contains("郵便番号")) {
                String yubin = yubinDF.format(genLogic((int) Math.pow(10, length - 1)));
                return yubin.substring(0, 3) + "-" + yubin.substring(3, 7);
            } else if (name.contains("電話番号") || name.contains("ＦＡＸ番号")) {
                String tel = telDF.format(genLogic((int) Math.pow(10, length - 1)));
                return tel.substring(0, 3) + "-" + tel.substring(3, 7) + "-" + tel.substring(7, 11);
            } else {
                return String.valueOf(genLogic((int) Math.pow(10, length - 1)));
            }
        } else if (name.contains("日") || name.contains("月")) {
            String date = randomDate().toString().replace("-", "");
            if (length <= 2) {
                return date.substring(4, 6);
            } else {
                return date;
            }
        } else if (name.contains("都道府県")) {
            return prefList.get(genLogic(47));
        }

        //データタイプに依存した生成
        if (type.equals("GRAPHIC") || type.contains("CHAR")) {
            return randomString(length);
        } else if (type.equals("INTEGER") || type.equals("NUMBER") || type.equals("DECIMAL")) {
            return String.valueOf(genLogic((int) Math.pow(10, length)));
        } else if (type.equals("TIMESTAMP")) {
            return randomDate() + " 00:00:00";
        }

        System.out.println(name + ":" + type);
        System.exit(0);
        return null;
    }

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(genLogic(AB.length())));
        }
        return sb.toString();
    }

    public String getCompany(int index) {
        return companyList.get(Math.abs(index) % companyList.size());
    }

    public String getType(int kiban) {
        return typeList.get(Math.abs(kiban) % typeList.size());
    }

    public String getSyhk(int kiban) {
        return syList.get(Math.abs(kiban) % syList.size());
    }

    private Map<String, Integer> map = new HashMap();

    public String getKiban(String kisy) {
        if (map.get(kisy) == null) {
            map.put(kisy, 0);
        }

        Integer kiban = map.get(kisy) + 1;
        map.put(kisy, kiban);

        return syaryoDF.format(kiban);
    }

    public static void main(String[] args) {
        DataGenerator idgen = new DataGenerator();

        /*
        for (int i = 0; i < 10000; i++) {
            System.out.println(idgen.genLogic(100));
            System.out.println(idgen.randomDate());
            System.out.println(idgen.randomString(10));  
        }*/
    }
}
