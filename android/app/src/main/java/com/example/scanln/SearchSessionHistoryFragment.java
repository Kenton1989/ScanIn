package com.example.scanln;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.scanln.databinding.FragmentSearchSessionHistoryBinding;
import com.example.scanln.model.Auth;
import com.example.scanln.model.History;
import com.example.scanln.model.Record;
import com.example.scanln.model.SessionBrief;
import com.example.scanln.model.UserInfo;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchSessionHistoryFragment extends Fragment {
    private FragmentSearchSessionHistoryBinding binding;
    private int start_day,start_month,start_year;
    private int end_day,end_month,end_year;
    private List<Record> records;
    private List<UserInfo> userInfos;
    private List<SessionBrief> sessionBriefs;
    private AdminViewModel model;
    public SearchSessionHistoryFragment(){
        super(R.layout.fragment_search_session_history);
    }
    private JSONObject auth;
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        binding=FragmentSearchSessionHistoryBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        model= new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        auth=model.getAuth();
        getValidHistoryParam();

        binding.from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
               Calendar cal=Calendar.getInstance();
               DatePickerDialog dialog=new DatePickerDialog(requireParentFragment().requireContext(),
                       new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String s=String.format("%2s/%2s/%4s",dayOfMonth,month,year);
                                binding.from.setText(s);
                                start_day=dayOfMonth;
                                start_month=month;
                                start_year=year;
                            }
                        },
                       cal.get(Calendar.YEAR),
                       cal.get(Calendar.MONTH),
                       cal.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        binding.to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Calendar cal=Calendar.getInstance();
                DatePickerDialog dialog=new DatePickerDialog(requireParentFragment().requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String s=String.format("%2s/%2s/%4s",dayOfMonth,month,year);
                                binding.to.setText(s);
                                end_day=dayOfMonth;
                                end_month=month;
                                end_year=year;
                            }
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        binding.backToAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action=SearchSessionHistoryFragmentDirections
                        .actionNavigationSearchRecordToNavigationAdminMenu();
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getHistory();
            }
        });
    }

    private void getValidHistoryParam(){
        Map<String,Object> params=new HashMap<>();
        String operation=VRequestQueue.GET_VALID_HISTORY_PARAM;
        Response.Listener<JSONObject> listener=response -> {
            if(response.optBoolean(VRequestQueue.RESULT_PARAM)){
                onGetHistoryParam(response);
            }
            else{
                onGetHistoryParamFail(response);
            }
        };
        Response.ErrorListener errorListener= this::onRequestError;
        VRequestQueue.getInstance(requireContext())
                .createRequest(params,operation,auth,listener,errorListener);
    }

    private void getHistory(){
        Map<String,Object> params=new HashMap<>();
        String operation=VRequestQueue.GET_HISTORY;
        int sid;
        if(binding.sessionSpin.getSelectedItemPosition()!= AdapterView.INVALID_POSITION){
            sid=sessionBriefs.get(binding.sessionSpin.getSelectedItemPosition()).getSid();
            params.put("sid",sid);
        }
        else params.put("sid",null);

        String pid;
        if(binding.studentSpin.getSelectedItemPosition()!=AdapterView.INVALID_POSITION){
            pid=userInfos.get(binding.studentSpin.getSelectedItemPosition()).getPid();
            params.put("pid",pid);
        }
        else params.put("pid",null);

        DateTimeFormatter dtf =  DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        if(start_year==0){
            params.put("beg_time",null);
        }
        else{
            LocalDateTime dt=LocalDateTime.of(start_year,start_month,start_day,0,0);
            String beg_time= ZonedDateTime.of(dt,ZoneId.systemDefault()).format(dtf);
            params.put("beg_time",beg_time);
        }

        if(end_year==0){
            params.put("end_time",null);
        }
        else{
            LocalDateTime dt=LocalDateTime.of(end_year,end_month,end_day,0,0);
            String end_time= ZonedDateTime.of(dt, ZoneId.systemDefault()).format(dtf);
            params.put("end_time",end_time);
        }
        params.put("max_num",null);
        Response.Listener<JSONObject> listener=response -> {
            if(response.optBoolean(VRequestQueue.RESULT_PARAM)){
                onGetHistory(response);
            }
            else{
                onGetHistoryFail(response);
            }
        };
        Response.ErrorListener errorListener= this::onRequestError;
        VRequestQueue.getInstance(requireContext())
                .createRequest(params,operation,auth,listener,errorListener);

    }
    private void onGetHistory(JSONObject response){
        List<History> histories=new ArrayList<>();
        JSONObject returns=response.optJSONObject(VRequestQueue.RETURN);
        try{
            JSONArray history=returns.getJSONArray("histories");
            for(int i=0;i<history.length();i++){
                JSONObject json=history.getJSONObject(i);
                History h=new History(json);
                histories.add(h);
            }
            setHistoryList(histories);
        } catch (Exception e){
            Log.e("get history param",e.getMessage());
        }
    }

    private void onGetHistoryFail(JSONObject response){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Fail to get history")
                .setMessage("Server response: "+response.optString("details"))
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void onGetHistoryParam(JSONObject response){
        JSONObject returns=response.optJSONObject(VRequestQueue.RETURN);
        try{
            JSONArray users=returns.getJSONArray("users");
            JSONArray sessions=returns.getJSONArray("sessions");
            userInfos=new ArrayList<>();
            sessionBriefs=new ArrayList<>();
            for(int i=0;i<users.length();i++){
                JSONObject json=users.getJSONObject(i);
                UserInfo info=new UserInfo(json);
                userInfos.add(info);
            }
            for(int i=0;i<sessions.length();i++){
                JSONObject json=sessions.getJSONObject(i);
                SessionBrief session=new SessionBrief(json);
                sessionBriefs.add(session);
            }
            setSessionSpin();
            setStudentSpin();
        }catch(JSONException e){
            Log.e("get history param",e.getMessage());
        }

    }

    private void onGetHistoryParamFail(JSONObject response){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Fail to get parameters")
                .setMessage("Server response: "+response.optString("details"))
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void onRequestError(VolleyError error){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Cannot get parameters")
                .setMessage("Due to server connection error: "+error.toString())
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void setStudentSpin(){
        ArrayAdapter<UserInfo> student_adapter=new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                userInfos);
        student_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.studentSpin.setAdapter(student_adapter);
    }

    private void setSessionSpin(){
        ArrayAdapter<SessionBrief> session_adapter=new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                sessionBriefs);
        session_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.sessionSpin.setAdapter(session_adapter);
    }

    private void setHistoryList(List<History> historyList){

        HistoryAdapter adapter=new HistoryAdapter(historyList,requireContext());
        binding.recordList.setAdapter(adapter);
    }
}
