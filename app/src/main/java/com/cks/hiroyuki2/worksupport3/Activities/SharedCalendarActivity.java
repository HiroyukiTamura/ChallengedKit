/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupprotlib.Entity.Group;
import com.cks.hiroyuki2.worksupport3.Fragments.SharedCalendarFragment;
import com.cks.hiroyuki2.worksupport3.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.cks.hiroyuki2.worksupprotlib.Util.DATE_PATTERN_DOT_YM;
import static com.cks.hiroyuki2.worksupprotlib.Util.cal2date;
import static com.cks.hiroyuki2.worksupprotlib.UtilSpec.getFabLp;
import static com.cks.hiroyuki2.worksupport3.Util.initAdMob;
import static com.cks.hiroyuki2.worksupprotlib.Util.logAnalytics;

/**
 * SharedCalendar系列の長。ひとり子分は{@link SharedCalendarFragment}
 */
@EActivity(R.layout.activity_shared_calendar)
public class SharedCalendarActivity extends AppCompatActivity {
    
    private static final String TAG = "MANUAL_TAG: " + SharedCalendarActivity.class.getSimpleName();
    @ViewById(R.id.fab) FloatingActionButton fab;
    @ViewById(R.id.toolbar) Toolbar toolbar;
    @ViewById(R.id.frame_container) FrameLayout fm;
    @ViewById(R.id.title) TextView toolTitle;
    @ViewById(R.id.mcv_prev) ImageButton mcvPrev;
    @ViewById(R.id.mcv_forward) ImageButton mcvFrw;
    @ViewById(R.id.tool_back) ImageButton backBtn;
    @Extra Group group;

    @AfterViews
    void afterViews() {
        initAdMob(this);
        logAnalytics(TAG + "起動", this);

        fab.setLayoutParams(getFabLp(this));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        SharedCalendarFragment fragment = com.cks.hiroyuki2.worksupport3.Fragments.SharedCalendarFragment_.builder()
                .arg("group", group).build();//要パッケージ名指定。importするとコンパイル通らず @see https://goo.gl/ru1n1x
        transaction.add(R.id.fragment_container, fragment).commit();
    }

    @Click({R.id.mcv_prev, R.id.mcv_forward, R.id.flip, R.id.fab})
    void onClick(View v){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        switch (v.getId()){
            case R.id.mcv_prev:
                ((SharedCalendarFragment)fragment).pagePrevious();
                break;
            case R.id.mcv_forward:
                ((SharedCalendarFragment)fragment).pageForward();
                break;
            case R.id.flip:
                ((SharedCalendarFragment)fragment).toggleCalendar();
                v.getMatrix().setRotate(180);
                break;
            case R.id.fab:
                ((SharedCalendarFragment)fragment).onClickFab();
                break;
        }
    }

    @Click(R.id.tool_back)
    void onClickToolBack(){
        finish();
    }

    public void showPagingPrevBtn(boolean paging){
        if (paging)
            mcvPrev.setVisibility(VISIBLE);
        else
            mcvPrev.setVisibility(INVISIBLE);
    }

    public void showPagingFrwBtn(boolean paging){
        if (paging)
            mcvFrw.setVisibility(VISIBLE);
        else
            mcvFrw.setVisibility(INVISIBLE);
    }

    public void changeToolbarTitle(Calendar cal){
        String title = cal2date(cal, DATE_PATTERN_DOT_YM);
        toolTitle.setText(title);
    }
}
