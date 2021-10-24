package com.example.scanln;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.scanln.databinding.FragmentAddSessionBinding;
import com.example.scanln.model.UserInfo;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.AbsListView.CHOICE_MODE_MULTIPLE;

public class AddSessionFragment extends Fragment {
    private FragmentAddSessionBinding binding;
    private int start_day,start_month,start_year;
    private int start_h,start_min,end_h,end_min;
    private List<UserInfo> userInfos=new ArrayList<>();
    private AdminViewModel model;
    public AddSessionFragment(){
        super(R.layout.fragment_add_session);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        binding=FragmentAddSessionBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        model= new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        getAttendees();

        ArrayAdapter<CharSequence> freq_adapter=ArrayAdapter
                .createFromResource(getContext(),
                        R.array.repeat_freq_array,
                        android.R.layout.simple_spinner_item);
        freq_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.sessionFreqSpin.setAdapter(freq_adapter);
        binding.sessionFreqSpin.setSelection(0);

        binding.addSessionConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSession();
            }
        });

        binding.sessionStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Calendar cal=Calendar.getInstance();
                DatePickerDialog dialog=new DatePickerDialog(requireParentFragment().requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String s=String.format("%2s/%2s/%4s",dayOfMonth,month,year);
                                binding.sessionStart.setText(s);
                                start_day=dayOfMonth;
                                start_month=month+1;
                                start_year=year;
                            }
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        binding.startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Calendar cal=Calendar.getInstance();
                TimePickerDialog dialog = new TimePickerDialog(requireParentFragment().requireContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String s = String.format("%2s:%2s", hourOfDay, minute);
                                binding.startTime.setText(s);
                                start_h = hourOfDay;
                                start_min = minute;
                            }
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(requireActivity().getApplicationContext()));
                dialog.show();
            }
        });
        binding.endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Calendar cal=Calendar.getInstance();
                TimePickerDialog dialog = new TimePickerDialog(requireParentFragment().requireContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String s = String.format("%2s:%2s", hourOfDay, minute);
                                binding.endTime.setText(s);
                                end_h = hourOfDay;
                                end_min = minute;
                            }
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(requireActivity().getApplicationContext()));
                dialog.show();
            }
        });

        binding.addSessionCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirm Exit?")
                        .setMessage("The information will not be saved, choose exit to to back, choose continue to stay.")
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavDirections action=AddSessionFragmentDirections
                                        .actionNavigationAddSessionToNavigationAdminMenu();
                                Navigation.findNavController(view).navigate(action);
                            }
                        })
                        .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });
    }

    private void getAttendees(){
        Map<String,Object> params=new HashMap<>();
        JSONObject auth=model.getAuth();
        String operation=VRequestQueue.GET_ATTENDEES;
        Response.Listener<JSONObject> listener= response -> {
            if(response.optBoolean(VRequestQueue.RESULT_PARAM)){
                onGetAttendees(response);
            }
            else{
                onGetAttendeesFail(response);
            }
        };
        Response.ErrorListener errorListener= this::onRequestError;
        VRequestQueue.getInstance(requireContext()).createRequest(params,operation,auth,listener,errorListener);

    }

    private void onGetAttendees(JSONObject response){
        try{
            JSONObject returns=response.getJSONObject(VRequestQueue.RETURN);
            JSONArray users=returns.getJSONArray("users");
            for(int i=0;i<users.length();i++){
                JSONObject json=users.getJSONObject(i);
                UserInfo user=new UserInfo(json);
                userInfos.add(user);
            }
            setAttendeesList();
        } catch (Exception e){
            Log.e("get attendees",e.getMessage());
        }

    }
    private void onGetAttendeesFail(JSONObject response){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Fail to get attendees")
                .setMessage("Server response: "+response.optString("details"))
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void onRequestError(VolleyError error){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Cannot get attendees")
                .setMessage("Due to server connection error: "+error.toString())
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void setAttendeesList(){
        binding.attendanceList.setChoiceMode(CHOICE_MODE_MULTIPLE);
        ArrayAdapter<UserInfo> student_adapter=new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_multiple_choice,
                userInfos);
        binding.attendanceList.setAdapter(student_adapter);
    }

    private void addSession(){
        Map<String,Object> params=new HashMap<>();
        JSONObject auth=model.getAuth();
        String operation=VRequestQueue.ADD_SESSION;
        String session_name=binding.sessionNameTxt.getText().toString();
        String venue=binding.sessionVenueTxt.getText().toString();
        DateTimeFormatter dtf =  DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        LocalDateTime start=LocalDateTime.of(start_year,start_month,start_day,start_h,start_min);
        ZonedDateTime iso_start=ZonedDateTime.of(start, ZoneId.systemDefault());
        String beg_time=iso_start.format(dtf);
        LocalDateTime end=LocalDateTime.of(start_year,start_month,start_day,end_h,end_min);
        ZonedDateTime iso_end=ZonedDateTime.of(end,ZoneId.systemDefault());
        String end_time=iso_end.format(dtf);
        int repeat=Integer.parseInt(binding.sessionRepeatNum.getText().toString());
        int period=Integer.parseInt(binding.periodLengthNum.getText().toString());
        String period_unit=binding.sessionFreqSpin.getSelectedItem().toString();
        List<String> attendees=new ArrayList<>();
        SparseBooleanArray checked=binding.attendanceList.getCheckedItemPositions();
        for (int i=0;i<checked.size();i++){
            if(checked.valueAt(i)) {
                int idx = checked.keyAt(i);
                attendees.add(userInfos.get(idx).getPid());
            }
        }
        params.put("session_name",session_name);
        params.put("venue",venue);
        params.put("beg_time",beg_time);
        params.put("end_time",end_time);
        params.put("attendees",attendees);
        params.put("repeat",repeat);
        params.put("period",period);
        params.put("period_unit",period_unit);
        Response.Listener<JSONObject> listener=response -> {
            if(response.optBoolean(VRequestQueue.RESULT_PARAM)){
                onSessionAdded(response);
            }
            else{
                onAddSessionFail(response);
            }
        };
        Response.ErrorListener errorListener= this::onRequestError;
        VRequestQueue.getInstance(requireContext()).createRequest(params,operation,auth,listener,errorListener);

    }
    private void onSessionAdded(JSONObject response){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Added Session")
                .setMessage("Session added successfully, click BACK to go back to main menu, "+
                        "click CONTINUE to add a new one")
                .setCancelable(false)
                .setPositiveButton("CONTINUE", (dialog, which) -> {
                    dialog.cancel();
                    clearText();
                })
                .setNegativeButton("BACK",(dialog,which)->{
                    NavDirections action=AddSessionFragmentDirections
                            .actionNavigationAddSessionToNavigationAdminMenu();
                    Navigation.findNavController(requireView()).navigate(action);
                });
        builder.show();
    }
    private void onAddSessionFail(JSONObject response){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Fail to add session")
                .setMessage("Server response: "+response.optString("details"))
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void clearText(){
        binding.sessionNameTxt.setText("");
        binding.sessionStart.setText("START DATE");
        binding.endTime.setText("TO");
        binding.startTime.setText("FROM");
        binding.sessionRepeatNum.setText("");
        binding.sessionVenueTxt.setText("");
        start_day=0;start_month=0;start_year=0;
        start_h=0;start_min=0;end_h=0;end_min=0;
    }
}
