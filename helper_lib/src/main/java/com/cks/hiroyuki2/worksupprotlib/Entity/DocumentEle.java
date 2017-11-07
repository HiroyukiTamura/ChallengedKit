/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupprotlib.Entity;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;

import static com.cks.hiroyuki2.worksupportlibrary.Util.cal2date;
import static com.cks.hiroyuki2.worksupportlibrary.Util.datePattern;

/**
 * {@link Document}の構成要素。
 */

public class DocumentEle implements Serializable{

    public User user;
    public String lastEdit;
    public String content;

    public DocumentEle(@NonNull User user, @NonNull String content){
        this(user, content, cal2date(Calendar.getInstance(), datePattern));
    }

    public DocumentEle(@NonNull User editor, @NonNull String content, @NonNull String lastEdit){
        this.user = editor;
        this.lastEdit = lastEdit;
        this.content = content;
    }
}
