package com.example.scanln;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.scanln.databinding.FragmentPermissionBinding;

import org.jetbrains.annotations.NotNull;

public class PermissionFragment extends Fragment {
    private String next_dest;
    private static final int CAMERA_REQUEST_CODE = 10;
    private String[] PERMISSIONS_REQUIRED=new String[] {Manifest.permission.CAMERA};

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        System.out.println("in onCreate");
        //requestPermissions(PERMISSIONS_REQUIRED,CAMERA_REQUEST_CODE);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        return FragmentPermissionBinding.inflate(inflater,container,false).getRoot();
    }
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState){
        System.out.println("in onViewCreated");
        requestPermissions(PERMISSIONS_REQUIRED,CAMERA_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int code, String[] permissions, int[] results){
        super.onRequestPermissionsResult(code, permissions, results);
        if(code==CAMERA_REQUEST_CODE){
            for (int i = 0; i < permissions.length; i++) {
                if(results[i]==PackageManager.PERMISSION_GRANTED){
//                    Toast.makeText(requireContext(),
//                            "Permission request granted", Toast.LENGTH_LONG).show();
                    toCamera();
                }
                else Toast.makeText(requireContext(),
                        "Permission request denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void toCamera(){
        next_dest=PermissionFragmentArgs.fromBundle(getArguments()).getNextDest();
        if(next_dest.equals("checkin")){
            NavDirections action=PermissionFragmentDirections.actionPermissionFragmentToNavigationCheckin();
            Navigation.findNavController(requireView()).navigate(action);
        }
        else{
            NavDirections action=PermissionFragmentDirections.actionPermissionFragmentToNavigationRegister();
            Navigation.findNavController(requireView()).navigate(action);
        }
    }

    private boolean hasPermission(){
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }
}
