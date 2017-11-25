package com.cks.hiroyuki2.worksupport3;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cks.hiroyuki2.worksupprotlib.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.concurrent.TimeUnit;

/**
 * Created by hiroyuki2 on 2017/11/25.
 */

public class FbTokenObserber {
    private static final String TAG = "MANUAL_TAG: " + FbTokenObserber.class.getSimpleName();
    private String authToken = "";

    private boolean isListenerFired = false;
    private OnCompleteListener<GetTokenResult> listener;
    private static final int TIMEOUT_SEC = 15;
    private Handler handler = new Handler();

    FbTokenObserber(){

    }

    public boolean setListener(){
        FirebaseUser user = com.cks.hiroyuki2.worksupprotlib.Util.getUserMe();
        if (user == null)
            return false;

        listener = new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                isListenerFired = true;
                if (task.isSuccessful())
                    authToken = task.getResult().getToken();
            }
        };

        user.getIdToken(false).addOnCompleteListener(listener);
        return true;
    }

    /**
     *
     * @return 空でありうる。obserber内でエラーハンドリングするのが面倒なので、エラー時にはnullではなく空のstringを渡す
     */
    @NonNull
    public String getToken(){
        return authToken;
    }

    public boolean isListenerFired() {
        return isListenerFired;
    }
}
