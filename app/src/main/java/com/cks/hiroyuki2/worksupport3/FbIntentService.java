package com.cks.hiroyuki2.worksupport3;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cks.hiroyuki2.worksupport3.Activities.MainActivity;
import com.cks.hiroyuki2.worksupport3.Fragments.GroupSettingFragment;
import com.cks.hiroyuki2.worksupprotlib.FbCheckAndWriter;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;

import static com.cks.hiroyuki2.worksupprotlib.FbCheckAndWriter.CODE_SET_VALUE;
import static com.cks.hiroyuki2.worksupprotlib.FbCheckAndWriter.CODE_UPDATE_CHILDREN;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRef;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRootRef;
import static com.cks.hiroyuki2.worksupprotlib.Util.showCompleteNtf;
import static com.cks.hiroyuki2.worksupprotlib.Util.toastNullable;

/**
 * Created by hiroyuki2 on 2017/11/26.
 */

@EIntentService
public class FbIntentService extends IntentService {
    private static final String TAG = "MANUAL_TAG: " + FbIntentService.class.getSimpleName();

    public FbIntentService(){
        // ActivityのstartService(intent);で呼び出されるコンストラクタはこちら
        super("FbIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //do nothing here
    }

    @ServiceAction
    public void sampleAction(@NonNull String groupKey, @NonNull String newGroupName){
        Log.d(TAG, "sampleAction() called");

        DatabaseReference ref = getRef("group", groupKey, "groupName");

        FbCheckAndWriter writer = new FbCheckAndWriter(ref, ref, getApplicationContext(), newGroupName) {
            @Override
            public void onSuccess(DatabaseReference ref) {
                Log.d(TAG, "onSuccess: 成功したよね");
            }
        };
        writer.update(CODE_SET_VALUE);
    }
}
