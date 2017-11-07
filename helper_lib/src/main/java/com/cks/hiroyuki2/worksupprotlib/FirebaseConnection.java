/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cks.hiroyuki2.worksupportlib.R;
import com.cks.hiroyuki2.worksupprotlib.Entity.RecordData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.Contract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.cks.hiroyuki2.worksupprotlib.Util.DATE_PATTERN_YM;
import static com.cks.hiroyuki2.worksupprotlib.Util.DEFAULT;
import static com.cks.hiroyuki2.worksupprotlib.Util.cal2date;
import static com.cks.hiroyuki2.worksupprotlib.Util.datePattern;
import static com.cks.hiroyuki2.worksupprotlib.Util.makeScheme;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;

/**
 * Firebaseとやりとりする係のおじさん！
 */
public class FirebaseConnection implements GoogleApiClient.OnConnectionFailedListener, DatabaseReference.CompletionListener {
    private static final String TAG = "MANUAL_TAG: " + FirebaseConnection.class.getSimpleName();
//    private GoogleApiClient mGoogleApiClient;
//    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private String userId;
    private ChildEventListener childtListener;
    private static FirebaseConnection firebase = new FirebaseConnection();
//    public static final String datePattern = "yyyyMMdd";
//    public static final String delimiter = "9mVSv";
//    public static final String delimiterOfNum = ",";
    public DatabaseReference userRecDir;
    public DatabaseReference userAttrDir;
    public DatabaseReference userParamSeries;
    private int annonymousePw;

    public static FirebaseConnection getInstance(){
        Log.d(TAG, "getInstance: fire");
        return firebase;
    }

    FirebaseConnection(){
        Log.d(TAG, "FirebaseConnection: constructor");
    }

//    void initFirebaseConnection(Context context){
//        this.context = context;
//
////        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(context.getResources().getString(R.string.firebase_client_id))
//                .requestEmail()
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(context)
//                .enableAutoManage((FragmentActivity)context /* FragmentActivity */, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
//
//        mAuth = FirebaseAuth.getInstance();
//
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//            }
//        };
//
//        setChildListener();
//    }

//    void addAuthListener(){
//        mAuth.addAuthStateListener(mAuthListener);
//    }

//    void removeAuthListener(){
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }

//    Intent makeSignInIntent(){
//        return Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//    }

//    void firebaseAuthWithGoogle(final GoogleSignInAccount acct, final Context context) {
//        userId = acct.getId();
//        Log.d(TAG, "firebaseAuthWithGoogle:" + userId);
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        mAuth.signInWithCredential(credential).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
//                        // If sign in fails, display a message to the user. If sign in succeeds
//                        // the auth state listener will be notified and logic to handle the
//                        // signed in user can be handled in the listener.
//                        if (!task.isSuccessful() || userId == null) {
//                            Log.w(TAG, "signInWithCredential Exe or userId==null?", task.getException());
//                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        setFireBaseRefs();
//                    }
//                });
//    }

    public void setFireBaseRefs(Context context){
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userRecDir = mDatabase.child("usersParam").child(userId);
//        userRecDir.addChildEventListener(childtListener);
//                        userRecDir.keepSynced(true);
        userAttrDir = mDatabase.child("userData").child(userId);
//        userAttrDir.addChildEventListener(childtListener);
//                        userAttrDir.keepSynced(true);
        userParamSeries = mDatabase.child("userParamSeries").child(userId);
//                        userParamSeries.keepSynced(true);
//        if (!getInternetState(context)){
//            Log.d(TAG, "setFireBaseRefs() called with: context = [" + context + "]");
//            return;
//        }

        setListenerForInitNode(context);
    }

    private boolean getInternetState(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void setListenerForInitNode(final Context context){
        final ValueEventListener listener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: fire");
                if (!dataSnapshot.exists()){
                    initNode();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: えらーーー！！");
                onError(context, "onCancelled: " + databaseError.getMessage(), R.string.error);
            }
        };

        userAttrDir.addListenerForSingleValueEvent(listener);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                userAttrDir.removeEventListener(listener);
//                Log.d(TAG, "run() called");
//                //UIに通知
//            }
//        }, 3000);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
    }

//    void setChildListener(){
//        childtListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Log.d(TAG, "onChildAdded: " + s);
////                if (!dataSnapshot.exists()) return;
////
////                if (dataSnapshot.getKey().equals("friend"))
////                    FriendJsonEditor.dataSnap2JsonFile(context, dataSnapshot);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Log.d(TAG, "onChildChanged: " + s);
//
////                if (!dataSnapshot.exists()) return;
////
////                if (dataSnapshot.getKey().equals("friend"))
////                    FriendJsonEditor.dataSnap2JsonFile(context, dataSnapshot);
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onChildRemoved: " + dataSnapshot.getKey());
//
////                if (!dataSnapshot.exists()) return;
////
////                if (dataSnapshot.getKey().equals("friend"))
////                    FriendJsonEditor.dataSnap2JsonFile(context, dataSnapshot);
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                Log.d(TAG, "onChildMoved: " + s);
//
////                if (!dataSnapshot.exists()) return;
////
////                if (dataSnapshot.getKey().equals("friend"))
////                    FriendJsonEditor.dataSnap2JsonFile(context, dataSnapshot);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w(TAG, "onCancelled: " + databaseError.getMessage());
//            }
//        };
//    }

    /* *********************ここから匿名認証********************************/

//    void setmAuth(@NonNull Context context){
//        mAuth = FirebaseAuth.getInstance();
//        this.context = context;
//    }

//    void signInAnonymously() {
////        showProgressDialog();
//        // [START signin_anonymously]
//
//        mAuth.signInAnonymously().addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInAnonymously:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
////                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInAnonymously:failure", task.getException());
//                            Toast.makeText(context, "ログイン失敗 failed.", Toast.LENGTH_SHORT).show();
////                            updateUI(null);
//                        }
//
//                        // [START_EXCLUDE]
////                        hideProgressDialog();
//                        // [END_EXCLUDE]
//                    }
//                });
//        // [END signin_anonymously]
//    }

    /**
     * listenerをimplementしてもいんだけど、onAttachやonDetach周りの処理の関係で、一応newしています。
     */
//    void setmAuthListener(){
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                    userId = user.getUid();
////                    setFireBaseRefs();
//                } else {
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//            }
//        };
//    }

    /* *********************ここまで匿名認証********************************/


    /* **********************簡素な匿名認証**********************************/
//    void setUserId(Context context){
//        SharedPreferences pref = context.getSharedPreferences(Util.PREF_NAME, Context.MODE_PRIVATE);
//        int annonymousePw = pref.getInt(Util.PREF_KEY_PW, 0);
//        if (annonymousePw == 0){
//            Random random = new Random();
//            annonymousePw = Math.abs(random.nextInt());
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putInt(Util.PREF_KEY_PW, annonymousePw);
//            editor.apply();
//        }
//        userId = String.valueOf(annonymousePw);
//        Log.d(TAG, "setUserId: userId" + userId);
//    }
    /* **********************ここまで簡素な匿名認証**********************************/

//    void checkIsLogin(){
//        mAuth = FirebaseAuth.getInstance();
//        if (mAuth.getCurrentUser() != null) {
//            Log.d(TAG, "checkIsLogin: 既にログインしている");
//        } else {
//            startActivityForResult(
//                    AuthUI.getInstance().createSignInIntentBuilder()
//                            .setTheme(AuthUI.getDefaultTheme())
//                            .setLogo(R.drawable.tw__composer_logo_blue)
//                            .setAvailableProviders(getSelectedProviders())
//                            .setTosUrl(getSelectedTosUrl())
//                            .setPrivacyPolicyUrl(getSelectedPrivacyPolicyUrl())
//                            .setIsSmartLockEnabled(mEnableCredentialSelector.isChecked(),
//                                    mEnableHintSelector.isChecked())
//                            .setAllowNewEmailAccounts(mAllowNewEmailAccounts.isChecked())
//                            .build(),
//                    RC_SIGN_IN);
//        }
//    }

//    private void getProvider(){
//        selectedProviders.add(
//                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
//                        .setPermissions(getGooglePermissions())
//                        .build());
//    }

    /**データ書き込みの記録だけして、特にUIに反映させる必要がない場合はこれをもちいる*/
    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null) //成功時にはdatabaseErrorはnullになる
            Log.d(TAG, "onComplete: " + databaseError.getMessage() + " data: " + databaseReference.toString());
        else
            Log.d(TAG, "onComplete: 成功！" + databaseReference.toString());
    }

    private void initNode(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String date = cal2date(Calendar.getInstance(), datePattern);
        //このへんまとめられる！！
        HashMap<String, Object> map = new HashMap<>();
        map.put(makeScheme("userData", uid, "registeredDate"), date);
        map.put(makeScheme("userData", uid, "template"), DEFAULT);
        map.put(makeScheme("userData", uid, "group", DEFAULT), DEFAULT);
        map.put(makeScheme("friend", uid, DEFAULT, "name"), DEFAULT);
        map.put(makeScheme("friend", uid, DEFAULT, "photoUrl"), DEFAULT);
        map.put(makeScheme("userParam", uid), DEFAULT);
        Calendar cal = Calendar.getInstance();
        for (int i=0; i<12; i++){
            String yearMon =  cal2date(cal, DATE_PATTERN_YM);
            map.put(yearMon, DEFAULT);
            cal.add(Calendar.MONTH, 1);
        }

        getRootRef().updateChildren(map, this);
    }

    private void setIndex(){
        Log.d(TAG, "setIndex: fire");
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM", Locale.getDefault());
        HashMap<String, String> hashMap = new HashMap<>();
        for (int i=0; i<12; i++){
            String yearMon = sdf1.format(cal.getTime());
            hashMap.put(yearMon, DEFAULT);
//            userRecDir.child(yearMon).setValue("ここからここまで", this);
            cal.add(Calendar.MONTH, 1);
        }
        userRecDir.setValue(hashMap, this);
    }

    public void logoutFirebase(){
        Log.d(TAG, "logoutFirebase: fire");
        FirebaseAuth.getInstance().signOut();
    }

    public boolean checkIsTemplateData(@NonNull List<RecordData> list, Context context){
        List<RecordData> template = TemplateEditor.deSerialize(context);
        if (template == null || template.size() != list.size()) return false;
        for (int i=0; i<template.size(); i++) {
            RecordData tempData = template.get(i);
            RecordData data = list.get(i);
            /*日付は比較対象としてないことに注意してください*/
//            if (!Util.nullableEqual(tempData.year, data.year))
//                return false;
//            if (!Util.nullableEqual(tempData.mon, data.mon))
//                return false;
//            if (!Util.nullableEqual(tempData.day, data.day))
//                return false;
            if (!Util.nullableEqual(tempData.dataName, data.dataName))
                return false;
            if (!Util.nullableEqual(tempData.dataType, data.dataType))
                return false;
            if (!checkMap(tempData.data, data.data))
                return false;
        }
        Log.d(TAG, "checkIsTemplateData: 結果: true");
        return true;
    }

    @Contract("null, null -> true")
    private boolean checkMap(@Nullable HashMap<String, Object> x, @Nullable HashMap<String, Object> y){
        Log.d(TAG, "checkMap: fire");
        if (x == null && y == null)
            return true;
        if (x == null || y == null)
            return false;
        if (x.isEmpty() && y.isEmpty())
            return true;
        if (!Util.setStrEqual(x.keySet(), y.keySet()))//keyの差分を双方向にチェックして、差分がないことを確認する.
            return false;
        for (String key: x.keySet()) { //値が一致するか
            if (!Util.nullableEqual(x.get(key), y.get(key)))
                return false;
        }
        return true;
    }

    public static DatabaseReference getRootRef(){
        return FirebaseDatabase.getInstance().getReference();
    }

    public static DatabaseReference getRef(String... schemes) {
        return getRef(FirebaseDatabase.getInstance().getReference(), schemes);
    }

    public static DatabaseReference getRef(DatabaseReference ref, String... schemes){
        StringBuilder sb = new StringBuilder();
        for (String scheme: schemes) {
            /*ここでref.child(scheme) としないのは、child句ごとにノードの有無が確かめられ、もしnullである場合にNPEを出してしまうから。たぶん。確かめてないけど。*/
            sb.append(scheme).append("/");
        }
        ref = ref.child(sb.toString());
        return ref;
    }
}
