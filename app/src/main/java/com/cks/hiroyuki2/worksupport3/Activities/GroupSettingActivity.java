/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.cks.hiroyuki2.worksupport3.Entity.Group;
import com.cks.hiroyuki2.worksupport3.Fragments.GroupSettingFragment;
import com.cks.hiroyuki2.worksupport3.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import static com.cks.hiroyuki2.worksupport3.Util.initAdMob;

/**
 * {@link GroupSettingFragment} の親。今のところこのActivityが関わるFragmentはこれひとつのみ。
 */
@EActivity(R.layout.activity_group_setting)
public class GroupSettingActivity extends AppCompatActivity {
    private static final String TAG = "MANUAL_TAG: " + GroupSettingActivity.class.getSimpleName();
    @ViewById(R.id.toolbar) Toolbar toolbar;
    @ViewById(R.id.fragment_container) FrameLayout fl;
    @Extra Group group;
    
    @AfterViews
    void afterViews() {
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(R.string.toolbar_title_setting_group);
        initAdMob(this);
        setFragment();
    }
    
    private void setFragment(){
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        GroupSettingFragment fragment = GroupSettingFragment.newInstance(group);
        t.add(R.id.fragment_container, fragment);
        t.commit();
    }
}
