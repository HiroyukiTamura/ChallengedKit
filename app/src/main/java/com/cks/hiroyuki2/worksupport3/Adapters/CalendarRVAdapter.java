/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.Activities.SharedCalendarActivity;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.SharedCalendarUIOperator;
import com.cks.hiroyuki2.worksupprotlib.Entity.CalendarEvent;
import com.cks.hiroyuki2.worksupprotlib.Entity.CalendarOneEvent;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cks.hiroyuki2.worksupprotlib.Util.DATE_PATTERN_SLASH_MD;
import static com.cks.hiroyuki2.worksupprotlib.Util.DATE_PATTERN_YM;
import static com.cks.hiroyuki2.worksupprotlib.Util.cal2date;
import static com.cks.hiroyuki2.worksupprotlib.Util.date2Cal;
import static com.cks.hiroyuki2.worksupprotlib.UtilSpec.colorId;

/**
 * {@link SharedCalendarUIOperator}の子分。{@link SharedCalendarActivity}系列で最も子分のおじさん！
 */

public class CalendarRVAdapter extends RecyclerView.Adapter<CalendarRVAdapter.ViewHolder> {

    private static final String TAG = "MANUAL_TAG: " + CalendarRVAdapter.class.getSimpleName();
    private SharedCalendarUIOperator operator;
    private TreeMap<Integer, List<CalendarOneEvent>> treeMap;
    private CalendarEvent calEve;
    private Calendar cal;
    private String[] wof;
    private Context context;
    private LayoutInflater inflater;

    public CalendarRVAdapter(CalendarEvent calEve, SharedCalendarUIOperator operator) throws ParseException{
        this.operator = operator;
        this.calEve = calEve;
        cal = date2Cal(calEve.getYm(), DATE_PATTERN_YM);
        treeMap = calEve.getMonthEvent();
        context = operator.getRootView().getContext();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wof = context.getResources().getStringArray(R.array.dof);
    }

    public Calendar getCal() {
        return cal;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.date) TextView date;
        @BindView(R.id.root) ViewGroup root;

        ViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    class Item{
        @BindView(R.id.color_line) View colorLine;
        @BindView(R.id.tv) TextView tv;
        String eventKey;
        int day;

        Item(int day, String eventKey){
            this.day = day;
            this.eventKey = eventKey;
        }

        @OnClick(R.id.vert)
        void deleteSchedule(){
            operator.removeScheduleOnFb(day, eventKey);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.calendar_rv_item_parent, parent, false);
        ButterKnife.bind(this, v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int day = (int)treeMap.keySet().toArray()[position];
        cal.set(Calendar.DATE, day);
        holder.date.setText(cal2DateWithSlash(cal, context));
        List<CalendarOneEvent> oneEveList = treeMap.get(day);
        while (holder.root.getChildAt(1) != null)
            holder.root.removeViewAt(1);
        for (CalendarOneEvent eve: oneEveList) {
            FrameLayout card = (FrameLayout) inflater.inflate(R.layout.calendar_rv_item, null);
            Item item = new Item(day, eve.getEventKey());
            ButterKnife.bind(item, card);
            int colorIdn = colorId.get(eve.getColorNum());
            item.colorLine.setBackgroundResource(colorIdn);
            item.tv.setText(eve.getTitle());
            holder.root.addView(card);
        }
    }

    @Override
    public int getItemCount() {
        return treeMap.size();
    }

    public void addSchedule(Calendar cal, @NonNull CalendarOneEvent newEve){
        int date = cal.get(Calendar.DATE);
        if (treeMap.containsKey(date)){
            List<CalendarOneEvent> eveList = treeMap.get(date);
            eveList.add(newEve);
            notifyItem(date);
        } else {
            List<CalendarOneEvent> eveList = new ArrayList<>();
            eveList.add(newEve);
            treeMap.put(date, eveList);
            notifyItemInserted(treeMap.size() - 1);
        }
    }

    private void notifyItem(int date){
        int pos = getPosFromDate(date);
        notifyItemChanged(pos);
    }

    public void removeSchedule(int day, String eventKey){
        List<CalendarOneEvent> oneEveList = treeMap.get(day);
        for (int i = 0; i < oneEveList.size(); i++) {
            if (oneEveList.get(i).getEventKey().equals(eventKey)){
                oneEveList.remove(i);
                break;
            }
        }

        if (oneEveList.isEmpty())
            treeMap.remove(day);

        notifyDataSetChanged();
    }

    public int getPosFromDate(int date){
        Set<Integer> set = treeMap.keySet();
        return new ArrayList<>(set).indexOf(date);
    }

    @NonNull
    public static String cal2DateWithSlash(@NonNull Calendar cal, @NonNull Context context){
        String md = cal2date(cal, DATE_PATTERN_SLASH_MD);
        String[] dof = context.getResources().getStringArray(R.array.dof);
        return md +" "+ dof[cal.get(Calendar.DAY_OF_WEEK)];
    }
}
