package com.example.scanln;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.scanln.databinding.FragmentAdminLoginBinding;
import com.example.scanln.model.Auth;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdminLoginFragment extends Fragment {
    private FragmentAdminLoginBinding binding;
    AdminViewModel model=new AdminViewModel();
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
        model=new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        binding.adminConfirmLoginBtn.setOnClickListener(view1 -> {
            boolean valid=validateInput();
            if(valid){
                verifyLogin();
            }
        });

        binding.adminCancelLoginBtn.setOnClickListener(view12 -> {
            NavDirections action=AdminLoginFragmentDirections
                    .actionNavigationAdminLoginToNavigationMainMenu();
            Navigation.findNavController(view12).navigate(action);
        });
    }

    private boolean validateInput(){
        boolean valid=true;
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        if(TextUtils.isEmpty(binding.adminId.getText())){
            builder.setMessage("please provide id")
                    .setTitle("Missing Input")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
            valid=false;
            AlertDialog alert=builder.create();
            alert.show();
        }
        if(valid&&TextUtils.isEmpty(binding.adminPw.getText())){
            builder.setMessage("please provide password")
                    .setTitle("Missing Input")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
            valid=false;
            AlertDialog alert=builder.create();
            alert.show();
        }
        return valid;
    }

    private void verifyLogin(){
        Map<String,Object> params=new HashMap<>();
        params.put("username",binding.adminId.getText().toString());
        params.put("password",binding.adminPw.getText().toString());
        Auth auth=new Auth("","");
        String operation=VRequestQueue.LOGIN;
        Response.Listener<JSONObject> listener= response -> {
            if(response.optBoolean(VRequestQueue.RESULT_PARAM)){
                onSuccess(response);
            }
            else{
                onFail(response);
            }
        };
        Response.ErrorListener errorListener= this::onRequestError;
        VRequestQueue.getInstance(requireContext())
                .createRequest(params,operation,auth,listener,errorListener);

    }

    private void onSuccess(JSONObject response){
        JSONObject auth=response.optJSONObject(VRequestQueue.RETURN)
                .optJSONObject(VRequestQueue.AUTHENTICATION_OBJECT);
       model.setAuth(auth);
        NavDirections action=AdminLoginFragmentDirections.actionNavigationAdminLoginToNavigationAdminMenu();
        Navigation.findNavController(getView()).navigate(action);
    }
    private void onFail(JSONObject response){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Login Fail")
                .setMessage("Reason: "+response.optString(VRequestQueue.DETAIL_FIELD))
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();

    }
    private void onRequestError(VolleyError error){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Login Fail")
                .setMessage("Due to server connection error: "+error.getMessage())
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
