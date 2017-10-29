/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Adapters;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupprotlib.Entity.User;
import com.cks.hiroyuki2.worksupport3.Fragments.AddGroupFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.SocialFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.refactor.library.SmoothCheckBox;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.VISIBLE;

/**
 * ユーザーリストの表示するRecyclerView
 * {@link AddGroupFragment}と{@link SocialFragment}からnewされることに注意してください
 */

public class SocialListRVAdapter extends RecyclerView.Adapter implements SmoothCheckBox.OnCheckedChangeListener{

    private static final String TAG = "MANUAL_TAG: " + SocialListRVAdapter.class.getSimpleName();
    List<User> list;
    private List<Boolean> checkList;
    private Fragment fragment;
    private LayoutInflater inflater;
    private Drawable defBackIcon;
    private List<String> newUserUids = new ArrayList<>();

    public SocialListRVAdapter(@NonNull List<User> list, Fragment fragment){
        super();
        this.list = list;
        this.fragment = fragment;
        inflater = fragment.getLayoutInflater();
        defBackIcon = new ColorDrawable(ContextCompat.getColor(fragment.getContext(), R.color.colorAccent));

        if (fragment instanceof AddGroupFragment){
            checkList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                checkList.add(false);
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.seem) View seem;
        @BindView(R.id.name) TextView name;
        @BindView(R.id.icon) CircleImageView icon;
        @BindView(R.id.icon_inner) ImageView iconInner;
        @BindView(R.id.container) LinearLayout container;
        @BindView(R.id.checkbox) SmoothCheckBox checkBox;
        @BindColor(R.color.green_light) int greenLight;
        @BindColor(android.R.color.white) int white;
        ViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.social_list_rv_item, parent, false);
        ButterKnife.bind(this, v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).container.setTag(position);
        User user = list.get(position);
        String name = user.getName();
        if (name == null)
            return;
        ((ViewHolder)holder).name.setText(name);

        int color;
        if (newUserUids.contains(user.getUserUid()))
            color = ((ViewHolder) holder).greenLight;
        else
            color = ((ViewHolder) holder).white;

        ((ViewHolder) holder).container.setBackgroundColor(color);

        setImgOnCircle(user, ((ViewHolder) holder).icon, ((ViewHolder) holder).iconInner);

        if (fragment instanceof AddGroupFragment){
            ((ViewHolder) holder).checkBox.setTag(position);
            ((ViewHolder) holder).checkBox.setVisibility(VISIBLE);
            ((ViewHolder) holder).checkBox.setOnCheckedChangeListener(this);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @OnClick(R.id.container)
    void onClickItem(LinearLayout container) {
        if (fragment instanceof SocialFragment) {

        } else if (fragment instanceof AddGroupFragment){
            SmoothCheckBox checkBox = container.findViewById(R.id.checkbox);
            checkBox.setChecked(!checkBox.isChecked(), true);
        }
    }

    /**
     * @return フィールド変数ではなく、一旦newして参照を別にしてから返します。
     */
    List<User> retrieveCheckedUser(){
        List<User> newList = new ArrayList<>();
        for (User user: list) {
            if (user.getIsChecked()){
                newList.add(user);
            }
        }
        
        return new ArrayList<>(newList);
    }

    @Override
    public void onCheckedChanged(SmoothCheckBox smoothCheckBox, boolean b) {
        int pos = (int)smoothCheckBox.getTag();
        checkList.set(pos, b);

        int i = 0;
        for (boolean check: checkList)
            if (check)
                i++;

        if (b && i==1)
            //新しく1コチェックされた
            ((AddGroupFragment)fragment).kickShowFab();
        else if (i==0)
            //チェックされた項目がない
            ((AddGroupFragment)fragment).kickHideFab();
    }

    @NonNull
    public List<User> getCheckedUsers(){
        List<Integer> listInt = new ArrayList<>();
        for (int i = 0; i < checkList.size(); i++) {
            if (checkList.get(i))
                listInt.add(i);
        }
        List<User> checkedUsers = new ArrayList<>();
        for (int i : listInt) {
            checkedUsers.add(list.get(i));
        }
        return checkedUsers;
    }

    private void setImgOnCircle(User user, final CircleImageView civ, final ImageView iv){
        if (user.getPhotoUrl() != null && !user.getPhotoUrl().equals("null")){
            Picasso.with(iv.getContext())
                    .load(user.getPhotoUrl())
                    .into(civ, new Callback() {
                        @Override
                        public void onSuccess() {}

                        @Override
                        public void onError() {
                            SocialListRVAdapter.this.onError(civ, iv);
                        }
                    });
        } else {
           onError(civ, iv);
        }
    }

    private void onError(final CircleImageView civ, final ImageView iv){
        civ.setImageDrawable(defBackIcon);
        iv.setImageResource(R.drawable.ic_face_white_48dp);
    }

    public void updateAllItem(@NonNull List<User> newUserList, @NonNull List<String> uids){
        this.list = newUserList;
        this.newUserUids = uids;
        notifyDataSetChanged();
    }
}
