/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import json.create.JSONCreator;

/**
 *
 * @author 産総研・東工大OIL_2-2
 */
public class JSONGenerator {
    private static String FILEPATH = "test\\";
    public static void main(String[] args) {
		File file = new File("json\\");
		if(!file.exists())
			file.mkdir();
		
		file = new File("error\\");
		if(!file.exists())
			file.mkdir();
		
        JSONCreator json = new JSONCreator();
        json.create(FILEPATH);
    }
}
