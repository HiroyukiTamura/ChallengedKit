package com.cks.hiroyuki2.worksupport3.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cks.hiroyuki2.worksupport3.R;
import com.cks.hiroyuki2.worksupport3.RecordDialogFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * @see AboutFragment
 */

public class AboutDialogFragment extends DialogFragment implements View.OnClickListener{
    private static final String TAG = "MANUAL_TAG: " + AboutDialogFragment.class.getSimpleName();
    public static final int CODE_IMG = 1;
    public static final String TAG_IMG = "TAG_IMG";

    public static AboutDialogFragment newInstance(Bundle bundle){
        AboutDialogFragment frag = new AboutDialogFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        switch (getTargetRequestCode()){
            case CODE_IMG:
                builder.setView(setAboutDialog());
                break;
        }
        return builder.create();
    }

    private View setAboutDialog(){
        View v = getActivity().getLayoutInflater().inflate(R.layout.about_dialog, null);
        LinearLayout container = v.findViewById(R.id.container);

        for (int i = 0; i < container.getChildCount(); i++) {
            int imgRes = 0;
            String string = null;
            TextView tv = container.getChildAt(i).findViewById(R.id.tv);
            switch (i){
                case 0:
                    imgRes = R.drawable.mixer;
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
                            tvError.setVisibility(View.VISIBLE);
                        }
                    });
        }

        v.findViewById(R.id.ok).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (getTargetRequestCode()){
            case CODE_IMG:
                dismiss();
                break;
        }
    }
}
