/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cks.hiroyuki2.worksupprotlib.Entity.RecordData;
import com.cks.hiroyuki2.worksupprotlib.Entity.TimeEvent;
import com.cks.hiroyuki2.worksupprotlib.Entity.TimeEventDataSet;
import com.cks.hiroyuki2.worksupprotlib.Entity.TimeEventRange;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.cks.hiroyuki2.worksupprotlib.Util.TEMPLATE_SERIALIZE;
import static com.cks.hiroyuki2.worksupprotlib.Util.delimiter;
import static com.cks.hiroyuki2.worksupprotlib.Util.logStackTrace;

/**
 * テンプレ編集を担うおじさん！信頼が厚い！
 */

public class TemplateEditor {

    private static final String TAG = "TemplateEditor";

    @Nullable
    public static List<RecordData> deSerialize(Context context){
        Log.d(TAG, "deSerialize: fire");
        List<RecordData> data = null;
        try {
            FileInputStream fis = context.openFileInput(TEMPLATE_SERIALIZE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (List<RecordData>) ois.readObject();
            ois.close();
        } catch (IOException e) {
            logStackTrace(e);
        } catch (ClassNotFoundException e) {
            logStackTrace(e);
        }
        return data;
    }

    public static boolean writeTemplate(int dataNum, @NonNull RecordData data, @NonNull Context context){
        List<RecordData> list = deSerialize(context);
        if (list == null) return false;
        list.set(dataNum, data);
        return applyTemplate(list, context);
    }

    static boolean addRecordData(Context context, RecordData data){
        List<RecordData> list = deSerialize(context);
        if (list == null) return false;
        list.add(data);
        return applyTemplate(list, context);
    }

    public static boolean addRecordData(Context context, RecordData data, int index){
        List<RecordData> list = deSerialize(context);
        if (list == null) return false;
        list.add(index, data);
        return applyTemplate(list, context);
    }

    public static boolean applyTemplate(List<RecordData> list, Context context){
        try {
            ObjectOutputStream out = new ObjectOutputStream(context.openFileOutput(TEMPLATE_SERIALIZE, Context.MODE_PRIVATE));
            out.writeObject(list);
            out.close();
            return true;
        } catch (IOException e){
            logStackTrace(e);
            return false;
        }
    }

    public static boolean initDefaultTemplate(Context context) {
        Log.d(TAG, "initFile: fire");
        List<RecordData> list = new ArrayList<>(5);
        for (int i = 0; i < 6; i++) {
            RecordData data = new RecordData();
            data.data = new HashMap<>();
            switch (i) {
                case 0:
                    data.dataType = 0;//ヘッダータグ
                    data.data.put("0", "勤務日" + delimiter + "2");
                    break;
                case 1:
                    data.dataType = 1;//タイムライン
                    TimeEvent wakeUp = new TimeEvent("起床", 0, 7, 0);
                    TimeEvent sleep = new TimeEvent("就寝", 0, 22, 0);
                    TimeEventRange range = new TimeEventRange(wakeUp, sleep);
                    List<TimeEventRange> rangeList = new LinkedList<>();
                    rangeList.add(range);
                    TimeEvent lunch = new TimeEvent("昼食", 0, 13, 0);
                    List<TimeEvent> eventList = new LinkedList<>();
                    eventList.add(lunch);
                    TimeEventDataSet dataSet = new TimeEventDataSet(eventList, rangeList);
                    data.data.put("0", new Gson().toJson(dataSet));
//                    data.data.put("7:00", "起床" + FirebaseConnection.delimiter + "1");
//                    data.data.put("8:50", "出勤");
//                    data.data.put("14:00", "退勤");
//                    data.data.put("22:00", "就寝" + FirebaseConnection.delimiter + "1");
                    break;
                case 2:
                    data.dataType = 2;//タグプール
                    data.dataName = "注意サイン";
                    data.data.put("0", "息苦しい" + delimiter + "0" + delimiter + "false");
                    data.data.put("1", "震え" + delimiter + "1" + delimiter + "false");
                    data.data.put("2", "やけ食いする" + delimiter + "2" + delimiter + "false");
                    break;
                case 3:
                    data.dataType = 2;
                    data.dataName = "危険サイン";
                    data.data.put("0", "動けない" + delimiter + "0" + delimiter + "false");
                    data.data.put("1", "テンションが上がる" + delimiter + "1" + delimiter + "false");
                    data.data.put("2", "謝りすぎる" + delimiter + "2" + delimiter + "false");
                    break;
                case 4:
                    data.dataType = 4;
                    data.dataName = "備考";
                    data.data.put("comment", null);
                    break;
                case 5:
                    data.dataType = 3;
                    data.dataName = "服薬";
                    data.data.put("0", "0" + delimiter + "朝" + delimiter + "true");
                    data.data.put("1", "0" + delimiter +  "頓服" + delimiter + "false" );
                    data.data.put("2", "1" + delimiter +  "気分" + delimiter + "3" + delimiter + "5");
                    break;
            }
            list.add(data);
        }

        return applyTemplate(list, context);
    }
}
