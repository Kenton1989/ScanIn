package com.example.scanln;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Path;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.scanln.databinding.FragmentRegisterBinding;

import org.jetbrains.annotations.NotNull;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    public RegisterFragment(){
        super(R.layout.fragment_register);
        //binding=FragmentRegisterBinding.inflate(getLayoutInflater());

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        binding=FragmentRegisterBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View view){
                NavDirections action = RegisterFragmentDirections.actionNavigationRegisterToNavigationMainMenu();
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                boolean valid=validateInput();
                if(valid){
                    NavDirections action =RegisterFragmentDirections.actionNavigationRegisterToNavigationTakePicture();
                    Navigation.findNavController(view).navigate(action);
                }

            }
        });

    }


    private boolean validateInput(){
        boolean valid=true;
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        if(TextUtils.isEmpty(binding.inputId.getEditText().getText())){
            builder.setMessage("please provide id")
                    .setTitle("Missing Input")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                         public void onClick(DialogInterface dialog, int which) {
                             dialog.cancel();
                        }
                    });
            valid=false;
            AlertDialog alert=builder.create();
            alert.show();
        }
        if(valid&&TextUtils.isEmpty(binding.inputPw.getEditText().getText())){
            builder.setMessage("please provide password")
                    .setTitle("Missing Input")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            valid=false;
            AlertDialog alert=builder.create();
            alert.show();
        }
        return valid;
    }
}
