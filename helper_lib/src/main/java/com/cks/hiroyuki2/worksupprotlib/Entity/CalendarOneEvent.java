/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib.Entity;

import com.google.firebase.database.Exclude;

/**
 * Firebaseとの書き込み・読み込みに使用します。このオブジェクトひとつで、ひとつのスケジュールを表します。
 * {@link CalendarEvent}付属。
 */

public class CalendarOneEvent {

    @Exclude private String eventKey;
    private String title;
    private int colorNum;

    CalendarOneEvent(){
        //ここのコンストラクタはFirebase側でつかうので、変えてはいけない
    }

    public CalendarOneEvent(String eventKey, String title, int colorNum){
        this.eventKey = eventKey;
        this.title = title;
        this.colorNum = colorNum;
    }

    public CalendarOneEvent(String title, int colorNum){
        this.title = title;
        this.colorNum = colorNum;
    }

    public int getColorNum() {
        return colorNum;
    }

    public void setColorNum(long colorNum) {
        this.colorNum = (int) colorNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Exclude
    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    @Exclude
    public String getEventKey() {
        return eventKey;
    }
}
