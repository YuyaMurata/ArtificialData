/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json.file;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import csv.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 *
 * @author 産総研・東工大OIL_2-2
 */
public class SyaryoObjectToJSON {

    public void write(String filename, Map syaryoMap) {
        try (JsonWriter writer = new JsonWriter(
                new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(filename))
                ))) {
            writer.setIndent("  ");

            Gson gson = new Gson();

            gson.toJson(syaryoMap, Map.class, writer);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void pretty(String file) {
        try (PrintWriter pw = CSVFileReadWrite.writer( file.replace("test", "ptest"));
                BufferedReader br = CSVFileReadWrite.reader(new File(file))) {
            String line;
            StringBuilder sb = null;
            Boolean flg = false;
            while ((line = br.readLine()) != null) {
                if (line.contains("[")) {
                    flg = true;
                    sb = new StringBuilder();
                } else if (line.contains("]")) {
                    line = "        "+sb.toString() + line.replace(" ", "");
                    sb = null;
                    flg = false;
                }
                
                if (flg) {
                    sb.append(line.replace(" ", ""));
                } else {
                    pw.println(line);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
