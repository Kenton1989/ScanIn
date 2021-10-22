package com.example.scanln;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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

public class TakePictureFragment extends Fragment implements FaceDetectionCallback{
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
        binding.hint.setText("Click the camera icon when you are ready.");
        binding.overlay.setVisibility(View.INVISIBLE);
        setUpCamera();
        binding.takePictureBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startAnimation();
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
        binding.nextBtn.setEnabled(false);
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
            analyser=new FaceDetectorProcessor(requireContext(),this);

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
                        checkPicture(image);

                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                    }
                });

    }

    @SuppressLint("UnsafeOptInUsageError")
    private boolean checkPicture(ImageProxy image){
        Bitmap face=analyser.getResult();
        if(face==null) return false;
        cur=face;
        model.setFront(face);
        model.setString_front(ImageUtils.getJPEGString(face));
        Log.d("check picture", String.valueOf(face==null));
        requireActivity().runOnUiThread(new Runnable(){
            @Override
            public void run(){
                binding.nextBtn.setEnabled(true);
                binding.hint.setText("Picture taken successfully, click next to continue.");
            }
        });
        return true;

    }

    @Override
    public void onSccess(Bitmap bitmap) {

    }

    private void startAnimation() {
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(100);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                binding.overlay.setAlpha(0.0f);
                binding.overlay.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        AlphaAnimation fadeIn=new AlphaAnimation(0.0f,1.0f);
        fadeIn.setDuration(100);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                binding.overlay.setAlpha(1.0f);
                binding.overlay.startAnimation(fadeOut);

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
                binding.overlay.setVisibility(View.VISIBLE);
            }
        });
        binding.overlay.startAnimation(fadeIn);
    }
}
