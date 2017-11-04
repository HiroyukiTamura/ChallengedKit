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
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.Adapters.AboutVPAdapter;
import com.cks.hiroyuki2.worksupport3.R;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

@EFragment(R.layout.fragment_about2)
public class AboutFragment extends Fragment {
    private AboutVPAdapter adapter;
    @ViewById(R.id.vp) ViewPager vp;
    @ViewById(R.id.tab) TabLayout tab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    public void afterViews(){
        vp.setAdapter(new AboutVPAdapter(getContext()));
        tab.setupWithViewPager(vp);
    }

//    @Click(R.id.mail)
//    void onClickMail(){
//        Intent intent = new Intent(Intent.ACTION_SENDTO);
//        intent.putExtra(Intent.EXTRA_EMAIL, devAddress);
//        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//            startActivity(intent);
//        }
//    }
//
//    @Click(R.id.card1)
//    void onClickCard1(){
//        new LibsBuilder()
//                .withLibraries("android-drag-FlowLayout", "autofittextview", "material_calendarview")
//                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
//                .withActivityTitle(getString(R.string.license))
//                .start(getContext());
//    }
}
