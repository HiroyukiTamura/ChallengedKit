/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.cks.hiroyuki2.worksupprotlib.Entity.Group;
import com.cks.hiroyuki2.worksupport3.Fragments.GroupSettingFragment;
import com.cks.hiroyuki2.worksupport3.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import icepick.Icepick;
import icepick.State;

import static com.cks.hiroyuki2.worksupport3.Util.initAdMob;

/**
 * {@link GroupSettingFragment} の親。今のところこのActivityが関わるFragmentはこれひとつのみ。
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_group_setting)
public class GroupSettingActivity extends AppCompatActivity {
    private static final String TAG = "MANUAL_TAG: " + GroupSettingActivity.class.getSimpleName();
    private boolean isSavedInstance = false;
    @ViewById(R.id.toolbar) Toolbar toolbar;
    @ViewById(R.id.fragment_container) FrameLayout fl;
    @State @Extra Group group;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        isSavedInstance = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @AfterViews
    void afterViews() {
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title_setting_group);
        initAdMob(this);

        if (isSavedInstance){
            setFragment();
        }
    }
    
    private void setFragment(){
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        GroupSettingFragment fragment = GroupSettingFragment.newInstance(group);
        t.add(R.id.fragment_container, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
