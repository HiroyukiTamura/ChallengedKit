/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.cks.hiroyuki2.worksupport3.Activities.MainActivity;
import com.cks.hiroyuki2.worksupprotlib.Entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.cks.hiroyuki2.worksupport3.BackService.INTENT_KEY_1;
import static com.cks.hiroyuki2.worksupport3.BackService.INTENT_KEY_2;
import static com.cks.hiroyuki2.worksupport3.BackService.MY_ACTION;
import static com.cks.hiroyuki2.worksupport3.BackService.SEND_CODE;
import static com.cks.hiroyuki2.worksupport3.BackService.SEND_CODE_ADD_COMMENT;
import static com.cks.hiroyuki2.worksupport3.BackService.SEND_CODE_SOCIAL_STATE;
import static com.cks.hiroyuki2.worksupport3.BackService.UNKNOWN_STATE;
import static com.cks.hiroyuki2.worksupprotlib.Util.getUserMe;
import static com.cks.hiroyuki2.worksupprotlib.Util.logStackTrace;

/**
 * {@link BackService}の{@link BroadcastReceiver}役。
 */

public class ServiceConnector extends BroadcastReceiver implements ServiceConnection {

    private static final String TAG = "MANUAL_TAG: " + ServiceConnector.class.getSimpleName();
    private Context context;
    private boolean isBind = false;
    private Messenger mServiceMessenger;

    public ServiceConnector(@NonNull Context context){
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getIntExtra(SEND_CODE, Integer.MAX_VALUE)){
            case BackService.SEND_CODE_FRIEND_CHANGED:
                String content = intent.getStringExtra(INTENT_KEY_1);
                List<User> list = snap2UserInNodeList(content);
                List<String> newUserUids = intent.getStringArrayListExtra(INTENT_KEY_2);
                if (context instanceof MainActivity){
                    ((MainActivity)context).notifyFriendChanged(list, newUserUids);
                }
                break;
            case SEND_CODE_SOCIAL_STATE:
                ((MainActivity) context).socialDbState = intent.getIntExtra(INTENT_KEY_1, UNKNOWN_STATE);
                break;
        }
    }

    /**
     * @return 空であり得る
     */
    @NonNull
    static private List<User> snap2UserInNodeList(@NonNull String s){
        List<User> list = new ArrayList<>();
        try {
            JSONArray ja = new JSONArray(s);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                String userUid = jo.getString("userUid");
                String name = jo.getString("name");
                String photoUrl = jo.getString("photoUrl");
                list.add(new User(userUid, name, photoUrl));
            }
        } catch (JSONException e) {
            logStackTrace(e);
        }
        return list;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.d(TAG, "onServiceConnected() called with: componentName = [" + componentName + "], iBinder = [" + iBinder + "]");
        mServiceMessenger = new Messenger(iBinder);
        isBind = true;

        ServiceMessage sm = new ServiceMessage(null, getUserMe(), "sampleGroupKey", "sampleContentsKey");
        send(SEND_CODE_ADD_COMMENT, sm);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.d(TAG, "onServiceDisconnected() called with: componentName = [" + componentName + "]");
        isBind = false;
    }

    public void startService(){
        Intent i = new Intent(context, com.cks.hiroyuki2.worksupport3.BackService_.class);
        context.startService(i);
        context.bindService(i, this, Service.BIND_AUTO_CREATE);
    }

    public void setIntentFilter(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MY_ACTION);//ここらへんManifestでできる？
        context.registerReceiver(this, intentFilter);
    }

    public void unRegisterReceiver(){
        context.unregisterReceiver(this);
    }

    public boolean send(int what, Object obj){
        boolean success = false;
        if (mServiceMessenger != null) {
            try {
                Message msg = Message.obtain(null, what, obj);
//                msg.replyTo = mSelfMessenger;
                mServiceMessenger.send(msg);
                success = true;
            } catch (RemoteException e) {
                logStackTrace(e);
            }
        }
        return success;
    }

    private class ResponseHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case MSG_CODE_SOCIAL_STATE:
//                    socialDbState = msg.arg1;
//                    break;
//            }
        }
    }
}
