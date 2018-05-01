/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json.obj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 産総研・東工大OIL_2-2
 */
public class SyaryoObject {
    public String name;
    public Map<String, Map<String, List>> map;
    
    public SyaryoObject(String name) {
        this.name = name;
        this.map = new HashMap();
    }
    
    public void add(String table, String id, List list){
        if(map.get(table) == null)
            map.put(table, new HashMap<>());
        map.get(table).put(id, list);
    }
}
