/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * CSVファイルの読み書き用
 * @author ZZ17390
 */
public class CSVFileReadWrite {
    public static PrintWriter writer(String filename){
        try {
            return  new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF8"));
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        
        return null;
    }
    
    public static BufferedReader reader(File file){
        try {
            return new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
        }
        
        return null;
    }
}
