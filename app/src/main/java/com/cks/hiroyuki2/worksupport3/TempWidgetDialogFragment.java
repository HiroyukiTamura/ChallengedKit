/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.cks.hiroyuki2.worksupport3.Adapters.TemplateDialogRVAdapter;
import com.cks.hiroyuki2.worksupport3.Fragments.EditTemplateFragment;

import java.util.LinkedList;
import java.util.List;

import static com.cks.hiroyuki2.worksupprotlib.UtilDialog.editBuilder;
import static com.cks.hiroyuki2.worksupprotlib.UtilDialog.sendIntent;

/**
 * {@link EditTemplateFragment}のUIに直接かかわる処理を行うDialogFragment Widgetの編集まわりを担当します。
 */

public class TempWidgetDialogFragment extends DialogFragment implements DialogInterface.OnClickListener, View.OnClickListener{

    private static final String TAG = "MANUAL_TAG: " + TempWidgetDialogFragment.class.getSimpleName();
    public static final String APPLY_TEMP_IS_SUCCESS = "APPLY_TEMP_IS_SUCCESS";
    public final static String TEMPLATE_ADD = "TEMPLATE_ADD";
    public final static int CALLBACK_TEMPLATE_ADD = 1030;
    public final static String TEMPLATE_EDIT = "TEMPLATE_EDIT";
    public final static int CALLBACK_TEMPLATE_EDIT = 1040;
    private LayoutInflater inflater;
    private List<RecordData> list;
    private RecordData firstItem;

    public static TempWidgetDialogFragment newInstance(@Nullable Bundle bundle){
        TempWidgetDialogFragment frag =  new TempWidgetDialogFragment();
        if (bundle != null)
            frag.setArguments(bundle);
        else
            frag.setArguments(new Bundle());
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (getTargetRequestCode()){
            case CALLBACK_TEMPLATE_ADD:
                builder = createAddDialog(builder);
                break;

            case CALLBACK_TEMPLATE_EDIT:
                return createEditDialog(builder);
        }

        return builder.create();
    }

    private AlertDialog.Builder createAddDialog(AlertDialog.Builder builder){
        FrameLayout view = (FrameLayout)inflater.inflate(R.layout.template_add, null);
        GridLayout gridLayout = (GridLayout)view.getChildAt(0);
        for (int i = 0; i <gridLayout.getChildCount() ; i++) {
            final CardView cv = (CardView)gridLayout.getChildAt(i);
            cv.setTag(i);
            cv.setOnClickListener(this);
        }

        editBuilder(builder, R.string.add_widget_dialog_title, 0, 0, view, this, null);
        return builder;
    }

    private AlertDialog createEditDialog(AlertDialog.Builder builder){
        List<RecordData> dataList = TemplateEditor.deSerialize(getContext());
        if (dataList == null)
            return builder.create();

        list = new LinkedList<>(dataList);//dialogでキャンセルしたときのために、listは直接操作しない
        firstItem = list.remove(0);//最初の空アイテム

        final int draggingColor = ContextCompat.getColor(getContext(), R.color.blue_gray_light);
        FrameLayout view = (FrameLayout)inflater.inflate(R.layout.template_edit_dialog, null);
        AlertDialog dialog = editBuilder(builder, R.string.edit_widget_dialog_title, R.string.ok, R.string.cancel, view, this, null).create();
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final TemplateDialogRVAdapter adapter = new TemplateDialogRVAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int old = viewHolder.getAdapterPosition();
                int newI = target.getAdapterPosition();
                adapter.move(old, newI);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.remove(viewHolder.getAdapterPosition());
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG)
                    viewHolder.itemView.setBackgroundColor(draggingColor);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
            }
        });
        recyclerView.addItemDecoration(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return dialog;
    }

    @Override
    public void onClick(View view) {
        int i = (int)view.getTag();
        getArguments().putInt(Util.CARD_INT, i);
        sendIntent(getTargetRequestCode(), TempWidgetDialogFragment.this);
        dismiss();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE && getTargetRequestCode() == CALLBACK_TEMPLATE_EDIT){
            list.add(0, firstItem);
            boolean b = TemplateEditor.applyTemplate(list, getContext());
            getArguments().putBoolean(APPLY_TEMP_IS_SUCCESS, b);
            sendIntent(getTargetRequestCode(), TempWidgetDialogFragment.this);
        }
    }
}
