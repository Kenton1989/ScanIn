package com.example.scanln.faceDetector;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;

import java.nio.ByteBuffer;

public abstract class VisionProcessorBase<T> implements ImageAnalysis.Analyzer{

    public ImageStringGenerator generator;

    public VisionProcessorBase(ImageStringGenerator generator){
        this.generator=generator;
    }

    @Override
    public void analyze(ImageProxy imageProxy){
        @SuppressLint("UnsafeOptInUsageError")
        Image mediaImage=imageProxy.getImage();
        Task<T> task=detectImage(InputImage.
                fromMediaImage(mediaImage,imageProxy.getImageInfo().getRotationDegrees()));
        task.addOnSuccessListener(new OnSuccessListener<T>() {
            @Override
            public void onSuccess(T t) {
                VisionProcessorBase.this.onSuccess(t,proxytToBitmap(imageProxy));
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

    private Bitmap proxytToBitmap(ImageProxy proxy){
        ImageProxy.PlaneProxy planeProxy=proxy.getPlanes()[0];
        ByteBuffer buffer=planeProxy.getBuffer();
        byte[] bytes=new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    protected abstract Task<T> detectImage(InputImage image);
    public abstract void onSuccess(T result, Bitmap bitmap);
    protected abstract void onFailure(Exception e);
    public abstract void stop();
}