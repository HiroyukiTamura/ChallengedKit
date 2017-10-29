/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupport3.Adapters.CalendarRVAdapter;
import com.cks.hiroyuki2.worksupport3.Adapters.SharedCalendarVPAdapter;
import com.cks.hiroyuki2.worksupprotlib.Entity.CalendarEvent;
import com.cks.hiroyuki2.worksupport3.MaterialCalendarView.DecoratorMaker;
import com.cks.hiroyuki2.worksupport3.MaterialCalendarView.MSVDecorator;
import com.cks.hiroyuki2.worksupport3.MaterialCalendarView.MultiDotDecorator;
import com.cks.hiroyuki2.worksupportlibrary.worksupportlibrary.CalendarOneEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.cks.hiroyuki2.worksupport3.FbCheckAndWriter.CODE_SET_NULL;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.delimiterOfNum;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRef;
import static com.cks.hiroyuki2.worksupprotlib.Util.DATE_PATTERN_YM;
import static com.cks.hiroyuki2.worksupprotlib.Util.cal2date;
import static com.cks.hiroyuki2.worksupprotlib.Util.logStackTrace;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;

/**
 * {@link SharedCalendarVPAdapter}の子分、{@link CalendarRVAdapter}の親分。
 * {@link SharedCalendarVPAdapter#instantiateItem(ViewGroup, int)}が発火するごとにnewされる、ミニドラ的存在！
 * UIの描画、FBとの通信を担当！SharedCalendar系列の中の大切なメンバー！
 */

public class SharedCalendarUIOperator implements ValueEventListener, OnDateSelectedListener{
    private static final String TAG = "MANUAL_TAG: " + SharedCalendarUIOperator.class.getSimpleName();
    private View rootView;
    private Calendar cal;
    private String ym;
    private int pos;
    private SharedCalendarVPAdapter adapter;
    private CalendarRVAdapter rvAdapter;
    private DatabaseReference ref;
    private CalendarEvent calEve;
    @BindView(R.id.calendarView) MaterialCalendarView mcv;
    @BindView(R.id.recycler) RecyclerView rv;

    public SharedCalendarUIOperator(SharedCalendarVPAdapter adapter, View rootView, Calendar cal, DatabaseReference ref){
        this.rootView = rootView;
        this.adapter = adapter;
        this.cal = cal;
        this.ref = ref;
        ym = cal2date(cal, DATE_PATTERN_YM);
    }

    public void initView(){
        ButterKnife.bind(this, rootView);
        mcv.setPagingEnabled(false);
        mcv.setDynamicHeightEnabled(true);
        mcv.setTopbarVisible(false);
        mcv.setShowOtherDates(0);

        mcv.state().edit().setFirstDayOfWeek(adapter.getStartDayOfWeek())
                .setMaximumDate(getMaxDateOfMon(cal))
                .setMinimumDate(getMinDateOfMon(cal))
                .commit();

        mcv.setCurrentDate(cal);/*calは1日に設定されている*/
        mcv.setSelectedDate(Calendar.getInstance());
        mcv.addDecorator(new MSVDecorator(mcv, adapter.getFragment().getContext()));
        mcv.setOnDateChangedListener(this);

        rv.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        rv.setNestedScrollingEnabled(false);
        ref.child(ym).addListenerForSingleValueEvent(this);
    }

    private Calendar getMaxDateOfMon(Calendar cal){
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        return cal;
    }

    private Calendar getMinDateOfMon(Calendar cal){
        cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
        return cal;
    }

    public View getRootView() {
        return rootView;
    }

    //region implementしたリスナ系
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        calEve = new CalendarEvent(dataSnapshot, ym);
        invalidateDecorator();

        try {
            rvAdapter = new CalendarRVAdapter(calEve, this);
            rv.setAdapter(rvAdapter);
        } catch (ParseException e) {
            logStackTrace(e);
            Toast.makeText(rootView.getContext(), R.string.error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        onError(adapter.getFragment(), TAG + databaseError.getDetails(), null);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        if (!selected)
            return;

        if (calEve.getMonthEvent().isEmpty())
            return;

        int selectedDate = date.getDay();
        int nearestDate = Integer.MAX_VALUE;
        int def = Integer.MAX_VALUE;
        calEve.getMonthEvent().keySet();
        for (int key: calEve.getMonthEvent().keySet()) {
            int defC = Math.abs(selectedDate - key);
            if (defC <def){
                nearestDate = key;
                def = defC;
            }
        }

        if (nearestDate == Integer.MAX_VALUE)
            return;

        int pos = rvAdapter.getPosFromDate(nearestDate);
        rv.smoothScrollToPosition(pos);
    }
    //endregion

    //region decoratorまわり
    private void invalidateDecorator(){
        /*calEveに空配列が格納されていた場合、!calEve.isEmpty()となるから、!calEve.getMonthEvent().isEmpty()で制御してはいけない。*/
        if (calEve.getMonthEvent().isEmpty()){
            mcv.invalidateDecorators();
            return;
        }

        boolean isEmpty = true;
        for (List<CalendarOneEvent> oneEve: calEve.getMonthEvent().values()) {
            if (!oneEve.isEmpty()){
                isEmpty = false;

                break;
            }
        }
        if (!isEmpty)
            addDecorator(calEve);
        mcv.invalidateDecorators();
    }

    private void addDecorator(CalendarEvent event){
        DecoratorMaker maker = new DecoratorMaker(event);
        HashMap<String, TreeSet<Integer>> patternMap = maker.makeBox();
        for (String key : patternMap.keySet()) {
            String[] indexes = key.split(delimiterOfNum);
            List<Integer> list = new ArrayList<>();
            for (String indexStr: indexes) {
                int index = Integer.parseInt(indexStr);
                if (index >= Util.colorId.size())
                    continue;
                int id = Util.colorId.get(index);
                int color = ContextCompat.getColor(adapter.getFragment().getContext()/*非同期でないからOK*/, id);
                list.add(color);
            }
            MultiDotDecorator mdd = new MultiDotDecorator(list, patternMap.get(key), adapter.getFragment().getContext(), event.getYm());
            mcv.addDecorator(mdd);
        }
    }
    //endregion

    //region コールバックで発火する系
    public void toggleCalendar(){
        int visibility = mcv.getVisibility();
        if (visibility == VISIBLE)
            mcv.setVisibility(GONE);
        else if (visibility == GONE)
            mcv.setVisibility(VISIBLE);
    }

    public Calendar getSelectedDate(){
        return mcv.getSelectedDate().getCalendar();
    }

    public void addSchedule(Calendar cal, CalendarOneEvent newEve){
//        rvAdapter.addSchedule(cal, newEve);/*ややこしいけど、ここでrvAdapter内でCalendarEventが編集されます*/
        addScheduleFromCalEve(cal, newEve);
        mcv.removeDecorators();
        invalidateDecorator();

        int date = cal.get(Calendar.DATE);
        invalidateRv(getRvPosFromDate(date));
    }

    public void removeScheduleOnFb(final int day, final String eventKey){
        DatabaseReference writeRef = getRef(ref,
                cal2date(cal, DATE_PATTERN_YM),
                Integer.toString(day),
                eventKey);
        FbCheckAndWriter writer = new FbCheckAndWriter(ref, writeRef, adapter.getFragment().getContext()/*非同期でないからOK*/, null) {
            @Override
            public void onSuccess(DatabaseReference ref) {
                //rvAdapter.removeSchedule(day, eventKey);/*ややこしいけど、ここでrvAdapter内でCalendarEventが編集されます*/
                removeScheduleFromCalEve(day, eventKey);
                Toast.makeText(adapter.getFragment().getContext(), R.string.remove_schedule_success, Toast.LENGTH_LONG).show();
                mcv.removeDecorators();
                invalidateDecorator();
                invalidateRv(getRvPosFromDate(day));
            }
            //            @Override
//            void onSuccess(DatabaseReference ref) {
////                rvAdapter.removeSchedule(day, eventKey);/*ややこしいけど、ここでrvAdapter内でCalendarEventが編集されます*/
//                removeScheduleFromCalEve(day, eventKey);
//                Toast.makeText(adapter.getFragment().getContext(), R.string.remove_schedule_success, Toast.LENGTH_LONG).show();
//                mcv.removeDecorators();
//                invalidateDecorator();
//                invalidateRv(getRvPosFromDate(day));
//            }
        };
        writer.update(CODE_SET_NULL);
    }
    //endregion

    private void addScheduleFromCalEve(Calendar cal, CalendarOneEvent newEve){
        int date = cal.get(Calendar.DATE);
        if (calEve.getMonthEvent().containsKey(date)){
            List<CalendarOneEvent> eveList = calEve.getMonthEvent().get(date);
            eveList.add(newEve);
        } else {
            List<CalendarOneEvent> eveList = new ArrayList<>();
            eveList.add(newEve);
            calEve.getMonthEvent().put(date, eveList);
        }
    }

    private void removeScheduleFromCalEve(int day, String eventKey){
        List<CalendarOneEvent> oneEveList = calEve.getMonthEvent().get(day);
        int i;
        for (i = 0; i < oneEveList.size(); i++) {
            if (oneEveList.get(i).getEventKey().equals(eventKey)){
                oneEveList.remove(i);
                break;
            }
        }

        if (oneEveList.isEmpty())
            calEve.getMonthEvent().remove(day);
    }

    /**
     * なぜいちいちAdapterをnewしているかというと、なぜかRVのアップデート動作がうまくいかないからです。
     * @return 今のところこの返り値は使われていません。
     */
    private boolean invalidateRv(int pos){
        try {
            rvAdapter = new CalendarRVAdapter(calEve, this);
            rv.setAdapter(rvAdapter);
            rv.invalidate();
            rv.scrollToPosition(pos);
            return true;
        } catch (ParseException e) {
            logStackTrace(e);
            Toast.makeText(getRootView().getContext(), R.string.error, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private int getRvPosFromDate(int date){
        Set<Integer> set = calEve.getMonthEvent().keySet();
        return new ArrayList<>(set).indexOf(date);
    }
}
