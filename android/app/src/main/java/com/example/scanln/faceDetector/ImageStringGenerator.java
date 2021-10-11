package com.example.scanln.faceDetector;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;

public class ImageStringGenerator {

    public void process(Bitmap bitmap, Rect rect){
        Bitmap cropped=Bitmap.createBitmap(bitmap,rect.left,rect.top,rect.width(),rect.height());

    }
}
