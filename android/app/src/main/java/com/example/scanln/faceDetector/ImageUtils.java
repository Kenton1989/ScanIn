package com.example.scanln.faceDetector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.Image;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Base64;
import android.util.Log;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageUtils {

    public static Bitmap crop(ImageProxy proxy, Rect rect, Context context){
        @SuppressLint("UnsafeOptInUsageError")
        Image image=proxy.getImage();
        Bitmap bitmap=yuv420ToBitmap(image,context);

        //System.out.println(proxy.getCropRect());
        //System.out.println(bitmap.getHeight()+" "+bitmap.getWidth());
        //System.out.println(rect.left+" "+rect.top+" "+rect.width()+" "+rect.height());
        //Log.d("crop",rect.toString());
        Matrix matrix=new Matrix();
        matrix.postRotate(270);
        bitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),
                bitmap.getHeight(),matrix,true);
        Bitmap cropped=null;
        try{
            cropped=Bitmap.createBitmap(bitmap,rect.top,rect.left,rect.height(),rect.width());
        } catch (IllegalArgumentException e){
            return null;
        }
        //Bitmap cropped=Bitmap.createBitmap(bitmap,rect.top,rect.left,rect.height(),rect.width());
        //System.out.println(cropped.getHeight()+" "+cropped.getWidth());
        return bitmap;
    }

    public static Bitmap yuv420ToBitmap(Image image, Context context){
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB script = ScriptIntrinsicYuvToRGB.create(
                rs, Element.U8_4(rs));

        byte[] yuvByteArray = yuv420ToByteArray(image);

        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs))
                .setX(yuvByteArray.length);
        Allocation in = Allocation.createTyped(
                rs, yuvType.create(), Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs))
                .setX(image.getWidth())
                .setY(image.getHeight());
        Allocation out = Allocation.createTyped(
                rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        in.copyFrom(yuvByteArray);
        script.setInput(in);
        script.forEach(out);

        Bitmap bitmap = Bitmap.createBitmap(
                image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        out.copyTo(bitmap);
        return bitmap;
    }

    private static byte[] yuv420ToByteArray(Image image){
        int width = image.getWidth();
        int height = image.getHeight();

        Image.Plane yPlane = image.getPlanes()[0];
        Image.Plane uPlane = image.getPlanes()[1];
        Image.Plane vPlane = image.getPlanes()[2];

        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();

        int numPixels = (int) (width * height * 1.5f);
        byte[] nv21 = new byte[numPixels];
        int index = 0;

        // Copy Y channel.
        int yRowStride = yPlane.getRowStride();
        int yPixelStride = yPlane.getPixelStride();
        for(int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                nv21[index++] = yBuffer.get(y * yRowStride + x * yPixelStride);
            }
        }

        int uvRowStride = uPlane.getRowStride();
        int uvPixelStride = uPlane.getPixelStride();
        int uvWidth = width / 2;
        int uvHeight = height / 2;

        for(int y = 0; y < uvHeight; ++y) {
            for (int x = 0; x < uvWidth; ++x) {
                int bufferIndex = (y * uvRowStride) + (x * uvPixelStride);
                // V channel.
                nv21[index++] = vBuffer.get(bufferIndex);
                // U channel.
                nv21[index++] = uBuffer.get(bufferIndex);
            }
        }
        return nv21;
    }

    public static String getJPEGString(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray,Base64.DEFAULT);

    }
}
