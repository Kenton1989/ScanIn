package com.example.scanln;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanln.databinding.FragmentSearchSessionHistoryBinding;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SearchSessionHistoryFragment extends Fragment {
    private FragmentSearchSessionHistoryBinding binding;
    private int start_day,start_month,start_year;
    private int end_day,end_month,end_year;
    private List<Record> records;
    public SearchSessionHistoryFragment(){
        super(R.layout.fragment_search_session_history);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        binding=FragmentSearchSessionHistoryBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        List<String> studentIdArr=getStudentIds();
        List<String> sessionIdArr=getSessionIds();
        ArrayAdapter<String> student_adapter=new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                studentIdArr);
        student_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.studentSpin.setAdapter(student_adapter);

        ArrayAdapter<String> session_adapter=new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                sessionIdArr);
        session_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.sessionSpin.setAdapter(session_adapter);

        binding.from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
               Calendar cal=Calendar.getInstance();
               DatePickerDialog dialog=new DatePickerDialog(requireParentFragment().requireContext(),
                       new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                binding.from.setText(dayOfMonth+"/"+month+"/"+year);
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
                                binding.to.setText(dayOfMonth+"/"+month+"/"+year);
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
                records=getRecords();
                showRecordList();
            }
        });
    }

    private List<String> getSessionIds(){
        return new ArrayList<>();
    }

    private List<String> getStudentIds(){
        return new ArrayList<>();
    }

    private List<Record> getRecords(){
        List<Record> r=new ArrayList<>();
        r.add(new Record("12345","session234",
                LocalDateTime.of(2021,2,19,10,20)));
        r.add(new Record("student34","session98",
                LocalDateTime.of(2021,5,19,10,20)));
        r.add(new Record("jnmm","session234",
                LocalDateTime.of(2021,9,19,10,50)));
        r.add(new Record("fkn","session90",
                LocalDateTime.of(2020,2,19,10,20)));
        r.add(new Record("wnfke","session88",
                LocalDateTime.of(2021,1,19,10,20)));
        r.add(new Record("student10","sessionkown",
                LocalDateTime.of(2021,7,19,10,20)));
        return r;
    }

    private void showRecordList(){
        RecordHistoryAdapter adapter=new RecordHistoryAdapter(records,
                requireActivity().getApplicationContext());
        binding.recordList.setAdapter(adapter);
    }

}
