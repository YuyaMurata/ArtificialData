/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csv;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ZZ17390
 */
public class CSVFileReadWrite {

    public static PrintWriter writer(String filename) {
        try {
            return new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(0);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static BufferedReader reader(String filename) {
        try {
            return new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(0);
        }

        return null;
    }
    
    public static BufferedReader readerSJIS(String filename) {
        try {
            return new BufferedReader(new InputStreamReader(new FileInputStream(filename), "SJIS"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(0);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
