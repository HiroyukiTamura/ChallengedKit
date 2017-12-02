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

package com.cks.hiroyuki2.worksupport3.Fragments;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.fragment_help)
public class HelpFragment extends Fragment implements View.OnClickListener{

    private final static int[] valRess = {R.string.item0, R.string.item1, R.string.item2, R.string.item3, R.string.item4};
    private final static int[] imgRess = {R.drawable.ic_mode_edit_black_24dp, R.drawable.ic_featured_play_list_black_24dp, R.drawable.ic_insert_chart_black_24dp, R.drawable.ic_menu_share, R.drawable.ic_menu_manage};
    private IHelpFragment listener;

    public interface IHelpFragment{
        void onClickItem(int tag);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IHelpFragment)
            listener = (IHelpFragment) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @AfterViews
    void afterViews(){
        LinearLayout root = (LinearLayout) getView();
        int count = 0;
        for (int i = 0; i <= 8; i+=2) {
            View v = root.getChildAt(i);
            v.setTag(i);
            v.setOnClickListener(this);
            ImageView iv = v.findViewById(R.id.item_icon);
            TextView tv = v.findViewById(R.id.item_val);
            iv.setImageResource(imgRess[count]);
            tv.setText(valRess[count]);
            count++;
        }
    }

    @Override
    public void onClick(View view) {
        int tag = (int) view.getTag();
        listener.onClickItem(tag);
    }
}
