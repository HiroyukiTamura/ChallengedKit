/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupportlibrary.Entity;

import android.support.annotation.NonNull;

import com.cks.hiroyuki2.worksupport3.FirebaseConnection;
import com.cks.hiroyuki2.worksupport3.Util;

import java.io.Serializable;
import java.util.Calendar;

/**
 * {@link Document}の構成要素。
 */

public class DocumentEle implements Serializable{

    public User user;
    public String lastEdit;
    public String content;

    public DocumentEle(@NonNull User user, @NonNull String content){
        this(user, content, Util.cal2date(Calendar.getInstance(), FirebaseConnection.datePattern));
    }

    public DocumentEle(@NonNull User editor, @NonNull String content, @NonNull String lastEdit){
        this.user = editor;
        this.lastEdit = lastEdit;
        this.content = content;
    }
}
