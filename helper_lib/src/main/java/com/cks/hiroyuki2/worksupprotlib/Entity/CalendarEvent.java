/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib.Entity;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by hiroyuki2 on 2017/09/25.
 */

public class CalendarEvent {

    private TreeMap<Integer/*date*/, List<CalendarOneEvent>> monthEvent;
    private String ym;

    public CalendarEvent(DataSnapshot dataSnapshot, String ym){
        monthEvent = snapShot2MonthEvent(dataSnapshot);
        this.ym = ym;
    }

    public TreeMap<Integer, List<CalendarOneEvent>> getMonthEvent() {
        return monthEvent;
    }

    public String getYm() {
        return ym;
    }

    public TreeMap<Integer, List<CalendarOneEvent>> snapShot2MonthEvent(DataSnapshot dataSnapshot){
        TreeMap<Integer, List<CalendarOneEvent>> monthEvent = new TreeMap<>();

        for (DataSnapshot snap: dataSnapshot.getChildren()) {
            int date = Integer.parseInt(snap.getKey());
            List<CalendarOneEvent> dayEvents = new ArrayList<>();
            for (DataSnapshot events : snap.getChildren()){
                CalendarOneEvent oneEve = events.getValue(CalendarOneEvent.class);
                if (oneEve == null) continue;
                oneEve.setEventKey(events.getKey());
                dayEvents.add(oneEve);
            }

            monthEvent.put(date, dayEvents);
        }
        return monthEvent;
    }
}
