/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.cks.hiroyuki2.worksupprotlib.Entity.TimeEvent;
import com.cks.hiroyuki2.worksupprotlib.Entity.TimeEventRange;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cks.hiroyuki2.worksupport3.Adapters.RecordVPAdapter.DATA_NUM;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.TIME_EVENT;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickInputDialog;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickTimePickerDialog;

/**
 * Created by hiroyuki2 on 2017/10/14.
 */
public class TimeEventRangeRVAdapter extends RecyclerView.Adapter {

    private TimeEventRange range;
    private LayoutInflater inflater;
    public static final String DIALOG_TAG_RANGE_CLICK_TIME = "DIALOG_TAG_RANGE_CLICK";
    public static final int CALLBACK_RANGE_CLICK_TIME = 2049;
    public static final String DIALOG_TAG_RANGE_CLICK_VALUE ="DIALOG_TAG_RANGE_CLICK_VALUE";
    public static final int CALLBACK_RANGE_CLICK_VALUE = 2050;
    private Fragment fragment;
    private int dataNum;
    private int posInList;
    public static final String POSITION = "POSITION";
    public static final String POS_IN_LIST = "POS_IN_LIST";

    public TimeEventRangeRVAdapter(Fragment fragment, @NonNull TimeEventRange range, int dataNum, int posInList){
        inflater = fragment.getLayoutInflater();
        this.range = range;
        this.fragment = fragment;
        this.dataNum = dataNum;
        this.posInList = posInList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.time) TextView time;
        @BindView(R.id.value) TextView value;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.timeevent_range_rv_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ButterKnife.bind(this, holder.itemView);
        TimeEvent timeEvent = range.getTimeEve(position);
        ((ViewHolder) holder).time.setText(timeEvent.getTimeStr());
        ((ViewHolder) holder).time.setTag(position);
        String value = timeEvent.getName() + Util.getStrOffset(fragment.getContext(), timeEvent);
        ((ViewHolder) holder).value.setText(value);
        ((ViewHolder) holder).value.setTag(position);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @OnClick({R.id.value, R.id.time})
    void onClickValue(View v){
        int pos = (int)v.getTag();
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION, pos);
        bundle.putInt(DATA_NUM, dataNum);
        bundle.putInt(POS_IN_LIST, posInList);
        bundle.putSerializable(TIME_EVENT, range.getTimeEve(pos));
        switch (v.getId()){
            case R.id.value:
                kickInputDialog(bundle, DIALOG_TAG_RANGE_CLICK_VALUE, CALLBACK_RANGE_CLICK_VALUE, fragment);
                break;
            case R.id.time:
                kickTimePickerDialog(DIALOG_TAG_RANGE_CLICK_TIME, CALLBACK_RANGE_CLICK_TIME, bundle, fragment);
                break;
        }
    }

    public void setPosInList(int posInList){
        this.posInList = posInList;
    }

    public void updateTime(TimeEvent timeEvent, int pos){
//        range.sort();
        notifyDataSetChanged();
    }

    public void updateValue(){
        notifyDataSetChanged();
    }
}
