package com.example.scanln;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.scanln.databinding.FragmentCheckinBinding;
import com.example.scanln.faceDetector.FaceDetectorProcessor;
import com.example.scanln.faceDetector.ImageUtils;
import com.example.scanln.model.Auth;
import com.example.scanln.model.SessionBrief;
import com.example.scanln.model.UserInfo;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckInFragment extends Fragment implements FaceDetectionCallback{
    private FragmentCheckinBinding binding;
    private Bitmap cur;
    private ExecutorService cameraExecutor;
    private boolean valid=false;
    private ImageCapture imageCapture;
    private ImageAnalysis analysis;
    private FaceDetectorProcessor analyser;
    private ProcessCameraProvider cameraProvider;
    private CheckinViewModel model;
    private LocalDateTime allowDetectionTime=LocalDateTime.MIN;
    private static final Duration detectionTimeGap=Duration.ofMillis(300);
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        binding=FragmentCheckinBinding.inflate(inflater,container,false);
        model=new ViewModelProvider(requireActivity()).get(CheckinViewModel.class);
        cameraExecutor = Executors.newSingleThreadExecutor();
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState){
        setUpCamera();
        binding.back.setOnClickListener(backToMain);
    }

    private View.OnClickListener backToMain = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            cameraProvider.unbindAll();
            cameraExecutor.shutdown();

            NavDirections action=CheckInFragmentDirections.actionNavigationCheckinToNavigationMainMenu();
            Navigation.findNavController(view).navigate(action);
        }
    };

    @SuppressLint("UnsafeOptInUsageError")
    private void setUpCamera(){
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture=
                ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            Preview preview=new Preview.Builder()
                    .build();
            CameraSelector cameraSelector=new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
            analysis=new ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();

            analyser=new FaceDetectorProcessor(requireContext(),this);

            analysis.setAnalyzer(cameraExecutor,analyser);
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


    private void recognizeFace(){
        Auth auth=new Auth("","");
        Map<String,Object> params=new HashMap<>();
        String imgString=ImageUtils.getJPEGString(cur);
        params.put(VRequestQueue.CHECKIN_FACE_FIELD,imgString);
        params.put(VRequestQueue.CHECKIN_WANT_SESSION_FIELD,true);
        String operation=VRequestQueue.RECOGNIZE_FACE;
        Response.Listener<JSONObject> listener= response -> {
            if(response.optBoolean(VRequestQueue.RESULT_PARAM)){
                onRecogniseSuccess(response);
            }
            else{
                Log.w("login result",response.toString());
            }
        };
        Response.ErrorListener errorListener= this::onRequestError;
        VRequestQueue.getInstance(requireContext()).createRequest(params,operation,auth,listener,errorListener);
    }

    private void onRecogniseSuccess(JSONObject response){
        valid=true;
        Log.w("login result",response.toString());
        try{
            JSONObject returns=response.getJSONObject(VRequestQueue.RETURN);
            JSONObject userInfo=returns.getJSONObject("user");
            JSONArray sessions=returns.getJSONArray("sessions");
            JSONObject auth=returns.getJSONObject("auth");
            model.setAuth(auth);
            UserInfo info=new UserInfo(userInfo);
            model.setPid(info.getPid());
            model.setPname(info.getName());
            List<SessionBrief> sessionBriefs=new ArrayList<>();
            for(int i=0;i<sessions.length();i++){
                JSONObject json=sessions.getJSONObject(i);
                SessionBrief sessionBrief=new SessionBrief(json);
                sessionBriefs.add(sessionBrief);
            }
            model.setRecords(sessionBriefs);
            cameraProvider.unbindAll();
            cameraExecutor.shutdown();
            cur=null;
            NavDirections action=CheckInFragmentDirections.actionNavigationCheckinToNavigationCheckinConfirm();
            Navigation.findNavController(requireView()).navigate(action);
        } catch (Exception e){
            Log.e("recognise face",e.getMessage());
        }


    }
    private void onRequestError(VolleyError error){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("request error")
                .setMessage("Due to server connection error: "+error.toString())
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    @Override
    public void onSccess(Bitmap result) {
        LocalDateTime now=LocalDateTime.now();
        Log.w("onSuccess",now.toString()+" "+allowDetectionTime.toString());
        if(now.compareTo(allowDetectionTime)>=0&&result!=null){
            cur=result;
            recognizeFace();
            allowDetectionTime=now.plus(detectionTimeGap);
        }
    }
}
