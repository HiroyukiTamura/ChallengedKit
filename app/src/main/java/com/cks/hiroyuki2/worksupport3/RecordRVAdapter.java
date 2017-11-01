/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.Fragments.EditTemplateFragment;
import com.cks.hiroyuki2.worksupprotlib.Entity.RecordData;
import com.cks.hiroyuki2.worksupprotlib.TemplateEditor;
import com.cks.hiroyuki2.worksupprotlib.Util;
import com.cks.hiroyuki2.worksupprotlib.UtilSpec;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.refactor.library.SmoothCheckBox;

import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickTimePickerDialog;
import static com.cks.hiroyuki2.worksupprotlib.Util.INDEX;
import static com.cks.hiroyuki2.worksupprotlib.Util.LIST_MAP_HOUR;
import static com.cks.hiroyuki2.worksupprotlib.Util.LIST_MAP_MIN;
import static com.cks.hiroyuki2.worksupprotlib.Util.LIST_MAP_VALUE;
import static com.cks.hiroyuki2.worksupprotlib.Util.delimiter;
import static com.cks.hiroyuki2.worksupprotlib.Util.time2String;

/**
 * timeLineを表示するおじさん！
 */

public class RecordRVAdapter extends RecyclerView.Adapter<RecordRVAdapter.ViewHolder> implements View.OnClickListener {

    private Fragment rf;
    private static final String TAG = "MANUAL_TAG: " + RecordRVAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private FragmentManager fm;
    public List<Bundle> list;
    private int key;
    private boolean circleClicked = false;
    private Snackbar snackbar;
    private Set<Integer> checkedList = new TreeSet<>();//RecordDialogFragment起点の際に使用します

    public static final String ITEM_ADD = "ITEM_ADD";
    public static final String DIALOG_LONGTAP = "DIALOG_LONGTAP";
    public static final int CALLBACK_TIME = 100;
    public static final int CALLBACK_LONGTAP = 110;
    public static final String KEY = "KEY";

    /**
     * @param rf RecordFragment, EditTemplateの3つが代入されます。onClickでsetTargetFragment時にこのrfを渡します。
     */
    public RecordRVAdapter(List<Bundle> list, FragmentManager fm, Fragment rf, int key) {
        super();
        Log.d(TAG, "RecordRVAdapter: constructer fire");
        this.fm = fm;
        this.rf = rf;
        this.list = list;
        sortList();
        this.key = key;
        inflater = (LayoutInflater)rf.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (rf instanceof RecordDialogFragment){
            for (int i = 0; i < list.size(); i++) {
                String value = list.get(i).getString(LIST_MAP_VALUE);
                if (value == null) continue;

                String color = value.split(delimiter)[1];
                if (Integer.parseInt(color) != 0)
                    checkedList.add(i);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.remove) ImageView remove;
        @BindView(R.id.circle) ImageView circle;
        @BindView(R.id.radio) SmoothCheckBox radio;
//        FrameLayout frame;
//        RadioButton radio2;
//        RadioGroup radioG;
        @BindView(R.id.value) TextView value;
        @BindView(R.id.time) TextView time;
        @BindView(R.id.time_card) CardView cardView;
        ViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
//            frame = (FrameLayout) v.findViewById(R.id.frame);
//            radioG = (RadioGroup) v.findViewById(R.id.radio_group);
//            radio2 = (RadioButton) v.findViewById(R.id.radio2);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.record_rv_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Bundle bundle = list.get(position);

        final String[] values = bundle.getString(LIST_MAP_VALUE).split(delimiter);
        holder.value.setText(values[0]);

        final int hour = bundle.getInt(LIST_MAP_HOUR);
        int min = bundle.getInt(LIST_MAP_MIN);
        String time = time2String(hour, min);
        holder.time.setText(time);

        int colorNum = Integer.parseInt(values[1]);
        int colorId = R.color.blue_gray;
        if (colorNum != 100){
            colorId = UtilSpec.colorId.get(colorNum);
        }
        holder.circle.setColorFilter(ContextCompat.getColor(rf.getContext(), colorId));

//        if (rf instanceof RecordDialogFragment){
//            holder.circle.setVisibility(View.GONE);
////            holder.radioG.setVisibility(View.VISIBLE);
//            holder.radio.setVisibility(View.VISIBLE);
//            holder.cardView.setCardElevation(0);
//            holder.radio.setChecked(checkedList.contains(holder.getAdapterPosition()));
//            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.frame.getLayoutParams();
//            lp.setMargins(lp.leftMargin, lp.topMargin, 0, lp.bottomMargin);
//            holder.frame.setLayoutParams(lp);

//            holder.cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//            holder.radio.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "onClick: fire " + holder.radio.isChecked());
//                    if (checkedList.contains(holder.getAdapterPosition())){
//                        holder.radio.setChecked(false);
//                    }
//                }
//            });
//            holder.radio.setChecked(checkedList.contains(holder.getAdapterPosition()));
//            holder.radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked){
//                        checkedList.add(holder.getAdapterPosition());
//                        holder.radio.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                holder.radio.setChecked(false);
//                            }
//                        });
//                    } else{
//                        holder.radio.setOnClickListener(null);
//                        checkedList.remove(holder.getAdapterPosition());
//                    }
//                    Log.d(TAG, "onCheckedChanged: " + checkedList.toString() + "isChecked: " + isChecked);
//                }
//            });
//            return;
//        }

        //todo butterKnifeでbindすればいちいちnewしなくていい説が浮上中
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putInt(KEY, key);
                kickTimePickerDialog(RecordDialogFragmentPicker.DIALOG_TIME_TIME, CALLBACK_TIME, bundle, rf);
            }
        });

        if (rf instanceof EditTemplateFragment){
            holder.remove.setVisibility(View.VISIBLE);
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bundle.putString("from", DIALOG_LONGTAP);
                    bundle.putInt(KEY, key);
                    DialogKicker.kickDialogInOnClick(DIALOG_LONGTAP, CALLBACK_LONGTAP, bundle, rf);
                }
            });

//            holder.circle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    bundle.putString("from", Util.TEMPLATE_TIME_COLOR);
//                    RecordDialogFragment dialog = RecordDialogFragment.newInstance(bundle);
//                    bundle.putInt(KEY, key);
//                    bundle.putInt(Util.COLOR_NUM, Integer.parseInt(values[1]));//0はグループなしを表す
//                    dialog.setTargetFragment(rf, Util.CALLBACK_TEMPLATE_TIME_COLOR);
//                    dialog.show(fm, Util.TEMPLATE_TIME_COLOR);
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getInt(INDEX);//INDEXの値は不変であることに注意してください
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: fire");
        switch (v.getId()){
            case R.id.circle:
                break;
        }
    }

    /**
     * editTemplateFragmentからも、RecordFragmentからも呼ばれるので注意してください。
     */
    public void changeData(Bundle newBundle, int key, Fragment fragment){
        int index = newBundle.getInt(INDEX);
        Log.d(TAG, "changeData: " + newBundle.getString(LIST_MAP_VALUE));
        int pos = getBundlePos(index);
        list.set(pos, newBundle);

        sortList();
        notifyItemChanged(index);
        if (fragment instanceof EditTemplateFragment)
            updateTemplate(key);
    }

    public void addItem(){
        Log.d(TAG, "addItem: fire");
        Bundle bundle = new Bundle();
        bundle.putInt(INDEX, list.size());//todo ん？これでいいのか？
        bundle.putInt(KEY, key);
        bundle.putString("from", RecordDialogFragmentPicker.DIALOG_TIME_TIME);
        bundle.putBoolean(ITEM_ADD, true);
        RecordDialogFragmentPicker dialog = RecordDialogFragmentPicker.newInstance(bundle);
        dialog.setTargetFragment(rf, CALLBACK_TIME);
        dialog.show(fm, RecordDialogFragmentPicker.DIALOG_TIME_TIME);
    }

    public void postAddItem(Bundle bundle, int key){
        Log.d(TAG, "postAddItem: postAddItem()");
        list.add(bundle);
        sortList();
        notifyDataSetChanged();
        updateTemplate(key);
    }

    public void deleteItem(int index, int key){
        Log.d(TAG, "deleteItem() called with: index = [" + index + "]");
        int pos = getBundlePos(index);
        list.remove(pos);
        notifyItemRemoved(index);
        updateTemplate(key);
    }

    public void updateCircle(Bundle bundle, int key){
        Log.d(TAG, "updateCircle() called");
        int index = bundle.getInt(INDEX);
        int pos = getBundlePos(index);
        list.set(pos, bundle);
        notifyDataSetChanged();
        updateTemplate(key);
    }

    private void sortList(){
        Collections.sort(list, new Comparator<Bundle>() {
            @Override
            public int compare(Bundle x, Bundle y) {
                int dif = x.getInt(LIST_MAP_HOUR) - y.getInt(LIST_MAP_HOUR);
                if (dif != 0)
                    return dif;
                else
                    return x.getInt(LIST_MAP_MIN) - y.getInt(LIST_MAP_MIN);
            }
        });
    }

    private void updateTemplate(int key){
        List<RecordData> dataList = TemplateEditor.deSerialize(rf.getContext());
        if (dataList == null) return;
        RecordData data = Util.bundle2Data(list, null, 1, 0, 0, 0);
        TemplateEditor.writeTemplate(key, data, rf.getContext());
    }

    /**
     * @return 該当するindexがない場合は、1000をIllegalな値として投げます
     */
    private int getBundlePos(int index) throws IllegalArgumentException{
        for (int i=0; i<list.size(); i++) {
            Bundle bundle = list.get(i);
            int indexTemp = bundle.getInt(INDEX, 1000);
            if (indexTemp == index){
                return i;
            }
        }
        throw new IllegalArgumentException("そのindexを持つBundleがlist内にありませんでした");
    }
}
