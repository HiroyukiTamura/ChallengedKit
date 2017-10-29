/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.RecordVpItems;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.RecordData;
import com.cks.hiroyuki2.worksupport3.Fragments.RecordFragment;
import com.cks.hiroyuki2.worksupport3.Adapters.RecordParamsRVAdapter;

import java.util.Calendar;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.datePattern;
import static com.cks.hiroyuki2.worksupprotlib.Util.bundle2DataParams;
import static com.cks.hiroyuki2.worksupprotlib.Util.cal2date;
import static com.cks.hiroyuki2.worksupprotlib.Util.data2BundleParams;
import static com.cks.hiroyuki2.worksupprotlib.Util.setNullableText;
import static com.cks.hiroyuki2.worksupprotlib.Util.setRecycler;
import static com.cks.hiroyuki2.worksupprotlib.Util.data2BundleParams;
import static com.cks.hiroyuki2.worksupprotlib.Util.setNullableText;

/**
 * アセット兄弟！Paramsおじさん！
 */

public class RecordVpItemParam extends com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItem {
    private static final String TAG = "MANUAL_TAG: " + RecordVpItemParam.class.getSimpleName();
    private OnClickParamsNameListener listener;

    @BindView(R.id.tag_pool_name) TextView name;
    @BindView(R.id.add_btn) ImageView addBtn;
    @BindView(R.id.recycler) RecyclerView recycler;
    @BindColor(R.color.blue_gray_light) int draggingColor;
    private RecordParamsRVAdapter adapter;

    public interface OnClickParamsNameListener{
        void onClickParamsName(int dataNum);
        void onClickParamsAddBtn(int dataNum);
    }

    public RecordVpItemParam(RecordData data, int dataNum, @Nullable Calendar cal, Fragment fragment){
        super(data, dataNum, cal, fragment);

        if (fragment instanceof OnClickParamsNameListener)
            listener = (OnClickParamsNameListener) fragment;
    }

    @Override @Nullable
    public View buildView() {
        List<Bundle> listBundle = data2BundleParams(getData());
        if (listBundle.isEmpty())
            return null;
        View view = getFragment().getLayoutInflater().inflate(R.layout.record_vp_item_params, null);
        ButterKnife.bind(this, view);
        setNullableText(name, getData().dataName);
        adapter = new RecordParamsRVAdapter(listBundle, getDataNum(), getData().dataName, getFragment(), this);
        setRecycler(getFragment().getContext(), view, adapter, R.id.recycler);

        if (getFragment() instanceof RecordFragment)
            return view;

        addBtn.setVisibility(View.VISIBLE);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {}

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int fromPos = viewHolder.getAdapterPosition();
                final int toPos = target.getAdapterPosition();
                adapter.swap(fromPos, toPos);
                adapter.updateData();
                adapter.notifyItemMoved(fromPos, toPos);
                return true;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG){
                    viewHolder.itemView.setBackgroundColor(draggingColor);
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
            }
        });

        itemTouchHelper.attachToRecyclerView(recycler);
        recycler.addItemDecoration(itemTouchHelper);

        return view;
    }

    public void syncFirebaseAndMap(List<Bundle> bundles){
        RecordFragment fragment = (RecordFragment)getFragment();
        RecordData data = bundle2DataParams(bundles, getData().dataName, getCal());
        super.setData(data);
        String date = cal2date(getCal(), datePattern);
        int dateInt = Integer.parseInt(date);
        List<RecordData> list = fragment.adapter.retrieveList(dateInt);
        if (list == null) return;
        list.set(getDataNum(), data);
        fragment.adapter.syncDataMapAndFireBase(list, date);
    }

    @OnClick(R.id.tag_pool_name)
    void onClickName(){
        if (listener != null)
            listener.onClickParamsName(getDataNum());
    }

    @OnClick(R.id.add_btn)
    void onClickAddBtn(){
        if (listener != null)
            listener.onClickParamsAddBtn(getDataNum());
    }

    public void updateName(String title){
        setNullableText(name, title);
    }

    public void updateItemValue(int pos, String[] newArr){
        adapter.updateItemValue(pos, newArr);
        adapter.notifyDataSetChanged();
    }

    public void addItem(@NonNull Bundle bundle){
        adapter.add(bundle);
        adapter.updateData();
    }
}
