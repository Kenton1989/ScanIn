package com.example.scanin.ui.register;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.scanin.R;
import com.example.scanin.ui.login.LoginFragment;

public class Register_Fragment extends Fragment {

    private RegisterViewModel mViewModel;

    public static Register_Fragment newInstance() {
        return new Register_Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register__fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        // TODO: Use the ViewModel

        final Button nextButton = view.findViewById(R.id.take_picture_btn);
        final Button cancelButton = view.findViewById(R.id.cancel_btn);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(Register_Fragment.this)
                        .navigate(R.id.action_register_Fragment_to_imageResgistration);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(Register_Fragment.this)
                        .navigate(R.id.action_register_Fragment_to_loginFragment2);
            }
        });
    }

}