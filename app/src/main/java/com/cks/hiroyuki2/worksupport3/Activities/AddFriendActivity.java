/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupport3.FirebaseConnection;
import com.cks.hiroyuki2.worksupport3.Fragments.AddFriendFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.OnAddedFriendFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import static com.cks.hiroyuki2.worksupportlibrary.Util.initAdMob;
import static com.cks.hiroyuki2.worksupportlibrary.Util.logAnalytics;

@EActivity(R.layout.activity_add_fridend_acitivity)
public class AddFriendActivity extends AppCompatActivity {
    
    private static final String TAG = "MANUAL_TAG: " + AddFriendActivity.class.getSimpleName();
    @ViewById(R.id.toolbar) Toolbar toolbar;

    @AfterViews
    void onAfterViews(){
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initAdMob(this);
        logAnalytics(TAG + "起動", this);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        AddFriendFragment frag = com.cks.hiroyuki2.worksupport3.Fragments.AddFriendFragment_
                .builder().build();
        fragmentTransaction.add(R.id.fragment_container, frag);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            String contents = result.getContents();
            if(contents == null) {
                Toast.makeText(this, "キャンセルしました", Toast.LENGTH_LONG).show();
            } else if (contents.equalsIgnoreCase("0")) {//0はエラーを表す?? @see https://goo.gl/nKV7Mw
                Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
            } else {
                String[] strings = contents.split(FirebaseConnection.delimiter);
                if (strings.length != 3){//このあたり、関係のないQRでないことを確認してください！！
                    Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
                    return;
                }

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                OnAddedFriendFragment frag = com.cks.hiroyuki2.worksupport3.Fragments.OnAddedFriendFragment_
                        .builder()
                        .name(strings[0])
                        .photoUrl(strings[1])
                        .userUid(strings[2])
                        .build();
                fragmentTransaction.replace(R.id.fragment_container, frag);
                fragmentTransaction.commitAllowingStateLoss();//todo これでいいのか検討すること @see https://goo.gl/jOz17J
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
