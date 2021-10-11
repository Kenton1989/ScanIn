package com.example.scanln;

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

import com.example.scanln.databinding.FragmentSummaryBinding;

import org.jetbrains.annotations.NotNull;

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
        Drawable front=new BitmapDrawable(getResources(),model.getFront().getValue());
        binding.retakeCenter.setBackground(front);
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

            }
        });
    }
}
