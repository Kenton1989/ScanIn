package com.example.scanln;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.scanln.databinding.FragmentCheckinBinding;
import com.example.scanln.faceDetector.FaceDetectorProcessor;
import com.example.scanln.faceDetector.ImageUtils;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckInFragment extends Fragment {
    private FragmentCheckinBinding binding;
    private Bitmap cur;
    private ExecutorService cameraExecutor;
    private boolean valid=false;
    private ImageCapture imageCapture;
    private ImageAnalysis analysis;
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        binding=FragmentCheckinBinding.inflate(inflater,container,false);
        cameraExecutor = Executors.newSingleThreadExecutor();
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState){
        setUpCamera();

    }

    @SuppressLint("UnsafeOptInUsageError")
    private void setUpCamera(){
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture=
                ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            Preview preview=new Preview.Builder()
                    .build();
            System.out.println(preview.getTargetRotation());

//            imageCapture = new ImageCapture.Builder()
//                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();
            ProcessCameraProvider cameraProvider;
            CameraSelector cameraSelector=new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
            analysis=new ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();
            analysis.setAnalyzer(cameraExecutor,new FaceDetectorProcessor(new ImageUtils(),requireContext()));
            try {

                cameraProvider = cameraProviderFuture.get();
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(requireActivity(), cameraSelector,
                        preview,analysis);
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());
                System.out.println("provider ready");

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));

    }
//    private void capture(){
//        if(imageCapture==null) return;
//        imageCapture.takePicture(cameraExecutor, new ImageCapture.OnImageCapturedCallback() {
//            @Override
//            public void onCaptureSuccess(ImageProxy imageProxy){
//                Bitmap bitmap=proxytToBitmap(imageProxy);
//                super.onCaptureSuccess(imageProxy);
//                cur=bitmap;
//            }
//
//            @Override
//            public void onError(ImageCaptureException e){
//                super.onError(e);
//            }
//        });
//    }

}
