package com.example.scanln;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.scanln.databinding.FragmentTakePictureBinding;
import com.example.scanln.faceDetector.FaceDetectorProcessor;
import com.example.scanln.faceDetector.ImageUtils;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TakePictureFragment extends Fragment{
    private RegisterViewModel model;
    private Bitmap cur;
    private ExecutorService cameraExecutor;
    private ImageCapture imageCapture;
    private FragmentTakePictureBinding binding;
    private FaceDetectorProcessor analyser;
    private ProcessCameraProvider cameraProvider;
    private boolean valid;

    public TakePictureFragment(){
        super(R.layout.fragment_take_picture);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        binding=FragmentTakePictureBinding.inflate(inflater,container,false);
        cameraExecutor = Executors.newSingleThreadExecutor();
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState){
        model=new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);
        setUpCamera();
        binding.takePictureBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                capture();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                NavDirections action=TakePictureFragmentDirections
                        .actionNavigationTakePictureToNavigationRegister();
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                System.out.println(model.getFront().getValue()==null);
                cameraProvider.unbindAll();
                cameraExecutor.shutdown();
                NavDirections action=TakePictureFragmentDirections
                        .actionNavigationTakePictureToNavigationSummary();
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.nextBtn.setActivated(false);

    }

    private void setUpCamera(){
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture=
                ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            Preview preview=new Preview.Builder().build();
            imageCapture = new ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();
            System.out.println("imagecapture ready");
            ImageAnalysis analysis=new ImageAnalysis.Builder().build();
            analyser=new FaceDetectorProcessor(new ImageUtils(),requireContext());

            analysis.setAnalyzer(cameraExecutor,analyser);
            preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());
            CameraSelector cameraSelector=new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
            try {

                cameraProvider = cameraProviderFuture.get();
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(requireActivity(), cameraSelector,
                        preview,imageCapture,analysis);
                System.out.println("provider ready");

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));

    }

    private void capture(){

        imageCapture.takePicture(
                cameraExecutor, new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        super.onCaptureSuccess(image);
                        checkPicture();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                    }
                });
    }

    private void checkPicture(){
        Bitmap face=analyser.getResult();
        Log.d("check picture", String.valueOf(face==null));
        model.setFront(face);
        model.setString_front(ImageUtils.getJPEGString(face));
        binding.nextBtn.setActivated(true);
        binding.hint.setText("Picture taken successfully, click next to continue.");
    }

}
