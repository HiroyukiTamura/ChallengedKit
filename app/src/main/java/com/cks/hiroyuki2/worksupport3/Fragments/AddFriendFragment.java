/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cks.hiroyuki2.worksupport3.Activities.AddFriendActivity;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.Util;
import com.example.hiroyuki3.worksupportlibw.Adapters.AddFriendVPAdapter;
import com.google.firebase.auth.FirebaseUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

import static com.cks.hiroyuki2.worksupport3.Util.checkPermission;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRef;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;

/**
 * {@link AddFriendActivity}のひとり子分。
 */
@EFragment(R.layout.fragment_add_friend)
public class AddFriendFragment extends Fragment implements AddFriendVPAdapter.IAddFriendVPAdapter{

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
        tab.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        tab.setupWithViewPager(vp);
    }

    //ただこれだけだからさ、いちいちimplementするほどでもないんだけどさ。fragmentからこういう処理しないと気持ち悪いのよ。
    @Override
    public void onClickCameraButton() {
        checkPermission(getActivity(), ((AddFriendActivity) getActivity()).getListener());/*非同期じゃないから大丈夫*/
    }
}
