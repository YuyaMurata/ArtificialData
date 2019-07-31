/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import csv.TestMasterCSV;
import gen.CreateRecode;
import gen.CreateTestMaster;
import id.DataGenerator;
import java.io.File;
import table.CustomerTable;
import table.GPSTable;
import table.InfoTable;
import table.OrderTable;
import table.PartsTable;
import table.SMRTable;
import table.SyaryoTable;
import table.WorkTable;
import valid.MetaDataSet;

/**
 * テストデータ生成メインクラス
 *
 * @author 産総研・東工大OIL_2-2
 */
public class TestDataGenerator {
    //Bussiness Dataのテーブルパス
    private static String BIS_LAYOUTPATH = "resource\\layout\\business\\";
    //IoT Dataのテーブルパス
    private static String IOT_LAYOUTPATH = "resource\\layout\\iot\\";
    
    //メタデータのパス
    private static String META_PATH = "metaset\\anonymous\\";
    //出力パス
    private static String OUTPATH = "G:\\test\\data\\";

    public static void main(String[] args) {
        
        //旧データ生成
        //システムテスト用 customer=100000 syaryo(N,機種)=(1000000, 676(MAX)) order=10000000 work=20000000 parts=30000000 sensor(GPS,SMR)=250000000
        //性能  testdata file=98.7GB  generate time=2h10m  json time= ?s  memory=? json file=?
        //generate(100000, 1000000, 676, 10000000, 20000000, 30000000, 250000000, 250000000);
        //小規模テスト(確認済み) c=1000 s=10000 機種=10 o=100000 w=200000 p=300000 sensor=250000
        //性能  testdata file=553MB  generate time=42s  json time= 92s  memory=3.5GB json file=436MB
        //generate(1000, 10000, 10, 100000, 200000, 300000, 250000, 250000);
        //確認用　c=10 s=1000 機種=10 o=10000 w=10000 p=10000 sensor=10000
        //性能  testdata file=38MB  generate time=4s  json time= 9s  memory=600MB json file=34MB
        //generate(10, 1000, 10, 10000, 10000, 10000, 10000, 10000);
        
        //共同研究用データ生成
        //true = オリジナルデータサイズ
        //false = 小規模データサイズ
        metagen(true);
    }

    //共同研究用テストデータ生成
    private static void metagen(Boolean flg) {
        int n = 1000;
        
        //出力フォルダの設定
        File file = new File(OUTPATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        
        //マスターデータの生成
        if(flg)CreateTestMaster.generate(1_000_000);
        else CreateTestMaster.generate(n/100);
        TestMasterCSV.getInstance().settings();
        
        //Bussiness
        MetaDataSet.setFiles(META_PATH);
        MetaDataSet.files.values().stream().forEach(f -> {
            CreateRecode rec = new CreateRecode(f);
            if(flg)
                rec.create(rec.origin, OUTPATH);
            else
                rec.create(n, OUTPATH);
        });
        
        //IoT
        InfoTable table = new InfoTable(OUTPATH);
        DataGenerator dataGen = new DataGenerator();
        if(flg){
            new GPSTable(25_000_000).createGPSTable(dataGen, table.getLayout(IOT_LAYOUTPATH + "Layout_GPS.csv"), table.getSyaryo());
            new SMRTable(25_000_000).createSMRTable(dataGen, table.getLayout(IOT_LAYOUTPATH + "Layout_SMR.csv"));
        }else{
            new GPSTable(n).createGPSTable(dataGen, table.getLayout(IOT_LAYOUTPATH + "Layout_GPS.csv"), table.getSyaryo());
            new SMRTable(n).createSMRTable(dataGen, table.getLayout(IOT_LAYOUTPATH + "Layout_SMR.csv"));
        }
    }

    //旧テストデータ生成
    private static void generate(int customer, int syaryo, int syaryo_kisy, int order, int work, int parts, int gps, int smr) {
        DataGenerator dataGen = new DataGenerator();
        
        //出力フォルダの設定
        InfoTable table = new InfoTable(OUTPATH);
        File file = new File(OUTPATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        
        //テストデータ生成
        new CustomerTable(customer).createCustTable(dataGen, "resource\\個人情報.csv", table.getLayout(BIS_LAYOUTPATH + "Layout_customer.csv"));
        new SyaryoTable(syaryo, syaryo_kisy).createSyaryoTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_syaryo.csv"));
        new OrderTable(order).createOrderTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_order.csv"), table.syaryoCUST());
        new WorkTable(work).createWorkTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_work.csv"), table.getSBN());
        new PartsTable(parts).createPartsTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_parts.csv"), table.getSBN());
        new GPSTable(gps).createGPSTable(dataGen, table.getLayout(IOT_LAYOUTPATH + "Layout_GPS.csv"), table.getSyaryo());
        new SMRTable(smr).createSMRTable(dataGen, table.getLayout(IOT_LAYOUTPATH + "Layout_SMR.csv"));
    }
}
