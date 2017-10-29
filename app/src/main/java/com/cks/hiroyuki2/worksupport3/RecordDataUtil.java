/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import com.cks.hiroyuki2.worksupprotlib.Entity.RecordData;

import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * RecordDataまわりをやってくれるおじさん！
 */

public class RecordDataUtil {
    static private RecordDataUtil rdu = new RecordDataUtil();
    public Map<Integer, List<RecordData>> dataMap = new TreeMap<>();//引数はnullでありうる

    @Contract(pure = true)
    public static RecordDataUtil getInstance() {
        return rdu;
    }
}
