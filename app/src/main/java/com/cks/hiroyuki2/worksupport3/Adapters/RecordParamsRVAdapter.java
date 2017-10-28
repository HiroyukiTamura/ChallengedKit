/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupport3.DialogKicker;
import com.cks.hiroyuki2.worksupport3.Fragments.EditTemplateFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.RecordFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.RecordData;
import com.cks.hiroyuki2.worksupport3.RecordVpItems.RecordVpItemParam;
import com.cks.hiroyuki2.worksupport3.TemplateEditor;
import com.cks.hiroyuki2.worksupport3.Util;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickInputDialog;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.makeBundleInOnClick;
import static com.cks.hiroyuki2.worksupport3.Util.PARAMS_VALUES;
import static com.cks.hiroyuki2.worksupport3.Util.bundle2DataParams;
import static com.cks.hiroyuki2.worksupport3.Util.onError;

/**
 * {@link RecordVpItemParam}所属！うーん、入り組んでる！
 */

public class RecordParamsRVAdapter extends RecyclerView.Adapter<RecordParamsRVAdapter.ViewHolder> implements CompoundButton.OnCheckedChangeListener, DiscreteSeekBar.OnProgressChangeListener {

    private static final String TAG = "MANUAL_TAG: " + RecordParamsRVAdapter.class.getSimpleName();
    public static final String INDEX = "INDEX";
    private LayoutInflater inflater;
    private List<Bundle> list;
    private int dataNum;
    private String dataName;
    private Fragment fragment;
//    private int indexMax;
    private RecordVpItemParam param;

    public RecordParamsRVAdapter(@NonNull List<Bundle> list, int dataNum, @Nullable String dataName, @NonNull Fragment fragment, @Nullable RecordVpItemParam param){
        Log.d(TAG, "RecordParamsRVAdapter: constructor fire");
        this.list = list;
        this.dataNum = dataNum;
        this.dataName = dataName;
        this.fragment = fragment;
        this.param = param;
//        indexMax = list.size()-1;
        inflater = (LayoutInflater)fragment.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    RecordParamsRVAdapter(@NonNull List<Bundle> list, int dataNum, @Nullable String dataName, @NonNull Fragment fragment){//todo 後でこれなくすこと
        this(list, dataNum, dataName, fragment, null);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.key) TextView key;
        @BindView(R.id.checkbox) CheckBox checkBox;
        @BindView(R.id.seek_bar) DiscreteSeekBar seekBar;
        @BindView(R.id.remove) ImageView remove;
        @BindView(R.id.max) ImageView max;
        @BindView(R.id.container) LinearLayout container;
        ViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.record_params_rv_item, parent, false);
        ButterKnife.bind(this, v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.container.setTag(position);

        final Bundle bundle = list.get(position);
        final String[] values = bundle.getStringArray(PARAMS_VALUES);
        if (values == null) return;
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()){
//                    case R.id.key:{
//                        DialogKicker.makeBundleInOnClick(bundle, Util.TEMPLATE_PARAMS_ITEM, dataNum);
//                        DialogKicker.kickDialogInOnClick(Util.TEMPLATE_PARAMS_ITEM, Util.CALLBACK_TEMPLATE_PARAMS_ITEM, bundle, fragment);
//                        break;}
//                    case R.id.max:{
//                        DialogKicker.makeBundleInOnClick(bundle, Util.TEMPLATE_PARAMS_SLIDER_MAX, dataNum);
//                        DialogKicker.kickDialogInOnClick(Util.TEMPLATE_PARAMS_SLIDER_MAX, Util.CALLBACK_TEMPLATE_PARAMS_SLIDER_MAX, bundle, fragment);
//                        break;}
//                    case R.id.remove:{
//                        list.remove(holder.getAdapterPosition());
//                        updateData();
//                        break;}
//                }
//            }
//        };
        holder.key.setText(values[1]);
//        if (fragment instanceof EditTemplateFragment)
//            holder.key.setOnClickListener(listener);
        switch (values[0]){
            case "0":{
                holder.checkBox.setChecked(Boolean.parseBoolean(values[2]));
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setOnCheckedChangeListener(this);
//                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        values[2] = Boolean.toString(isChecked);
//                        callBack(values, holder.getAdapterPosition());
//                    }
//                });
                break;}

            case "1":{
                holder.seekBar.setMax(Integer.parseInt(values[3]));
                holder.seekBar.setProgress(Integer.parseInt(values[2]));
                holder.seekBar.setVisibility(View.VISIBLE);
                holder.seekBar.setOnProgressChangeListener(this);
//                holder.seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
//                    @Override
//                    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {}
//
//                    @Override
//                    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}
//
//                    @Override
//                    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
//                        values[2] = Integer.toString(seekBar.getProgress());
//                        callBack(values, holder.getAdapterPosition());
//                    }
//                });

                if (!(fragment instanceof EditTemplateFragment))
                    break;

                holder.max.setVisibility(View.VISIBLE);
//                holder.max.setOnClickListener(listener);
                break;}
        }

        if (!(fragment instanceof EditTemplateFragment)) return;

        holder.remove.setVisibility(View.VISIBLE);
//        holder.remove.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * ここでDialogFragmentから返されたbundleをlistにsetしないのは、返されたbundleはそもそもlistを構成しているbundleであるから。
     */
    public void updateData(){
        RecordData data = Util.bundle2Data(list, dataName, 3, 0, 0, 0);/*bundle2DataParamsでなくていいのか？*/
        boolean success = TemplateEditor.writeTemplate(dataNum, data, fragment.getContext());
        if (success)
            notifyDataSetChanged();
        else
            Toast.makeText(fragment.getContext(), R.string.template_failure, Toast.LENGTH_LONG).show();
    }

    public void swap(int fromPos, int toPos){
        Bundle bundle = list.remove(fromPos);
        list.add(toPos, bundle);
    }

    public void add(@NonNull Bundle bundle){
//        indexMax++;
//        bundle.putInt(INDEX, indexMax);
        list.add(bundle);
    }

    //region seekBar/CheckBox OnChange系列
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        int pos = (int) ((ViewGroup)compoundButton.getParent()).getTag();
        Bundle bundle = list.get(pos);
        String[] values = bundle.getStringArray(PARAMS_VALUES);
        if (values == null){
            onError(fragment.getContext(), TAG + "onCheckedChanged", null);
            return;
        }

        values[2] = Boolean.toString(isChecked);
        callBack(values, pos);
    }

    /////////////////////////////seekBarOnChange系列ここから////////////////////////*/
    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {}

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
        int pos = (int) ((ViewGroup)seekBar.getParent()).getTag();
        Bundle bundle = list.get(pos);
        String[] values = bundle.getStringArray(PARAMS_VALUES);
        if (values == null){
            onError(fragment.getContext(), TAG + "onCheckedChanged", null);
            return;
        }

        values[2] = Integer.toString(seekBar.getProgress());
        callBack(values, pos);
    }
    /////////////////////////////seekBarOnChange系列ここまで////////////////////////*/

    private void callBack(String[] values, int pos){
        Bundle bundle = list.get(pos);
        bundle.putStringArray(PARAMS_VALUES, values);
        if (fragment instanceof EditTemplateFragment){
            RecordData data = bundle2DataParams(list, dataName, 0, 0, 0);
            boolean success = TemplateEditor.writeTemplate(dataNum, data, fragment.getContext());
            if (!success)
                onError(fragment.getContext(), "!success", R.string.template_failure);
        } else if (fragment instanceof RecordFragment){
            param.syncFirebaseAndMap(list);
        }
    }
    //endregion

    //region onClick系列
    @OnClick({R.id.key, R.id.max, R.id.remove})
    void onClickBtn(View view){
        if (!(fragment instanceof EditTemplateFragment))
            return;

        int pos = (int) ((ViewGroup)view.getParent()).getTag();

        switch (view.getId()){
            case R.id.key:
                onClickKey(pos);
                break;
            case R.id.max:
                onClickMax(pos);
                break;
            case R.id.remove:
                onClickRemove(pos);
                break;
        }
    }

    private void onClickKey(int pos){
        Bundle bundle = list.get(pos);
        bundle.putInt(INDEX, pos);
        makeBundleInOnClick(bundle, Util.TEMPLATE_PARAMS_ITEM, dataNum);
        kickInputDialog(bundle, Util.TEMPLATE_PARAMS_ITEM, Util.CALLBACK_TEMPLATE_PARAMS_ITEM, fragment);
    }

    private void onClickMax(int pos){
        Bundle bundle = list.get(pos);
        makeBundleInOnClick(bundle, Util.TEMPLATE_PARAMS_SLIDER_MAX, dataNum);
        bundle.putInt(Util.TEMPLATE_PARAMS_SLIDER_MAX, pos);
        DialogKicker.kickDialogInOnClick(Util.TEMPLATE_PARAMS_SLIDER_MAX, Util.CALLBACK_TEMPLATE_PARAMS_SLIDER_MAX, bundle, fragment);
    }

    private void onClickRemove(int pos){
        list.remove(pos);
        updateData();
    }
    //endregion

    public void updateItemValue(int pos, String[] newArr){
        list.get(pos).putStringArray(PARAMS_VALUES, newArr);
    }
}
