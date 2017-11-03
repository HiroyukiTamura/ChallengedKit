package com.cks.hiroyuki2.worksupport3.Activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cks.hiroyuki2.worksupport3.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class TutorialActivityFragment extends Fragment {

    public TutorialActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial, container, false);
    }
}
