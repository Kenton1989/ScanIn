package com.example.scanln.faceDetector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import com.example.scanln.FaceDetectionCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class VisionProcessorBase<T> implements ImageAnalysis.Analyzer{

    public Context context;
    public Bitmap result;
    public FaceDetectionCallback callback;

    public VisionProcessorBase(Context context, FaceDetectionCallback callback){
        this.context=context;
        this.callback=callback;
    }

    @Override
    public void analyze(ImageProxy imageProxy){
        LocalDateTime now=LocalDateTime.now();
        @SuppressLint("UnsafeOptInUsageError")
        Image mediaImage=imageProxy.getImage();
        Task<T> task=detectImage(InputImage.
                fromMediaImage(mediaImage,imageProxy.getImageInfo().getRotationDegrees()));
        task.addOnSuccessListener(new OnSuccessListener<T>() {
            @Override
            public void onSuccess(T t) {
                VisionProcessorBase.this.onSuccess(t,imageProxy);
                imageProxy.close();
            }
        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e){
                VisionProcessorBase.this.onFailure(e);
                imageProxy.close();
            }
        });

    }

    public Bitmap getResult(){
        return result;
    }

    protected abstract Task<T> detectImage(InputImage image);
    public abstract void onSuccess(T result, ImageProxy image);
    protected abstract void onFailure(Exception e);
    public abstract void stop();
}
