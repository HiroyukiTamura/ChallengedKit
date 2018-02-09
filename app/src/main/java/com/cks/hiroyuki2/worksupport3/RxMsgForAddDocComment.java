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

/**
 * RxBusで使うためのデータ格納用クラス
 */
public class RxMsgForAddDocComment {
    private String contentKey;
    private String groupKey;
    private String newVal;

    RxMsgForAddDocComment(@NonNull String groupKey, @NonNull String contentKey, @NonNull String newVal){
        this.groupKey = groupKey;
        this.contentKey = contentKey;
        this.newVal = newVal;
    }

    @NonNull
    public String getContentKey() {
        return contentKey;
    }

    @NonNull
    public String getGroupKey() {
        return groupKey;
    }

    @NonNull
    public String getNewVal() {
        return newVal;
    }
}
