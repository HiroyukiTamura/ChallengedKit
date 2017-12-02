/*
 * Copyright 2017 Hiroyuki Tamura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupprotlib.Fab;
import com.cks.hiroyuki2.worksupport3.R;
import com.example.hiroyuki3.worksupportlibw.Adapters.AnalyticsVPAdapter;
import com.example.hiroyuki3.worksupportlibw.Presenter.AnalyticsVPUiOperator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.gordonwong.materialsheetfab.MaterialSheetFab;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;
import org.androidannotations.annotations.res.DrawableRes;
import org.apmem.tools.layouts.FlowLayout;

import java.util.List;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import icepick.Icepick;
import icepick.State;

import static com.cks.hiroyuki2.worksupprotlib.Util.getToolBarHeight;
import static com.cks.hiroyuki2.worksupprotlib.Util.makeCircleAndTxt;
import static com.cks.hiroyuki2.worksupprotlib.Util.time2String;
import static com.example.hiroyuki3.worksupportlibw.Adapters.AnalyticsVPAdapter.OFFSET;

@EFragment(R.layout.analytics_vp)
public class AnalyticsFragment extends Fragment implements IValueFormatter, ViewPager.OnPageChangeListener, AnalyticsVPUiOperator.IAnalyticsVPUiOperator {
    private static final String TAG = "MANUAL_TAG: " + AnalyticsFragment.class.getSimpleName();

    @ViewById(R.id.vertical_vp) VerticalViewPager vp;
//    @ViewById(R.id.wof_container) LinearLayout wofLL;
    @ViewById(R.id.hamburger) ImageView hamburger;
    @ViewById(R.id.in_wof_ll) LinearLayout wofLL;
//    @ViewById(R.id.spacer) public Space space;
    @ViewById(R.id.fab) Fab fab;
    @ViewById(R.id.fab_sheet) View sheetView;
    @ViewById(R.id.overlay) View overlay;
    @ViewById(R.id.fl) FlowLayout fl;
    @ColorRes(R.color.blue_gray) int fabSheetCol;
    @ColorRes(R.color.colorAccent) int fabColor;
    @ColorRes(R.color.cardview_dark_background) int textColor;
    @DimensionPixelSizeRes(R.dimen.tooltip_margin_table) int margin;
    @DrawableRes(R.drawable.circle) Drawable circle;
    private OnHamburgerClickListener mListener;
    @FragmentArg("uid") public String uid;
    @FragmentArg("isMine") boolean isMine;
    private View rootView;
    private Context context;
    private MaterialSheetFab materialSheetFab;//fabには特にリスナをセットしなくても自動的に動く
    private AnalyticsVPAdapter adapter;
    @State int currentPos = -1;
    @State int scrollX = -1;

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnHamburgerClickListener) {
            mListener = (OnHamburgerClickListener) context;
        }
    }

    public interface OnHamburgerClickListener {
        void onHamburgerClick();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        context = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (AnalyticsVPUiOperator operator: adapter.getOperators().values()) {
            operator.unbind();
        }
    }

    public View getRootView() {
        return rootView;
    }

    @AfterViews
    void onAfterViews(){
        rootView = getView();

        if (!isMine){
            hamburger.setImageResource(R.drawable.ic_arrow_back_white_24dp);
        }

        showWeekOfDay();
        adapter = new AnalyticsVPAdapter(getContext(), this, uid, getToolBarHeight(getContext()));
        vp.setAdapter(adapter);
        int pos = currentPos == -1 ?
                AnalyticsVPAdapter.PAGE/2 : currentPos;
        vp.setCurrentItem(pos);
        vp.setOffscreenPageLimit(OFFSET);

        vp.setOnPageChangeListener(null);//一応これしておく。stack over flowしてしまうかもしれないから。
        if (scrollX != -1){
            for (AnalyticsVPUiOperator operator: adapter.getOperators().values()) {
                operator.scroll(operator.getHsv(), scrollX);
            }
        }

        vp.setOnPageChangeListener(this);
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, Color.WHITE, fabColor);
        editLegend(vp.getCurrentItem());
    }

    private void showWeekOfDay(){
        //曜日を表示する
        String[] wof = getResources().getStringArray(R.array.dof);
        for (int i = 0; i <7 ; i++) {
            TextView tv = new TextView(getContext());
            tv.setText(wof[i]);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            lp.weight = 1;
            tv.setLayoutParams(lp);
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(16);
            tv.setGravity(Gravity.CENTER);
            if (wof[i].equals("日"))
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.red_anton));
            wofLL.addView(tv);
        }
    }

    @Override
    public void onClickUpBtn() {
        vp.setCurrentItem(vp.getCurrentItem()-1);
    }

    @Override
    public void onClickDownBtn() {
        vp.setCurrentItem(vp.getCurrentItem()+1);
    }

    public LinearLayout getWofLL(){
        return wofLL;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        int hour = (int)entry.getX();
        int min = Math.round((entry.getX() - hour) * 60);
        return time2String(hour, min);
    }

    @Click(R.id.hamburger)
    void openNavigation(){
        mListener.onHamburgerClick();
    }

    @Override
    public void onScrollChanged(HorizontalScrollView horizontalScrollView, int x) {
        if (fab == null) //時々NPEで落ちる
            return;

        if (x >= 0){
            scrollX = x;
        }

        if (x > 0){
            fab.hide();
        } else {
            fab.show();
        }
    }

    private void innerEditLegend(List<Pair<Integer, String>> legendList){
        for (Pair<Integer, String> pair: legendList) {
            View v = makeCircleAndTxt(getContext(), pair.second, pair.first);
            v.setPadding(margin, 0, margin, 0);
            if (v == null)
                continue;
            TextView tv = v.findViewById(R.id.tv);
            tv.setTextColor(textColor);
            tv.setTextSize(18);
            fl.addView(v);
        }
    }

    @Override
    public void onPageSelected(int position) {
        currentPos = vp.getCurrentItem();
        fl.removeAllViews();
        editLegend(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    private void editLegend(int position){
        AnalyticsVPUiOperator operator = adapter.getOperators().get(position);
        if (operator == null)//時々NPEで落ちる
            return;

        List<Pair<Integer, String>> rangeLegendList = operator.getLegendListForRange();
        List<Pair<Integer, String>> timeEveLegendList = operator.getLegendListForTimeEve();
        innerEditLegend(rangeLegendList);
        innerEditLegend(timeEveLegendList);
    }

    public boolean isSheetVisible(){
        return materialSheetFab.isSheetVisible();
    }

    public void hideSheet(){
        materialSheetFab.hideSheet();
    }
}
