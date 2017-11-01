/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.cks.hiroyuki2.worksupport3.Activities.AddFriendActivity;
import com.cks.hiroyuki2.worksupport3.Adapters.AddFriendVPAdapter;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.Util;
import com.google.zxing.integration.android.IntentIntegrator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * {@link AddFriendActivity}のひとり子分。
 */
@EFragment(R.layout.fragment_add_friend)
public class AddFriendFragment extends Fragment {

    private static final String TAG = "MANUAL_TAG: " + AddFriendFragment.class.getSimpleName();
    private AddFriendVPAdapter adapter;
    @ViewById(R.id.tab) TabLayout tab;
    @ViewById(R.id.vp) ViewPager vp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void onAfterViews(){
        adapter = new AddFriendVPAdapter(getContext(), this);
        vp.setAdapter(adapter);
        tab.setupWithViewPager(vp);
    }

    public void checkPermission(){
        Util.checkPermission(getActivity(), ((AddFriendActivity) getActivity()).getListener());
    }

    public void onPermissionAdmitted(){
        if (getActivity() == null)
            return;

        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }
}
