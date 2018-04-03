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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Entity.Group;
import com.crashlytics.android.Crashlytics;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.fabric.sdk.android.Fabric;

import static com.cks.hiroyuki2.worksupport3.Util.getStatusBarHeight;
import static com.cks.hiroyuki2.worksupport3.Util.initAdMob;
import static com.cks.hiroyuki2.worksupprotlib.Util.getToolBarHeight;
import static com.cks.hiroyuki2.worksupprotlib.Util.logAnalytics;

@SuppressLint("Registered")
@EActivity(R.layout.app_bar_main)
public class TemplateActivity extends AppCompatActivity {

    @ViewById(R.id.toolbar) Toolbar toolbar;
    @ViewById(R.id.fragment_container) FrameLayout fm;

    @AfterViews
    void onAfterViews(){
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.item4);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initAdMob(this);
        Fabric.with(this, new Crashlytics());
        logAnalytics(this.getClass().getSimpleName() + "起動", this);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null){
            fragment = com.cks.hiroyuki2.worksupport3.Fragments.EditTemplateFragment_
                    .builder()
                    .build();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
