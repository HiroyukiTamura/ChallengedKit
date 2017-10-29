/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupportlibrary.Entity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * Groupの末節にあるContentを扱うおじさん！将来はFbから直接読み込んでほしいな！
 */

public class Content implements Serializable{

    public String contentKey;
    public String lastEdit;
    public String lastEditor;
    public String whose;
    public String type;
    public String contentName;
    public String comment;

   public Content(@NonNull String contentKey, @NonNull String contentName, @NonNull String lastEdit, @NonNull String lastEditor, @NonNull String whose, @NonNull String type, @Nullable String comment){
        this.contentKey = contentKey;
        this.lastEdit = lastEdit;
        this.lastEditor = lastEditor;
        this.whose = whose;
        this.type = type;
        this.contentName = contentName;
        this.comment = comment;
    }
}
