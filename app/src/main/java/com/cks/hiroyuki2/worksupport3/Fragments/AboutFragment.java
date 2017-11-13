/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.cks.hiroyuki2.worksupport3.R;
import com.example.hiroyuki3.worksupportlibw.Adapters.AboutVPAdapter;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickDialogInOnClick;
import static com.cks.hiroyuki2.worksupport3.AboutDialogFragment.CALLBACK_LAUNCHER_ICON;
import static com.cks.hiroyuki2.worksupport3.AboutDialogFragment.CALLCACK_IMG;
import static com.cks.hiroyuki2.worksupport3.AboutDialogFragment.TAG_IMG;
import static com.cks.hiroyuki2.worksupport3.AboutDialogFragment.TAG_LAUNCHER_ICON;

@EFragment(R.layout.fragment_about2)
public class AboutFragment extends Fragment implements AboutVPAdapter.IAboutVPAdapter{
    private static final String TAG = "MANUAL_TAG: " + AboutFragment.class.getSimpleName();
    private static final String TOS_URL = "http://freqmodu874.hatenadiary.com/entry/2017/08/21/023601";

    private AboutVPAdapter adapter;
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
        kickDialogInOnClick(TAG_LAUNCHER_ICON, CALLBACK_LAUNCHER_ICON, new Bundle(), this);
    }

    @Override
    public void onClickAppLicense() {
        kickDialogInOnClick(TAG_IMG, CALLCACK_IMG, new Bundle(), this);
    }

    @Override
    public void onClickAppTos() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TOS_URL));
        startActivity(intent);
    }

    @Override
    public void onClickResetData() {

    }
}
