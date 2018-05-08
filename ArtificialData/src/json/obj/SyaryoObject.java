/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json.obj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 車両データを集約するためのオブジェクト
 *
 * @author 産総研・東工大OIL_2-2
 */
public class SyaryoObject {

	public String name;
	public Map<String, TreeMap<String, List>> map;

	//車両名変換
	private static transient Map<String, String> transNameMap = new HashMap();

	public SyaryoObject(String name) {
		this.name = name;
		this.map = new HashMap();
		transNameMap.put(name.split("-")[0] + "-" + name.split("-")[name.split("-").length - 1], name);
	}

	public void add(String table, String id, List list) {
		if (map.get(table) == null) {
			map.put(table, new TreeMap<>());
		}

		//ID重複チェック
		if (map.get(table).get(id) != null) {
			if (id.contains("#")) {
				id = id.split("#")[0] + "#" + (Integer.valueOf(id.split("#")[1]) + 1);
			} else {
				id = id + "#1";
			}
		}

		//Key-Valueで格納
		map.get(table).put(id, list);
	}

	public static String transName(String name) {
		//機種-機番 →　機種・型・小変形・機番
		return transNameMap.get(name);
	}
}
