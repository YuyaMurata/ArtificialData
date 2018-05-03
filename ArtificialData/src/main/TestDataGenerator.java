/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import id.DataGenerator;
import table.CustomerTable;
import table.GPSTable;
import table.InfoTable;
import table.OrderTable;
import table.PartsTable;
import table.SMRTable;
import table.SyaryoTable;
import table.WorkTable;

/**
 *
 * @author 産総研・東工大OIL_2-2
 */
public class TestDataGenerator {

	private static String BIS_LAYOUTPATH = "resource\\layout\\business\\";
	private static String IOT_LAYOUTPATH = "resource\\layout\\iot\\";

	public static void main(String[] args) {
		DataGenerator dataGen = new DataGenerator();
		InfoTable table = new InfoTable();

		//Test c=100000 s=1000000 o=10000000 w=20000000 p=30000000 sensor=2500000000
		new CustomerTable(1000).createCustTable(dataGen, "resource\\個人情報.csv", table.getLayout(BIS_LAYOUTPATH + "Layout_customer.csv"));
		new SyaryoTable(10000).createSyaryoTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_syaryo.csv"));
		new OrderTable(100000).createOrderTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_order.csv"), table.syaryoCUST());
		new WorkTable(200000).createWorkTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_work.csv"));
		new PartsTable(300000).createPartsTable(dataGen, table.getLayout(BIS_LAYOUTPATH + "Layout_parts.csv"));
		new GPSTable(200000).createGPSTable(dataGen, table.getLayout(IOT_LAYOUTPATH + "Layout_GPS.csv"), table.getSyaryo());
		new SMRTable(200000).createSMRTable(dataGen, table.getLayout(IOT_LAYOUTPATH + "Layout_SMR.csv"));
	}
}
