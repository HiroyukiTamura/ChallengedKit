/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.github.sumimakito.awesomeqr.AwesomeQRCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.cks.hiroyuki2.worksupportlibrary.Util.PREF_KEY_PROF;
import static com.cks.hiroyuki2.worksupportlibrary.Util.PREF_NAME;
import static com.cks.hiroyuki2.worksupportlibrary.Util.QR_FILE_NAME;
import static com.cks.hiroyuki2.worksupportlibrary.Util.RC_SIGN_IN;
import static com.cks.hiroyuki2.worksupportlibrary.Util.delimiter;
import static com.cks.hiroyuki2.worksupportlibrary.Util.logStackTrace;

/**
 * アプリ起動時に、ログインしているかどうかとかそこらへんを担ってくれるおじさん！
 */

public class LoginCheck {
    private static final String TAG = "MANUAL_TAG: " + LoginCheck.class.getSimpleName();

    private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
    private static final String FIREBASE_TOS_URL = "https://firebase.google.com/terms/";
    private static final String GOOGLE_PRIVACY_POLICY_URL = "https://www.google.com/policies/privacy/";
    private static final String FIREBASE_PRIVACY_POLICY_URL = "https://firebase.google.com/terms/analytics/#7_privacy";
    public static final String IS_PRE_SETTING = "IS_PRE_SETTING";
    private Context context;
    private SharedPreferences pref;

    public LoginCheck(Context context){
        Log.d(TAG, "LoginCheck: fire");
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }

    public static boolean checkIsLogin() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        return auth.getCurrentUser() != null;
    }

    public void signIn(boolean isPreSetting, @DrawableRes int logoRes) {
        Log.d(TAG, "signIn: fire");
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
//                        .setTheme(AuthUI.getDefaultTheme())
                .setLogo(logoRes)
                .setAvailableProviders(getProviderList())//最新バージョンではメソッド名変更
                .setTosUrl(GOOGLE_TOS_URL)
                .setPrivacyPolicyUrl(GOOGLE_PRIVACY_POLICY_URL)//最新バージョンではメソッドが存在
//                        .setIsSmartLockEnabled(mEnableCredentialSelector.isChecked(), mEnableHintSelector.isChecked())//最新バージョンでは引数変更
                .setIsSmartLockEnabled(false)
                .setAllowNewEmailAccounts(true)
                .build();

        intent.putExtra(IS_PRE_SETTING, isPreSetting);

        ((Activity)context).startActivityForResult(intent, RC_SIGN_IN);
    }

    @Contract(" -> !null")
    @NonNull
    private List<AuthUI.IdpConfig> getProviderList(){
        return Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());
//                new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build());
    }

    public void writeLocalProf(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            return;
        String jsonUser = new Gson().toJson(user);
        String s = pref.getString(PREF_KEY_PROF, "");
        if (jsonUser.equals(s))
            return;

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_KEY_PROF, jsonUser);
        editor.apply();
        writeStringToPref(PREF_KEY_PROF, jsonUser);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String string = user.getUid() + delimiter
                        + user.getDisplayName() + delimiter
                        + user.getPhotoUrl();
                Bitmap bitmap = new AwesomeQRCode.Renderer()
                        .contents(string)
                        .size(800).margin(20)
                        .dotScale(1.0f)
                        .render();

                File file = new File(context.getFilesDir(), QR_FILE_NAME);
                try {
                    FileOutputStream outStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.close();
                } catch (IOException e) {
                    logStackTrace(e);
                    writeStringToPref(PREF_KEY_PROF, "");
                }
            }
        }).start();
    }

    private void writeStringToPref(String key, String value){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
