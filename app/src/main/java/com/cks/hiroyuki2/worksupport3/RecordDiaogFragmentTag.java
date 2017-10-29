/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.cks.hiroyuki2.worksupport3.Adapters.RecordVPAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_ADD2;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.CALLBACK_ITEM_CLICK2;
import static com.cks.hiroyuki2.worksupport3.Adapters.TimeEventRVAdapter.TIME_EVENT;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.delimiter;
import static com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItemTime.CALLBACK_RANGE_COLOR;
import static com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItemTime.TIME_EVE_RANGE;
import static com.cks.hiroyuki2.worksupprotlib.Util.PREF_KEY_COLOR;
import static com.cks.hiroyuki2.worksupprotlib.Util.PREF_NAME;
import static com.cks.hiroyuki2.worksupprotlib.Util.circleId;
import static com.cks.hiroyuki2.worksupprotlib.UtilDialog.editBuilder;
import static com.cks.hiroyuki2.worksupprotlib.UtilDialog.onClickCircle;
import static com.cks.hiroyuki2.worksupprotlib.UtilDialog.sendIntent;
import static com.cks.hiroyuki2.worksupprotlib.UtilDialog.setColorCircle;

/**
 * Created by hiroyuki2 on 2017/09/22.
 */

public class RecordDiaogFragmentTag extends DialogFragment implements DialogInterface.OnClickListener, View.OnClickListener{

    private static final String TAG = "MANUAL_TAG: " + RecordDiaogFragmentTag.class.getSimpleName();
    private SharedPreferences pref;
    private FrameLayout fm;
    private int num;
    private LinearLayout rootView;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    public final static String RECORD_DATA = "RECORD_DATA";
    private RecordData data;
    @BindView(R.id.display_check) LinearLayout ll;
    @BindView(R.id.text_input) TextInputLayout inputLayout;
    @BindView(R.id.edit_text) TextInputEditText autv;
    @BindView(R.id.checkbox) CheckBox checkBoxDisplay;
    @BindInt(R.integer.tag_max_len) int tagMaxLen;
    @BindString(R.string.comment_max_restriction) String restriction;
    @BindString(R.string.restriction_multi_tag) String rstnMultiTag;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {VISIBLE, INVISIBLE, GONE})
    @interface VISIBLE_FLAG {}

    // getTargetRequestCode() == Util.CALLBACK_TEMPLATE_TAG_EDIT でのみ使用
    private String[] strings;

    ParamsForCallbackItemClick2 params;
    private class ParamsForCallbackItemClick2 {
        TimeEvent timeEvent;
    }

    ParamsForCallbackRangeColor paramsForCallbackRangeColor;
    private class ParamsForCallbackRangeColor {
        TimeEventRange range;
    }

    public static RecordDiaogFragmentTag newInstance(Bundle bundle){
        Log.d(TAG, "newInstance: fire");
        RecordDiaogFragmentTag frag = new RecordDiaogFragmentTag();
        frag.setArguments(bundle);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        builder = new AlertDialog.Builder(getContext());
        pref = getContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        data = (RecordData) getArguments().getSerializable(RECORD_DATA);
        @StringRes int titleId;

        switch (getTargetRequestCode()){
            case RecordVPAdapter.CALLBACK_TAG_ITEM:{
                num = pref.getInt(PREF_KEY_COLOR, 0);
                createAddingTagDialog(num, VISIBLE);
                titleId = R.string.dialog_title_add_tag;
                editBuilder(builder, titleId, R.string.ok, R.string.cancel, rootView, this, null);
                dialog = builder.create();
                autv.addTextChangedListener(createTw());
                break;}

            case Util.CALLBACK_TEMPLATE_TAG_EDIT:{
                String dataTxt = getArguments().getString(Integer.toString(R.id.data_txt));
                strings = dataTxt.split(delimiter);
                num = Integer.parseInt(strings[1]);
                createAddingTagDialog(num, VISIBLE);
                autv.setText(strings[0]);
                checkBoxDisplay.setChecked(Boolean.parseBoolean(strings[2]));
                titleId = R.string.dialog_title_edit_tag;
                editBuilder(builder, titleId, R.string.ok, R.string.cancel, rootView, this, null);
                dialog = builder.create();
                autv.addTextChangedListener(createTw());
                break;}

            case CALLBACK_ITEM_CLICK2: {
                params = new ParamsForCallbackItemClick2();
                params.timeEvent = (TimeEvent) getArguments().getSerializable(TIME_EVENT);
                num = params.timeEvent.getColorNum();
                createAddingTagDialog(num, GONE);
                autv.setText(params.timeEvent.getName());
                inputLayout.setHint(null);
                editBuilder(builder, null, R.string.ok, R.string.cancel, rootView, this, null);
                dialog = builder.create();
                autv.addTextChangedListener(createTw(R.string.comment_max_restriction, 0, R.integer.tag_max_len, true));
                break;}

            case CALLBACK_ITEM_ADD2:{
                params = new ParamsForCallbackItemClick2();
                params.timeEvent = (TimeEvent) getArguments().getSerializable(TIME_EVENT);
                num = params.timeEvent.getColorNum();
                createAddingTagDialog(num, GONE);
                inputLayout.setHint(getString(R.string.hint_time_eve));
                editBuilder(builder, null, R.string.ok, R.string.cancel, rootView, this, null);
                dialog = builder.create();
                autv.addTextChangedListener(createTw(R.string.comment_max_restriction, R.string.hint_time_eve, R.integer.tag_max_len, true));
                break;}

            case CALLBACK_RANGE_COLOR:{
                paramsForCallbackRangeColor = new ParamsForCallbackRangeColor();
                paramsForCallbackRangeColor.range = (TimeEventRange) getArguments().getSerializable(TIME_EVE_RANGE);
                rootView = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_circles, null);
                setCircles(paramsForCallbackRangeColor.range.getColorNum());
                editBuilder(builder, null, R.string.ok, R.string.cancel, rootView, this, null);
                dialog = builder.create();
                break;}
        }

        return dialog;
    }

    public TextWatcher createTw(@StringRes int restriction, @StringRes int hintDef, @IntegerRes int max, boolean rejectNullInput){
        UtilDialog util = new UtilDialog(dialog);
        util.initView(inputLayout);
        util.setRestriction(restriction);
        if (hintDef == 0)
            util.setHintDef(null);
        return util.createTwMax(max, rejectNullInput);
    }

    private void createAddingTagDialog(int circleDefNum, @VISIBLE_FLAG int llVisibility){
        rootView = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.record_vp_item_tagitem_dialog2, null);
        ButterKnife.bind(this, rootView);
        autv.setSingleLine(true);
        ll.setVisibility(llVisibility);
        setCircles(circleDefNum);
    }

    private void setCircles(int circleDefNum){
        int defId = circleId.get(circleDefNum);
        fm = rootView.findViewById(defId);
        fm.getChildAt(1).setVisibility(VISIBLE);
        for (int i = 0; i< circleId.size(); i++) {
            setColorCircle(getContext(), rootView, this, i);
        }
    }

    @Override
    public void onClick(View view) {
        onClickCircle(getContext(), view, rootView);
    }

    @Nullable
    private TextWatcher createTw(){
        UtilDialog util = new UtilDialog(dialog);
        util.initView(rootView);
        util.setRestriction(R.string.comment_max_restriction);
        util.setSecondRst(R.string.restriction_multi_tag);
        List<String> multiList = new ArrayList<>();
        if (data.data == null || data.data.isEmpty()){
            multiList = null;
        } else {
            for (String key : data.data.keySet()) {
                String value = ((String) data.data.get(key)).split(delimiter)[0];
                multiList.add(value);
            }
        }

        switch (getTargetRequestCode()){
            case RecordVPAdapter.CALLBACK_TAG_ITEM:
                return util.createTwMaxAndMulti(R.integer.tag_max_len, null, multiList);
            case Util.CALLBACK_TEMPLATE_TAG_EDIT:
                return util.createTwMaxAndMulti(R.integer.tag_max_len, strings[0], multiList);
        }
        return null;//ここにはこない
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (getTargetRequestCode()){
            case RecordVPAdapter.CALLBACK_TAG_ITEM:{
                String s = autv.getText().toString() + delimiter
                        + pref.getInt(PREF_KEY_COLOR, 0) + delimiter + Boolean.toString(checkBoxDisplay.isChecked());
                getArguments().putString(RecordVPAdapter.TAG_ITEM, s);
                break;}

            case Util.CALLBACK_TEMPLATE_TAG_EDIT:{
                String s = autv.getText().toString() + delimiter
                        + pref.getInt(PREF_KEY_COLOR, 0) + delimiter + Boolean.toString(checkBoxDisplay.isChecked());
                getArguments().putString(Util.TEMPLATE_TAG_EDIT, s);
                break;}

            case CALLBACK_ITEM_CLICK2:
            case CALLBACK_ITEM_ADD2:{
                params.timeEvent.setColorNum(pref.getInt(PREF_KEY_COLOR, 0));
                params.timeEvent.setName(autv.getText().toString());
                break;}
            case CALLBACK_RANGE_COLOR:{
                paramsForCallbackRangeColor.range.setColorNum(pref.getInt(PREF_KEY_COLOR, 0));
                break;}
        }

        sendIntent(getTargetRequestCode(), this);
    }
}
