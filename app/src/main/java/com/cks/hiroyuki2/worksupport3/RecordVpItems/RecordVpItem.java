/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.RecordVpItems;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.cks.hiroyuki2.worksupport3.RecordData;

import java.util.Calendar;

/**
 * Created by hiroyuki2 on 2017/09/17.
 */

public abstract class RecordVpItem {

    private RecordData data;
    private int dataNum;
    private Fragment fragment;
    private Calendar cal;

    public RecordVpItem(@NonNull RecordData data, int dataNum, @Nullable Calendar cal, @NonNull Fragment fragment){
        this.data = data;
        this.dataNum = dataNum;
        this.fragment = fragment;
        this.cal = cal;
    }

    public int getDataNum() {
        return dataNum;
    }

    public RecordData getData() {
        return data;
    }

    @Nullable
    public Calendar getCal() {
        return cal;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setData(RecordData data) {
        this.data = data;
    }

    public abstract View buildView();
}
