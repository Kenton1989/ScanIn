package com.example.scanln;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.scanln.databinding.FragmentAddSessionBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static android.widget.AbsListView.CHOICE_MODE_MULTIPLE;

public class AddSessionFragment extends Fragment {
    private FragmentAddSessionBinding binding;
    private List<String> allStudents;
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
        allStudents=getAllStudents();
        ArrayAdapter<CharSequence> adapter=ArrayAdapter
                .createFromResource(getContext(),
                        R.array.repeat_num_array,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.sessionNumSpin.setAdapter(adapter);

        ArrayAdapter<CharSequence> freq_adapter=ArrayAdapter
                .createFromResource(getContext(),
                        R.array.repeat_freq_array,
                        android.R.layout.simple_spinner_item);
        freq_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.sessionFreqSpin.setAdapter(freq_adapter);

        binding.addSessionConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        binding.attendanceList.setChoiceMode(CHOICE_MODE_MULTIPLE);
        binding.attendanceList.setAdapter(new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_multiple_choice,allStudents));


    }

    private List<String> getAllStudents(){
        List<String> r=new ArrayList<>();
        r.add("student1");
        r.add("student2");
        r.add("student3");
        r.add("student4");
        r.add("student5");
        r.add("student6");
        r.add("student7");
        r.add("student8");
        return r;
    }
}
