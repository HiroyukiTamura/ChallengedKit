/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cks.hiroyuki2.worksupprotlib.Entity.RecordData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * 一日の記録のList<RecordData>をオブジェクトとして持つ
 * さらにその日付を表すCalendar
 * そしてリスナも持つ
 */

public abstract class FirebaseEventHandler {
    private static final String TAG = "MANUAL_TAG: " + FirebaseEventHandler.class.getSimpleName();
    public List<RecordData> list = new ArrayList<>();
    private ValueEventListener listener;
    private Calendar cal;

    public FirebaseEventHandler(Calendar cal){
        this.cal = cal;
    }

    public void initValueEventListener(){
        listener =  new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                boolean isSnapShotExist = dataSnapshot.exists();
                if (isSnapShotExist){
                    for (int i=0; i<dataSnapshot.getChildrenCount(); i++){
                        HashMap<String, Object> hashMap = (HashMap<String, Object>)dataSnapshot.child(Integer.toString(i)).getValue();
                        RecordData recordData = new RecordData(hashMap);
                        list.add(recordData);
                    }
                }
                onOnDataChange(dataSnapshot, isSnapShotExist);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: " + databaseError.getMessage());
                onOnCancelled(databaseError);
            }
        };
    }

    public abstract void onOnDataChange(DataSnapshot dataSnapshot, boolean isSnapShotExist);

    public abstract void onOnCancelled(DatabaseError databaseError);

    @NonNull
    public ValueEventListener getListener() {
        if (listener == null){
            throw new IllegalArgumentException("listener == null");
        }
        return listener;
    }
}
