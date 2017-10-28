/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cks.hiroyuki2.worksupport3.AnalyticsVPUiOperator;
import com.cks.hiroyuki2.worksupport3.Fragments.AnalyticsFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.Util;

import java.util.Calendar;
import java.util.TreeMap;

/**
 * Analytics画面のVPAdapterおじさん！でもUIの操作{@link AnalyticsVPUiOperator}におまかせ
 * @see AnalyticsFragment
 */
public class AnalyticsVPAdapter extends PagerAdapter {
    private static final String TAG = "MANUAL_TAG: " + AnalyticsVPAdapter.class.getSimpleName();
    public static final int PAGE = 7;
    private LayoutInflater inflater;
    private Context context;
    private Calendar startCal;
    private TreeMap<Integer, AnalyticsVPUiOperator> operators = new TreeMap<>();
    private AnalyticsFragment analyticsFragment;

    public AnalyticsVPAdapter(Context context, AnalyticsFragment analyticsFragment){
        this.context = context;
        this.startCal = makeStartCal(context);
        this.analyticsFragment = analyticsFragment;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return PAGE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        operators.remove(position);
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View root =  inflater.inflate(R.layout.fragment_analytics, null);
        root.setTag(position);
        Calendar cal = getCal(position);
        Log.d(TAG, "instantiateItem: "+ cal.getTime().toString());
        AnalyticsVPUiOperator operator = new AnalyticsVPUiOperator(root, cal, analyticsFragment);
        operators.put(position, operator);
        container.addView(root);
        return root;
    }

    //カレンダーを週の頭へ
    private Calendar makeStartCal(Context context){
        Calendar startCal = Calendar.getInstance();
        SharedPreferences pref = context.getSharedPreferences(Util.PREF_NAME, Context.MODE_PRIVATE);
        int startDof = pref.getInt(Util.PREF_KEY_START_OF_WEEK, Calendar.SUNDAY);
        while (startCal.get(Calendar.DAY_OF_WEEK) != startDof){
            startCal.add(Calendar.DATE, -1);
        }
        return startCal;
    }

    private Calendar getCal(int position){
        Calendar cal = Calendar.getInstance();
        cal.setTime(startCal.getTime());
        cal.add(Calendar.DATE, 7*(position - PAGE/2));
        return cal;
    }
}
