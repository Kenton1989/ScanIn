package com.example.scanln;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.scanln.databinding.FragmentRegisterBinding;

import org.jetbrains.annotations.NotNull;

public class RegisterFragment extends Fragment {

    private RegisterViewModel model;
    private String name;
    private String id;
    private String pwd;

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
        super.onViewCreated(view, savedInstanceState);
        model=new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);

        name=model.getName();
        if(name!=null && name!=""){
            binding.inputName.getEditText().setText(name);
        }

        pwd=model.getPassword();
        if(pwd!=null && pwd!=""){
            binding.inputPw.getEditText().setText(pwd);
        }

        id=model.getId();
        if(id!=null && id!=""){
            binding.inputId.getEditText().setText(id);
        }

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
                    model.setId(binding.inputId.getEditText().getText().toString());
                    model.setName(binding.inputName.getEditText().getText().toString());

                    NavDirections action=RegisterFragmentDirections.
                            actionNavigationRegisterToNavigationTakePicture();
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
