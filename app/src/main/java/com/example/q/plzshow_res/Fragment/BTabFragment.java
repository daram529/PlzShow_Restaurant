package com.example.q.plzshow_res.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.q.plzshow_res.R;

public class BTabFragment extends Fragment {

    public BTabFragment() {
        // Required empty public constructor
    }

    public static BTabFragment newInstance() {
        return new BTabFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_btab, container, false);
        // Add actions here
        return rootView;
    }
}
