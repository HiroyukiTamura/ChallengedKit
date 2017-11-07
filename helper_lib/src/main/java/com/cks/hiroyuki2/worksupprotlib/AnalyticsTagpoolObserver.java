/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import org.apmem.tools.layouts.FlowLayout;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hiroyuki2 on 2017/10/24.
 */
public class AnalyticsTagpoolObserver {
    private static final String TAG = "MANUAL_TAG: " + AnalyticsTagpoolObserver.class.getSimpleName();
    private FlowLayout fl;
    private float flBottom;
    private float currentFlWidth;
    private List<View> tagList = new LinkedList<>();
    final static String VIEW = "VIEW";
    final static String POSITION = "POSITION";
    final static String TAG_BOTTOM = "TAG_BOTTOM";

    AnalyticsTagpoolObserver(final FlowLayout fl, final List<View> tagList){
        this.fl = fl;
        this.tagList = tagList;
        fl.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                flBottom = fl.getHeight() + fl.getY();
                currentFlWidth = fl.getWidth();
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)currentFlWidth, 0);
                lp.weight = 1;
                fl.setLayoutParams(lp);

                for (final View tag: tagList) {
                    tag.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            Rect r = new Rect();
                            tag.getGlobalVisibleRect(r);
                            Log.d(TAG, "onGlobalLayout: " + r.toString());
                            if (r.height() < tag.getHeight()){
                                Log.d(TAG, "onGlobalLayout: flBottom < tagBottom FL拡張");
                                expandFl();
                            }

                            tag.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
                    fl.addView(tag);

                }

                fl.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void expandFl(){
        currentFlWidth = currentFlWidth + 200;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)currentFlWidth, 0);
        lp.weight = 1;
        fl.setLayoutParams(lp);

        for (final View tag: tagList) {
            tag.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    boolean b = tag.getGlobalVisibleRect(r);
                    Log.d(TAG, "onGlobalLayout: " + b);
                    if (r.height() < tag.getHeight()){
                        Log.d(TAG, "onGlobalLayout: flBottom < tagBottom FL拡張");
                        expandFl();
                    }

                    tag.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }
}
