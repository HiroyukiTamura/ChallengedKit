/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib.Entity;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import static com.cks.hiroyuki2.worksupprotlib.Util.logStackTrace;

/**
 * 項目の記録を保持するクラス。このままFirebaseに書き込むことができる。
 * クラス・変数は全てpublicでなければいけない。
 * また、簡略化するため、dataは必ずHashMap型で登録すること。
 * keyを指定する必要がない場合は、keyに0を起点にする整数を与えること。
 * @see "https://firebase.google.com/docs/database/android/save-data?hl=ja"
 */

public class RecordData implements Serializable {

    public int year;
    public int mon;
    public int day;
    public int dataType;
    public String dataName;
    public HashMap<String, Object> data;

    public RecordData(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public RecordData(int year, int mon, int day, int dataType, @NonNull String dataName, @NonNull HashMap<String, Object> data){
        this.year = year;
        this.mon = mon;
        this.day = day;
        this.dataType = dataType;
        this.dataName = dataName;
        this.data = data;
    }

    public RecordData(HashMap<String, Object> hashMap){
        for (String key: hashMap.keySet()){
            Object obj = hashMap.get(key);
            if (key.equals("data")){
                if (obj == null){
                    Log.w(getClass().getSimpleName(), "RecordData: list == null");
                } else {
                    try {
                        data = (HashMap<String, Object>) obj;
                        continue;
                    } catch (ClassCastException e) {
                        logStackTrace(e);
                    }

                    try {
                        List list = (List)obj;
                        data = new HashMap<>();
                        for (int i=0; i<list.size(); i++) {
                            data.put(Integer.toString(i), list.get(i));
                        }
                    } catch (ClassCastException e){
                        logStackTrace(e);
                    }
                }
                continue;
            }

            try {
                Field field = getClass().getDeclaredField(key);
                Class c = field.getType();
                if (c == int.class){
                    int num = Integer.valueOf(obj.toString());
                    field.setInt(this, num);
                } else if (c == short.class){
                    short num = Short.valueOf(obj.toString());
                    field.setShort(this, num);
                } else if (c == byte.class){
                    byte num = Byte.valueOf(obj.toString());
                    field.setByte(this, num);
                } else {
                    field.set(this, obj);
                }
            } catch (NoSuchFieldException e) {
                logStackTrace(e);
            } catch (IllegalAccessException e){
                logStackTrace(e);
            }
        }
    }

    public int getYear() {return year;}

    public int getMon() {
        return mon;
    }

    public int getDay() {
        return day;
    }

    public int getDataType() {
        return dataType;
    }

    public String getDataName() {
        return dataName;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMon(int mon) {
        this.mon = mon;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
}
