/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.RecordVpItems;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter;
import com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRangeRVAdapter;
import com.cks.hiroyuki2.worksupport3.Fragments.EditTemplateFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.RecordFragment;
import com.cks.hiroyuki2.worksupport3.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cks.hiroyuki2.worksupport3.Adapters.RecordVPAdapter.DATA_NUM;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_ADD;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.DIALOG_TAG_ITEM_ADD;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.TIME_EVENT;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRangeRVAdapter.POS_IN_LIST;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickCircleAndInputDialog;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickTimePickerDialog;
import static com.cks.hiroyuki2.worksupprotlib.UtilSpec.colorId;
import static com.cks.hiroyuki2.worksupprotlib.Util.getTimeEveDataSetFromRecordData;
import static com.cks.hiroyuki2.worksupprotlib.Util.initRecycler;

/**
 * RecordVpItem兄弟！Timelineおじさん！
 */
public class RecordVpItemTime extends RecordVpItem {

    private static final String TAG = "MANUAL_TAG: " + RecordVpItemTime.class.getSimpleName();
    private TimeEventRVAdapter adapter;
    private List<Pair<TimeEventRangeParams, TimeEventRangeRVAdapter>> rangePairList = new ArrayList<>();
    private TimeEventDataSet dataSet;
    public static final String DIALOG_TAG_RANGE_COLOR = "DIALOG_TAG_RANGE_COLOR";
    public static final int CALLBACK_RANGE_COLOR = 2048;
    public static String TIME_EVE_RANGE = "TIME_EVE_RANGE";
    @BindView(R.id.time_event_rv) RecyclerView timeEventRv;
    @BindView(R.id.rv_container) LinearLayout container;

    public RecordVpItemTime(RecordData data, int dataNum, Calendar cal, RecordFragment fragment) {
        super(data, dataNum, cal, fragment);
    }

    public RecordVpItemTime(RecordData data, int dataNum, EditTemplateFragment fragment){
        super(data, dataNum, Calendar.getInstance(), fragment);
    }

    /**
     * posInListは、アイテムが削除されるたびに値が変更されることに注意してください。
     */
    class TimeEventRangeParams {
        @BindView(R.id.start_circle) ImageView startCircle;
        @BindView(R.id.end_circle) ImageView endCircle;
        @BindView(R.id.stroke) View stroke;
        @BindView(R.id.rv) RecyclerView rv;
        int posInList;

        TimeEventRangeParams(int posInList){
            this.posInList = posInList;
        }

        void setColor(int colorRes){
            stroke.setBackgroundResource(colorRes);
            startCircle.setColorFilter(ContextCompat.getColor(getFragment().getContext(), colorRes));
            endCircle.setColorFilter(ContextCompat.getColor(getFragment().getContext(), colorRes));
        }

        @OnClick(R.id.remove)
        void onClickRemove(View view){
            removeRangeItem(posInList);
        }

        @OnClick(R.id.color_fl)
        void onClickColorFl(){
            Bundle bundle = new Bundle();
            bundle.putInt(DATA_NUM, getDataNum());
            bundle.putInt(POS_IN_LIST, posInList);
            bundle.putSerializable(TIME_EVE_RANGE, dataSet.getRangeList().get(posInList));
            kickCircleAndInputDialog(DIALOG_TAG_RANGE_COLOR, CALLBACK_RANGE_COLOR, bundle, getFragment());
        }
    }

    /**
     * {@link RecordData#data}のエントリはただ一つ、かつkey="0"である
     */
    @Override
    @Nullable
    public View buildView() {
        View view = getFragment().getLayoutInflater().inflate(R.layout.record_vp_item_timeline2, null);
        ButterKnife.bind(this, view);
        dataSet = getTimeEveDataSetFromRecordData(getData());
        if (dataSet == null)
            return null;

        timeEventRv.setLayoutManager(new LinearLayoutManager(getFragment().getContext()));
        timeEventRv.setNestedScrollingEnabled(false);
        adapter = new TimeEventRVAdapter(dataSet.getEventList(), getFragment(), getCal(), getDataNum());
        timeEventRv.setAdapter(adapter);

        for (int i=0; i<dataSet.getRangeList().size(); i++) {
            TimeEventRange range = dataSet.getRangeList().get(i);
            addRangeItem(range, i);
        }
        return view;
    }

    @OnClick(R.id.add_time_eve)
    void onClickAddTimeEveBtn() {
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_NUM, getDataNum());
        TimeEvent timeEvent = new TimeEvent("", 0, getCopyOfCal());
        bundle.putSerializable(TIME_EVENT, timeEvent);
        kickTimePickerDialog(DIALOG_TAG_ITEM_ADD, CALLBACK_ITEM_ADD, bundle, getFragment());
    }

    @OnClick(R.id.add_range)
    void onClickAddRangeBtn() {
        TimeEvent start = new TimeEvent("起床", 0, getCopyOfCal());
        TimeEvent end = new TimeEvent("就寝", 0, getCopyOfCal());
        TimeEventRange range = new TimeEventRange(start, end);

        addRangeItem(range, rangePairList.size());
        addRangeToList(range);
    }

    private Calendar getCopyOfCal(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCal().getTime());
        return cal;
    }

    public void updateTime(@IntRange(from = 0) int dataNum, TimeEvent timeEvent) {
        adapter.update();
    }

    public void addItem(TimeEvent timeEvent) {
        adapter.addItem(timeEvent);
    }

    public TimeEventDataSet getDataSet() {
        return dataSet;
    }

    private void addRangeItem(@NonNull TimeEventRange range, int i){
        View v = getFragment().getLayoutInflater().inflate(R.layout.timeeve_range_container, container, false);
        container.addView(v, container.getChildCount() - 1);

        TimeEventRangeParams params = new TimeEventRangeParams(i);
        ButterKnife.bind(params, v);
        int colorIdC = colorId.get(range.getColorNum());
        params.setColor(colorIdC);
        TimeEventRangeRVAdapter rangeAdapter = new TimeEventRangeRVAdapter(getFragment(), range, getDataNum(), i);
        initRecycler(getFragment().getContext(), params.rv, rangeAdapter);
        rangePairList.add(new Pair<>(params, rangeAdapter));
    }

    void removeRangeItem(int posInList){
        rangePairList.remove(posInList);
        container.removeViewAt(posInList+3);// recycler、"+"ボタン、仕切り線の分を追加
        for (int i = 0; i < rangePairList.size(); i++) {
            rangePairList.get(i).first.posInList = i;
            rangePairList.get(i).second.setPosInList(i);
        }

        dataSet.getRangeList().remove(posInList);
    }

    public void updateRangeTime(TimeEvent timeEvent, int pos, int posInList){
        rangePairList.get(posInList).second.updateTime(timeEvent, pos);
    }

    public void updateRangeValue(TimeEvent timeEvent, int pos, int posInList){
        rangePairList.get(posInList).second.updateValue();
    }

    public void updateRangeColor(TimeEventRange range, int posInList){
        rangePairList.get(posInList).first.setColor(colorId.get(range.getColorNum()));
    }

    private void addRangeToList(@NonNull TimeEventRange range){
        dataSet.getRangeList().add(range);
    }
}
