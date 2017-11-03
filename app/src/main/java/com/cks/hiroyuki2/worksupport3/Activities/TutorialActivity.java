package com.cks.hiroyuki2.worksupport3.Activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.cks.hiroyuki2.worksupport3.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringRes;

@EActivity
public class TutorialActivity extends AppIntro {

    private static final String TAG = "MANUAL_TAG: " + TutorialActivity.class.getSimpleName();
    private AppIntroFragment firstFrag;
    private AppIntroFragment currentFrag;

    @StringRes(R.string.title0) String title0;
    @StringRes(R.string.title1) String title1;
    @StringRes(R.string.title2) String title2;
    @StringRes(R.string.title3) String title3;
    @StringRes(R.string.desc0) String desc0;
    @StringRes(R.string.desc1) String desc1;
    @StringRes(R.string.desc2) String desc2;
    @StringRes(R.string.desc3) String desc3;
    @ColorRes(R.color.colorPrimary) int cb0;
    @ColorRes(R.color.bc_input) int cb1;
    @ColorRes(R.color.bc_analytics) int cb2;
    @ColorRes(R.color.bc_meeting) int cb3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firstFrag = makeSlide(title0, desc0, R.drawable.mixer, cb0);
        addSlide(firstFrag);
        addSlide(makeSlide(title1, desc1, R.drawable.tutorial_input, cb1));
        addSlide(makeSlide(title2, desc2, R.drawable.tutorial_analytics, cb2));
        addSlide(makeSlide(title3, desc3, R.drawable.tutorial_meeting, cb3));

        setFadeAnimation();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        currentFrag = (AppIntroFragment) newFragment;
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        setResult(Activity.RESULT_OK);
        finish();
    }

    @NonNull
    private AppIntroFragment makeSlide(String title, String desc, @DrawableRes int imgRes, @ColorInt int color){
        return AppIntroFragment.newInstance(title, null, desc, null, imgRes, color, Color.WHITE, Color.WHITE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (currentFrag.equals(firstFrag)){
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }
}
