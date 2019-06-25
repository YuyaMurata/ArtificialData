/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id;

import ec.util.MersenneTwisterFast;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * KMデータ用ID生成器
 *
 * @author kaeru
 */
public class ID {

    private static final int ALPHABET_SIZE = 'Z' - 'A';
    private static int NUM_COMPANY = 9;
    public static int NUM_KISY = 10; //TEST
    public static int NUM_SYARYO = 1000000;
    public static int NUM_CUSTOMER = 100000;
    private static DecimalFormat custmerDF = new DecimalFormat("00000");
    private static DecimalFormat syaryoDF = new DecimalFormat("000000");
    private static DecimalFormat yubinDF = new DecimalFormat("0000000");
    private static DecimalFormat telDF = new DecimalFormat("00000000000");
    private List<String> kisyList = new ArrayList();
    private List<String> companyList = new ArrayList();
    private List<String> prefList = new ArrayList();
    private List<String> typeList = new ArrayList();
    private List<String> syList = new ArrayList();
    private static MersenneTwisterFast rand = new MersenneTwisterFast();

    public static String A(int n) {
        return RandomStringUtils.randomAlphabetic(n).toUpperCase();
    }
}
