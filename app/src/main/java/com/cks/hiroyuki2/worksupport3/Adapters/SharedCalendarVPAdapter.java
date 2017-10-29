/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.cks.hiroyuki2.worksupportlibrary.worksupportlibrary.CalendarOneEvent;
import com.cks.hiroyuki2.worksupport3.Fragments.SharedCalendarFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.SharedCalendarUIOperator;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.Contract;

import java.util.Calendar;
import java.util.TreeMap;

import static com.cks.hiroyuki2.worksupprotlib.Util.PREF_KEY_START_OF_WEEK;
import static com.cks.hiroyuki2.worksupprotlib.Util.PREF_NAME;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;

/**
 * {@link SharedCalendarFragment}の子分。{@link SharedCalendarUIOperator}の親分。
 * {@link SharedCalendarUIOperator}のおかげで、VPAdapterとしての機能だけで済んでいるおじさん！
 */

public class SharedCalendarVPAdapter extends PagerAdapter {
    private static final String TAG = "MANUAL_TAG: " + SharedCalendarVPAdapter.class.getSimpleName();
    private static final int COUNT = 24;
    private Fragment fragment;
    private Calendar calMed;
    private Calendar startCal;
    private int startDayOfWeek;
    private DatabaseReference ref;
    private TreeMap<Integer, SharedCalendarUIOperator> operatorMap = new TreeMap<>();
    private int currentPos;

    public SharedCalendarVPAdapter(Fragment fragment, Calendar calMed){
        this.fragment = fragment;
        this.calMed = calMed;

        startCal = Calendar.getInstance();
        startCal.setTime(calMed.getTime());
        startCal.add(Calendar.MONTH, -COUNT/2);

        SharedPreferences pref = fragment.getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        startDayOfWeek = pref.getInt(PREF_KEY_START_OF_WEEK, Calendar.SUNDAY);
        ref = ((SharedCalendarFragment)fragment).getRef();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        operatorMap.remove(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View root =  fragment.getLayoutInflater().inflate(R.layout.calendar_vp_item, container, false);
        Calendar cal = getCalenderOfItem(position);
        SharedCalendarUIOperator operator = new SharedCalendarUIOperator(this, root, cal, ref);
        operatorMap.put(position, operator);
        operator.initView();
        container.addView(root);
        return root;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    public int getStartDayOfWeek() {
        return startDayOfWeek;
    }

    @Contract(pure = true)
    private Calendar getStartCal() {
        return startCal;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public Calendar getCalenderOfItem(int pos){
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartCal().getTime());
        cal.add(Calendar.MONTH, pos);
        return cal;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        currentPos = position;
    }

    public void toggleCalendar(){
        operatorMap.get(currentPos).toggleCalendar();
    }

    public void addSchedule(Calendar cal, @NonNull CalendarOneEvent oneEve){
        Calendar currentCal = getCalenderOfItem(currentPos);
        if (cal.get(Calendar.YEAR) != currentCal.get(Calendar.YEAR)
                || cal.get(Calendar.MONTH) != currentCal.get(Calendar.MONTH)){
            onError(getFragment().getContext(), "ym != currentPos", R.string.error);
            return;
        }

        operatorMap.get(currentPos).addSchedule(cal, oneEve);
    }

    public Calendar getSelectedDate(){
        return operatorMap.get(currentPos).getSelectedDate();
    }
}
