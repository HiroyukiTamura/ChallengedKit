/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupportlib.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.LIMIT_SIZE_PROF;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.isOverSize;
import static com.cks.hiroyuki2.worksupprotlib.Util.DEFAULT;
import static com.cks.hiroyuki2.worksupprotlib.Util.INTENT_KEY_NEW_PARAM;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;

/**
 * 自分のプロフィールのアイコンを変更する。
 */

public abstract class SettingFbCommunicator implements OnSuccessListener<UploadTask.TaskSnapshot>, OnProgressListener<UploadTask.TaskSnapshot>, OnPausedListener<UploadTask.TaskSnapshot>, OnCompleteListener<Void>, ValueEventListener, DatabaseReference.CompletionListener {
    private static final String TAG = "MANUAL_TAG: " + SettingFbCommunicator.class.getSimpleName();
    public Fragment fragment;
    public Intent intent;
    private Uri downloadUrl;
    private String param;
    public FirebaseUser user;
    private String myUid;
    private HashMap<String, Object> updatingMap = new HashMap<>();
    private boolean isSet1stListener = false;
    private String scheme;
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({SCHEME_PHOTO_URL, SCHEME_NAME})
    @interface schemeCode {}
    public static final String SCHEME_PHOTO_URL = "photoUrl";
    public static final String SCHEME_NAME = "name";

    /**
     * @param scheme :0-> {@link #uploadIcon()}
     *                :1-> {@link #updateProfPhotoUrl()}がコールされなければならない
     */
    public SettingFbCommunicator(Fragment fragment, Intent intent, @schemeCode String scheme){
        this.fragment = fragment;
        this.intent = intent;
        user = FirebaseAuth.getInstance().getCurrentUser();
        myUid = user.getUid();
        this.scheme = scheme;
    }

    //region アイコンアップデートパート
    ////////////////////////////////////アイコンアップデート系////////////////////////////
    /** 第一のエンドポイント。*/
    public void uploadIcon(){
        final Uri uri = intent.getData();
        if (isOverSize(uri, LIMIT_SIZE_PROF)){
            Toast.makeText(fragment.getContext(), R.string.over_size_err, Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(fragment.getContext(), "アップロードしています...", Toast.LENGTH_SHORT).show();
        String type = Util.getExtension(fragment.getContext(), uri);
        String fileName = myUid + "." + type;

        FirebaseStorageUtil.uploadFile("profile_icon/" + fileName, uri, fragment, this, this, this);
    }

    @Override
    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {

    }

    @Override
    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

    }

    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        downloadUrl = taskSnapshot.getDownloadUrl();
        if (downloadUrl != null)
            param = taskSnapshot.getDownloadUrl().toString();
        updateProfPhotoUrl();
    }
    ////////////////////////////////////アイコンアップデート系ここまで////////////////////////////
    //endregion

    //region プロフィール更新パート
    ////////////////////////プロフィール更新パート////////////////////////////
    private void updateProfPhotoUrl(){
        UserProfileChangeRequest req = new UserProfileChangeRequest.Builder()
                .setPhotoUri(downloadUrl)
                .build();
        user.updateProfile(req).addOnCompleteListener(this);
    }

    /** アカウント名を再設定するおっさん ここが第二のエンドポイントとなる*/
    public void updateProfName(){
        param = intent.getStringExtra(INTENT_KEY_NEW_PARAM);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(param)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(this);
    }
    ///////////////プロフィール更新パート。次はDBの更新パートへ////////////
    //endregion

    //region DB更新パート
    ///////////////////////////////DB更新パート/////////////////////////////
    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (!task.isSuccessful()){
            onError(fragment.getContext(), "onComplete: onSuccess: addOnSuccessListener:", R.string.error);
            return;
        }

        DatabaseReference refMyFriends = FirebaseDatabase.getInstance().getReference()
                .child("friend")
                .child(myUid);

        refMyFriends.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (!isSet1stListener){

            isSet1stListener = true;

            setUpdateMap(dataSnapshot);

            DatabaseReference refMyGroup = FirebaseDatabase.getInstance().getReference()
                        .child("userData")
                        .child(myUid)
                        .child("group");
            refMyGroup.addListenerForSingleValueEvent(this);

        } else {

            setUpdateMap(dataSnapshot);

            if (updatingMap.isEmpty()){
                onSuccess(param);//やることなし！-> 成功！
            } else
                FirebaseDatabase.getInstance().getReference().updateChildren(updatingMap, this);
        }
    }

    private void setUpdateMap(DataSnapshot dataSnapshot){
        if (!dataSnapshot.exists() && dataSnapshot.hasChildren()){
            for (DataSnapshot snap: dataSnapshot.getChildren()) {
                String key = snap.getKey();
                if (!key.equals(DEFAULT)){

                    String value = null;
                    switch (scheme){
                        case SCHEME_NAME:
                            value = param;
                            updatingMap.put("friend/"+ key +"/"+ myUid +"/"+ scheme, value);
                            break;
                        case SCHEME_PHOTO_URL:
                            value = downloadUrl.toString();
                            break;
                    }

                    updatingMap.put("/group/"+ key+ "/member/"+ myUid +"/"+ scheme, value);
                }
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        onError(fragment.getContext(), databaseError.getDetails(), R.string.error);
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null){
            onError(fragment.getContext(), databaseError.getDetails(), R.string.error);
        } else {
            onSuccess(param);
        }
    }
    ///////////////////////////////DB更新パートここまで/////////////////////////////
    //endregion

    public abstract void onSuccess(String param);
}
