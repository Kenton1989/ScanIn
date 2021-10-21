package com.example.scanln;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.scanln.databinding.FragmentCheckinConfirmBinding;
import com.example.scanln.model.History;
import com.example.scanln.model.SessionBrief;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckinConfirmFragment extends Fragment {

    private CheckinViewModel model;
    private FragmentCheckinConfirmBinding binding;
    private List<SessionBrief> sessions;
    private SessionBrief selected;
    public CheckinConfirmFragment(){
        super(R.layout.fragment_checkin_confirm);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        binding=FragmentCheckinConfirmBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState){
        model=new ViewModelProvider(requireActivity()).get(CheckinViewModel.class);
        binding.name.setText(model.getPname());
        binding.pid.setText(model.getPid());
        showSessionList();
        binding.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(validateInput())
                    checkInOut();
            }
        });
        binding.retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                model.clear();
                NavDirections action=CheckinConfirmFragmentDirections
                        .actionNavigationCheckinConfirmToNavigationCheckin();
                Navigation.findNavController(view).navigate(action);
            }
        });

    }

    private void showSessionList(){
        sessions=model.getRecords();
        CheckinSessionListAdapter adapter=new CheckinSessionListAdapter(sessions,requireContext());
        binding.sessionList.setAdapter(adapter);
        binding.sessionList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        binding.sessionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected=(SessionBrief)adapter.getItem(position);
                binding.action.setActivated(true);
            }
        });
    }

    private void checkInOut(){
        JSONObject auth=model.getAuth();
        Map<String,Object> params=new HashMap<>();
        int sid=selected.getSid();
        params.put("sid",sid);
        String operation=VRequestQueue.CHECK_IN_OUT;
        Response.Listener<JSONObject> listener= response -> {
            if(response.optBoolean(VRequestQueue.RESULT_PARAM)){
                onActionSuccess(response);
            }
            else{
                onActionFail(response);
            }
        };
        Response.ErrorListener errorListener= this::onRequestError;
        VRequestQueue.getInstance(requireContext()).createRequest(params,operation,auth,listener,errorListener);
    }

    private void onActionSuccess(JSONObject response){
        try{
            JSONObject returns=response.getJSONObject(VRequestQueue.RETURN);
            History new_history=new History(returns.optJSONObject("new_history"));
            String operation=new_history.isIn()?"Check In":"Check Out";
            String session=new_history.getSessionName();
            String pname=new_history.getName();
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            builder.setMessage(pname+" successfully "+operation+" session "+session)
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> {
                        NavDirections action=CheckinConfirmFragmentDirections
                                .actionNavigationCheckinConfirmToNavigationCheckin();
                        Navigation.findNavController(requireView()).navigate(action);
                    });
            builder.show();
        } catch (Exception e){
            Log.e("action success",e.getMessage());
        }
    }
    private void onActionFail(JSONObject response){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Fail to add session")
                .setMessage("Server response: "+response.optString("details"))
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void onRequestError(VolleyError error){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Cannot perform action")
                .setMessage("Due to server connection error: "+error.toString())
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private boolean validateInput(){
        if(selected!=null)
            return true;
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Cannot perform action")
                .setMessage("You have not selected any session")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
        return false;
    }
}
