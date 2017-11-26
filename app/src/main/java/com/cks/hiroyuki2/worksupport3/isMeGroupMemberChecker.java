package com.cks.hiroyuki2.worksupport3;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.cks.hiroyuki2.worksupport3.Util.checkAdmittionAsMember;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRef;
import static com.cks.hiroyuki2.worksupprotlib.Util.makeScheme;

/**
 * Created by hiroyuki2 on 2017/11/27.
 */

public abstract class isMeGroupMemberChecker {

    public void check(@NonNull String uid, @NonNull String groupKey){
        getRef(makeScheme("group", groupKey))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String errMsg = checkAdmittionAsMember(dataSnapshot, uid);
                        if (errMsg != null)
                            onError(errMsg);
                        else
                            onSuccess(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        onError(databaseError.getMessage());
                    }
                });
    }

    protected void onSuccess(DataSnapshot dataSnapshot){

    }

    protected void onError(@NonNull String errMsg){

    }
}
