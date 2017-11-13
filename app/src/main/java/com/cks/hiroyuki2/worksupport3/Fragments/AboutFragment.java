/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.DialogKicker;
import com.cks.hiroyuki2.worksupport3.R;
import com.example.hiroyuki3.worksupportlibw.Adapters.AboutVPAdapter;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickDialogInOnClick;

@EFragment(R.layout.fragment_about2)
public class AboutFragment extends Fragment implements AboutVPAdapter.IAboutVPAdapter{
    private static final String TAG = "MANUAL_TAG: " + AboutFragment.class.getSimpleName();

    private AboutVPAdapter adapter;
    public static final String TAG_LAUNCHER_ICON = "TAG_LAUNCHER_ICON";
    public static final int CALLBACK_LAUNCHER_ICON = 198937;
    @ViewById(R.id.vp) ViewPager vp;
    @ViewById(R.id.tab) TabLayout tab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    public void afterViews(){
        vp.setAdapter(new AboutVPAdapter(getContext(), this));
        tab.setupWithViewPager(vp);
    }

    @Override
    public void onClickLibItem() {
        new LibsBuilder()
                .withLibraries("android-drag-FlowLayout", "autofittextview", "material_calendarview")
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withActivityTitle(getString(R.string.license))
                .withAboutAppName(getString(R.string.app_name))
                .start(getContext());
    }

    @Override
    public void onClickLauncher() {
        Bundle bundle = new Bundle();
        bundle.putString("from", TAG_LAUNCHER_ICON);
        kickDialogInOnClick(TAG_LAUNCHER_ICON, CALLBACK_LAUNCHER_ICON, bundle, this);
    }

    @Override
    public void onClickAppLicense() {

    }

    @Override
    public void onClickAppTos() {

    }

    @Override
    public void onClickResetData() {

    }
}
