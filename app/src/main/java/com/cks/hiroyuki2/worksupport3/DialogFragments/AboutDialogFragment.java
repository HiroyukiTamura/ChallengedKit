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

package com.cks.hiroyuki2.worksupport3.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.webkit.WebView;

import com.cks.hiroyuki2.worksupport3.Fragments.AboutFragment;

/**
 * @see AboutFragment
 */

public class AboutDialogFragment extends DialogFragment implements View.OnClickListener{
    private static final String TAG = "MANUAL_TAG: " + AboutDialogFragment.class.getSimpleName();
//    public static final String TAG_LAUNCHER_ICON = "TAG_LAUNCHER_ICON";
//    public static final int CALLBACK_LAUNCHER_ICON = 198937;
    public static final String TAG_IMG = "TAG_IMG";
    public static final int CALLCACK_IMG = 198938;
    private static final String htmlPath = "file:///android_asset/license.html";
    private Dialog licenseDialog;

    public static AboutDialogFragment newInstance(Bundle bundle){
        AboutDialogFragment frag = new AboutDialogFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (licenseDialog != null)
            return licenseDialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        switch (getTargetRequestCode()){
//            case CALLBACK_LAUNCHER_ICON:
//                builder.setView(setAboutDialog());
//                break;
            case CALLCACK_IMG:
                builder.setView(setLicenseView());
                break;
        }
        return licenseDialog = builder.create();
    }

//    private View setAboutDialog(){
//        View v = getActivity().getLayoutInflater().inflate(R.layout.about_dialog, null);
//        LinearLayout container = v.findViewById(R.id.container);
//
//        for (int i = 0; i < container.getChildCount(); i++) {
//            @DrawableRes int imgRes;
//            TextView tv = container.getChildAt(i).findViewById(R.id.tv);
//            switch (i){
//                case 0:
//                    imgRes = R.drawable.ic_001_pencil;
//                    tv.setText(R.string.about_launcher_credit);
//                    break;
//                case 1:
//                    imgRes = R.drawable.tutorial_input;
//                    tv.setText(Html.fromHtml(getString(R.string.about_input_credit)));
//                    break;
//                case 2:
//                    imgRes = R.drawable.tutorial_analytics;
//                    tv.setText(Html.fromHtml(getString(R.string.about_analytics_credit)));
//                    break;
//                case 3:
//                    imgRes = R.drawable.tutorial_meeting;
//                    tv.setText(Html.fromHtml(getString(R.string.about_meeting_credit)));
//                    break;
//                default://ここには来ないはず
//                    continue;
//            }
//
//            ImageView iv = container.getChildAt(i).findViewById(R.id.iv);
//            final TextView tvError = container.getChildAt(i).findViewById(R.id.error_tv);
//            Picasso.with(getContext())
//                    .load(imgRes)
//                    .into(iv, new Callback() {
//                        @Override
//                        public void onSuccess() {}
//
//                        @Override
//                        public void onError() {
//                            if(tvError != null)
//                                tvError.setVisibility(VISIBLE);
//                        }
//                    });
//        }
//
//        v.findViewById(R.id.ok).setOnClickListener(this);
//        return v;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        licenseDialog = null;
    }

    private View setLicenseView(){
        WebView webView = new WebView(getContext());
        webView.loadUrl(htmlPath);
        return webView;
    }

    @Override
    public void onClick(View view) {
//        switch (getTargetRequestCode()){
//            case CALLBACK_LAUNCHER_ICON:
//                dismiss();
//                break;
//        }
    }
}
