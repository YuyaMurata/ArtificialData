/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package valid;

import ec.util.MersenneTwisterFast;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author kaeru
 */
public class Anonymous {
    private static MersenneTwisterFast rand = new MersenneTwisterFast();
    
    public static void main(String[] args) {
        System.out.println(C(100));
        System.out.println(N("+0000001234."));
    }
    
    //インデックス
    static Map<String, Integer> id = new HashMap();
    public static String ID(String key, String orig){
        if(orig.length() < 2)
            return orig;
        
        if(id.get(key) == null){
            id.put(key, 0);
        }else
            id.put(key, id.get(key)+1);
        
        return orig.substring(0)+key.substring(0)+id.get(key);
    }
    
    //ランダム文字列
    public static String C(int len) {
        return RandomStringUtils.randomAlphanumeric(len);
    }
    
    //ランダム数字
    public static String N(String orig) {
        if(orig.contains("+") || orig.contains("-")){
            String f = orig.replace("+", "").replace("-", "");
            f = "%+0"+(f.length()-1)+"d.";
            //f = "+"+f+";"+"-"+f;
            //DecimalFormat df = new DecimalFormat(f);
            //return df.format(rand.nextInt());
            return String.format(f, rand.nextInt());
        }
        
        if(orig.contains("."))
            return String.format("%.1f", rand.nextFloat());
        
        return String.valueOf(rand.nextInt());
    }
}
