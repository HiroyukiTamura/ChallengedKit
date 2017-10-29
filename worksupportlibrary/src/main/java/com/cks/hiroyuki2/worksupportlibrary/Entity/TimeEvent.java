/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupportlibrary.Entity;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;

import static com.cks.hiroyuki2.worksupportlibrary.Util.DATE_PATTERN_COLON_HM;
import static com.cks.hiroyuki2.worksupportlibrary.Util.cal2date;

/**
 * {@link RecordData#dataType} == 1の場合にこのクラスが使われます。
 */

public class TimeEvent implements Serializable{

    private Calendar cal;
    private boolean notSetDay;
    private String name;
    private int colorNum = 0;

    public TimeEvent(@NonNull String name, int colorNum, @IntRange(from = 0, to = 24) int hour, @IntRange(from = 0, to = 60) int min){
        this.name = name;
        this.colorNum = colorNum;
        notSetDay = true;
        cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
    }

    public TimeEvent(@NonNull String name, int colorNum, @NonNull Calendar cal){
        this.name = name;
        notSetDay = false;
        this.cal = cal;
        this.colorNum = colorNum;
    }

    public int getHour() {
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public int getMin() {
        return cal.get(Calendar.MINUTE);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHour(int hour) {
        cal.set(Calendar.HOUR_OF_DAY, hour);
    }

    public void setMin(int min) {
        cal.set(Calendar.MINUTE, min);
    }

    public Calendar getCal(){
        return cal;
    }

    public String getTimeStr(){
        return cal2date(cal, DATE_PATTERN_COLON_HM);
    }

    public int getColorNum() {
        return colorNum;
    }

    public void setColorNum(int colorNum) {
        this.colorNum = colorNum;
    }
}
