/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupprotlib.Entity.User;
import com.cks.hiroyuki2.worksupport3.Fragments.GroupSettingFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickDialogInOnClick;
import static com.cks.hiroyuki2.worksupprotlib.Util.UID;
import static com.cks.hiroyuki2.worksupprotlib.Util.setNullableText;

/**
 * Groupのメンバーを表すRVAdapter. {@link GroupSettingFragment}の舎弟。
 * itemの識別には、positionを一切使わずにUIDで識別しているところがミソ。
 */

public class GroupSettingRVAdapter extends RecyclerView.Adapter implements CompoundButton.OnCheckedChangeListener{

    private GroupSettingFragment fragment;
    private List<User> userList;
    public static final String REMOVE_MEMBER = "REMOVE_MEMBER";
    public static final String USER = "USER";
    public static final int CALLBACK_REMOVE_MEMBER = 8731;
    private FirebaseUser userMe;

    public GroupSettingRVAdapter(GroupSettingFragment fragment, List<User> userList, @NonNull FirebaseUser userMe) {
        this.fragment = fragment;
        this.userList = userList;
        this.userMe = userMe;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon) CircleImageView icon;
        @BindView(R.id.switch_widget) SwitchCompat switchWidget;
        @BindView(R.id.remove) ImageButton remove;
        @BindView(R.id.name) TextView name;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = fragment.getLayoutInflater().inflate(R.layout.group_setting_rv_item, parent, false);
        ButterKnife.bind(this, v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User member = userList.get(position);
        String uid = member.getUserUid();
        ((ViewHolder)holder).remove.setTag(uid);
        ((ViewHolder) holder).switchWidget.setTag(uid);

        setNullableText(((ViewHolder) holder).name, member.name);
        Picasso.with(fragment.getContext())
                .load(member.photoUrl)
                .into(((ViewHolder) holder).icon);
        ((ViewHolder) holder).switchWidget.setOnCheckedChangeListener(this);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @OnClick(R.id.remove)
    public void onViewClicked(View v) {
        String uid = (String)v.getTag();
        if (userMe.getUid().equals(uid)) {
            fragment.onClickItemExit();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(UID, uid);
        bundle.putString("from", REMOVE_MEMBER);
        kickDialogInOnClick(REMOVE_MEMBER, CALLBACK_REMOVE_MEMBER, bundle, fragment);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }

    /**
     * @return 例外時Integer.MAX_VALUE
     */
    public int getPosFromUid(@NonNull String uid){
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserUid().equals(uid)){
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }

    public void removeMember(int pos){
        userList.remove(pos);
    }

    public User getUser(int pos){
        return userList.get(pos);
    }
}
