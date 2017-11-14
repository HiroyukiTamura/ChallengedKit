/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;

import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.google.firebase.auth.FirebaseUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRef;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;
import static com.cks.hiroyuki2.worksupprotlib.Util.setImgFromStorage;


@EFragment(R.layout.fragment_on_added_friend)
public class OnAddedFriendFragment extends Fragment {

    private static final String TAG = "MANUAL_TAG: " + OnAddedFriendFragment.class.getSimpleName();

    @FragmentArg()  String name;
    @FragmentArg() String photoUrl;//"null"が代入されうる
    @FragmentArg() String userUid;

    @ViewById(R.id.icon) ImageView iv;
    @ViewById(R.id.name) TextView tv;
    @ViewById(R.id.button) Button btn;

    @AfterViews
    void onAfterViews(){
        setImgFromStorage(photoUrl, iv, R.drawable.ic_face_origin_48dp);
        tv.setText(name);
    }

    @Click(R.id.button)
    void onClickBtn(){
        FirebaseUser user = Util.getUserMe();
        if (user == null){
            onError(this, "FirebaseAuth.getInstance().getCurrentUser() == null", R.string.error);
            if (getActivity() != null)
                getActivity().finish();
            return;
        }

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

        if (getActivity() != null)
            getActivity().finish();
    }
}
