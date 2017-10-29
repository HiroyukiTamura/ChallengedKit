/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.RecordVpItems;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.UtilSpec;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by hiroyuki2 on 2017/09/17.
 */

class RecordVpItemTag {
    private View view;
    private Context context;
    @BindView(R.id.tv) TextView tv;
    @BindView(R.id.card_container) CardView cv;

    RecordVpItemTag(Context context){
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.record_vp_item_tagitem, null);
        ButterKnife.bind(this, view);
    }

    View buildView(String string0, String string1){
        int color = UtilSpec.colorId.get(Integer.parseInt(string1));
        tv.setText(string0);
        cv.setCardBackgroundColor(ContextCompat.getColor(context, color));
        return view;
    }

    void setTagVisible(boolean visible){
        if (visible)
            view.setVisibility(VISIBLE);
        else
            view.setVisibility(GONE);
    }
}
