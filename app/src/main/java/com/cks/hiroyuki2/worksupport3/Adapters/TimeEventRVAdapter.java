/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Entity.TimeEvent;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cks.hiroyuki2.worksupport3.Adapters.RecordVPAdapter.DATA_NUM;
import static com.cks.hiroyuki2.worksupport3.Adapters.RecordVPAdapter.PAGE_TAG;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickTimePickerDialog;
import static com.cks.hiroyuki2.worksupprotlib.Util.datePattern;
import static com.cks.hiroyuki2.worksupprotlib.Util.cal2date;
import static com.cks.hiroyuki2.worksupprotlib.UtilSpec.colorId;
import static com.cks.hiroyuki2.worksupprotlib.Util.getCalFromTimeEvent;

/**
 * itemにpositionをtagとして張り付けるため、必ず{@link RecyclerView.Adapter#notifyDataSetChanged()}を使用してください。それ以外のnotify系は使わないでください。
 */
public class TimeEventRVAdapter extends RecyclerView.Adapter implements Comparator<TimeEvent>{

    private LayoutInflater inflater;
    private Fragment fragment;
    private List<TimeEvent> list = new LinkedList<>();
    private int dataNum;
    private Calendar cal;
    //region static member
    public static final String DIALOG_TAG_ITEM_CLICK = "DIALOG_TAG_ITEMCLICK";
    public static final String DIALOG_TAG_ITEM_ADD = "DIALOG_TAG_ITEM_ADD";
    public static final String DIALOG_TAG_ITEM_CLICK2 = "DIALOG_TAG_ITEM_CLICK2";
    public static final String DIALOG_TAG_ITEM_ADD2 = "DIALOG_TAG_ITEM_ADD2";
    public static final int CALLBACK_ITEM_CLICK = 1579;
    public static final int CALLBACK_ITEM_ADD = 1580;
    public static final int CALLBACK_ITEM_CLICK2 = 1581;
    public static final int CALLBACK_ITEM_ADD2 = 1582;
    public static final String HOUR = "HOUR";
    public static final String MIN = "MIN";
    public static final String RV_POS = "RV_POS";
    public static final String TIME_EVENT = "TIME_EVENT";
    //endregion

    public TimeEventRVAdapter(@Nullable List<TimeEvent> list, @NonNull Fragment fragment, Calendar cal, int dataNum) {
        this.fragment = fragment;
        this.dataNum = dataNum;
        this.cal = cal;
        inflater = fragment.getLayoutInflater();
        if (list != null)
            this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.circle) ImageView circle;
        @BindView(R.id.time) TextView time;
        @BindView(R.id.value) TextView value;
        @BindView(R.id.remove) ImageButton removeBtn;
        @BindView(R.id.item_ll) View itemLL;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.timeevent_rv_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ButterKnife.bind(this, holder.itemView);

        TimeEvent timeEvent = list.get(position);
        ((ViewHolder)holder).itemLL.setTag(position);
        ((ViewHolder) holder).removeBtn.setTag(position);
        ((ViewHolder)holder).time.setText(timeEvent.getTimeStr());
        ((ViewHolder) holder).value.setText(timeEvent.getName());
        int color = ContextCompat.getColor(fragment.getContext(), colorId.get(timeEvent.getColorNum()));
        ((ViewHolder) holder).circle.setColorFilter(color);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @OnClick(R.id.item_ll)
    void onClickItem(View v){
        int pos = (int)v.getTag();
        Bundle bundle = new Bundle();
        bundle.putInt(DATA_NUM, dataNum);
        bundle.putString(PAGE_TAG, cal2date(cal, datePattern));
        TimeEvent timeEvent = list.get(pos);
        bundle.putSerializable(TIME_EVENT, timeEvent);
        bundle.putInt(RV_POS, pos);
        kickTimePickerDialog(DIALOG_TAG_ITEM_CLICK, CALLBACK_ITEM_CLICK, bundle, fragment);
    }

    @OnClick(R.id.remove)
    void onClickRemoveBtn(View v){
        int pos = (int)v.getTag();
        list.remove(pos);
        notifyDataSetChanged();
        //todo Firebase同期 toast表示を入れること
    }

    public void update(){
        sortList();
        notifyDataSetChanged();
    }

    public void addItem(TimeEvent timeEvent){
        list.add(timeEvent);
        update();
    }

    private void sortList(){
        Collections.sort(list, this);
    }

    @Override
    public int compare(TimeEvent timeEvent, TimeEvent t1) {
        Calendar calendar = getCalFromTimeEvent(timeEvent);
        return calendar.compareTo(getCalFromTimeEvent(t1));
    }
}
