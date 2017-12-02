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

        final FirebaseUser user = Util.getUserMe();
        if (user == null){
            onError(this, "FirebaseAuth.getInstance().getCurrentUser() == null", R.string.error);
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
        /*え？リスナーをセットしないの？と君は言うだろう。だが、friend分枝はサービス側で監視しているんだ。friend分枝が書き込まれたら、サービス側でリスナが走って、UIに更新してくれるんだ。*/
    }
}
