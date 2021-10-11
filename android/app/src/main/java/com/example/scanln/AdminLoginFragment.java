package com.example.scanln;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.scanln.databinding.FragmentAdminLoginBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class AdminLoginFragment extends Fragment {
    private FragmentAdminLoginBinding binding;

    public AdminLoginFragment(){
        super(R.layout.fragment_admin_login);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        binding=FragmentAdminLoginBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState){
        binding.adminConfirmLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                boolean valid=validateInput();
                boolean success=true;
                String error="";
                if(valid){
                    //send to backend

                }
                else return;
                if(!success){
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setTitle("Login Fail")
                           .setCancelable(false)
                            .setMessage(error)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                }
                else{
                    NavDirections action=AdminLoginFragmentDirections
                            .actionNavigationAdminLoginToNavigationAdminMenu();
                    Navigation.findNavController(view).navigate(action);
                }
            }
        });

        binding.adminCancelLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                NavDirections action=AdminLoginFragmentDirections
                        .actionNavigationAdminLoginToNavigationMainMenu();
                Navigation.findNavController(view).navigate(action);
            }
        });
    }

    private boolean validateInput(){
        boolean valid=true;
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        if(TextUtils.isEmpty(binding.adminId.getText())){
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
        if(valid&&TextUtils.isEmpty(binding.adminPw.getText())){
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

    private void verifyLogin(){
        String id=binding.adminId.getText().toString();
        String pwd=binding.adminPw.getText().toString();
        RequestQueue queue= Volley.newRequestQueue(requireContext());
        JSONObject postData=new JSONObject();
        JSONObject auth=new JSONObject();
        JSONObject params=new JSONObject();
        try {
            auth.put("username", id);
            auth.put("password",pwd);
            postData.put("auth",auth);
            postData.put("operation",R.string.login);
            postData.put("param",params);

        }catch (JSONException e) {
            e.printStackTrace();
        }

    }
}