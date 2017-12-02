/*
 * Copyright 2017 Hiroyuki Tamura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cks.hiroyuki2.worksupport3.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.cks.hiroyuki2.worksupport3.Fragments.AnalyticsFragment;
import com.cks.hiroyuki2.worksupport3.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import static com.cks.hiroyuki2.worksupport3.Util.initAdMob;
import static com.cks.hiroyuki2.worksupprotlib.Util.logAnalytics;

@SuppressLint("Registered")
@EActivity(R.layout.activity_shared_data_view_activity)
public class SharedDataViewActivity extends AppCompatActivity implements AnalyticsFragment.OnHamburgerClickListener {

    private static final String TAG = "MANUAL_TAG: " + SharedDataViewActivity.class.getSimpleName();
    public static final String INTENT_KEY_UID = "INTENT_KEY_UID";
    private boolean isSavedInstsancse = false;

    @ViewById(R.id.fragment_container) FrameLayout fm;
    @Extra(INTENT_KEY_UID) String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            isSavedInstsancse = true;
    }

    @AfterViews
    void afterViews(){
        initAdMob(this);
        logAnalytics(TAG + "起動", this);

        if (isSavedInstsancse)
            return;

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        AnalyticsFragment frag = com.cks.hiroyuki2.worksupport3.Fragments.AnalyticsFragment_.builder()
                .uid(uid)
                .isMine(false)
                .build();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
