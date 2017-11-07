/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cks.hiroyuki2.worksupportlib.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

import static com.cks.hiroyuki2.worksupprotlib.Util.onError;

/**
 * Firebaseに書き込むまえに、nodeの存在をチェックして書き込む際に用いるクラス。
 */

public abstract class FbCheckAndWriter implements ValueEventListener, DatabaseReference.CompletionListener{
    private static final String TAG = "MANUAL_TAG: " + FbCheckAndWriter.class.getSimpleName();
    private DatabaseReference checkRef;
    private DatabaseReference writeRef;
    private Context context;
    private Object obj;
    private HashMap<String, Object> hashMap;
    private int code = -1;
    public final static int CODE_SET_VALUE = 0;
    public final static int CODE_SET_NULL = 1;
    public final static int CODE_UPDATE_CHILDREN = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {CODE_SET_VALUE, CODE_SET_NULL, CODE_UPDATE_CHILDREN})
    @interface CODE {}

    public FbCheckAndWriter(@NonNull DatabaseReference checkRef, @Nullable DatabaseReference writeRef, @NonNull Context context, @Nullable Object obj){
        this.writeRef = writeRef;
        this.checkRef = checkRef;
        this.context = context;
        this.obj = obj;
    }

    public FbCheckAndWriter(@NonNull DatabaseReference checkRef, @Nullable DatabaseReference writeRef, @NonNull Context context, @Nullable HashMap<String, Object> hashMap){
        this.writeRef = writeRef;
        this.checkRef = checkRef;
        this.context = context;
        this.hashMap = hashMap;
    }


    @Nullable
    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj){
        this.obj = obj;
    }

    public  void setHashMap(HashMap<String, Object> hashMap){
        this.hashMap = hashMap;
    }

    public void setWriteRef(DatabaseReference writeRef){
        this.writeRef = writeRef;
    }

    public  void setWriteRef(String scheme){
        writeRef = FirebaseDatabase.getInstance().getReference().child(scheme);
    }

    public void update(@CODE int code){
        this.code = code;
        checkRef.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists())
            onNodeVanished(checkRef);
        else
            onNodeExist(dataSnapshot);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        onFailed(databaseError);
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null)
            onFailed(databaseError);
        else
            onSuccess(databaseReference);
    }

    public abstract void onSuccess(DatabaseReference ref);

    protected void onNodeVanished(DatabaseReference ref){
        onError(context, TAG +"onNodeVanished"+ ref.toString(), R.string.error);
    }

    protected void onFailed(@NonNull DatabaseError databaseError){
        onError(context, TAG+databaseError.getDetails(), R.string.error);
    }

    protected void onNodeExist(@NonNull DataSnapshot dataSnapshot){
        if (!dataSnapshot.exists()) {
            onError(context, TAG + dataSnapshot.toString(), R.string.error);
            return;
        }

        switch (code){
            case CODE_SET_VALUE:
                writeRef.setValue(obj, this);
                break;
            case CODE_SET_NULL:
                writeRef.setValue(null, this);
                break;
            case CODE_UPDATE_CHILDREN:
                writeRef.updateChildren(hashMap, this);
                break;
        }
    }
}
