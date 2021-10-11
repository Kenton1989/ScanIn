package com.example.scanin.ui.admin;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.scanin.R;

public class AttendanceCaptureFragment extends Fragment {

    private AttendanceCaptureViewModel mViewModel;

    public static AttendanceCaptureFragment newInstance() {
        return new AttendanceCaptureFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.attendance_capture_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AttendanceCaptureViewModel.class);
        // TODO: Use the ViewModel
    }

}