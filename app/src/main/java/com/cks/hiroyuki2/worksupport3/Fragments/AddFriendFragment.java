/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cks.hiroyuki2.worksupport3.Activities.AddFriendActivity;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.Util;
import com.example.hiroyuki3.worksupportlibw.Adapters.AddFriendVPAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

import static android.app.Activity.RESULT_CANCELED;
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
        // TODO: 2017/11/16 テスト解除時コードを戻すこと
        final FirebaseUser user = com.cks.hiroyuki2.worksupprotlib.Util.getUserMe();
        if (user == null){
            onError(this, "FirebaseAuth.getInstance().getCurrentUser() == null", R.string.error);
            return;
        }
        String userUid = "q1Ov1EZ8DYQ4yS2tpaBHe5VmuYx2";
        String name = "hiroyukiSubTest";
        String photoUrl = "null";

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("/"+ user.getUid() +"/"+ userUid +"/name", name);
        hashMap.put("/"+ user.getUid() +"/"+ userUid +"/photoUrl", photoUrl);
        hashMap.put("/"+ userUid + "/" + user.getUid() + "/name", user.getDisplayName());
        String myPhotoUrl = "null";
        if (user.getPhotoUrl() != null){
            myPhotoUrl = user.getPhotoUrl().toString();
        }
        hashMap.put("/"+ userUid + "/" + user.getUid() + "/photoUrl", myPhotoUrl);
        getRef("friend").updateChildren(hashMap);

        /*
        Util.checkPermission(getActivity(), ((AddFriendActivity) getActivity()).getListener());/*非同期じゃないから大丈夫*/
    }
}
