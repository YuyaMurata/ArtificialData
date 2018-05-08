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
		DataGenerator dataGen = new DataGenerator();
		
		//出力フォルダの設定
		InfoTable table = new InfoTable(OUTPATH);
		File file = new File(OUTPATH);
		if(!file.exists())
			file.mkdirs();

		//システムテスト用 c=100000 s=1000000 機種=1000 o=10000000 w=20000000 p=30000000 sensor=2500000000
		//小規模テスト(確認済み) c=1000 s=10000 機種=10 o=100000 w=200000 p=300000 sensor=200000
		//確認用　c=10 s=1000 機種=10 o=10000 w=10000 p=10000 sensor=10000
		new CustomerTable(1000).createCustTable(dataGen, "resource\\個人情報.csv", table.getLayout(BIS_LAYOUTPATH + "Layout_customer.csv"));
		new SyaryoTable(10000, 10).createSyaryoTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_syaryo.csv"));
		new OrderTable(100000).createOrderTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_order.csv"), table.syaryoCUST());
		new WorkTable(200000).createWorkTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_work.csv"), table.getSBN());
		new PartsTable(300000).createPartsTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_parts.csv"), table.getSBN());
		new GPSTable(200000).createGPSTable(dataGen, table.getLayout(IOT_LAYOUTPATH + "Layout_GPS.csv"), table.getSyaryo());
		new SMRTable(200000).createSMRTable(dataGen, table.getLayout(IOT_LAYOUTPATH + "Layout_SMR.csv"));
	}
}
