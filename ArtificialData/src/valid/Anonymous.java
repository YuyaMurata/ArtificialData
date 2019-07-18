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
import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 * @author kaeru
 */
public class Anonymous {
    private static MersenneTwisterFast rand = new MersenneTwisterFast();
    
    //匿名化処理の分岐
    public static String A(String anonym, String key, String orig){
        if(anonym == null)
            return null;
        
        if(anonym.contains("ID") || anonym.contains("担当名") || anonym.contains("機種・機番")){
            return ID(key, orig);
        }else if(anonym.contains("文字列")){
            return C(orig.length());
        }else if(anonym.contains("数値") || anonym.contains("金額")  || anonym.contains("SMR")){
            return N(orig);
        }else if(anonym.contains("その他")){
            return null;
        }else
            return orig;
    }
    
    //インデックス
    private static Map<String, Integer> id = new HashMap();
    public static String ID(String key, String orig){
        if(orig.length() < 2)
            return orig;
        
        String kid = key + orig;
        //文字が含まれる場合、文字つきID
        if(!NumberUtils.isNumber(orig))
            kid = orig.substring(0,1)+key.toUpperCase().substring(0,1);
            
        if(id.get(kid) == null)
            id.put(kid, 0);
        else
            id.put(kid, id.get(kid)+1);
        
        if(!NumberUtils.isNumber(orig))
            return kid+String.format("%03d", id.get(kid));
        else
            return String.format("%06d", id.get(kid));
    }
    
    //ランダム文字列
    public static String C(int len) {
        return RandomStringUtils.randomAlphanumeric(len);
    }
    
    //ランダム数字
    public static String N(String orig) {
        if(orig.contains("+") || orig.contains("-0")){
            String f = orig.replace("+", "").replace("-", "");
            f = "%+0"+(f.length()-1)+"d.";
            return String.format(f, rand.nextInt());
        }
        
        if(orig.contains("."))
            return String.format("%.1f", rand.nextFloat());
        
        return String.valueOf(rand.nextInt());
    }
}
