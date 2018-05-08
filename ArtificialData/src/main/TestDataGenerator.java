/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

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

/**
 * テストデータ生成メインクラス
 * @author 産総研・東工大OIL_2-2
 */
public class TestDataGenerator {
	//Bussiness Dataのテーブルパス
	private static String BIS_LAYOUTPATH = "resource\\layout\\business\\";
	//IoT Dataのテーブルパス
	private static String IOT_LAYOUTPATH = "resource\\layout\\iot\\";
	//出力パス
	private static String OUTPATH = "test\\data\\";

	public static void main(String[] args) {
		
		//システムテスト用 customer=100000 syaryo(N,機種)=(1000000, 1000) order=10000000 work=20000000 parts=30000000 sensor(GPS,SMR)=250000000
		//性能  testdata file=553MB  generate time=42s  json time= 9s  memory=600MB json file=34MB
		//generate(100000, 1000000, 1000, 10000000, 20000000, 30000000, 250000000, 250000000);
		
		//小規模テスト(確認済み) c=1000 s=10000 機種=10 o=100000 w=200000 p=300000 sensor=250000
		//性能  testdata file=553MB  generate time=42s  json time= 9s  memory=600MB json file=34MB
		generate(1000, 10000, 10, 100000, 200000, 300000, 250000, 250000);
		
		//確認用　c=10 s=1000 機種=10 o=10000 w=10000 p=10000 sensor=10000
		//性能  testdata file=38MB  generate time=4s  json time= 9s  memory=600MB json file=34MB
		//generate(10, 1000, 10, 10000, 10000, 10000, 10000, 10000);
	}
	
	private static void generate(int customer, int syaryo, int syaryo_kisy, int order, int work, int parts, int gps, int smr){
		DataGenerator dataGen = new DataGenerator();
		
		//出力フォルダの設定
		InfoTable table = new InfoTable(OUTPATH);
		File file = new File(OUTPATH);
		if(!file.exists())
			file.mkdirs();
		
		new CustomerTable(customer).createCustTable(dataGen, "resource\\個人情報.csv", table.getLayout(BIS_LAYOUTPATH + "Layout_customer.csv"));
		new SyaryoTable(syaryo, syaryo_kisy).createSyaryoTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_syaryo.csv"));
		new OrderTable(order).createOrderTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_order.csv"), table.syaryoCUST());
		new WorkTable(work).createWorkTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_work.csv"), table.getSBN());
		new PartsTable(parts).createPartsTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_parts.csv"), table.getSBN());
		new GPSTable(gps).createGPSTable(dataGen, table.getLayout(IOT_LAYOUTPATH + "Layout_GPS.csv"), table.getSyaryo());
		new SMRTable(smr).createSMRTable(dataGen, table.getLayout(IOT_LAYOUTPATH + "Layout_SMR.csv"));
	}
}
