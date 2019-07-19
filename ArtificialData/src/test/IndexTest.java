/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ZZ17807
 */
public class IndexTest {
    public static void main(String[] args) {
        List<String> a = new ArrayList();
        a.add("A");a.add("B");a.add("C");
        
        System.out.println("A:"+a.indexOf("A"));
    }
}
