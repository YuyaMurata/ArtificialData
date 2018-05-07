/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json.create;

import csv.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import json.file.SyaryoObjectToJSON;
import json.obj.SyaryoObject;

/**
 *
 * @author 産総研・東工大OIL_2-2
 */
public class JSONCreator {

	private String filename = "test_syaryo.json";
	private String errpath;
	
	public void create(String filepath, String outpath, String errpath) {
		this.errpath = errpath;

		//Initialize Map
		Map<String, SyaryoObject> syaryoMap = new HashMap();

		syaryo(new File(filepath + "TEST_SYARYO.csv"), syaryoMap);
		customer(new File(filepath + "TEST_CUSTOMER.csv"), syaryoMap);
		order(new File(filepath + "TEST_ORDER.csv"), syaryoMap);
		work(new File(filepath + "TEST_WORK.csv"), syaryoMap);
		parts(new File(filepath + "TEST_PARTS.csv"), syaryoMap);
		gps(new File(filepath + "TEST_GPS.csv"), syaryoMap);
		smr(new File(filepath + "TEST_SMR.csv"), syaryoMap);

		SyaryoObjectToJSON json = new SyaryoObjectToJSON();
		json.write(outpath + filename, syaryoMap);
		json.pretty(outpath + filename);
	}

	//Syaryo Table to JSON
	private void syaryo(File file, Map<String, SyaryoObject> map) {
		List<String> stopwords = new ArrayList();
		stopwords.add("XXXXX");
		stopwords.add(" ");
		stopwords.add("--");

		try (PrintWriter pw = CSVFileReadWrite.writer(errpath+"error_" + file.getName());
			BufferedReader br = CSVFileReadWrite.reader(file)) {
			String line = br.readLine();
			List<String> header = Arrays.asList(line.split(","));
			List<Integer> keys = new ArrayList();
			keys.add(header.indexOf("機種"));
			keys.add(header.indexOf("型式"));
			keys.add(header.indexOf("小変形"));
			keys.add(header.indexOf("機番"));

			int subKey = header.indexOf("納入年月日");
			List<Integer> custkeys = new ArrayList();
			custkeys.add(header.indexOf("会社コード"));
			custkeys.add(header.indexOf("保有顧客コード"));

			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String name = keys.stream()
					.map(key -> data[key])
					.collect(Collectors.joining("-"));

				if (stopwords.contains(name.split("-")[0])) {
					pw.println(name + ":" + line);
					continue;
				}

				//Customert
				String cust = custkeys.stream()
					.map(key -> data[key])
					.collect(Collectors.joining("-"));
				if (custMap.get(cust) == null) {
					custMap.put(cust, new ArrayList());
				}
				custMap.get(cust).add(name);

				//Syaryo
				SyaryoObject syaryo;
				if ((syaryo = map.get(name)) == null) {
					syaryo = new SyaryoObject(name);
				}

				syaryo.add("車両", data[subKey], Arrays.asList(line.split(",")));

				//Register
				map.put(name, syaryo);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		System.out.println("SyaryoTable Finish!");
	}

	//Customer Table to JSON
	Map<String, List<String>> custMap = new HashMap();

	private void customer(File file, Map<String, SyaryoObject> map) {
		try (PrintWriter pw = CSVFileReadWrite.writer(errpath+"error_" + file.getName());
			BufferedReader br = CSVFileReadWrite.reader(file)) {
			String line = br.readLine();
			List<String> header = Arrays.asList(line.split(","));
			List<Integer> keys = new ArrayList();
			keys.add(header.indexOf("会社コード"));
			keys.add(header.indexOf("顧客コード"));

			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String cust = keys.stream()
					.map(key -> data[key])
					.collect(Collectors.joining("-"));

				if (custMap.get(cust) == null) {
					pw.println(cust + ":" + line);
					continue;
				}

				for (String name : custMap.get(cust)) {
					SyaryoObject syaryo = map.get(name);
					syaryo.add("顧客", cust, Arrays.asList(line.split(",")));

					//Register
					map.put(name, syaryo);
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("CustomerTable Finish!");
	}

	//Order Table to JSON
	Map<String, String> orderMap = new HashMap();

	private void order(File file, Map<String, SyaryoObject> map) {
		try (PrintWriter pw = CSVFileReadWrite.writer(errpath+"error_" + file.getName());
			BufferedReader br = CSVFileReadWrite.reader(file)) {
			String line = br.readLine();
			List<String> header = Arrays.asList(line.split(","));
			List<Integer> keys = new ArrayList();
			keys.add(header.indexOf("機種"));
			keys.add(header.indexOf("機番"));

			List<Integer> orderKeys = new ArrayList();
			orderKeys.add(header.indexOf("会社コード"));
			orderKeys.add(header.indexOf("作番"));

			int subKey = header.indexOf("受注日");

			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String name = keys.stream()
					.map(key -> data[key])
					.collect(Collectors.joining("-"));
				String sname = SyaryoObject.transName(name);

				String sbnID = orderKeys.stream()
					.map(key -> data[key])
					.collect(Collectors.joining("-"));

				if (map.get(sname) == null) {
					pw.println(name + ":" + sbnID + ":" + line);
					continue;
				}

				SyaryoObject syaryo = map.get(sname);
				syaryo.add("受注", data[subKey], Arrays.asList(line.split(",")));

				//Register
				orderMap.put(sbnID, sname);
				map.put(sname, syaryo);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		System.out.println("OrderTable Finish!");
	}

	private void work(File file, Map<String, SyaryoObject> map) {
		try (PrintWriter pw = CSVFileReadWrite.writer(errpath+"error_" + file.getName());
			BufferedReader br = CSVFileReadWrite.reader(file)) {
			String line = br.readLine();
			List<String> header = Arrays.asList(line.split(","));
			List<Integer> keys = new ArrayList();
			keys.add(header.indexOf("会社コード"));
			keys.add(header.indexOf("作番"));

			int subKey = header.indexOf("作番");

			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String sbnID = keys.stream()
					.map(key -> data[key])
					.collect(Collectors.joining("-"));

				String sname = orderMap.get(sbnID);
				if (orderMap.get(sbnID) == null) {
					pw.println("Null :" + sbnID + ":" + line);
					continue;
				}

				SyaryoObject syaryo = map.get(sname);
				syaryo.add("作業", data[subKey], Arrays.asList(line.split(",")));

				//Register
				orderMap.put(sbnID, sname);
				map.put(sname, syaryo);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("WorkTable Finish!");
	}

	private void parts(File file, Map<String, SyaryoObject> map) {
		try (PrintWriter pw = CSVFileReadWrite.writer(errpath+"error_" + file.getName());
			BufferedReader br = CSVFileReadWrite.reader(file)) {
			String line = br.readLine();
			List<String> header = Arrays.asList(line.split(","));
			List<Integer> keys = new ArrayList();
			keys.add(header.indexOf("会社コード"));
			keys.add(header.indexOf("作番"));

			int subKey = header.indexOf("作番");

			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String sbnID = keys.stream()
					.map(key -> data[key])
					.collect(Collectors.joining("-"));

				String sname = orderMap.get(sbnID);
				if (orderMap.get(sbnID) == null) {
					pw.println("Null :" + sbnID + ":" + line);
					continue;
				}

				SyaryoObject syaryo = map.get(sname);
				syaryo.add("部品", data[subKey], Arrays.asList(line.split(",")));

				//Register
				orderMap.put(sbnID, sname);
				map.put(sname, syaryo);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("PartsTable Finish!");
	}

	private void gps(File file, Map<String, SyaryoObject> map) {
		try (PrintWriter pw = CSVFileReadWrite.writer(errpath+"error_" + file.getName());
			BufferedReader br = CSVFileReadWrite.reader(file)) {
			String line = br.readLine();
			List<String> header = Arrays.asList(line.split(","));
			List<Integer> keys = new ArrayList();
			keys.add(header.indexOf("機種"));
			keys.add(header.indexOf("型式"));
			keys.add(header.indexOf("小変形"));
			keys.add(header.indexOf("機番"));

			int subKey = header.indexOf("GPS時間");

			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String name = keys.stream()
					.map(key -> data[key])
					.collect(Collectors.joining("-"));

				//Syaryo
				SyaryoObject syaryo;
				if ((syaryo = map.get(name)) == null) {
					pw.println(name + ":" + line);
					continue;
				}

				syaryo.add("GPS", data[subKey], Arrays.asList(line.split(",")));

				//Register
				map.put(name, syaryo);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("GPSTable Finish!");
	}

	private void smr(File file, Map<String, SyaryoObject> map) {
		try (PrintWriter pw = CSVFileReadWrite.writer(errpath+"error_" + file.getName());
			BufferedReader br = CSVFileReadWrite.reader(file)) {
			String line = br.readLine();
			List<String> header = Arrays.asList(line.split(","));
			List<Integer> keys = new ArrayList();
			keys.add(header.indexOf("機種"));
			keys.add(header.indexOf("型式"));
			keys.add(header.indexOf("小変形"));
			keys.add(header.indexOf("機番"));

			int subKey = header.indexOf("SMR記録時間");

			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String name = keys.stream()
					.map(key -> data[key])
					.collect(Collectors.joining("-"));

				//Syaryo
				SyaryoObject syaryo;
				if ((syaryo = map.get(name)) == null) {
					pw.println(name + ":" + line);
					continue;
				}
				syaryo.add("SMR", data[subKey], Arrays.asList(line.split(",")));

				//Register
				map.put(name, syaryo);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("SMRTable Finish!");
	}
}
