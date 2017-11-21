package com.cks.hiroyuki2.worksupport3;

import android.util.Log;

import com.cks.hiroyuki2.worksupport3.Activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.cks.hiroyuki2.worksupprotlib.Util.showFcmMsg;

/**
 * FCM用のサービス。フォアグランドでもFCMを受け取るにはこのサービスが必要となる。
 */

public class FcmService extends FirebaseMessagingService {
    private static final String TAG = "MANUAL_TAG: " + FcmService.class.getSimpleName();
    
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            String msg = remoteMessage.getNotification().getBody();
            Log.d(TAG, "onMessageReceived: " +msg);
            showFcmMsg(msg, getApplicationContext(), MainActivity.class);
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.d(TAG, "onDeletedMessages: fire");
    }
}
