/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRef;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;
import static com.cks.hiroyuki2.worksupprotlib.Util.setImgFromStorage;


@EFragment(R.layout.fragment_on_added_friend)
public class OnAddedFriendFragment extends Fragment {

    private static final String TAG = "MANUAL_TAG: " + OnAddedFriendFragment.class.getSimpleName();

    @FragmentArg()  String name;
    @FragmentArg() String photoUrl;//"null"が代入されうる
    @FragmentArg() String userUid;
    @FragmentArg() boolean isNewUser;

    @ViewById(R.id.icon) ImageView iv;
    @ViewById(R.id.name) TextView tv;
    @ViewById(R.id.button) Button btn;
    @ViewById(R.id.error_tv) TextView errTv;

    @AfterViews
    void onAfterViews(){
        setImgFromStorage(photoUrl, iv, R.drawable.ic_face_origin_48dp);
        tv.setText(name);
        if (!isNewUser){
            btn.setVisibility(GONE);
            errTv.setVisibility(VISIBLE);
        }
    }

    @Click(R.id.button)
    void onClickBtn(){
        if (!isNewUser)
            return;

        if (getActivity() != null){
            getActivity().setResult(RESULT_CANCELED);
            getActivity().getIntent()
                    .putExtra("name", name)
                    .putExtra("photoUrl", photoUrl)
                    .putExtra("userUid", userUid);
            getActivity().finish();
        }

//        FirebaseUser user = Util.getUserMe();
//        if (user == null){
//            onError(this, "FirebaseAuth.getInstance().getCurrentUser() == null", R.string.error);
//            if (getActivity() != null){
//                getActivity().setResult(RESULT_CANCELED);
//                getActivity().finish();
//            }
//            return;
//        }
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("/"+ user.getUid() +"/"+ userUid +"/name", name);
//        hashMap.put("/"+ user.getUid() +"/"+ userUid +"/photoUrl", photoUrl);
//        hashMap.put("/"+ userUid + "/" + user.getUid() + "/name", user.getDisplayName());
//        String myPhotoUrl = "null";
//        if (user.getPhotoUrl() != null){
//            myPhotoUrl = user.getPhotoUrl().toString();
//        }
//        hashMap.put("/"+ userUid + "/" + user.getUid() + "/photoUrl", myPhotoUrl);
//        getRef("friend").updateChildren(hashMap);
    }
}
