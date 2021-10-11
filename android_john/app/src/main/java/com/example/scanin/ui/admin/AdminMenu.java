package com.example.scanin.ui.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.scanin.R;
import com.example.scanin.ui.login.LoginFragment;
import com.example.scanin.ui.register.Register_Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminMenu extends Fragment {


    public static AdminMenu newInstance() {
        return new AdminMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_menu, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button scanbutton = view.findViewById(R.id.scanin);
        final Button historybutton = view.findViewById(R.id.history);
        final Button sessionbutton = view.findViewById(R.id.sessions);

        scanbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AdminMenu.this)
                        .navigate(R.id.action_adminMenu_to_attendanceCaptureFragment);
            }
        });
        historybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AdminMenu.this)
                        .navigate(R.id.action_adminMenu_to_historyFragment);
            }
        });
        sessionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AdminMenu.this)
                        .navigate(R.id.action_adminMenu_to_sessionsFragment);
            }
        });
    }
}