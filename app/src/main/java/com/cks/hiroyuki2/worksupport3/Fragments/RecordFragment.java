/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.Adapters.RecordTabVPAdapter;
import com.cks.hiroyuki2.worksupport3.Adapters.RecordVPAdapter;
import com.cks.hiroyuki2.worksupprotlib.FirebaseConnection;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.RecordRVAdapter;
import com.cks.hiroyuki2.worksupprotlib.Util;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.PageSelected;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_ADD;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_ADD2;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_CLICK;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_CLICK2;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.DIALOG_TAG_ITEM_ADD2;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.DIALOG_TAG_ITEM_CLICK2;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRangeRVAdapter.CALLBACK_RANGE_CLICK_TIME;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRangeRVAdapter.CALLBACK_RANGE_CLICK_VALUE;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickCircleAndInputDialog;
import static com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItemTime.CALLBACK_RANGE_COLOR;
import static com.cks.hiroyuki2.worksupprotlib.Util.date2Cal;
import static com.cks.hiroyuki2.worksupprotlib.Util.logStackTrace;


@EFragment(R.layout.record_vp_content)
public class RecordFragment extends Fragment implements RecordTabVPAdapter.AdapterCallback, ViewPager.OnPageChangeListener {
    
    private static final String TAG = "MANUAL_TAG: " + RecordFragment.class.getSimpleName();

    //以下の変数を使う際、値の起点はカレンダークラスのフィールドに依拠する
    public static final String MEDIAN_YEAR = "MEDIAN_YEAR";
    public static final String MEDIAN_MONTH = "MEDIAN_MONTH";
    public static final String MEDIAN_DAY = "MEDIAN_DAY";
    public static final String DAY_OF_WEEK = "DAY_OF_WEEK";

    public RecordVPAdapter adapter;
    public RecordTabVPAdapter tabVPAdapter;
    @ViewById(R.id.viewpager) ViewPager viewPager;
    @ViewById(R.id.tab) ViewPager viewPagerTab;
    @ViewById(R.id.content) LinearLayout content;
    @ViewById(R.id.progress_bar) ProgressBar progressBar;
    @ViewById(R.id.progress_bar_inner) ProgressBar innerPb;
    private Calendar upDatingCal;//アップデート中に使用する。nullのとき、upDate中でないことを表す
//    List<String> list;

    @FragmentArg int yearMed;
    @FragmentArg int monMed;
    @FragmentArg int dayMed;
    private Calendar cal = Calendar.getInstance();
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cal.set(yearMed, monMed, dayMed);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        content = null;
    }

    @AfterViews
    void afterViews(){
        adapter = new RecordVPAdapter(cal, this);
        adapter.initData();
    }

    private Calendar getSwipedCal(int position){
        Calendar cal = Calendar.getInstance();//calMedのposはMED_NUMとなる
        cal.set(yearMed, monMed, dayMed);
        cal.add(Calendar.DATE, - RecordVPAdapter.MED_NUM + position);//これでCalが設定できた
        return cal;
    }

    @Override
    public void postOnClick(Calendar cal) {
        Log.d(TAG, "postOnClick() cal:" + Util.cal2date(cal, FirebaseConnection.datePattern));
        String currentDateStr = Integer.toString((int)adapter.currentPage.getTag());
        Calendar currentCal;
        try {
            currentCal = date2Cal(currentDateStr, FirebaseConnection.datePattern);
        } catch (ParseException e) {
            logStackTrace(e);
            return;
        }
        int diff = (int)TimeUnit.MILLISECONDS.toDays(cal.getTimeInMillis() - currentCal.getTimeInMillis());
        if (Math.abs(diff) > RecordVPAdapter.DATALIST_DEF_LENGTH/2){
            upDatingCal = cal;
            viewPager.setVisibility(View.GONE);
            innerPb.setVisibility(View.VISIBLE);
            Calendar calTemp = Calendar.getInstance();
            calTemp.setTime(cal.getTime());
            adapter.retrieveData(calTemp);
        } else {
            int posNew = viewPager.getCurrentItem() + diff;
            if (0<posNew && posNew<adapter.getCount()){
                viewPager.setCurrentItem(posNew, true);
                innerPb.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
            }
        }
//        if (adapter.dataMap == null || adapter.dataMap.isEmpty())
//            return;
//        cal.add(Calendar.DATE, -RecordVPAdapter.DATALIST_DEF_LENGTH/2);
//        for (int i=0; i<RecordVPAdapter.DATALIST_DEF_LENGTH; i++){
//            String date = Util.cal2date(cal, FirebaseConnection.datePattern);
//            if (adapter.dataMap.containsKey(date)){
//
//            }
//        }
//        cal = null;
//        String currentDataStr = Integer.toString((Integer) adapter.currentPage.getTag());
//        Calendar currentCal;
//        try {
//            currentCal = Util.date2Cal(currentDataStr, FirebaseConnection.datePattern);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return;
//        }
    }

    public void onPostUpdateData(){
        Log.d(TAG, "onPostUpdateData: fire");
        viewPager.setCurrentItem(adapter.findPosition(upDatingCal), false);
        innerPb.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        upDatingCal = null;
    }

    public void onPostInitData(){
        Log.d(TAG, "onPostInitData: fire");
        progressBar.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(RecordVPAdapter.MED_NUM);
//        viewPagerTab.setOffscreenPageLimit();
        tabVPAdapter = new RecordTabVPAdapter(this.getContext(), cal, this);
        viewPagerTab.setAdapter(tabVPAdapter);
        viewPagerTab.setCurrentItem(RecordTabVPAdapter.MED_NUM);

        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "onPageSelected: fire");
        
        if (upDatingCal != null)
            return;

        Calendar cal = getSwipedCal(position);
        adapter.onPageSelected(cal);
        cal = getSwipedCal(position);
        int tag = (Integer) tabVPAdapter.currentItem.getTag();

        try {
            LinearLayout ll = tabVPAdapter.currentItem.findViewById(R.id.date_container);
            View circle = ll.findViewWithTag(RecordTabVPAdapter.TAG_VISIBLE);
            circle.setTag(null);
            circle.setVisibility(View.GONE);
            TextView tv = ((View)circle.getParent()).findViewById(R.id.tv);
            tv.setTextColor(Color.WHITE);

            Calendar tagCal = date2Cal(Integer.toString(tag), FirebaseConnection.datePattern);
            if (cal.compareTo(tagCal) < 0){
                Log.d(TAG, "onPageSelected: 前週へGO");
                viewPagerTab.setCurrentItem(viewPagerTab.getCurrentItem() -1, true);//前週へ
                tagCal.add(Calendar.DATE, -7);//tagCalを前週のものへ
            } else {
                tagCal.add(Calendar.DATE, 7);//翌週のtagCal
                if (cal.compareTo(tagCal) >= 0){
                    Log.d(TAG, "onPageSelected: 翌週へGo");
                    viewPagerTab.setCurrentItem(viewPagerTab.getCurrentItem() +1, true);//翌週へ
                } else {
                    Log.d(TAG, "onPageSelected: 今週のまま！");
                    tagCal.add(Calendar.DATE, -7);//tagCalを今週のものへ戻す
                }
            }

            //まず,遷移先のviewを取得する.
            int def = (int) TimeUnit.MILLISECONDS.toDays(cal.getTimeInMillis() - tagCal.getTimeInMillis());
            int date = Integer.parseInt(Util.cal2date(tagCal, FirebaseConnection.datePattern));
            Log.d(TAG, "onPageSelected: date " + date);
            if (viewPagerTab.findViewWithTag(date) == null){
                Log.w(TAG, "onPageSelected: viewはまだcreateされていない");
                return;
            }
            ll = viewPagerTab.findViewWithTag(date).findViewById(R.id.date_container);
            FrameLayout newFm = (FrameLayout) ll.getChildAt(def);
            View newCircle = newFm.findViewById(R.id.iv);
            newCircle.setTag(RecordTabVPAdapter.TAG_VISIBLE);
            newCircle.setVisibility(View.VISIBLE);
            ((TextView)newFm.findViewById(R.id.tv)).setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        } catch (ParseException e) {
            logStackTrace(e);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @PageSelected(R.id.tab)
    void onTabVpSelected(){
        Log.d(TAG, "onTabVpSelected() called");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: fire");
        if (resultCode != RESULT_OK)
            return;

        Bundle bundle = data.getExtras();
        switch (requestCode){
            case RecordRVAdapter.CALLBACK_LONGTAP:
                Log.d(TAG, "onActivityResult: RecordRVAdapter.CALLBACK_LONGTAP");
                int index = bundle.getInt(RecordRVAdapter.INDEX);
                int key1 = bundle.getInt(RecordRVAdapter.KEY);
                int dateInt = (Integer) adapter.currentPage.getTag();
                RecordRVAdapter rvAdapter1 = adapter.timeAdapterTree.get(dateInt).get(key1);
                rvAdapter1.deleteItem(index, key1);
                break;
            case CALLBACK_ITEM_CLICK:
                kickCircleAndInputDialog(DIALOG_TAG_ITEM_CLICK2, CALLBACK_ITEM_CLICK2, bundle, this);
                break;

            case CALLBACK_ITEM_CLICK2:
            case CALLBACK_RANGE_CLICK_TIME:
            case CALLBACK_RANGE_CLICK_VALUE:
            case CALLBACK_ITEM_ADD2:
            case CALLBACK_RANGE_COLOR:
            case RecordVPAdapter.CALLBACK_COMMENT:
            case RecordVPAdapter.CALLBACK_TAG_ADD:
                adapter.callbackFragment(data, requestCode);
                break;
            case CALLBACK_ITEM_ADD:
                kickCircleAndInputDialog(DIALOG_TAG_ITEM_ADD2, CALLBACK_ITEM_ADD2, bundle, this);
                break;
        }
    }
}
