/*
 * Copyright 2017 Hiroyuki Tamura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    private String groupKey;
    private String contentsKey;

    public ServiceMessage(@NonNull DocumentEle ele, @NonNull FirebaseUser user, @NonNull String groupKey, @NonNull String contentsKey){
        this.documentEle = ele;
        this.user = user;
        this.groupKey = groupKey;
        this.contentsKey = contentsKey;
    }

    public DocumentEle getDocumentEle() {
        return documentEle;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public String getContentsKey() {
        return contentsKey;
    }
}
