/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import icepick.Icepick;
import icepick.State;

import static com.cks.hiroyuki2.worksupport3.Fragments.SocialFragment.REQ_CODE_CREATE_GROUP;
import static com.cks.hiroyuki2.worksupport3.Util.initAdMob;
import static com.cks.hiroyuki2.worksupprotlib.Util.logAnalytics;
import static com.cks.hiroyuki2.worksupprotlib.UtilSpec.getFabLp;

/**
 * ここで取り扱われるfragmentは{@link AddGroupFragment}のみ。
 * todo 画面回転
 * {@link com.cks.hiroyuki2.worksupport3.Fragments.GroupSettingFragment}と{@link com.cks.hiroyuki2.worksupport3.Fragments.SocialFragment}から呼ばれる。
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_add_group)
public class AddGroupActivity extends AppCompatActivity implements AddGroupFragment.IAddGroupFragment{

    private static final String TAG = "MANUAL_TAG: " + AddGroupActivity.class.getSimpleName();
    public static final String INTENT_BUNDLE_GROUP_NAME = "groupName";
    public static final String INTENT_BUNDLE_GROUP_PHOTO_URL = "INTENT_BUNDLE_GROUP_PHOTO_URL";
//    static final int DLG_TAG_MK_GROUP_CODE = 85751;
    public static final String KEY_PARCELABLE = "KEY_PARCELABLE";
    public static final int REQ_CODE_ADD_GROUP_MEMBER = 5438;

    @ViewById(R.id.toolbar) Toolbar toolbar;
    @ViewById(R.id.fab) FloatingActionButton fab;
    @ViewById(R.id.fragment_container) FrameLayout fl;
    @State @Extra ArrayList<User> userList;//空でありうる
    @State @Extra int requestCode;
    private AddGroupFragment fragment;
    private boolean isSavedInstance = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        if (savedInstanceState != null){
            isSavedInstance = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @AfterViews
    void afterView(){
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switch (requestCode){
            case REQ_CODE_CREATE_GROUP:
                toolbar.setTitle(R.string.title_add_group);
                break;
            case REQ_CODE_ADD_GROUP_MEMBER:
                toolbar.setTitle(R.string.add_member_title);
                break;
        }

        logAnalytics(TAG + "起動", this);
        initAdMob(this);

        fab.setLayoutParams(getFabLp(this));
        fab.hide();

        if (isSavedInstance)
            return;

        addFragment();
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
        switch (requestCode){
            case REQ_CODE_CREATE_GROUP:
                intent.putExtra(INTENT_BUNDLE_GROUP_NAME, fragment.groupName);
                intent.putExtra(INTENT_BUNDLE_GROUP_PHOTO_URL, fragment.dlIconUri);
                break;
            case REQ_CODE_ADD_GROUP_MEMBER:
                break;
        }
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

    private void addFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = com.cks.hiroyuki2.worksupport3.Fragments.AddGroupFragment_
                .builder()
                .userList(userList)
                .requestCode(requestCode)
                .build();//要パッケージ名指定。importするとコンパイル通らず @see https://goo.gl/ru1n1x
        ft.add(R.id.fragment_container, fragment).commit();
    }
}
