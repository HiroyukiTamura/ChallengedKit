package com.cks.hiroyuki2.worksupport3.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.Fragments.AboutFragment;
import com.cks.hiroyuki2.worksupport3.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.view.View.VISIBLE;

/**
 * @see AboutFragment
 */

public class AboutDialogFragment extends DialogFragment implements View.OnClickListener{
    private static final String TAG = "MANUAL_TAG: " + AboutDialogFragment.class.getSimpleName();
    public static final String TAG_LAUNCHER_ICON = "TAG_LAUNCHER_ICON";
    public static final int CALLBACK_LAUNCHER_ICON = 198937;
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
            case CALLBACK_LAUNCHER_ICON:
                builder.setView(setAboutDialog());
                break;
            case CALLCACK_IMG:
                builder.setView(setLicenseView());
                break;
        }
        return licenseDialog = builder.create();
    }

    private View setAboutDialog(){
        View v = getActivity().getLayoutInflater().inflate(R.layout.about_dialog, null);
        LinearLayout container = v.findViewById(R.id.container);

        for (int i = 0; i < container.getChildCount(); i++) {
            @DrawableRes int imgRes;
            TextView tv = container.getChildAt(i).findViewById(R.id.tv);
            switch (i){
                case 0:
                    imgRes = R.drawable.ic_001_pencil;
                    tv.setText(R.string.about_launcher_credit);
                    break;
                case 1:
                    imgRes = R.drawable.tutorial_input;
                    tv.setText(Html.fromHtml(getString(R.string.about_input_credit)));
                    break;
                case 2:
                    imgRes = R.drawable.tutorial_analytics;
                    tv.setText(Html.fromHtml(getString(R.string.about_analytics_credit)));
                    break;
                case 3:
                    imgRes = R.drawable.tutorial_meeting;
                    tv.setText(Html.fromHtml(getString(R.string.about_meeting_credit)));
                    break;
                default://ここには来ないはず
                    continue;
            }

            ImageView iv = container.getChildAt(i).findViewById(R.id.iv);
            final TextView tvError = container.getChildAt(i).findViewById(R.id.error_tv);
            Picasso.with(getContext())
                    .load(imgRes)
                    .into(iv, new Callback() {
                        @Override
                        public void onSuccess() {}

                        @Override
                        public void onError() {
                            if(tvError != null)
                                tvError.setVisibility(VISIBLE);
                        }
                    });
        }

        v.findViewById(R.id.ok).setOnClickListener(this);
        return v;
    }

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
        switch (getTargetRequestCode()){
            case CALLBACK_LAUNCHER_ICON:
                dismiss();
                break;
        }
    }
}
