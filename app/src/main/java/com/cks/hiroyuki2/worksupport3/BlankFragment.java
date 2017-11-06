package com.cks.hiroyuki2.worksupport3;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupprotlib.*;
import com.cks.hiroyuki2.worksupprotlib.Entity.RecordData;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import java.util.List;

@EFragment(R.layout.fragment_blank)
public class BlankFragment extends Fragment {
    @FragmentArg int tag;
    @ViewById(R.id.date_container) LinearLayout tabLL;
    @ColorRes(R.color.pink) int pink;

    @AfterViews
    void afterViews(){
        switch (tag){
            case 0:
                initOnTag0();
                break;
        }
    }

    private void initOnTag0(){
        for (int i = 0; i < tabLL.getChildCount(); i++) {
            TextView tv = tabLL.getChildAt(i).findViewById(R.id.tv);
            tv.setText(i);
            if (i == 0){
                tv.setTextColor(pink);
                ImageView iv = tabLL.getChildAt(i).findViewById(R.id.iv);
                iv.setColorFilter(Color.WHITE);
            }
        }


        List<RecordData> list = TemplateEditor.deSerializeDefault(getContext());
        if (list == null){
            com.cks.hiroyuki2.worksupprotlib.Util.onError(this, "list == null", R.string.error);
            return;
        }

        RecordUiOperator operator = new RecordUiOperator(list, tabLL, null, this);
    }
}
