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

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by hiroyuki2 on 2017/10/27.
 */
public class ShareBoardDialogAdapter extends BaseAdapter {
    private int requestCode;
    private Context context;
    private LayoutInflater inflater;
    private List<String> list;
    @ColorInt private int color0;
    @ColorInt private int color1;

    public ShareBoardDialogAdapter(@NonNull Context context, int requestCode, @NonNull List<String> list){
        this.requestCode = requestCode;
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        switch (requestCode){
            case ShareBoardFragment.DIALOG_CODE:
                color0 = ContextCompat.getColor(context, R.color.colorAccentDark);
                color1 = ContextCompat.getColor(context, R.color.colorPrimaryDark);
                this.list = list;
                break;
        }
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.board_dialog_list_item, viewGroup, false);

        TextView tv = view.findViewById(R.id.tv);
        tv.setText(list.get(i));

        ImageView iv = view.findViewById(R.id.iv);
        switch (requestCode){
            case ShareBoardFragment.DIALOG_CODE:
                switch (i){
                    case 0:
                        iv.setImageResource(R.drawable.ic_cloud_upload_white_24dp);
                        iv.setColorFilter(color0);
                        break;
                    case 1:
                        iv.setImageResource(R.drawable.doc);
                        break;
                    case 2:
                        iv.setImageResource(R.drawable.ic_sync_white_24dp);
                        iv.setColorFilter(color1);
                        break;
                }
                break;
        }
        return view;
    }
}
