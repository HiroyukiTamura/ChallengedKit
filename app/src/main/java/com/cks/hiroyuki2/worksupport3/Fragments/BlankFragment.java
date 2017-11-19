package com.cks.hiroyuki2.worksupport3.Fragments;


import android.support.v4.app.Fragment;
import android.widget.LinearLayout;

import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupprotlib.*;
import com.cks.hiroyuki2.worksupprotlib.Entity.RecordData;
import com.example.hiroyuki3.worksupportlibw.Presenter.RecordUiOperator;
import com.example.hiroyuki3.worksupportlibw.RecordVpItems.RecordVpItemTime;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.example.hiroyuki3.worksupportlibw.AdditionalUtil.CODE_BLANK_FRAG;

@EFragment(R.layout.fragment_blank)
public class BlankFragment extends Fragment {

    private static final String SHOWCASE_ID = "SHOWCASE_ID";
    @FragmentArg int tag;
    @ViewById(R.id.date_container) LinearLayout tabLL;
    @ViewById(R.id.content) LinearLayout content;
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

        List<RecordData> list = TemplateEditor.deSerializeDefault(getContext());
        if (list == null){
            com.cks.hiroyuki2.worksupprotlib.Util.onError(this, "list == null", R.string.error);
            return;
        }

        RecordUiOperator operator = new RecordUiOperator(list, content, null, this, CODE_BLANK_FRAG);
        operator.initRecordData();
        RecordVpItemTime time = (RecordVpItemTime) operator.getItem(0);

        new MaterialShowcaseView.Builder(getActivity())
                .setTarget(time.getView())
                .setDismissText("OK")
                .setContentText("時刻を入力する画面です")
//                .setDelay(withDelay) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                .show();

        // sequence example
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID);

        sequence.setConfig(config);

//        sequence.addSequenceItem(mButtonOne,
//                "This is button one", "GOT IT");
//
//        sequence.addSequenceItem(mButtonTwo,
//                "This is button two", "GOT IT");
//
//        sequence.addSequenceItem(mButtonThree,
//                "This is button three", "GOT IT");

        sequence.start();
    }
}
