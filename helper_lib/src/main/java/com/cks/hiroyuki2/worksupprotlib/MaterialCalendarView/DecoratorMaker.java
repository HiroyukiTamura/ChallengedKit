/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib.MaterialCalendarView;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cks.hiroyuki2.worksupprotlib.Entity.CalendarEvent;
import com.cks.hiroyuki2.worksupprotlib.Entity.CalendarOneEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.cks.hiroyuki2.worksupprotlib.Util.delimiterOfNum;

/**
 * ここで、なぜカレンダーのデータをもとに{@link MultiDotDecorator #shouldDecorate()}で分岐しないかというと、shouldDecorate()はdecorate()を非同期で発火するからです。
 * @see MultiDotDecorator
 */

public class DecoratorMaker {

    private static final String TAG = "MANUAL_TAG: " + DecoratorMaker.class.getSimpleName();
    private CalendarEvent event;
    private HashMap<String, TreeSet<Integer>> patternMap = new HashMap<>();//keyは、("4,6,2")などといった文字列です。数字はColorIdの番目を表しています。値は、日付の集まりです。日付は要素間で重複してはいけないことに注意してください。

    public DecoratorMaker(@NonNull CalendarEvent event){
        this.event = event;
    }

    public HashMap<String, TreeSet<Integer>> makeBox(){
        Log.d(TAG, "makeBox: fire");
        //まず、keyを日付、値を "4,6,2" などとするmapを作成する
        TreeMap<Integer, String> tm = makeTreeFromEventCalendar();

        for (Map.Entry<Integer, String> entry : tm.entrySet()) {
            TreeSet set = patternMap.get(entry.getValue());
            if (set == null){
                set = new TreeSet<>();
                set.add(entry.getKey());
                patternMap.put(entry.getValue(), set);
            } else {
                set.add(entry.getKey());
            }
        }
        return patternMap;
    }

//    private TreeMap<Integer, String> makeTree(){
//        Log.d(TAG, "makeTree: fire");
//        TreeMap<Integer, String> tm = new TreeMap<>();
//        for (String key : hashMap.keySet()) {
//            String[] strings = hashMap.get(key).split(delimiter);//文字列を区切るのは通常のデリミタであることに注意してください
//            StringBuilder sb = new StringBuilder();
//            for (String string : strings) {
//                int index = Character.getNumericValue(string.charAt(string.length()-1));
//                sb.append(index);
//                sb.append(delimiterOfNum);
//            }
//            sb.delete(sb.lastIndexOf(delimiterOfNum), sb.length());//最後のデリミタを削除する
//            tm.put(Integer.parseInt(key), sb.toString());
//        }
//        return tm;
//    }

    private TreeMap<Integer, String> makeTreeFromEventCalendar(){
        Log.d(TAG, "makeTree: fire");
        TreeMap<Integer, String> tm = new TreeMap<>();
        TreeMap<Integer, List<CalendarOneEvent>> monthEve = event.getMonthEvent();
        for (int date : monthEve.keySet()) {
            List<CalendarOneEvent> oneEveList = monthEve.get(date);
            StringBuilder sb = new StringBuilder();
            for (CalendarOneEvent oneEve : oneEveList) {
                int colorNum = oneEve.getColorNum();
                sb.append(colorNum);
                sb.append(delimiterOfNum);
            }
            sb.delete(sb.lastIndexOf(delimiterOfNum), sb.length());//最後のデリミタを削除する
            tm.put(date, sb.toString());
        }
        return tm;
    }
}
