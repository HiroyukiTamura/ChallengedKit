package com.cks.hiroyuki2.worksupport3;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

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

    public void setListener(FirebaseUser user){
        listener = new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                isListenerFired = true;
                if (task.isSuccessful())
                    authToken = task.getResult().getToken();
            }
        };

        user.getIdToken(false).addOnCompleteListener(listener);
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
