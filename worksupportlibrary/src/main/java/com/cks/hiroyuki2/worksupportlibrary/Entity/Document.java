/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupportlibrary.Entity;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hiroyuki2 on 2017/09/08.
 */

public class Document implements Serializable{

    public String title;
    public List<DocumentEle> eleList;

    public Document(@NonNull String title, @NonNull List<DocumentEle> eleList){
        this.title = title;
        this.eleList = eleList;
    }
}
