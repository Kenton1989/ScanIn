package com.example.scanln;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.scanln.databinding.FragmentMainMenuBinding;

import org.jetbrains.annotations.NotNull;

public class MainMenuFragment extends Fragment {
    private FragmentMainMenuBinding binding;
    public MainMenuFragment(){
        super(R.layout.fragment_main_menu);
        //binding=FragmentMainMenuBinding.inflate(getLayoutInflater());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        System.out.println("main menu created");
        binding=FragmentMainMenuBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState){
        binding.adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action=MainMenuFragmentDirections
                        .actionNavigationMainMenuToNavigationAdminLogin();
                Navigation.findNavController(view).navigate(action);

            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action=MainMenuFragmentDirections
                        .actionNavigationMainMenuToNavigationRegister();
                Navigation.findNavController(view).navigate(action);
            }
        });
    }
}
