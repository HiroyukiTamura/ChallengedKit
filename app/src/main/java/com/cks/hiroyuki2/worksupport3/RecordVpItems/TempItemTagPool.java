/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.RecordVpItems;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.Fragments.EditTemplateFragment;
import com.cks.hiroyuki2.worksupport3.R;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cks.hiroyuki2.worksupprotlib.Util.setNullableText;
import static com.cks.hiroyuki2.worksupprotlib.Util.delimiter;

/**
 * 本来は{@link RecordVpItemTag}が担当してもいいんだけど、テンプレまわりの処理が特に大変なので、tagPoolに関してだけ、テンプレ専用のクラスを作成しました。
 */
public class TempItemTagPool extends RecordVpItem {

    private static final String TAG = "MANUAL_TAG: " + TempItemTagPool.class.getSimpleName();
    private static final String DUMMY_VIEW = "DUMMY_VIEW";

    private int dataNum;
    private View view;
    private List<TempItemTag> tagList = new ArrayList<>();
    @BindView(R.id.tag_pool_name) TextView name;
    @BindView(R.id.tag_box0) FlowLayout fl0;
    @BindView(R.id.tag_box1) FlowLayout fl1;

    public TempItemTagPool(EditTemplateFragment frag, int dataNum){
        super(frag.getList().get(dataNum), dataNum, null, frag);
        this.dataNum = dataNum;
    }

    @Override
    public View buildView() {
        view = getFragment().getLayoutInflater().inflate(R.layout.record_vp_item_tags_edit, null);
        ButterKnife.bind(this, view);

        setNullableText(name, getData().dataName);

        if (getData().data != null && !getData().data.isEmpty()){
            int i=0;
            for (String key : getData().data.keySet()) {
                final String value = (String) getData().data.get(key);
                if (value != null && !value.isEmpty())
                    inputTag(value, i);

                i++;
            }
        }

        setFlHeightIfEmpty(fl0);
        setFlHeightIfEmpty(fl1);

        return view;
    }

    @OnClick(R.id.tag_pool_name)
    void onClickTagPoolName(){
        ((EditTemplateFragment)getFragment()).onClickTagPoolName(dataNum);
    }

    @OnClick(R.id.add_btn)
    void onClickAddBtn(){
        ((EditTemplateFragment)getFragment()).onClickTagPoolAdd(dataNum);
    }

    public void inputTag(final @NonNull String value, final int tagNum){
        String[] strings = value.split(delimiter);

        Log.d(TAG, "setTagsView: value:" + strings[0]);

        TempItemTag tag = new TempItemTag(tagNum, value, dataNum, (EditTemplateFragment)getFragment());
        tagList.add(tag);
        final View item = tag.buildView();

        if (Boolean.parseBoolean(strings[2])){
            deleteDummyIfExist(fl0);
            fl0.addView(item);
        } else {
            deleteDummyIfExist(fl1);
            fl1.addView(item);
        }
    }

    private void setFlHeightIfEmpty(FlowLayout fl){
        if (fl.getChildCount() != 0)
            return;

        View view1 = getFragment().getLayoutInflater().inflate(R.layout.record_vp_item_tagitem, null);
        view1.setTag(DUMMY_VIEW);
        view1.setVisibility(View.INVISIBLE);
        fl.addView(view1);
    }

    private void deleteDummyIfExist(FlowLayout fl){
        View dummy = fl.findViewWithTag(DUMMY_VIEW);
        if (dummy != null)
            ((FlowLayout) dummy.getParent()).removeView(dummy);
    }

    public void removeTag(int tagNum){
        View tag = view.findViewWithTag(tagNum);
        ((ViewGroup)tag.getParent()).removeView(tag);

        setFlHeightIfEmpty(fl0);
        setFlHeightIfEmpty(fl1);

        tagList.remove(tagNum);
        for (int j = 0; j < tagList.size(); j++) {
            tagList.get(j).updateDataNum(j);
        }
    }

    public void updateTag(int tagNum, @NonNull String string){
        String[] strings = string.split(delimiter);
        View tag = view.findViewWithTag(tagNum);
        FlowLayout currentFl = (FlowLayout) tag.getParent();
        boolean visibility = Boolean.parseBoolean(strings[2]);
        if (currentFl.equals(fl0) && !visibility){
            innerUpdateTag(tag, currentFl, fl1, visibility, tagNum, string);
        } else if (currentFl.equals(fl1) && visibility) {
            innerUpdateTag(tag, currentFl, fl0, visibility, tagNum, string);
        } else {
            TempItemTag itemTag = tagList.get(tagNum);
            itemTag.updateValue(string);
        }
    }

    private void innerUpdateTag(View tag, FlowLayout currentFl, FlowLayout otherFl, boolean visibility, int tagNum, String value){
        currentFl.removeView(tag);
        setFlHeightIfEmpty(currentFl);
        int i = getInsertPos(tagNum, visibility);
        TempItemTag itemTag = new TempItemTag(tagNum, value, dataNum, (EditTemplateFragment)getFragment());
        tagList.set(tagNum, itemTag);
        otherFl.addView(itemTag.buildView(), i);
    }

    private int getInsertPos(int tagNum, boolean visibility){
        if (getData().data == null || getData().data.isEmpty())
            return 0;

        int count =0;

        for (int i = 0; i < getData().data.size(); i++) {
            if (tagNum == i) break;

            String value = (String)getData().data.get(Integer.toString(i));
            if (value == null) continue;

            String[] strings = value.split(delimiter);
            boolean b = Boolean.parseBoolean(strings[2]);
            if (visibility == b)
                count++;
        }

        return count;
    }

    public void updateName(String title){
        setNullableText(name, title);
    }
}
