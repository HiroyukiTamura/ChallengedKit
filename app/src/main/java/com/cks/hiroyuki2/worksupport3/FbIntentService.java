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

package com.cks.hiroyuki2.worksupport3;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupport3.Activities.MainActivity;
import com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment;
import com.cks.hiroyuki2.worksupprotlib.*;
import com.cks.hiroyuki2.worksupprotlib.Entity.RecordData;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.UploadTask;

import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.cks.hiroyuki2.worksupport3.RxBus.UPDATE_GROUP_PHOTO;
import static com.cks.hiroyuki2.worksupport3.RxBus.UPDATE_PROF_NAME_SUCCESS;
import static com.cks.hiroyuki2.worksupprotlib.FbCheckAndWriter.CODE_SET_VALUE;
import static com.cks.hiroyuki2.worksupprotlib.FbCheckAndWriter.CODE_UPDATE_CHILDREN;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRef;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRootRef;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.isOverSize;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseStorageUtil.uploadFile;
import static com.cks.hiroyuki2.worksupprotlib.Util.PREF_NAME;
import static com.cks.hiroyuki2.worksupprotlib.Util.getExtension;
import static com.cks.hiroyuki2.worksupprotlib.Util.logAnalytics;
import static com.cks.hiroyuki2.worksupprotlib.Util.logStackTrace;
import static com.cks.hiroyuki2.worksupprotlib.Util.makeScheme;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;
import static com.cks.hiroyuki2.worksupprotlib.Util.showCompleteNtf;
import static com.cks.hiroyuki2.worksupprotlib.Util.showDownloadingNtf;
import static com.cks.hiroyuki2.worksupprotlib.Util.showUploadingNtf;

/**
 * Fbにぶん投げる系
 */
@SuppressLint("Registered")
@EIntentService
public class FbIntentService extends IntentService implements OnFailureListener, OnPausedListener<UploadTask.TaskSnapshot>{
    private static final String TAG = "MANUAL_TAG: " + FbIntentService.class.getSimpleName();
    private Handler toastHandler = new Handler(Looper.getMainLooper());
    public static String PREF_KEY_ACCESS_SOCIAL = "PREF_KEY_ACCESS_SOCIAL";

    public FbIntentService(){
        // ActivityのstartService(intent);で呼び出されるコンストラクタはこちら
        super("FbIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //do nothing here
    }

    @ServiceAction
    public void updateGroupName(@NonNull String uid, @NonNull String groupKey, @NonNull String newGroupName){
        Log.d(TAG, "sampleAction() called");

        new isMeGroupMemberChecker(){
            @Override
            protected void onSuccess(DataSnapshot dataSnapshot) {
                DatabaseReference ref = getRef("group", groupKey, "groupName");
                FbCheckAndWriter writer = new FbCheckAndWriter(ref, ref, getApplicationContext(), newGroupName) {
                    @Override
                    public void onSuccess(DatabaseReference ref) {
                        Log.d(TAG, "onSuccess: 成功したよね");
                        RxBus.publish(RxBus.UPDATE_GROUP_NAME, "てってれー");
                    }
                };
                writer.update(CODE_SET_VALUE);
            }

            @Override
            protected void onError(@NonNull String errMsg) {
                onErrorForService(errMsg, R.string.error);
            }
        }.check(uid, groupKey);
    }

    @ServiceAction
    public void updateGroupPhotoUrl(@NonNull String uid, @NonNull String groupKey, @NonNull String groupName, /*このuriは、ローカルファイルのuri*/ @NonNull Uri uri){
        new isMeGroupMemberChecker(){
            @Override
            protected void onSuccess(DataSnapshot dataSnapshot) {
                uploadGroupIcon(groupKey, groupName, uri, UPDATE_GROUP_PHOTO);
            }

            @Override
            protected void onError(@NonNull String errMsg) {
                onErrorForService(errMsg, R.string.error);
            }
        }.check(uid, groupKey);
    }

    @ServiceAction
    void uploadGroupIcon(@NonNull String groupKey, @NonNull String groupName, /*このuriは、ローカルファイルのuri*/ @NonNull Uri uri, @RxBus.subject int subject){
        toastHandler.post(new DisplayToast(R.string.msg_start_upload));
        String type = getExtension(getApplicationContext(), uri);
        String key = getRef("keyPusher").push().getKey();
        final String fileName = key + "." + type;

        final int ntfId = (int) System.currentTimeMillis();

        uploadFile("group_icon/" + fileName, uri, FbIntentService.this, taskSnapshot -> {
                    Uri uri1 = taskSnapshot.getDownloadUrl();
                    RxBus.publish(subject, uri1);/*Firebaseの仕様上NPEはあり得ないので、you can ignore this warning*/

                    DatabaseReference ref = getRef("group", groupKey, "photoUrl");
                    FbCheckAndWriter writer = new FbCheckAndWriter(ref, ref, getApplicationContext(), uri1.toString()) {
                        @Override
                        public void onSuccess(DatabaseReference ref) {
                            showCompleteNtf(MainActivity.class, getApplicationContext(), groupName, ntfId, R.string.ntf_txt_change_group_img);
                        }
                    };
                    writer.update(CODE_SET_VALUE);
                },
                FbIntentService.this,
                taskSnapshot -> showUploadingNtf(MainActivity.class, getApplicationContext(), taskSnapshot, fileName, ntfId));
    }

    @ServiceAction
    public void removeMember(@NonNull String groupKey, @NonNull String uid, @NonNull String name){
        new isMeGroupMemberChecker(){
            @Override
            protected void onError(@NonNull String errMsg) {
                onErrorForService(errMsg, R.string.error);
            }

            @Override
            protected void onSuccess(DataSnapshot dataSnapshot) {
                DatabaseReference checkRef = getRef("group", groupKey);
                HashMap<String, Object> children = new HashMap<>();
                children.put(makeScheme("group", groupKey, "member", uid), null);
                children.put(makeScheme("userData", uid, "group", groupKey), null);
                new FbCheckAndWriter(checkRef, getRootRef(), getApplicationContext(), children) {
                    @Override
                    public void onSuccess(DatabaseReference ref) {
                        RxBus.publish(RxBus.REMOVE_MEMBER, name);
                    }
                }.update(CODE_UPDATE_CHILDREN);
            }
        }.check(uid, groupKey);
    }

    @ServiceAction
    public void editNormalComment(@NonNull String uid, @NonNull String groupKey, @NonNull String contentKey, @Nullable String newComment){
        new isMeGroupMemberChecker(){
            @Override
            protected void onError(@NonNull String errMsg) {
                onErrorForService(errMsg, R.string.error);
            }

            @Override
            protected void onSuccess(DataSnapshot dataSnapshot) {
                DatabaseReference checkRef = getRef("group", groupKey, "contents", contentKey);
                DatabaseReference writeRef = getRef(checkRef, "comment");
                FbCheckAndWriter writer = new FbCheckAndWriter(checkRef, writeRef, getApplicationContext(), newComment) {
                    @Override
                    public void onSuccess(DatabaseReference ref) {
                        Log.d(TAG, "onSuccess: succeed to edit comment"+ ref.toString());
                    }
                };
                writer.update(CODE_SET_VALUE);
            }
        }.check(uid, groupKey);
    }

    /**
     * todo アイコン表示おかしいので要修正
     * 一旦tempファイルを作成して、それをコピーして保存する。
     * じゃないと謎のエラーが出るんだ。謎。
     * ここでは、{@link isMeGroupMemberChecker}でのチェックは行わない。
     * なぜなら、1.この動作はDb書き込みでないから許容でき、2.どのみち更新動作時には{@link ShareBoardFragment#onRefresh()}でDBへのアクセスが拒否されるから。
     */
    @ServiceAction
    public void dlFileFromStorage(@NonNull String groupKey, @NonNull String contentKey, @NonNull String contentName, @NonNull Uri localUri){
        String tempFileName = String.valueOf(new Random().nextInt());
        File file = new File(getCacheDir(), tempFileName);
        final int ntfId = (int)System.currentTimeMillis();

        FirebaseStorage.getInstance().getReference()
            .child(makeScheme("shareFile", groupKey, contentKey))
            .getFile(file)
            .addOnFailureListener(e -> {
                logStackTrace(e);
                toastHandler.post(new DisplayToast(R.string.error));
                showCompleteNtf(MainActivity.class, getApplicationContext(), contentName, ntfId, R.string.msg_failed_download);
            }).addOnPausedListener(taskSnapshot -> {
                //何かするか？？
            }).addOnProgressListener(taskSnapshot -> showDownloadingNtf(MainActivity.class, getApplicationContext(), taskSnapshot, contentName, ntfId))
            .addOnSuccessListener(taskSnapshot -> {
                try(OutputStream outputStream = getContentResolver().openOutputStream(localUri)) {
                    FileInputStream fis = new FileInputStream(file);

                    // byte型の配列を宣言
                    byte[] buf = new byte[256];
                    int len;

                    // ファイルの終わりまで読み込む
                    while((len = fis.read(buf)) != -1){
                        outputStream.write(buf);
                    }

                    //ファイルに内容を書き込む
                    outputStream.flush();

                    //ファイルの終了処理
                    outputStream.close();
                    fis.close();

                } catch(Exception e){
                    logStackTrace(e);
                    toastHandler.post(new DisplayToast(R.string.error));
                    showCompleteNtf(MainActivity.class, getApplicationContext(), contentName, ntfId, R.string.msg_failed_download);
                    return;

                } finally {
                    file.delete();
                }

                toastHandler.post(new DisplayToast(R.string.msgDlSuccess));
                showCompleteNtf(MainActivity.class, getApplicationContext(), contentName, ntfId, R.string.msgDlSuccess);
            });
    }

    /**
     * ネストの深さは心の闇の深さ・・・
     */
    @ServiceAction
    public void removeFileFromStorage(@NonNull String uid, @NonNull String groupKey, @NonNull String contentKey, final boolean isDoc){
        new isMeGroupMemberChecker(){
            @Override
            protected void onError(@NonNull String errMsg) {
                onErrorForService(errMsg, R.string.error);
            }

            @Override
            protected void onSuccess(DataSnapshot dataSnapshot) {
                getRef("group", groupKey, "contents", contentKey)
                        .removeValue((databaseError, databaseReference) -> {
                            if (databaseError != null)
                                onErrorForService(databaseError.getMessage(), R.string.error);
                            else {
                                RxBus.publish(RxBus.REMOVE_STORAGE_FILE, contentKey);

                                if (!isDoc){
                                    FirebaseStorage.getInstance().getReference()
                                            .child(makeScheme("shareFile", groupKey, contentKey))
                                            .delete()
                                            .addOnFailureListener(com.cks.hiroyuki2.worksupprotlib.Util::logStackTrace).addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: addOnSuccessListener"));
                                }
                            }
                        });
            }
        }.check(uid, groupKey);
    }

    @ServiceAction
    public void updateProfName(@NonNull String newMyName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            onErrorForService(TAG+ "user == null", R.string.error);
            return;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newMyName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()){
                        onErrorForService(task.getResult().toString(), R.string.error);
                        return;
                    }

                    getRef(makeScheme("userData", user.getUid(), "displayName"))
                            .setValue(newMyName, (databaseError, databaseReference) -> {
                                if (databaseError != null)
                                    onErrorForService(task.getResult().toString(), R.string.error);
                                else
                                    RxBus.publish(UPDATE_PROF_NAME_SUCCESS, newMyName);
                            });
                });
    }

    @ServiceAction
    void updateProfIcon(Uri uri){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            onErrorForService(TAG+ "user == null", R.string.error);
            return;
        }

        if(isOverSize(uri, 5 * 1000 * 1000)) {
            toastHandler.post(new DisplayToast(R.string.over_size_err));
        } else {
            toastHandler.post(new DisplayToast(R.string.msg_start_upload));
            String type = getExtension(getApplicationContext(), uri);
            String fileName = user.getUid() + "." + type;
            final int ntfId = (int)System.currentTimeMillis();
            String ntfTitle = "プロフィール画像";

            uploadFile("profile_icon/" + fileName, uri, e -> {
                logStackTrace(e);
                toastHandler.post(new DisplayToast(R.string.error));
                showCompleteNtf(MainActivity.class, getApplicationContext(), ntfTitle, ntfId, R.string.msg_failed_upload);
            }, taskSnapshot -> {
                Uri uri1 = taskSnapshot.getDownloadUrl();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri1)
                        .build();

                user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()){
                        showCompleteNtf(MainActivity.class, getApplicationContext(), ntfTitle, ntfId, R.string.msg_failed_upload);
                    } else {
                        getRef(makeScheme("userData", user.getUid(), "photoUrl"))
                                .setValue(uri1.toString(), (databaseError, databaseReference) -> {
                                    if (databaseError != null)
                                        showCompleteNtf(MainActivity.class, getApplicationContext(), ntfTitle, ntfId, R.string.msg_failed_upload);
                                    else {
                                        showCompleteNtf(MainActivity.class, getApplicationContext(), ntfTitle, ntfId, R.string.msg_succeed_upload);
                                        RxBus.publish(RxBus.UPDATE_PROF_ICON, uri1);
                                    }
                                });
                    }
                });
            }, this, taskSnapshot -> showUploadingNtf(MainActivity.class, getApplicationContext(), taskSnapshot, ntfTitle, ntfId));
        }
    }

    @ServiceAction
    void updateTemplate(List<RecordData> dataList){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            onErrorForService(TAG+ "user == null", R.string.error);
            return;
        }

        getRef("userData", user.getUid(), "template").setValue(dataList, (databaseError, databaseReference) -> {
            if (databaseError != null){
                logAnalytics(databaseError.getMessage(), getApplicationContext());
            }
        });
    }

    @ServiceAction
    void checkShareAvailable(){
        getRef("accept", "social").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                            .edit()
                            .putBoolean(PREF_KEY_ACCESS_SOCIAL, dataSnapshot.getValue(Boolean.class))//Fbの仕様上nonNullなので大丈夫
                            .apply();
                } else {
                    onError(getApplicationContext(), "checkShareAvailable datasnap null", null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onError(getApplicationContext(), TAG+"checkShareAvailable()"+databaseError.getMessage(), null);
            }
        });
    }

    @Override
    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {

    }

    @Override
    public void onFailure(@NonNull Exception e) {
        logStackTrace(e);
        toastHandler.post(new DisplayToast(R.string.error));
    }

    /**
     * @link "https://stackoverflow.com/questions/3955410/how-to-create-toast-from-intentservice-it-gets-stuck-on-the-screen/3955826#3955826"
     */
    private class DisplayToast implements Runnable{
        String mText;

        private DisplayToast(String text){
            mText = text;
        }

        private DisplayToast(@StringRes int strRes){
            mText = getString(strRes);
        }

        public void run(){
            Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_LONG).show();
        }
    }

    public void onErrorForService(@NonNull String errLog, @StringRes int msgRes){
        toastHandler.post(new DisplayToast(msgRes));
        logAnalytics(errLog, this);
        Answers.getInstance().logCustom(new CustomEvent(errLog));
    }
}
