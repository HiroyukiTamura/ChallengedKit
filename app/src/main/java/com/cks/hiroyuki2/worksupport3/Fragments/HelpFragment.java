package com.cks.hiroyuki2.worksupport3.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.Util;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import java.util.List;

@EFragment(R.layout.fragment_help)
public class HelpFragment extends Fragment implements View.OnClickListener{

    private final static int[] valRess = {R.string.item0, R.string.item1, R.string.item2, R.string.item3, R.string.item4};
    private final static int[] ImgRess = {R.drawable.ic_mode_edit_black_24dp, R.drawable.ic_featured_play_list_black_24dp, R.drawable.ic_insert_chart_black_24dp, R.drawable.ic_menu_share, R.drawable.ic_menu_manage};
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
        for (int i = 0; i <= 10; i++) {
            View v = root.getChildAt(i);
            v.setTag(i);
            v.setOnClickListener(this);
            ImageView iv = v.findViewById(R.id.item_icon);
            TextView tv = v.findViewById(R.id.item_val);
            iv.setImageResource(valRess[count]);
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
