/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.RecordVpItems;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.Fragments.EditTemplateFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.cks.hiroyuki2.worksupprotlib.UtilSpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.VISIBLE;
import static com.cks.hiroyuki2.worksupprotlib.Util.delimiter;

/**
 * Created by hiroyuki2 on 2017/09/21.
 */

class TempItemTag {
    private static final String TAG = "MANUAL_TAG: " + TempItemTag.class.getSimpleName();
    @BindView(R.id.tv) TextView tv;
    @BindView(R.id.card_container) CardView cv;
    @BindView(R.id.remove) ImageView removeBtn;
    private View view;
    private int tagNum;
    private String value;
    private int dataNum;
    private EditTemplateFragment frag;

    TempItemTag(int tagNum, String value, int dataNum, EditTemplateFragment frag){
        this.tagNum = tagNum;
        this.value = value;
        this.dataNum = dataNum;
        this.frag = frag;
    }

    View buildView(){
        view = frag.getLayoutInflater().inflate(R.layout.record_vp_item_tagitem, null);
        view.setTag(tagNum);
        ButterKnife.bind(this, view);
        removeBtn.setVisibility(VISIBLE);
        setTextAndColor();
        return view;
    }

    View getView() {
        return view;
    }

    @OnClick(R.id.remove)
    void onClickRemoveTagBtn(){
        frag.onClickRemoveTagBtn(dataNum, tagNum);
    }

    @OnClick(R.id.tv)
    void onClickTv(){
        frag.onClickTag(tagNum, dataNum, value);
    }

    void updateDataNum(int dataNum){
        this.dataNum = dataNum;
        view.setTag(dataNum);
    }

    void updateValue(@NonNull String value){
        this.value = value;
        setTextAndColor();
    }

    private void setTextAndColor(){
        final String[] strings = value.split(delimiter);
        int color = UtilSpec.colorId.get(Integer.parseInt(strings[1]));
        tv.setText(strings[0]);
        cv.setCardBackgroundColor(ContextCompat.getColor(frag.getContext(), color));
    }
}
