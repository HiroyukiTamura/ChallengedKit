package com.cks.hiroyuki2.worksupport3;

import android.support.annotation.NonNull;

import com.cks.hiroyuki2.worksupprotlib.Entity.DocumentEle;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by hiroyuki2 on 2017/11/26.
 */

public class ServiceMessage {
    private static final String TAG = "MANUAL_TAG: " + ServiceMessage.class.getSimpleName();

    private DocumentEle documentEle;
    private FirebaseUser user;

    public ServiceMessage(@NonNull DocumentEle ele, @NonNull FirebaseUser user){
        this.documentEle = ele;
        this.user = user;
    }

    public DocumentEle getDocumentEle() {
        return documentEle;
    }

    public FirebaseUser getUser() {
        return user;
    }
}
