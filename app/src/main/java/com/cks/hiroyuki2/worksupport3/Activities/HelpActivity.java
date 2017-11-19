package com.cks.hiroyuki2.worksupport3.Activities;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.cks.hiroyuki2.worksupport3.Fragments.BlankFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.BlankFragment_;
import com.cks.hiroyuki2.worksupport3.Fragments.HelpFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.HelpFragment_;
import com.cks.hiroyuki2.worksupport3.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import static com.cks.hiroyuki2.worksupport3.Util.initAdMob;

@SuppressLint("Registered")
@EActivity(R.layout.activity_help)
public class HelpActivity extends AppCompatActivity implements HelpFragment.IHelpFragment{

    @ViewById(R.id.toolbar) Toolbar toolbar;
    @ViewById(R.id.fragment_container) FrameLayout fl;

    @AfterViews
    void afterViews(){
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initAdMob(this);
        setFragment();
    }

    private void setFragment(){
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        HelpFragment fragment = HelpFragment_.builder().build();
        t.add(R.id.fragment_container, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickItem(int tag) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        BlankFragment fragment = BlankFragment_.builder().tag(tag).build();
        t.replace(R.id.fragment_container, fragment);
        t.commit();
    }
}
