package com.scout.hospitalapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scout.hospitalapp.R;

public class NotificationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("NotificationFragment","onCreate Run");

        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        return view;
    }
}
