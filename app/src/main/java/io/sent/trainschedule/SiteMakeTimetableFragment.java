package io.sent.trainschedule;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SiteMakeTimetableFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_site_make_timetable, container, false);
        ((MakeTimetableActivity)getActivity()).setFragment();
        findViews();
        initViews();
        return view;
    }

    private void findViews(){


    }

    private void initViews(){

    }
}