/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Activities;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.cks.hiroyuki2.worksupport3.Fragments.AddGroupFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Entity.User;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import static com.cks.hiroyuki2.worksupport3.Util.initAdMob;
import static com.cks.hiroyuki2.worksupprotlib.Util.logAnalytics;
import static com.cks.hiroyuki2.worksupprotlib.UtilSpec.getFabLp;

/**
 * ここで取り扱われるfragmentは{@link AddGroupFragment}のみ。
 */
@EActivity(R.layout.activity_add_group)
public class AddGroupActivity extends AppCompatActivity implements AddGroupFragment.IAddGroupFragment{

    private static final String TAG = "MANUAL_TAG: " + AddGroupActivity.class.getSimpleName();
    public static final String INTENT_BUNDLE_GROUP_NAME = "groupName";
    public static final String INTENT_BUNDLE_GROUP_PHOTO_URL = "INTENT_BUNDLE_GROUP_PHOTO_URL";
    static final int DLG_TAG_MK_GROUP_CODE = 85751;
    static final String KEY_PARCELABLE = "KEY_PARCELABLE";

    @ViewById(R.id.toolbar) Toolbar toolbar;
    @ViewById(R.id.fab) FloatingActionButton fab;
    @ViewById(R.id.fragment_container) FrameLayout fl;
    @Extra("userList") ArrayList<User> userList;//空でありうる
    private AddGroupFragment fragment;

    @AfterViews
    void afterView(){
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        logAnalytics(TAG + "起動", this);
        initAdMob(this);

        fab.setLayoutParams(getFabLp(this));
        fab.hide();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = com.cks.hiroyuki2.worksupport3.Fragments.AddGroupFragment_.builder()
                .parcelableArrayListArg("userList", userList).build();//要パッケージ名指定。importするとコンパイル通らず @see https://goo.gl/ru1n1x
        ft.add(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void showFab() {
        fab.show();
    }

    @Override
    public void hideFab() {
        fab.hide();
    }

    @Click(R.id.fab)
    void onClickFab(){
        Intent intent = new Intent();
        intent.putExtra(INTENT_BUNDLE_GROUP_NAME, fragment.groupName);
        intent.putExtra(INTENT_BUNDLE_GROUP_PHOTO_URL, fragment.dlIconUri);
        intent.putParcelableArrayListExtra(KEY_PARCELABLE, (ArrayList<? extends Parcelable>) fragment.userAdapter.getCheckedUsers());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
