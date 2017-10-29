/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupportlibrary.MaterialCalendarView;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupportlibrary.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.TreeSet;

import static com.cks.hiroyuki2.worksupportlibrary.Util.DATE_PATTERN_YM;
import static com.cks.hiroyuki2.worksupportlibrary.Util.date2Cal;
import static com.cks.hiroyuki2.worksupportlibrary.Util.logStackTrace;

/**
 * 複数ドットを描画するかしないかを判定するおじさん！
 * @see MultiDotSpan と連携するよ
 */

public class MultiDotDecorator implements DayViewDecorator {
    
    private static final String TAG = "MANUAL_TAG: " + MultiDotDecorator.class.getSimpleName();
    private List<Integer> colorList;
    private Context context;
    private static final float DEFAULT_RADIUS = 3;
    private float radius;
    private Calendar cal;
//    private List<Integer> list = new ArrayList<>();
    private TreeSet<Integer> days;

    public MultiDotDecorator(List<Integer> colorIdList, TreeSet<Integer> days, Context context, String ym){
        this(colorIdList, context, days, ym, DEFAULT_RADIUS);
    }

    public MultiDotDecorator(List<Integer> colorList, Context context, TreeSet<Integer> days, String ym, float radius){
        this.colorList = colorList;
        this.context = context;
        this.days = days;
        this.radius = radius;

        try {
            cal = date2Cal(ym, DATE_PATTERN_YM);
        } catch (ParseException e) {
            logStackTrace(e);
            Toast.makeText(context, R.string.error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return days.contains(day.getDay()) && day.getMonth() == cal.get(Calendar.MONTH) && day.getYear() == cal.get(Calendar.YEAR);
    }

    @Override
    public void decorate(DayViewFacade view) {
        Log.d(TAG, "decorate: fire");
        view.addSpan(new MultiDotSpan(context, colorList));
    }
}
