/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.cks.hiroyuki2.worksupport3.Fragments.AnalyticsFragment;
import com.cks.hiroyuki2.worksupport3.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import static com.cks.hiroyuki2.worksupport3.Util.initAdMob;
import static com.cks.hiroyuki2.worksupprotlib.Util.logAnalytics;

@EActivity(R.layout.activity_shared_data_view_activity)
public class SharedDataViewActivity extends AppCompatActivity implements AnalyticsFragment.OnHamburgerClickListener {

    private static final String TAG = "MANUAL_TAG: " + SharedDataViewActivity.class.getSimpleName();
    public static final String INTENT_KEY_UID = "INTENT_KEY_UID";

    @ViewById(R.id.fragment_container) FrameLayout fm;
    @Extra(INTENT_KEY_UID) String uid;

    @AfterViews
    void afterViews(){
        initAdMob(this);
        logAnalytics(TAG + "起動", this);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        AnalyticsFragment frag = com.cks.hiroyuki2.worksupport3.Fragments.AnalyticsFragment_.builder().uid(uid).isMine(false).build();
        fragmentTransaction.add(R.id.fragment_container, frag).commit();
    }

    @Override
    public void onHamburgerClick() {
        finish();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof AnalyticsFragment){
            if (((AnalyticsFragment) fragment).isSheetVisible()){
                ((AnalyticsFragment) fragment).hideSheet();
                return;
            }
        }

        super.onBackPressed();
    }
}
