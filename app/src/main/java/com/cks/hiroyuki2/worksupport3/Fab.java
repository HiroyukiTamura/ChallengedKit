package com.cks.hiroyuki2.worksupport3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import com.gordonwong.materialsheetfab.AnimatedFab;

/**
 * Created by hiroyuki2 on 2017/11/04.
 */

public class Fab extends FloatingActionButton implements AnimatedFab {

    Fab(@NonNull Context context){
        super(context);
    }

    Fab(@NonNull Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    @Override
    public void show() {
        show(0, 0);
    }

    @Override
    public void show(float v, float v1) {
        setVisibility(VISIBLE);
    }

    @Override
    public void hide() {
        setVisibility(INVISIBLE);
    }
}
