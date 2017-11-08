/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.Activities.MainActivity;
import com.cks.hiroyuki2.worksupport3.DialogKicker;
import com.cks.hiroyuki2.worksupprotlib.Entity.RecordData;
import com.cks.hiroyuki2.worksupprotlib.Entity.TimeEvent;
import com.cks.hiroyuki2.worksupprotlib.FirebaseConnection;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.example.hiroyuki3.worksupportlibw.Adapters.RecordRVAdapter;
import com.example.hiroyuki3.worksupportlibw.Adapters.RecordTabVPAdapter;
import com.example.hiroyuki3.worksupportlibw.Adapters.RecordVPAdapter;
import com.example.hiroyuki3.worksupportlibw.Presenter.RecordUiOperator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.PageSelected;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickCircleAndInputDialog;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickDialogInOnClick;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickInputDialog;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickTimePickerDialog;
import static com.cks.hiroyuki2.worksupprotlib.Util.date2Cal;
import static com.cks.hiroyuki2.worksupprotlib.Util.datePattern;
import static com.cks.hiroyuki2.worksupprotlib.Util.logStackTrace;
//import static com.cks.hiroyuki2.worksupprotlib.Util.KEY;
import static com.cks.hiroyuki2.worksupprotlib.Util.INDEX;
import static com.cks.hiroyuki2.worksupprotlib.Util.time2String;
import static com.example.hiroyuki3.worksupportlibw.Adapters.RecordVPAdapter.DATA_NUM;
import static com.example.hiroyuki3.worksupportlibw.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_ADD;
import static com.example.hiroyuki3.worksupportlibw.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_ADD2;
import static com.example.hiroyuki3.worksupportlibw.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_CLICK;
import static com.example.hiroyuki3.worksupportlibw.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_CLICK2;
import static com.example.hiroyuki3.worksupportlibw.Adapters.TimeEventRVAdapter.DIALOG_TAG_ITEM_ADD;
import static com.example.hiroyuki3.worksupportlibw.Adapters.TimeEventRVAdapter.DIALOG_TAG_ITEM_ADD2;
import static com.example.hiroyuki3.worksupportlibw.Adapters.TimeEventRVAdapter.DIALOG_TAG_ITEM_CLICK;
import static com.example.hiroyuki3.worksupportlibw.Adapters.TimeEventRVAdapter.DIALOG_TAG_ITEM_CLICK2;
import static com.example.hiroyuki3.worksupportlibw.Adapters.TimeEventRVAdapter.TIME_EVENT;
import static com.example.hiroyuki3.worksupportlibw.Adapters.TimeEventRangeRVAdapter.CALLBACK_RANGE_CLICK_TIME;
import static com.example.hiroyuki3.worksupportlibw.Adapters.TimeEventRangeRVAdapter.CALLBACK_RANGE_CLICK_VALUE;
import static com.example.hiroyuki3.worksupportlibw.Adapters.TimeEventRangeRVAdapter.DIALOG_TAG_RANGE_CLICK_TIME;
import static com.example.hiroyuki3.worksupportlibw.Adapters.TimeEventRangeRVAdapter.DIALOG_TAG_RANGE_CLICK_VALUE;
import static com.example.hiroyuki3.worksupportlibw.Presenter.RecordUiOperator.makeBundleInOnClick;
import static com.example.hiroyuki3.worksupportlibw.RecordVpItems.RecordVpItemTime.CALLBACK_RANGE_COLOR;


@EFragment(R.layout.record_vp_content)
public class RecordFragment extends Fragment implements RecordTabVPAdapter.AdapterCallback, ViewPager.OnPageChangeListener, RecordVPAdapter.IRecordVPAdapter, RecordUiOperator.IRecordUiOperator {
    
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
    private SharedPreferences pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cal.set(yearMed, monMed, dayMed);
        pref = context.getSharedPreferences(Util.PREF_NAME, Context.MODE_PRIVATE);
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
        Log.d(TAG, "postOnClick() cal:" + Util.cal2date(cal, Util.datePattern));
        String currentDateStr = Integer.toString((int)adapter.currentPage.getTag());
        Calendar currentCal;
        try {
            currentCal = date2Cal(currentDateStr, Util.datePattern);
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
//            String date = Util.cal2date(cal, Util.datePattern);
//            if (adapter.dataMap.containsKey(date)){
//
//            }
//        }
//        cal = null;
//        String currentDataStr = Integer.toString((Integer) adapter.currentPage.getTag());
//        Calendar currentCal;
//        try {
//            currentCal = Util.date2Cal(currentDataStr, Util.datePattern);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return;
//        }
    }

    //region RecordVPAdapter.IRecordVPAdapter
    @Override
    public void onPostUpdateData(){
        Log.d(TAG, "onPostUpdateData: fire");
        viewPager.setCurrentItem(adapter.findPosition(upDatingCal), false);
        innerPb.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        upDatingCal = null;
    }

    @Override
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
    //endregion

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

            Calendar tagCal = date2Cal(Integer.toString(tag), Util.datePattern);
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
            int date = Integer.parseInt(Util.cal2date(tagCal, Util.datePattern));
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
    public void onTabVpSelected(){
        Calendar opeCal;
        if (tabVPAdapter.currentItem != null){
            int tag = (int)tabVPAdapter.currentItem.getTag();
            try {
                Calendar calC = Util.date2Cal(Integer.toString(tag), datePattern);
                opeCal = Util.getCopyOfCal(calC);
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
        } else {
//            tabVPAdapter.currentItem == null、つまり初回動作時
            opeCal = Util.getCopyOfCal(cal);
        }

        int monthBf = opeCal.get(Calendar.MONTH)+1;//monthは0始まりだから
        opeCal.add(Calendar.DATE, 7);
        int monthAf = opeCal.get(Calendar.MONTH)+1;

        String s = monthBf + "月";
        if (monthBf != monthAf){
            s = s +" → "+ monthAf + "月";
        }

        ((MainActivity)getActivity()).setToolbarTitle(s);
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
                int index = bundle.getInt(INDEX);
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

    //region RecordUiOperator.IRecordUiOperator
    @Override
    public void onClickCommentEdit(@NonNull Bundle bundle) {
        kickInputDialog(bundle, RecordVPAdapter.COMMENT, RecordVPAdapter.CALLBACK_COMMENT, this);
    }

    @Override
    public void updateAndSync(List<RecordData> list, String s) {
        adapter.syncDataMapAndFireBase(list, s);
    }

    @Override
    public void onClickTagPoolContent(Calendar calendar, int i) {
        Bundle bundle = RecordUiOperator.makeBundleInOnClick(RecordVPAdapter.TAG_ADD, calendar, i);/*RecordUiOperatorのmakeBundleInOnClickであることに注意してください　同名メソッドがあります*/
        kickDialogInOnClick(RecordVPAdapter.TAG_ADD, RecordVPAdapter.CALLBACK_TAG_ADD, bundle, this);
    }

    @Override
    public void onClickAddTimeEveBtn(Bundle bundle) {
        kickTimePickerDialog(DIALOG_TAG_ITEM_ADD, CALLBACK_ITEM_ADD, bundle, this);
    }
    //endregion

    @Override
    public void onClickValue(Bundle bundle) {
        kickInputDialog(bundle, DIALOG_TAG_RANGE_CLICK_VALUE, CALLBACK_RANGE_CLICK_VALUE, this);
    }

    @Override
    public void onClickTime(Bundle bundle) {
        kickTimePickerDialog(DIALOG_TAG_RANGE_CLICK_TIME, CALLBACK_RANGE_CLICK_TIME, bundle, this);
    }

    @Override
    public void onClickItem(Bundle bundle) {
        kickTimePickerDialog(DIALOG_TAG_ITEM_CLICK, CALLBACK_ITEM_CLICK, bundle, this);
    }
}
