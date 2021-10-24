package com.example.scanln;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import com.example.scanln.databinding.FragmentAdminBinding;
import org.jetbrains.annotations.NotNull;

public class AdminFragment extends Fragment {
    private FragmentAdminBinding binding;

    public AdminFragment(){
        super(R.layout.fragment_admin);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        binding=FragmentAdminBinding.inflate(inflater,container,false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState){
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                NavDirections action=AdminFragmentDirections
                        .actionNavigationAdminMenuToNavigationAddSession();
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                NavDirections action=AdminFragmentDirections
                        .actionNavigationAdminMenuToNavigationSearchRecord();
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                NavDirections action=AdminFragmentDirections.actionNavigationAdminMenuToNavigationMainMenu();
                Navigation.findNavController(view).navigate(action);
                AdminViewModel model=new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
                model.clear();
            }
        });
    }
}
