package com.example.scanln;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.scanln.databinding.FragmentSummaryBinding;
import com.example.scanln.model.Auth;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SummaryFragment extends Fragment {
    private FragmentSummaryBinding binding;
    RegisterViewModel model;

    public SummaryFragment(){
        super(R.layout.fragment_summary);
    }
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        binding=FragmentSummaryBinding.inflate(inflater,container,false);
        model=new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState){
        binding.idTxt.setText(model.getId());
        binding.nameTxt.setText(model.getName());
        Bitmap bitmap=model.getFront().getValue();

        binding.retakeCenter.setImageBitmap(bitmap);
        binding.retakeCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                NavDirections action=SummaryFragmentDirections
                        .actionNavigationSummaryToNavigationTakePicture();
                Navigation.findNavController(view).navigate(action);
            }
        });
        binding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                registerUser();
            }
        });

        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                NavDirections action=SummaryFragmentDirections
                        .actionNavigationSummaryToNavigationRegister();
                Navigation.findNavController(view).navigate(action);
            }
        });
    }

    private void registerUser(){
        Auth auth=new Auth("","");
        String pid=model.getId();
        String name=model.getName();
        String pwd=model.getPassword();
        String front_img=model.getStringFront();
        Map<String,Object> params=new HashMap<>();
        params.put(VRequestQueue.REGISTER_PID_FIELD,pid);
        params.put(VRequestQueue.REGISTER_IMG_FIELD,front_img);
        params.put(VRequestQueue.REGISTER_NAME_FIELD,name);
        params.put(VRequestQueue.REGISTER_PWD_FIELD,pwd);

        String operation=VRequestQueue.REGISTER_USER;
        Response.Listener<JSONObject> listener= response -> {
            if(response.optBoolean(VRequestQueue.RESULT_PARAM)){
                onSuccess(response);
            }
            else{
                onFail(response);
            }
        };
        Response.ErrorListener errorListener= this::onRequestError;
        VRequestQueue.getInstance(requireContext()).createRequest(params,operation,auth,listener,errorListener);
    }

    private void onSuccess(JSONObject response){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Register Succeed")
                .setMessage("User "+model.getName()+" is registered.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    model.clear();
                    NavDirections action=SummaryFragmentDirections.actionNavigationSummaryToNavigationMainMenu();
                    Navigation.findNavController(getView()).navigate(action);
                });
        builder.show();
    }
    private void onFail(JSONObject response){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Register Fail")
                .setMessage("Fail due to error: "+response.optString(VRequestQueue.DETAIL_FIELD))
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void onRequestError(VolleyError error){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Login Fail")
                .setMessage("Due to server connection error: "+error.toString())
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
