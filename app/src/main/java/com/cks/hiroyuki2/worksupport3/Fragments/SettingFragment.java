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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.Activities.MainActivity;
import com.cks.hiroyuki2.worksupport3.DialogFragments.SettingDialogFragment;
import com.cks.hiroyuki2.worksupport3.FbIntentService_;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.RxBus;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle2.components.support.RxFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.functions.Consumer;

import static com.cks.hiroyuki2.worksupprotlib.Util.getTextNullable;
import static com.cks.hiroyuki2.worksupprotlib.Util.getUserMe;
import static com.cks.hiroyuki2.worksupprotlib.Util.kickIntentIcon;
import static com.cks.hiroyuki2.worksupprotlib.Util.logStackTrace;
import static com.cks.hiroyuki2.worksupprotlib.Util.setImgFromStorage;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickSettingDialog;
import static com.cks.hiroyuki2.worksupprotlib.Util.toastNullable;

/**
 * プロフィールクリックで発動するよ！
 */
@EFragment(R.layout.fragment_setting_fragment2)
public class SettingFragment extends RxFragment implements OnFailureListener, Callback {

    //region Dialog周りの定数
    public static final String INTENT_KEY_ISSUCCESS = "IKI";
    public static final String INTENT_KEY_METHOD = "IKM";
    public static final String INTENT_KEY_NEW_PARAM = "INTENT_KEY_NEW_PARAM";
    public static final String ACCOUNT_NAME = "ACCOUNT_NAME";
    //endregion

    private static final String TAG = "MANUAL_TAG: " + SettingFragment.class.getSimpleName();
    private MainActivity mainActivity;
    public static final int REQ_CODE_MY_ICON_CHANGE = 1892;

    @ViewById(R.id.icon) CircleImageView iconIv;
    @ViewById(R.id.name) TextView nameTv;
    @ViewById(R.id.email) TextView mailTv;
    @ViewById(R.id.icon_fl) FrameLayout fl;
    @ViewById(R.id.password) TextView pwTv;
    @org.androidannotations.annotations.res.StringRes(R.string.non_set) String errMsg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RxBus.subscribe(RxBus.UPDATE_PROF_NAME_SUCCESS, this, newName -> {
            toastNullable(getContext(), R.string.success_name);
            nameTv.setText((String) newName);
        });

        RxBus.subscribe(RxBus.UPDATE_PROF_NAME_SUCCESS, this, uri -> Picasso.with(getContext())
            .load((Uri)uri)
            .into(iconIv, SettingFragment.this));
    }

    @Override
    public void onAttach(Context context) {
        mainActivity = (MainActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        mainActivity = null;
        super.onDetach();
    }

    @AfterViews
    void onAfterViews(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            toastNullable(getContext(), R.string.error);
            return;
        }

        nameTv.setText(getTextNullable(user.getDisplayName(), errMsg));
        mailTv.setText(getTextNullable(user.getEmail(), errMsg));
        setImgFromStorage(user, iconIv, R.drawable.ic_face_white_48dp);
    }

    @Click(R.id.icon_fl)
    void onClickIcon(){
        kickIntentIcon(this, REQ_CODE_MY_ICON_CHANGE);
    }

    @Click(R.id.edit_name)
    void editName(){
        kickSettingDialog(SettingDialogFragment.DIALOG_TAG_NAME, SettingDialogFragment.METHOD_ACCOUNT_NAME, this);
    }

    @Click(R.id.edit_mail)
    void editEmail(){
        kickSettingDialog(SettingDialogFragment.DIALOG_TAG_EMAIL, SettingDialogFragment.METHOD_UPDATE_EMAIL, this);
    }

    @Click(R.id.pw_edit)
    void editPw(){
        kickSettingDialog(SettingDialogFragment.DIALOG_TAG_PW, SettingDialogFragment.METHOD_UPDATE_PW, this);
    }

    @OnActivityResult(REQ_CODE_MY_ICON_CHANGE)
    void onResultChangeIcon(Intent data, int resultCode){
        if (resultCode != Activity.RESULT_OK)
            return;

        FbIntentService_.intent(getContext().getApplicationContext())
                .updateProfIcon(data.getData())
                .start();

//        new SettingFbCommunicator(this, data, SCHEME_PHOTO_URL) {
//            @Override
//            public void onSuccess(String uri) {
//                Picasso.with(getContext())
//                        .load(uri)
//                        .into(iconIv, new Callback() {
//                            @Override
//                            public void onSuccess() {
//                                toastNullable(getContext(), R.string.update_success_msg);
//                            }
//
//                            @Override
//                            public void onError() {
//                                Log.d(TAG, "onError: picasso");
//                            }
//                        });
//
//                if (mainActivity != null)
//                    mainActivity.getLoginCheck().writeLocalProf();
//            }
//        }.uploadIcon();
    }

    @OnActivityResult(SettingDialogFragment.DIALOG_CALLBACK)
    void onResultDialog(Intent data, int resultCode){
        if (resultCode != Activity.RESULT_OK) return;

        FirebaseUser user = getUserMe();
        if (user == null){
            toastNullable(getContext(), R.string.error);
            return;
        }

        final String newParam = data.getStringExtra(INTENT_KEY_NEW_PARAM);
        switch (data.getIntExtra(INTENT_KEY_METHOD, 100)){
            case SettingDialogFragment.METHOD_REAUTH:
                String email = user.getEmail();
                if (email == null){
                    toastNullable(getContext(), R.string.error);
                    return;
                }
                reAuthenticate(user, email, newParam);
                kickSettingDialog(SettingDialogFragment.DIALOG_TAG_PW, SettingDialogFragment.METHOD_UPDATE_PW, this);
                break;
            case SettingDialogFragment.METHOD_UPDATE_PW:
                updatePassWord(user, newParam);
                break;
            case SettingDialogFragment.METHOD_ACCOUNT_NAME:
                String newMyName = data.getStringExtra(INTENT_KEY_NEW_PARAM);
                updateProfName(newMyName);
//                new SettingFbCommunicator(this, data, SettingFbCommunicator.SCHEME_NAME){
//                    @Override
//                    public void onSuccess(String param) {
//                        //ここでUriはnullである
//                        toastNullable(getContext(), R.string.success_name);
//                        nameTv.setText(param);
//                        if (mainActivity != null)
//                            mainActivity.getLoginCheck().writeLocalProf();
//                    }
//                }.updateProfName();
                break;
            case SettingDialogFragment.METHOD_UPDATE_EMAIL:
                upDateEmail(user, newParam);
                break;
        }
    }

    /**
     * Db修正が行われると、今度はCloudFunctionでリスナが走り、必要箇所に対してDB更新を行います。
     */
    private void updateProfName(String newMyName){
        FbIntentService_.intent(getContext().getApplicationContext())
                .updateProfName(newMyName)
                .start();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        logStackTrace(e);
        Util.onError(this, "なんだこれは。", R.string.error);
    }

    //region picasso callback
    @Override
    public void onSuccess() {
        toastNullable(getContext(), R.string.update_success_msg);
    }

    @Override
    public void onError() {
        Log.d(TAG, "onError: picasso");
    }
    //endregion

    //region FireBaseパスワード問い合わせ系
    //////////////////////////////FireBaseパスワード問い合わせ系 onClick配属 ここから///////////////////////////
    /** 再認証をおこなうおっさん！*/
    private void reAuthenticate(@NonNull FirebaseUser user, @NonNull final String email, @NonNull String pw){
        AuthCredential credential = EmailAuthProvider.getCredential(email, pw);
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "User re-authenticated.");
                    if (getActivity() != null)
                        kickSettingDialog(SettingDialogFragment.DIALOG_TAG_PW, SettingDialogFragment.METHOD_UPDATE_PW, SettingFragment.this);
                });
    }

    /** パスワードを再設定するおっさん*/
    private void updatePassWord(@NonNull FirebaseUser user, @NonNull final String pw){
        user.updatePassword(pw)
                .addOnCompleteListener(task -> toastNullable(getContext(), R.string.success_pw));
    }

    /** メールアドレスを再設定するおっさん*/
    private void upDateEmail(@NonNull FirebaseUser user, @NonNull final String email){
        user.updateEmail(email)
                .addOnCompleteListener(task -> {
                    if (mailTv != null)
                        mailTv.setText(email);
                    toastNullable(getContext(), R.string.update_success_mail);
                });
    }
    //////////////////////////////FireBaseパスワード問い合わせ系 onClick配属 ここまで///////////////////////////
    //endregion
}
