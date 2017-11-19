/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.EService;
import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRef;
import static com.cks.hiroyuki2.worksupprotlib.FriendJsonEditor.readFriendPref;
import static com.cks.hiroyuki2.worksupprotlib.FriendJsonEditor.snap2Json;
import static com.cks.hiroyuki2.worksupprotlib.FriendJsonEditor.writeFriendPref;
import static com.cks.hiroyuki2.worksupprotlib.FriendJsonEditor.writeGroup;
import static com.cks.hiroyuki2.worksupprotlib.FriendJsonEditor.writeGroupKeys;
import static com.cks.hiroyuki2.worksupprotlib.Util.DEFAULT;
import static com.cks.hiroyuki2.worksupprotlib.Util.logStackTrace;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;
import static com.cks.hiroyuki2.worksupprotlib.Util.printHashKey;
import static com.cks.hiroyuki2.worksupprotlib.Util.toastNullable;

/**
 * BackServiceおじさん！
 */

@EService
public class BackService extends Service implements FirebaseAuth.AuthStateListener, ValueEventListener{

    private static final String TAG = "MANUAL_TAG: " + BackService.class.getSimpleName();
    static final String INTENT_KEY_1 = "INTENT_KEY_1";
    static final String INTENT_KEY_2 = "INTENT_KEY_2";
    private String uid;
    private static final String urlStart = "https://wordsupport3.firebaseio.com";/*まさかのwor"D"support*/
    private List<String> groupKeys = new ArrayList<>();
    private Messenger mServiceMessenger;
    public static final int SEND_CODE_SOCIAL_STATE = 1;

    static final String SEND_CODE = "SEND_CODE";
    static final int SEND_CODE_FRIEND_CHANGED = 10;

    public static final String MY_ACTION ="MY_ACTION";
    public static final int REJECT_SOCIAL = 1;
    public static final int ACCEPT_SOCIAL = 0;
    public static final int UNKNOWN_STATE = -1;

    public @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {REJECT_SOCIAL, ACCEPT_SOCIAL, UNKNOWN_STATE})
    @interface socialState{}
    @socialState private int isAcceptSocial = UNKNOWN_STATE;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY},
            flag = true)
    @interface COMMAND_FLAG {}

    class RequestHandler extends Handler {
        private final WeakReference<BackService> contextWeakReference;
        RequestHandler(BackService backService){
            contextWeakReference = new WeakReference<>(backService);
        }

        @Override
        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case MSG_CODE_SOCIAL_STATE:
//                    break;
//                default:
//                    super.handleMessage(msg);
//                    break;
//            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() called");
//        groupKeys = FriendJsonEditor.getGroupKeys(getApplicationContext());
        mServiceMessenger = new Messenger(new RequestHandler(this));
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() called with: intent = [" + intent + "]");
        return mServiceMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, @COMMAND_FLAG int flags, int startId) {
        Log.d(TAG, "onStartCommand() called with: intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() called with: intent = [" + intent + "]");
        return true;//ここでtrueを返すと、DestroyされずにまたbindされたときにonRebindが呼ばれる。  onBindは、初回bind時しか呼ばれないので注意。
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind() called with: intent = [" + intent + "]");
        super.onRebind(intent);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
//            writeLocalProf(user);
            getRef("accept", "social").addValueEventListener(this);
            getRef("friend", uid).addValueEventListener(this);
            getRef("userData", uid, "group").addValueEventListener(this);

//            rootRef.child("friend").child(uid).addChildEventListener(this);
//            rootRef.child("userData").child(uid).child("group").addChildEventListener(this);
//            rootRef.child("userData").child(uid).child("group").addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    String groupKey = dataSnapshot.getKey();
//                    rootRef.child("group").child(groupKey).addChildEventListener(this);
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        } else {
//            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
    }

    @Override
    public void onDestroy() {
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        super.onDestroy();
    }

//    @Override
//    public void onChildRemoved(DataSnapshot dataSnapshot) {
//        Log.d(TAG, "onChildRemoved() called with: dataSnapshot = [" + dataSnapshot + "]");
//    }
//
//    @Override
//    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//        String url = dataSnapshot.getRef().getParent().toString();
//        Log.d(TAG, "onChildAdded() called with: dataSnapshot = [" + url + "], s = [" + s + "]");
//
//        if (url.equals(urlStart +"/friend/"+ uid)){
//            String uid = dataSnapshot.getKey();
//            String name = (String) retrieveValue(dataSnapshot, "name");
//            String photoUrl = (String) retrieveValue(dataSnapshot, "photoUrl");
//            FriendJsonEditor.addFriendPref(getApplicationContext(), uid, name, photoUrl);
//        } else if (url.equals(urlStart +"/userData/"+ uid + "/group")){
//            String groupKey = dataSnapshot.getKey();
//            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//            groupUrls.add("group" + groupKey);
//            rootRef.child("group").child(groupKey).addChildEventListener(this);
//        } else {
//            if (groupUrls.isEmpty()) return;
//            for (String urlG: groupUrls) {
//                if (urlG.equals(url)){
//
//                    break;
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//        Log.d(TAG, "onChildMoved() called with: dataSnapshot = [" + dataSnapshot + "], s = [" + s + "]");
//    }
//
//    @Override
//    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//        Log.d(TAG, "onChildChanged() called with: dataSnapshot = [" + dataSnapshot + "], s = [" + s + "]");
//    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "onCancelled() called with: databaseError = [" + databaseError + "]");
    }


    @Contract("null, _ -> !null")
    private Object retrieveValue(@Nullable DataSnapshot snap, @NonNull String key){
        if (snap == null || !snap.hasChild(key))
            return "null";
        else
            return snap.child(key).getValue();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        String url = dataSnapshot.getRef().toString();
        if (url.equals(urlStart + "/accept/social")){
            if (dataSnapshot.exists()){
                boolean accept = dataSnapshot.getValue(Boolean.class);
                isAcceptSocial = accept?
                        ACCEPT_SOCIAL:
                        REJECT_SOCIAL;
                Intent i = new Intent()
                        .setAction(MY_ACTION)
                        .putExtra(SEND_CODE, SEND_CODE_SOCIAL_STATE)
                        .putExtra(INTENT_KEY_1, isAcceptSocial);
                sendBroadcast(i);
            }

        } else if (url.equals(urlStart +"/friend/"+ uid)){
            if (!dataSnapshot.exists())
                return;

            JSONArray ja = new JSONArray();
            List<String> newUidList = new ArrayList<>();
            for (DataSnapshot snap: dataSnapshot.getChildren()) {
                JSONObject jo = new JSONObject();
                String userUid = snap.getKey();
                if (userUid.equals(DEFAULT))
                    continue;

                newUidList.add(userUid);

                String name = (String) retrieveValue(snap, "name");
                String photoUrl = (String) retrieveValue(snap, "photoUrl");
                try {
                    jo.put("userUid", userUid);
                    jo.put("name", name);
                    jo.put("photoUrl", photoUrl);
                    ja.put(jo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            String content = ja.toString();

            writeFriendPref(getApplicationContext(), content);

            if (content == null)//初回登録時はここでreturnされるはず
                return;

            /*一連のPrefまわりって、全部Gsonに書き換えたらFirebaseからの読み出しとか楽そうだよなあ。Firebaseの乗り換え時に色々変えよう。*/
            JSONArray oldJa = readFriendPref(getApplicationContext());
            for (int i = 0; i < oldJa.length(); i++) {
                try {
                    String oldUserUid = oldJa.getJSONObject(i).getString("userUid");
                    if (newUidList.contains(oldUserUid))
                        newUidList.remove(oldUserUid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Intent intent = new Intent(MY_ACTION);
            intent.putExtra(SEND_CODE, SEND_CODE_FRIEND_CHANGED);
            intent.putExtra(INTENT_KEY_1, content);
            intent.putStringArrayListExtra(INTENT_KEY_2, (ArrayList<String>) newUidList);
            sendBroadcast(intent);

        } else if (url.equals(urlStart +"/userData/"+ uid + "/group")){
            if (dataSnapshot.exists()){
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    String groupKey = snap.getKey();
                    if (!groupKey.equals(DEFAULT)){
                        groupKeys.add(groupKey);
                        getRef("group", groupKey).addValueEventListener(this);
                    }
                }
                writeGroupKeys(getApplicationContext(), groupKeys);
            }


        } else {

            if (groupKeys.isEmpty()) return;

            for (String urlG: groupKeys) {
                String lastNode = url.substring(url.lastIndexOf("/")+1);
                if (urlG.equals(lastNode)){
                    Log.d(TAG, "onDataChange: うごきがあったぞ！");

                    JSONObject jo = snap2Json(dataSnapshot);
                    if (jo == null){
                        onError(getApplicationContext(), TAG + " url: " + url, null);
                        return;
                    }

                    writeGroup(getApplicationContext(), dataSnapshot.getKey(), jo);
                    return;
                }
            }
        }
    }
}
