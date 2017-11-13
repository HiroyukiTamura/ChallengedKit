/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.cks.hiroyuki2.worksupport3.R;
import com.example.hiroyuki3.worksupportlibw.Adapters.AboutVPAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.PageScrollStateChanged;
import org.androidannotations.annotations.PageSelected;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

import butterknife.OnClick;
import butterknife.OnItemSelected;
import icepick.Icepick;
import icepick.State;

import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickDialogInOnClick;
import static com.cks.hiroyuki2.worksupport3.DialogFragments.AboutDialogFragment.CALLBACK_LAUNCHER_ICON;
import static com.cks.hiroyuki2.worksupport3.DialogFragments.AboutDialogFragment.CALLCACK_IMG;
import static com.cks.hiroyuki2.worksupport3.DialogFragments.AboutDialogFragment.TAG_IMG;
import static com.cks.hiroyuki2.worksupport3.DialogFragments.AboutDialogFragment.TAG_LAUNCHER_ICON;
import static com.cks.hiroyuki2.worksupprotlib.TemplateEditor.initDefaultTemplate;
import static com.cks.hiroyuki2.worksupprotlib.Util.DEFAULT;
import static com.cks.hiroyuki2.worksupprotlib.Util.makeScheme;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;

@EFragment(R.layout.fragment_about2)
public class AboutFragment extends Fragment implements AboutVPAdapter.IAboutVPAdapter{
    private static final String TAG = "MANUAL_TAG: " + AboutFragment.class.getSimpleName();
    private static final String TOS_URL = "http://freqmodu874.hatenadiary.com/entry/2017/08/21/023601";

    private AboutVPAdapter adapter;
    @State int currentPos;
    @ViewById(R.id.vp) ViewPager vp;
    @ViewById(R.id.tab) TabLayout tab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @AfterViews
    public void afterViews(){
        vp.setAdapter(new AboutVPAdapter(getContext(), this));
        vp.setCurrentItem(currentPos, false);
        tab.setupWithViewPager(vp);
    }

    @PageSelected(R.id.vp)
    void onItemSelect(){
        currentPos = vp.getCurrentItem();
    }

    @Override
    public void onClickLibItem() {
        new LibsBuilder()
                .withLibraries("android-drag-FlowLayout", "autofittextview", "material_calendarview")
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withActivityTitle(getString(R.string.license))
                .withAboutAppName(getString(R.string.app_name))
                .start(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.unbind();
    }

    @Override
    public void onClickLauncher() {
        kickDialogInOnClick(TAG_LAUNCHER_ICON, CALLBACK_LAUNCHER_ICON, new Bundle(), this);
    }

    @Override
    public void onClickAppLicense() {
        kickDialogInOnClick(TAG_IMG, CALLCACK_IMG, new Bundle(), this);
    }

    @Override
    public void onClickAppTos() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TOS_URL));
        startActivity(intent);
    }

    @Override
    public void onClickResetData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            onError(this, TAG+"user == null", R.string.error);
            return;
        }

        boolean b = initDefaultTemplate(getContext());
        if (!b){
            onError(this, TAG+"applyTemplate returns false", R.string.error);
            return;
        }

        String uid = user.getUid();
        HashMap<String, Object> map = new HashMap<>();
//        map.put(makeScheme("userData", uid, "registeredDate"), date);
        map.put(makeScheme("userData", uid, "template"), DEFAULT);
        map.put(makeScheme("userData", uid, "group", DEFAULT), DEFAULT);
        map.put(makeScheme("friend", uid, DEFAULT, "name"), DEFAULT);
        map.put(makeScheme("friend", uid, DEFAULT, "photoUrl"), DEFAULT);
        map.put(makeScheme("userParam", uid), DEFAULT);
        //todo 次回ここから
    }
}
