/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupport3.Fragments.AddFriendFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.OnAddedFriendFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Entity.User;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import static com.cks.hiroyuki2.worksupport3.Util.initAdMob;
import static com.cks.hiroyuki2.worksupprotlib.Util.delimiter;
import static com.cks.hiroyuki2.worksupprotlib.Util.logAnalytics;

@EActivity(R.layout.activity_add_fridend_acitivity)
public class AddFriendActivity extends AppCompatActivity implements PermissionListener{
    
    private static final String TAG = "MANUAL_TAG: " + AddFriendActivity.class.getSimpleName();
    private PermissionListener listener;
    private boolean isSavedInstanceState = false;
    @ViewById(R.id.toolbar) Toolbar toolbar;
    @ViewById(R.id.coordinator) CoordinatorLayout cl;
    @Extra("userList") ArrayList<User> userList;//空でありうる

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            isSavedInstanceState = true;
    }

    @AfterViews
    void onAfterViews(){
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initAdMob(this);
        logAnalytics(TAG + "起動", this);

        listener = new CompositePermissionListener(
                this,

                SnackbarOnDeniedPermissionListener.Builder
                        .with(cl, R.string.permission_rational)
                        .withOpenSettingsButton(R.string.permission_snb_btn)
                        .build()
        );

        if (!isSavedInstanceState){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            AddFriendFragment frag = com.cks.hiroyuki2.worksupport3.Fragments.AddFriendFragment_
                    .builder().build();
            fragmentTransaction.replace(R.id.fragment_container, frag, AddFriendFragment.class.getSimpleName()).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            String contents = result.getContents();
            if(contents == null) {
                Toast.makeText(this, "キャンセルしました", Toast.LENGTH_LONG).show();
            } else if (contents.equalsIgnoreCase("0")) {//0はエラーを表す?? @see https://goo.gl/nKV7Mw
                Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
            } else {
                String[] strings = contents.split(delimiter);
                if (strings.length != 3){//このあたり、関係のないQRでないことを確認してください！！
                    Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
                    return;
                }

                String tag = OnAddedFriendFragment.class.getSimpleName();
                OnAddedFriendFragment frag = com.cks.hiroyuki2.worksupport3.Fragments.OnAddedFriendFragment_
                        .builder()
                        .userUid(strings[0])
                        .name(strings[1])
                        .photoUrl(strings[2])
                        .isNewUser(checkisNewUser(strings[0]))
                        .build();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, frag, tag)
                        .addToBackStack(tag)
                        .commitAllowingStateLoss();//todo これでいいのか検討すること @see https://goo.gl/jOz17J
            }
        }
    }

    public PermissionListener getListener() {
        return listener;
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
        Log.d(TAG, "onPermissionDenied: fire");
       //do nothing
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
        //dailogだすのか？
        Log.d(TAG, "onPermissionRationaleShouldBeShown: fire");
    }

    private boolean checkisNewUser(@NonNull String userUid){
        for (User user: userList)
            if (user.getUserUid().equals(userUid))
                return false;

        return true;
    }
}
