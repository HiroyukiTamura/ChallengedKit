/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.AppLaunchChecker;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.Space;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.cks.hiroyuki2.worksupport3.BackService;
import com.cks.hiroyuki2.worksupport3.Fragments.AboutFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.AboutFragment_;
import com.cks.hiroyuki2.worksupport3.Fragments.AnalyticsFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.EditTemplateFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.GroupSettingFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.RecordFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.SettingFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.SettingFragment_;
import com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment;
import com.cks.hiroyuki2.worksupport3.Fragments.SocialFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.ServiceConnector;
import com.cks.hiroyuki2.worksupport3.Util;
import com.cks.hiroyuki2.worksupprotlib.Entity.Group;
import com.cks.hiroyuki2.worksupprotlib.Entity.GroupInUserDataNode;
import com.cks.hiroyuki2.worksupprotlib.Entity.User;
import com.cks.hiroyuki2.worksupprotlib.FirebaseConnection;
import com.cks.hiroyuki2.worksupprotlib.LoginCheck;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;

import java.util.Calendar;
import java.util.List;

import icepick.Icepick;
import icepick.State;
import io.fabric.sdk.android.Fabric;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.cks.hiroyuki2.worksupport3.BackService.UNKNOWN_STATE;
import static com.cks.hiroyuki2.worksupport3.Util.getFragmentTag;
import static com.cks.hiroyuki2.worksupport3.Util.initAdMob;
import static com.cks.hiroyuki2.worksupprotlib.LoginCheck.checkIsLogin;
import static com.cks.hiroyuki2.worksupprotlib.TemplateEditor.initDefaultTemplate;
import static com.cks.hiroyuki2.worksupprotlib.Util.DATE_PATTERN_DOT_YMD;
import static com.cks.hiroyuki2.worksupprotlib.Util.NOTIFICATION_CHANNEL;
import static com.cks.hiroyuki2.worksupprotlib.Util.PREF_KEY_WIDTH;
import static com.cks.hiroyuki2.worksupprotlib.Util.PREF_NAME;
import static com.cks.hiroyuki2.worksupprotlib.Util.RC_SIGN_IN;
import static com.cks.hiroyuki2.worksupprotlib.Util.cal2date;
import static com.cks.hiroyuki2.worksupprotlib.Util.datePattern;
import static com.cks.hiroyuki2.worksupprotlib.Util.getMonthIllust;
import static com.cks.hiroyuki2.worksupprotlib.Util.getToolBarHeight;
import static com.cks.hiroyuki2.worksupprotlib.Util.getUserMe;
import static com.cks.hiroyuki2.worksupprotlib.Util.logAnalytics;
import static com.cks.hiroyuki2.worksupprotlib.Util.onError;
import static com.cks.hiroyuki2.worksupprotlib.UtilSpec.getFabLp;
import static com.example.hiroyuki3.worksupportlibw.Adapters.AboutVPAdapter.PREF_KEY_SHOW_NAV_IMG;

import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseUser;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener/*, CalenderFragment.OnFragmentInteractionListener*/,
        SocialFragment.IOnCompleteGroup, AnalyticsFragment.OnHamburgerClickListener, FragmentManager.OnBackStackChangedListener {

    private static final String TAG = "MANUAL_TAG: " + MainActivity.class.getSimpleName();
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    static final int REQ_CODE_EDIT_MY_PROFILE = 1000;
    static final int REQ_CODE_ADD_USER = 1100;
    static final int REQ_OPEN_ICON_IMG = 1200;
    static final int REQ_CODE_GROUP_SETTING = 1300;
    private static final int REQ_CODE_TUTORIAL = 1400;
    private ServiceConnector connector;
    private SharedPreferences pref;
    private LoginCheck check;
    private boolean isSavedInstanceState = false;
    @State boolean isFirstLaunch = false;
//    private MultiplePermissionsListener listener;

    @ViewById(R.id.appbar) AppBarLayout appbar;
    @ViewById(R.id.toolbar) public Toolbar toolbar;
    @ViewById(R.id.fab) public FloatingActionButton fab;
    @ViewById(R.id.drawer_layout) DrawerLayout drawer;
    @ViewById(R.id.nav_view) NavigationView navigationView;
    @ViewById(R.id.fragment_container_ll) LinearLayout container_outer;
    @ViewById(R.id.toolbar_shadow) View toolbarShadow;
    @ViewById(R.id.fragment_container) FrameLayout fragContainer;
    private int toolbarHeight;
    @BackService.socialState @State public int socialDbState = UNKNOWN_STATE;
//    @Extra String groupKey;
    @org.androidannotations.annotations.res.StringRes(R.string.ntf_channel) String channelName;
    @org.androidannotations.annotations.res.StringRes(R.string.ntf_channel_description) String channelDsc;
    @ColorRes(R.color.colorPrimary) int colorNavHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        isSavedInstanceState = savedInstanceState != null;

        Fabric.with(this, new Crashlytics());
        logAnalytics(getClass().getSimpleName() + "起動", this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

//        initDefaultTemplate(this);

        pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if(!AppLaunchChecker.hasStartedFromLauncher(this)){
            if (initDefaultTemplate(this)){
                AppLaunchChecker.onActivityCreate(this);
                isFirstLaunch = true;
            } else
                onError(this, "onCreate: !initDefaultTemplate()", null);
        }

        if (connector == null){
            //ブロードキャストレシーバーの登録
            connector = new ServiceConnector(this);
            connector.setIntentFilter();
            connector.startService();
        }

        initNtfChannel();
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (isFirstLaunch){
            Intent intent = new Intent(this, TutorialActivity_.class);
            startActivityForResult(intent, REQ_CODE_TUTORIAL);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    private void initNtfChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = NOTIFICATION_CHANNEL;
            String channelName = getString(R.string.ntf_channel);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDsc);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }


    @AfterViews
    void onAfterViews(){
//        saveWindowWidth();

        if (isFirstLaunch)
            return;

        setSupportActionBar(toolbar);
        toolbarHeight = getToolBarHeight(this);

        if(!getSupportFragmentManager().getFragments().isEmpty())
            initToolBar(getSupportFragmentManager().getFragments().get(0));

        initAdMob(this);

//        FirebaseConnection.getInstance().setmAuth(this);
//        FirebaseConnection.getInstance().setmAuthListener();
//        FirebaseConnection.getInstance().setChildListener();
//        FirebaseConnection.getInstance().setUserId(this);
//        FirebaseConnection.getInstance().setFireBaseRefs();

        //fabを設定
        fab.setLayoutParams(getFabLp(this));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        showNavHeader(pref.getBoolean(PREF_KEY_SHOW_NAV_IMG, true));

        navigationView.setNavigationItemSelectedListener(this);

        loginCheck();

//        setContentFragment();

//        signIn();
    }

    @Override
    public void onCompleteGroup(@NonNull Group group) {
        changeContentFragment(com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment_
                .builder()
                .group(group)
                .build());
    }

    @Override
    public void onClickGroupItem(@NonNull GroupInUserDataNode groupNode) {
        changeContentFragment(com.cks.hiroyuki2.worksupport3.Fragments.ShareBoardFragment_
                .builder()
                .groupNode(groupNode)
                .build());
    }

    @Override
    public void onHamburgerClick() {
        drawer.openDrawer(Gravity.START);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        saveWindowWidth();
        super.onConfigurationChanged(newConfig);
    }

    private void saveWindowWidth(){
        drawer.getRootView().getWidth();
        SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_KEY_WIDTH,  drawer.getRootView().getWidth());
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof AnalyticsFragment){
            if (((AnalyticsFragment) fragment).isSheetVisible()){
                ((AnalyticsFragment) fragment).hideSheet();
                return;
            }
        }

        List<Fragment> list = getSupportFragmentManager().getFragments();
        Log.d(TAG, "onBackPressed: " + list.toString());
        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStack();
        else
            finish();//最初のFragmentだったらfinish()
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: fire");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        MenuItem calendar = menu.getItem(0);
        calendar.setVisible(fragment instanceof ShareBoardFragment);
        MenuItem setting = menu.getItem(1);
        setting.setVisible(fragment instanceof ShareBoardFragment);
        return true;
    }

    @OptionsItem(R.id.calendar)
    void onSelectMenuCalendar(){
        onClickMenuCalendar();
    }

    @OptionsItem(R.id.action_settings)
    void onSelectMenuSetting(){
        onClickMenuSetting();
    }

    private void onClickMenuCalendar(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof ShareBoardFragment){
            Group group = ((ShareBoardFragment)fragment).getGroup();
            showSharedCalendarActivity(group);
        }

        invalidateOptionsMenu();
    }

    private void onClickMenuSetting(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof ShareBoardFragment){
            Intent intent = new Intent(this, com.cks.hiroyuki2.worksupport3.Activities.GroupSettingActivity_.class);
            intent.putExtra("group", ((ShareBoardFragment) fragment).getGroup());
            startActivityForResult(intent, REQ_CODE_GROUP_SETTING);
        }

        invalidateOptionsMenu();
    }

    private void showSharedCalendarActivity(@NonNull Group group){
        com.cks.hiroyuki2.worksupport3.Activities.SharedCalendarActivity_.intent(this).group(group).start();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.d(TAG, "onNavigationItemSelected: fire pushed:" + getResources().getResourceName(id));
        if (id == R.id.nav_camera) {
            changeContentFragment(getRecordFragmentInstance());
        } else if (id == R.id.nav_slideshow) {
            changeContentFragment(com.cks.hiroyuki2.worksupport3.Fragments.EditTemplateFragment_
                    .builder()
                    .build());
        } else if (id == R.id.nav_manage) {
            container_outer.setPadding(0, 0, 0, 0);
            if (getUserMe() != null){
                String uid = getUserMe().getUid();
                changeContentFragment(com.cks.hiroyuki2.worksupport3.Fragments.AnalyticsFragment_
                        .builder()
                        .uid(uid)
                        .isMine(true)
                        .build());
            } else {
                Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.social){
            changeContentFragment(com.cks.hiroyuki2.worksupport3.Fragments.SocialFragment_
                    .builder()
                    .build());
        } else if (id == R.id.nav_share) {
            changeContentFragment(AboutFragment_.builder().build());
        } else if (id == R.id.menu_help) {
            Intent i = new Intent(this, HelpActivity_.class);
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        connector.unRegisterReceiver();
        unbindService(connector);
        super.onDestroy();
    }

    @Click(R.id.fab)
    void onClickFab(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof ShareBoardFragment)
            ((ShareBoardFragment)fragment).showAddItemDialog();
    }

    @Override
    public void onBackStackChanged() {
        if(!getSupportFragmentManager().getFragments().isEmpty())
            initToolBar(getSupportFragmentManager().getFragments().get(0));
    }

    private void setContentFragment(){
        Log.d(TAG, "setContentFragment: fire");

        if (getSupportFragmentManager().getBackStackEntryCount() == 0){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            RecordFragment fragment = getRecordFragmentInstance();
            String tag = Util.getFragmentTag(fragment);
            ft.add(R.id.fragment_container, fragment, tag);
            ft.addToBackStack(tag);
            ft.commit();
        } else {
            //onCreate()->saveinstance有
            getSupportFragmentManager().popBackStack();
        }
    }

    private void changeContentFragment(Fragment fragment){
        String tag = Util.getFragmentTag(fragment);
        Fragment fragmentOld = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (tag.equals(fragmentOld.getTag()))//現在のfragmentが同じなら何もしない
            return;

        changeToolbarTitle(fragment);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment, tag);
        ft.addToBackStack(tag);

        List<Fragment> list = getSupportFragmentManager().getFragments();
        if (list.size() > 3){
            Fragment old = list.get(0);
            ft.remove(old);
        }

        ft.commit();
    }

    //region toolbarまわり
    private void changeToolbarTitle(Fragment fragment){
        if (!(fragment instanceof AnalyticsFragment))
            toolbar.setTitle(getString(R.string.app_name));
    }

    public void initToolBar(Fragment attachedFrag){
        supportInvalidateOptionsMenu();

        if (attachedFrag instanceof RecordFragment){
            innerInitToolBar(false, 0, true);//タイトルはRecordFragment側でいじる
            fab.hide();
        } else if (attachedFrag instanceof AboutFragment){
            innerInitToolBar(true, R.string.item4, true);
            fab.hide();
        } else if (attachedFrag instanceof EditTemplateFragment){
            innerInitToolBar(true, R.string.item1, true);
            fab.hide();
        } else if (attachedFrag instanceof SocialFragment){
            innerInitToolBar(true, R.string.item3, true);
            fab.hide();
        } else if (attachedFrag instanceof AnalyticsFragment) {
            innerInitToolBar(false, 0, false);
            fab.hide();
        } else if (attachedFrag instanceof SettingFragment) {
            innerInitToolBar(true, R.string.setting_toolbar_title, true);
            fab.hide();
        }
    }

    public void initToolBar(ShareBoardFragment fragment, @NonNull String title){
        supportInvalidateOptionsMenu();

        setToolBarShadowVisibility(true);
        setToolbarTitle(title);
        setToolBarVisibility(true);
    }

    private void innerInitToolBar(boolean toolbarShadow, @StringRes int titleRes, boolean toolbarVisibility){
        setToolBarShadowVisibility(toolbarShadow);
        setToolbarTitle(titleRes);
        setToolBarVisibility(toolbarVisibility);
    }

    private void setToolBarShadowVisibility(boolean visibility){
        if (visibility)
            toolbarShadow.setVisibility(VISIBLE);
        else
            toolbarShadow.setVisibility(INVISIBLE);
    }

    private void setToolBarVisibility(boolean toolbarVisibility){
        if (getSupportActionBar() != null)
            if (toolbarVisibility) {
                fragContainer.setPadding(0, toolbarHeight, 0, 0);
                getSupportActionBar().show();
            } else {
                fragContainer.setPadding(0, 0, 0, 0);
                getSupportActionBar().hide();
            }

    }
    //endregion

    public void showFab(@DrawableRes int imgRes){
        fab.show();
        fab.setImageResource(imgRes);
    }

    public void notifyFriendChanged(@NonNull List<User> userList, @NonNull List<String> newUserUids){
        for (Fragment frag: getSupportFragmentManager().getFragments()) {
            if (frag instanceof  SocialFragment){
                ((SocialFragment)frag).updateFriend(userList, newUserUids);
                return;
            }
        }
    }

    @OnActivityResult(RC_SIGN_IN)
    void handleSignInResponse(Intent data, int resultCode) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == ResultCodes.OK) {
            Log.w(TAG, "handleSignInResponse: Successfully signed in");
            //結局今のところは、乱数を作成してログインしている。後々要修正。
            check.writeLocalProf();
            boolean isPreSetting = data.getBooleanExtra(LoginCheck.IS_PRE_SETTING, false);
            if (isPreSetting){
                changeContentFragment(com.cks.hiroyuki2.worksupport3.Fragments.SettingFragment_
                        .builder().build());
            } else {
                FirebaseConnection.getInstance().setFireBaseRefs(this);
                if (!isSavedInstanceState)
                    setContentFragment();
            }

        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                onError(this, "handleSignInResponse: User pressed back button", R.string.error);
                finish();
                return;
            }

            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                onError(this, "handleSignInResponse: no_internet_connection", R.string.no_network_msg);
                finish();
                return;
            }

            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                onError(this, "handleSignInResponse: unknown_error", R.string.error);
                finish();
                return;
            }

            onError(this, "handleSignInResponse: unknown_sign_in_response", R.string.error);
        }
    }

    @OnActivityResult(REQ_CODE_TUTORIAL)
    void onResultTutorial(){
        isFirstLaunch = false;
        onAfterViews();
    }

    public void editMyProf(){
        if (checkIsLogin()){
            changeContentFragment(SettingFragment_.builder().build());
        } else {
            Toast.makeText(this, R.string.exe_logout, Toast.LENGTH_LONG).show();
            new LoginCheck(this).signIn(true);
        }
    }

    @OnActivityResult(REQ_CODE_GROUP_SETTING)
    void onResultExitGroup(int requesCode, @OnActivityResult.Extra(GroupSettingFragment.GROUP) Group group){
        if (requesCode != Activity.RESULT_OK)
            return;

        FirebaseUser userMe = getUserMe();
        if (userMe == null){
            onError(this, TAG+"userMe == null", R.string.error);
            return;
        }

        String tag = SocialFragment.class.getSimpleName();
        getSupportFragmentManager().popBackStack(tag, 0);
        SocialFragment fragment = (SocialFragment) getSupportFragmentManager().findFragmentByTag(tag);
        fragment.exitGroup(getUserMe().getUid(), group.groupKey, R.string.exit_group_toast);
    }

    private RecordFragment getRecordFragmentInstance(){
        Calendar cal = Calendar.getInstance();
        int year = pref.getInt(RecordFragment.MEDIAN_YEAR, cal.get(Calendar.YEAR));
        int month = pref.getInt(RecordFragment.MEDIAN_MONTH, cal.get(Calendar.MONTH));
        int day = pref.getInt(RecordFragment.MEDIAN_DAY, cal.get(Calendar.DAY_OF_MONTH));
        return com.cks.hiroyuki2.worksupport3.Fragments.RecordFragment_.
                builder()
                .yearMed(year)
                .monMed(month)
                .dayMed(day)
                .build();
    }

    private void loginCheck(){
        check = new LoginCheck(this);
        if (checkIsLogin()){
            Log.i(TAG, "loginCheck: check.checkIsLogin()");
            check.writeLocalProf();
            FirebaseConnection.getInstance().setFireBaseRefs(this);
            if (!isSavedInstanceState)
                setContentFragment();
        } else {
            check.signIn(false);
        }
    }

    public void setToolbarTitle(@Nullable String title){
        toolbar.setTitle(title);
    }

    public void setToolbarTitle(@StringRes int title){
        if (title != 0)
            toolbar.setTitle(title);
    }

    public LoginCheck getLoginCheck(){
        return check;
    }

    public void showNavHeader(boolean show){
        FrameLayout navRoot = (FrameLayout) navigationView.getHeaderView(0);
        LinearLayout headerLL = navRoot.findViewById(R.id.nav_wrapper);
        Space space = navRoot.findViewById(R.id.header_space);
        if (show){
            int mon = Calendar.getInstance().get(Calendar.MONTH);
            TextView tv = headerLL.findViewById(R.id.header_tv);
            if (mon == -1){
                headerLL.setVisibility(GONE);
                space.setVisibility(VISIBLE);
                return;
            } else if (mon == 10 || mon == 11 || mon == 0 || mon == 1){
                tv.setTextColor(colorNavHeader);
            }
            headerLL.setVisibility(VISIBLE);
            space.setVisibility(GONE);
            headerLL.setBackgroundResource(getMonthIllust(mon));
            tv.setText(cal2date(Calendar.getInstance(), DATE_PATTERN_DOT_YMD));
        } else {
            headerLL.setVisibility(GONE);
            space.setVisibility(VISIBLE);
        }
    }
}
