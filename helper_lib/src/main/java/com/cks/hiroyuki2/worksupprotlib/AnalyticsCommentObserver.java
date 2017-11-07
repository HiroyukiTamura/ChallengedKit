/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib;

import android.graphics.Rect;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

/**
 * Created by hiroyuki2 on 2017/10/24.
 */

public class AnalyticsCommentObserver implements ViewTreeObserver.OnGlobalLayoutListener{
    private static final String TAG = "MANUAL_TAG: " + AnalyticsCommentObserver.class.getSimpleName();
    private FlowLayout fl;
    private TextView tv;
    private int flHeight;

    AnalyticsCommentObserver(FlowLayout fl, TextView tv){
        this.fl = fl;
        this.tv = tv;
        fl.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        flHeight = fl.getHeight();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(fl.getWidth(), 0);
        lp.weight = 1;
        fl.setLayoutParams(lp);
        tv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                tv.getGlobalVisibleRect(r);
                if (r.height() < tv.getHeight()){
                    Log.d(TAG, "onGlobalLayout: flBottom < tagBottom FL拡張");
                    expandFl();
                }

                tv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        fl.addView(tv);
        fl.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private void expandFl(){
        FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fl.setLayoutParams(lp);
    }
}
