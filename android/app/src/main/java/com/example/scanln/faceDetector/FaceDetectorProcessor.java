package com.example.scanln.faceDetector;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import androidx.camera.core.ImageProxy;

import com.example.scanln.FaceDetectionCallback;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FaceDetectorProcessor extends VisionProcessorBase<List<Face>>{

    private FaceDetectorOptions realTimeOpts= new FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .build();

    private FaceDetector detector= FaceDetection.getClient(realTimeOpts);

    public FaceDetectorProcessor(Context context, FaceDetectionCallback callback) {
        super(context,callback);
    }

    @Override
    protected Task<List<Face>> detectImage(InputImage image) {
        return detector.process(image);
    }

    @Override
    public void onSuccess(List<Face> result, ImageProxy image) {
        if(result.size()==1){
            Face face=result.get(0);
            this.result=ImageUtils.crop(image,face.getBoundingBox(),context);
            callback.onSccess(this.result);
        }
    }

    @Override
    protected void onFailure(Exception e) {
        Log.w("face detector processor","fail to detect face");
    }

    @Override
    public void stop() {
        try{
            detector.close();
        } catch (Exception e){
            Log.e("face detector processor", "fail to stop "+e.toString());
        }
    }
}
