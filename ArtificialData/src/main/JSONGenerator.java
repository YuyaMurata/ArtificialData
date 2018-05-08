/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import json.create.JSONCreator;

/**
 * JSON生成メインクラス
 * @author 産総研・東工大OIL_2-2
 */
public class JSONGenerator {
	//テストデータフォルダのパス
	private static String FILEPATH = "test\\data\\";
	//出力パス
	private static String OUTPATH = "test\\json\\";
	//エラーデータの出力パス
	private static String ERRPATH = "test\\error\\";

	public static void main(String[] args) {
		//出力フォルダの設定
		File file = new File(OUTPATH);
		if (!file.exists()) {
			file.mkdir();
		}

		//エラーフォルダの設定
		file = new File(ERRPATH);
		if (!file.exists()) {
			file.mkdir();
		}
		
		//JSON生成
		JSONCreator json = new JSONCreator();
		json.create(FILEPATH, OUTPATH, ERRPATH);
	}
}
