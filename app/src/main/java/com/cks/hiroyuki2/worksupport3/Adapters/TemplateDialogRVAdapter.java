/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.RecordData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cks.hiroyuki2.worksupport3.Util.setNullableText;

/**
 * {@link com.cks.hiroyuki2.worksupport3.TempWidgetDialogFragment}附属。
 * テンプレの並びかえを担当。
 */
public class TemplateDialogRVAdapter extends RecyclerView.Adapter<TemplateDialogRVAdapter.ViewHolder>{

    private LayoutInflater inflater;
    private Context context;
    private List<RecordData> list;

    public TemplateDialogRVAdapter(Context context, @NonNull List<RecordData> list){
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv) ImageView iv;
        @BindView(R.id.tv) TextView tv;
        ViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.template_dialog_rv_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ButterKnife.bind(this, holder.itemView);
        holder.iv.setTag(position);
        String s = list.get(position).getDataName();
        setNullableText(holder.tv, s);
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        else
            return list.size();
    }

    public void remove(int index){
        list.remove(index);
        notifyItemRemoved(index);
    }

    public void move(int old, int newI){
        RecordData data = list.remove(old);
        list.add(newI, data);
        notifyItemMoved(old, newI);
    }

    @OnClick(R.id.iv)
    void onClickIv(View v){
        int pos = (int)v.getTag();
        remove(pos);
    }
}
