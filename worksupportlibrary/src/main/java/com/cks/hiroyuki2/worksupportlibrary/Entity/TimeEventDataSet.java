/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupportlibrary.Entity;

import android.support.annotation.NonNull;

import com.google.gson.JsonElement;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link TimeEvent}, {@link TimeEventRange}のデータを保持するクラス。
 * このクラスは、{@link RecordData#dataType == 1}の場合にのみ、{@link RecordData#data}に格納されます。
 * このとき、{@link RecordData#data}は{@link java.util.HashMap}であるため、key=="0", value=="このクラスを{@link com.google.gson.Gson#toJson(JsonElement)}で変換したstring"とします。
 * つまり、{@link RecordData#dataType == 1}の場合、常にエントリはひとつで、keyは"0"に限られます。
 * 将来は、{@link RecordData#data}をHashMapからデータ保持系の独自クラスにするべきです。
 */

public class TimeEventDataSet implements Comparator<TimeEvent>{

    private List<TimeEvent> eventList;
    private List<TimeEventRange> rangeList;

    TimeEventDataSet(@NonNull List<TimeEvent> eventList, @NonNull List<TimeEventRange> rangeList) {
        this.eventList = eventList;
        Collections.sort(this.eventList, this);
        this.rangeList = rangeList;
    }

    /**
     * @return コンストラクタから、実質nonNull
     */
    public List<TimeEvent> getEventList() {
        return eventList;
    }

    /**
     * @return コンストラクタから、実質nonNull
     */
    public List<TimeEventRange> getRangeList() {
        return rangeList;
    }

    @Override
    public int compare(TimeEvent timeEvent, TimeEvent t1) {
        Calendar cal = timeEvent.getCal();
        Calendar cal1 = t1.getCal();
        return cal.compareTo(cal1);
    }
}
