/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.cks.hiroyuki2.worksupport3.Activities.SharedCalendarActivity;
import com.cks.hiroyuki2.worksupport3.Adapters.SharedCalendarVPAdapter;
import com.cks.hiroyuki2.worksupprotlib.Entity.CalendarOneEvent;
import com.cks.hiroyuki2.worksupprotlib.Entity.Group;
import com.cks.hiroyuki2.worksupprotlib.RecordDataUtil;
import com.cks.hiroyuki2.worksupprotlib.FirebaseConnection;
import com.cks.hiroyuki2.worksupport3.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;

import static com.cks.hiroyuki2.worksupport3.CalendarDialogFragment.ADD_SCHEDULE;
import static com.cks.hiroyuki2.worksupport3.CalendarDialogFragment.CALENDAR;
import static com.cks.hiroyuki2.worksupport3.CalendarDialogFragment.CALLBACK_ADD_SCHEDULE;
import static com.cks.hiroyuki2.worksupport3.CalendarDialogFragment.INPUT;
import static com.cks.hiroyuki2.worksupport3.DialogKicker.kickCalendarDialog;
import static com.cks.hiroyuki2.worksupprotlib.FbCheckAndWriter.CODE_SET_VALUE;
import com.cks.hiroyuki2.worksupprotlib.FbCheckAndWriter;
import static com.cks.hiroyuki2.worksupprotlib.FirebaseConnection.getRootRef;
import static com.cks.hiroyuki2.worksupprotlib.Util.COLOR_NUM;
import static com.cks.hiroyuki2.worksupprotlib.Util.DATE_PATTERN_YM;
import static com.cks.hiroyuki2.worksupprotlib.Util.cal2date;
import static com.cks.hiroyuki2.worksupprotlib.Util.makeScheme;

/**
 * {@link SharedCalendarActivity}のひとり子分。
 * {@link SharedCalendarVPAdapter}を従える。子孫は多い！
 */
@EFragment(R.layout.fragment_shared_calendar)
public class SharedCalendarFragment extends Fragment implements ViewPager.OnPageChangeListener{

    @FragmentArg("group") Group group;
    private DatabaseReference ref;
    private SharedCalendarVPAdapter adapter;
    private SharedCalendarActivity activity;
    @ViewById(R.id.vp) ViewPager vp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ref = FirebaseConnection.getRef(getRootRef(), makeScheme("calendar", group.groupKey));
    }

    @Override
    public void onAttach(Context context) {
        activity = (SharedCalendarActivity) context;
        activity.changeToolbarTitle(Calendar.getInstance());
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        activity = null;
        super.onDetach();
    }

    @AfterViews
    void afterViews(){
        adapter = new SharedCalendarVPAdapter(this, Calendar.getInstance());
        vp.setAdapter(adapter);
        vp.setCurrentItem(adapter.getCount()/2);
        vp.addOnPageChangeListener(this);
    }

    public DatabaseReference getRef() {
        return ref;
    }

    @Override
    public void onPageSelected(int position) {
        Calendar itemCal = adapter.getCalenderOfItem(position);
        activity.changeToolbarTitle(itemCal);

        activity.showPagingPrevBtn(position != 0);
        activity.showPagingFrwBtn(position != adapter.getCount()-1);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageScrollStateChanged(int state) {}

    public void pagePrevious(){
        int pagedPos = vp.getCurrentItem()-1;
        if (pagedPos >0)
            vp.setCurrentItem(pagedPos);
    }

    public void pageForward(){
        int pagedPos = vp.getCurrentItem()+1;
        if (pagedPos <adapter.getCount())
            vp.setCurrentItem(vp.getCurrentItem()+1);
    }

    public void toggleCalendar(){
        adapter.toggleCalendar();
    }

    public void onClickFab(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(CALENDAR, adapter.getSelectedDate());
        kickCalendarDialog(bundle, ADD_SCHEDULE, CALLBACK_ADD_SCHEDULE, this);
    }

    @OnActivityResult(CALLBACK_ADD_SCHEDULE)
    void onResultAddSchedule(int resultCode,
                             @OnActivityResult.Extra(INPUT) final String input,
                             @OnActivityResult.Extra(CALENDAR) final Calendar cal,
                             @OnActivityResult.Extra(COLOR_NUM) final int colorNum){
        if (resultCode != Activity.RESULT_OK) return;

        DatabaseReference checkRef = FirebaseConnection.getRef("calendar", group.groupKey);
        FbCheckAndWriter writer = new FbCheckAndWriter(checkRef, null, getContext()/*非同期でないからok*/, null) {
            CalendarOneEvent calEve;

            @Override
            public void onSuccess(DatabaseReference ref) {
                adapter.addSchedule(cal, calEve);//ここ、今はこれで問題ないけど、変えるときは処理の順序を気を付けてください
            }

            @Override
            protected void onNodeExist(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference ref = dataSnapshot.getRef().push();
                calEve = new CalendarOneEvent(ref.getKey(), input, colorNum);
                setObj(calEve);
                String ym = cal2date(cal, DATE_PATTERN_YM);
                String d = Integer.toString(cal.get(Calendar.DATE));
                DatabaseReference writeRef = FirebaseConnection.getRef("calendar", group.groupKey, ym, d, ref.getKey());
                setWriteRef(writeRef);
                super.onNodeExist(dataSnapshot);
            }
        };
        writer.update(CODE_SET_VALUE);
    }
}
