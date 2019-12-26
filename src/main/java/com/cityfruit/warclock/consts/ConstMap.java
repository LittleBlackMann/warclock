package com.cityfruit.warclock.consts;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TongTong
 * @date 2019/12/25
 */
public class ConstMap {
    public static Map<Integer, String> stateMap = new HashMap<Integer, String>();

    static {
        stateMap.put(1, "LIVE DANGER");
        stateMap.put(2, "PEATCH COMBAT");
        stateMap.put(3, "PROBLEM COLSED");
    }
}
