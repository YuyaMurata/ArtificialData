/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table;

import csv.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * テーブル情報を管理するクラス
 * @author murata
 */
public class InfoTable {

	//テーブル出力パス
	public static String filepath = "";
	public InfoTable(String path) {
		this.filepath = path;
	}

	//レイアウトファイルの読み込み
	public List getLayout(String filename) {
		List layout = new ArrayList();
		try (BufferedReader br = CSVFileReadWrite.reader(filename)) {
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				layout.add(line);
			}
		} catch (IOException ex) {
		}
		return layout;
	}

	//車両リストの読み込み(少量テーブル生成後)
	public List getSyaryo() {
		List list = new ArrayList();
		try (BufferedReader br = CSVFileReadWrite.reader(filepath+SyaryoTable.syaryoFile)) {
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] f = line.split(",");
				String key = f[1] + "_" + f[2] + "_" + f[3] + "_" + f[4];
				list.add(key);
			}
		} catch (IOException ex) {
		}

		return list;
	}

	//受注リストの取得(受注テーブル生成後)
	public List getSBN() {
		List list = new ArrayList();
		try (BufferedReader br = CSVFileReadWrite.reader(filepath+OrderTable.orderFile)) {
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] f = line.split(",");
				String key = f[1];
				list.add(key);
			}
		} catch (IOException ex) {
		}

		return list;
	}

	//車両と顧客の取得(車両テーブル生成後)
	public Map syaryoCUST() {
		Map index = new HashMap();
		try (BufferedReader br = CSVFileReadWrite.reader(filepath+SyaryoTable.syaryoFile)) {
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] f = line.split(",");
				String key = f[0] + f[1] + f[2] + f[3] + f[4];
				index.put(key, f[13]);
			}
		} catch (IOException ex) {
		}

		return index;
	}
}
