/*
 * Copyright (c) $year. Hiroyuki Tamura All rights reserved.
 */

package com.cks.hiroyuki2.worksupport3;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.cks.hiroyuki2.worksupport3.Fragments.SettingFragment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.Contract;

import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;

/**
 * SettingFragmentのdialogを担当するおじさん！
 * @see com.cks.hiroyuki2.worksupport3.Fragments.SettingFragment
 */
public class SettingDialogFragment extends DialogFragment implements AlertDialog.OnClickListener {

    private static final String TAG = "MANUAL_TAG: " + SettingDialogFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_COMMAND = "BUNDLE_KEY_COMMAND";
    public static final int METHOD_REAUTH = 0;
    public static final String DIALOG_TAG_REAUTH = "DIALOG_TAG_REAUTH";
    public static final int METHOD_UPDATE_PW = 1;
    public static final String DIALOG_TAG_PW = "DIALOG_TAG_PW";
    public static final int METHOD_ACCOUNT_NAME = 2;
    public static final String DIALOG_TAG_NAME = "DIALOG_TAG_NAME";
    public static final int METHOD_UPDATE_EMAIL = 3;
    public static final String DIALOG_TAG_EMAIL = "DIALOG_TAG_EMAIL";
    public static final String DIALOG_TAG = "DIALOG_TAG";
    public static final int DIALOG_CALLBACK = 1;
    private AlertDialog dialog = null;
    private TextInputEditText edit;
    private View rootView;

    public static SettingDialogFragment newInstance(@NonNull Bundle bundle){
        SettingDialogFragment sdf = new SettingDialogFragment();
        sdf.setArguments(bundle);
        return sdf;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int command = getArguments().getInt(BUNDLE_KEY_COMMAND);
        switch (command){
            case METHOD_REAUTH:
                dialog = makeDialog(R.string.input_currentPw, R.string.pw_restriction);
                edit.addTextChangedListener(makePwTextWatcher());
                break;
            case METHOD_UPDATE_PW:
                dialog = makeDialog(R.string.input_newPw, R.string.pw_restriction);
                edit.addTextChangedListener(makePwTextWatcher());

                break;
            case METHOD_ACCOUNT_NAME:
                dialog = makeDialog(R.string.input_name, R.string.name_restriction);
                edit.addTextChangedListener(makeNameTextWatcher());
                break;
            case METHOD_UPDATE_EMAIL:
                dialog = makeDialog(R.string.input_mail, 0);
                break;
        }
        return dialog;//これnull返したら落ちますよ @see https://goo.gl/DDnQPd
    }

    @Contract(" -> !null")
    private TextWatcher makePwTextWatcher(){
        UtilDialog util = new UtilDialog(dialog);
        util.initView(rootView);
        util.setRestriction(R.string.pw_restriction);
        return util.createTwMin(R.integer.firebase_min_pw_len);
    }

    @Contract(" -> !null")
    private TextWatcher makeNameTextWatcher(){
        UtilDialog util = new UtilDialog(dialog);
        util.initView(rootView);
        util.setRestriction(R.string.name_restriction);
        return util.createTwMax(R.integer.firebase_account_max, true);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                TextInputEditText edit = ((AlertDialog)dialog).findViewById(R.id.edit_text);
                if (edit == null){
                    Toast.makeText(getContext(), "something wrong", Toast.LENGTH_LONG).show();//エラー処理を行ってください
                    return;
                }

                String inputStr =edit.getText().toString();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null){
                    Toast.makeText(getContext(), "something wrong", Toast.LENGTH_LONG).show();//エラー処理を行ってください
                    return;
                }

                int tag = (int)edit.getTag();
                if (tag == R.string.input_currentPw){

//                    reAuthenticate(user, email, inputStr);
                    callActivityResult(inputStr);

                } else if (tag == R.string.input_newPw){
                    callActivityResult(inputStr);
                } else if (tag == R.string.input_name){
//                    updateName(user, inputStr);
                    callActivityResult(inputStr);
                } else if (tag == R.string.input_mail){
//                    upDateEmail(user, inputStr);
                    callActivityResult(inputStr);
                }
                break;
        }
    }

    /**
     * @param titleId dialogのeditにtitleIdをタグ付けする。これは、dialogの判別に用いる。また、実際はtitleに使われずhintに使っています。
     */
    @Nullable
    private AlertDialog makeDialog(@StringRes int titleId, @StringRes int messageId){
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.dialog_content, null);
        edit = rootView.findViewById(R.id.edit_text);
        edit.setTag(titleId);//dialogのeditにtitleIdをタグ付けする。これは、dialogの判別に用いる。

        switch (titleId){
            case R.string.input_name:
                edit.setHint(messageId);
                break;
            case R.string.input_currentPw:
            case R.string.input_newPw:
                edit.setHint(messageId);
                edit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case R.string.input_mail:
                edit.setInputType(TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
        }
        edit.setSingleLine();

        label: if (titleId == R.string.input_name){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) break label;
            String name = user.getProviderData().get(0).getDisplayName();
            if (name == null) break label;
            if (name.length() > 15){
                name = name.substring(0, 15);
            }
            edit.setText(name);
        }

        AlertDialog.Builder builder =  new AlertDialog.Builder(getContext())
//                .setTitle(titleId)
                .setView(rootView)
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel, null);

        switch (titleId){
            case R.string.input_name:
            case R.string.input_currentPw:
            case R.string.input_newPw:
                builder.setMessage(messageId);
                break;
            case R.string.input_mail:
                break;
        }

        return builder.create();
    }

    private void callActivityResult(Task<Void> task, @NonNull String newParam){
        Intent intent = new Intent();
        intent.putExtra(SettingFragment.INTENT_KEY_ISSUCCESS, task.isSuccessful());
        intent.putExtra(SettingFragment.INTENT_KEY_METHOD, getArguments().getInt(BUNDLE_KEY_COMMAND));
        intent.putExtra(SettingFragment.INTENT_KEY_NEW_PARAM, newParam);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }

    private void callActivityResult(@NonNull String newParam){
        Intent intent = new Intent();
        intent.putExtra(SettingFragment.INTENT_KEY_METHOD, getArguments().getInt(BUNDLE_KEY_COMMAND));
        intent.putExtra(SettingFragment.INTENT_KEY_NEW_PARAM, newParam);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }

//    //region FireBaseパスワード問い合わせ系 onClick配属
//    //////////////////////////////FireBaseパスワード問い合わせ系 onClick配属 ここから///////////////////////////
//    /**
//     * 再認証をおこなうおっさん！
//     * @param user currentUserがnullかどうかは、事前にチェックを行ってください。
//     */
//    private void reAuthenticate(@NonNull FirebaseUser user, @NonNull final String email, @NonNull String pw){
//        AuthCredential credential = EmailAuthProvider.getCredential(email, pw);
//        user.reauthenticate(credential)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.d(TAG, "User re-authenticated.");
//                        callActivityResult(task, email);
//                    }
//                });
//    }
//
//    /**
//     * パスワードを再設定するおっさん
//     * @param user currentUserがnullかどうかは、事前にチェックを行ってください。
//     */
//    private void updatePassWord(@NonNull FirebaseUser user, @NonNull final String pw){
//        user.updatePassword(pw)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        callActivityResult(task, pw);
//                    }
//                });
//    }
//
//    /**
//     * アカウント名を再設定するおっさん
//     * @param user currentUserがnullかどうかは、事前にチェックを行ってください。
//     */
//    private void updateName(@NonNull FirebaseUser user, @NonNull final String name){
//        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                .setDisplayName(name)
//                .build();
//
//        user.updateProfile(profileUpdates)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        callActivityResult(task, name);
//                    }
//                });
//    }
//
//    private void upDateEmail(@NonNull FirebaseUser user, @NonNull final String email){
//        user.updateEmail(email)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        callActivityResult(task, email);
//                    }
//                });
//    }
//    //////////////////////////////FireBaseパスワード問い合わせ系 onClick配属 ここまで///////////////////////////
//    //endregion
}
