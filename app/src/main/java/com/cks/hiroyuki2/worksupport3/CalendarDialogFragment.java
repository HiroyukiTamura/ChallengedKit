/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cks.hiroyuki2.worksupport3.Adapters.CalendarRVAdapter.cal2DateWithSlash;
import static com.cks.hiroyuki2.worksupport3.Util.COLOR_NUM;
import static com.cks.hiroyuki2.worksupport3.Util.PREF_KEY_COLOR;
import static com.cks.hiroyuki2.worksupport3.Util.PREF_NAME;
import static com.cks.hiroyuki2.worksupport3.UtilDialog.editBuilder;
import static com.cks.hiroyuki2.worksupport3.UtilDialog.sendIntent;
import static com.cks.hiroyuki2.worksupport3.UtilDialog.setColorCircle;

/**
 * Created by hiroyuki2 on 2017/09/30.
 */

public class CalendarDialogFragment extends DialogFragment implements DialogInterface.OnClickListener, View.OnClickListener{
    private static final String TAG = "MANUAL_TAG: " + CalendarDialogFragment.class.getSimpleName();

    public static final String ADD_SCHEDULE = "ADD_SCHEDULE";
    public static final int CALLBACK_ADD_SCHEDULE = 511;
    public static final String INPUT = "INPUT";
    public static final String CALENDAR = "CALENDAR";

    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private LinearLayout rootView;
    private AddSchedule addSchedule;

    public static CalendarDialogFragment newInstance(Bundle bundle){
        CalendarDialogFragment frag = new CalendarDialogFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getContext());
        switch (getTargetRequestCode()){
            case CALLBACK_ADD_SCHEDULE:
                innerCreateDialog();
        }
        return dialog;
    }

    private void innerCreateDialog(){
        rootView = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.record_vp_item_tagitem_dialog2, null);
        addSchedule = new AddSchedule();
        ButterKnife.bind(addSchedule, rootView);
        addSchedule.inputLayout.setHint(getContext().getString(R.string.dialog_title));
        int defId = Util.circleId.get(addSchedule.num);
        FrameLayout fm = rootView.findViewById(defId);
        fm.getChildAt(1).setVisibility(View.VISIBLE);
        for (int i=0; i<Util.circleId.size(); i++) {
            setColorCircle(getContext(), rootView, this, i);
        }
        dialog = editBuilder(builder, addSchedule.title, R.string.ok, R.string.cancel, rootView, this, null).create();
        addSchedule.editText.addTextChangedListener(createTw());
    }

    private TextWatcher createTw(){
        UtilDialog util = new UtilDialog(dialog);
        util.initView(addSchedule.inputLayout);
        util.setHintDef(R.string.dialog_title);
        util.setRestriction(R.string.comment_max_restriction);
        return util.createTwMax(R.integer.comment_max_len, true);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i != Dialog.BUTTON_POSITIVE)
            return;

        switch (getTargetRequestCode()){
            case CALLBACK_ADD_SCHEDULE:
                onOkAddSchedule();
                break;
        }
    }

    private void onOkAddSchedule(){
        String input = addSchedule.editText.getText().toString();
        getArguments().putString(INPUT, input);
        getArguments().putInt(COLOR_NUM, addSchedule.pref.getInt(PREF_KEY_COLOR, 0));
        sendIntent(getTargetRequestCode(), this);
    }

    class AddSchedule{
        @BindView(R.id.text_input) TextInputLayout inputLayout;
        @BindView(R.id.edit_text) TextInputEditText editText;
        Calendar cal;
        SharedPreferences pref;
        String title;
        int num;

        AddSchedule(){
            cal = (Calendar) getArguments().getSerializable(CALENDAR);
            pref = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            title = cal2DateWithSlash(cal, getContext());
            num = pref.getInt(PREF_KEY_COLOR, 0);
        }
    }

    @Override
    public void onClick(View view) {
        UtilDialog.onClickCircle(getContext(), view, rootView);
    }
}
