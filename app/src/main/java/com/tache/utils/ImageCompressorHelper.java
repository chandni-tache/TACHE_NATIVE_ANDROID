package com.tache.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

import id.zelory.compressor.Compressor;

/**
 * Created by mayank on 14/10/16.
 */

public class ImageCompressorHelper {

    private Context context;

    public ImageCompressorHelper(Context context) {
        this.context = context;
    }

    public File compressImage(File inputFile) {
        return compressImage(inputFile, 1080);
    }

    public File compressImage(File inputFile, int desiredSize) {
        if (desiredSize <= 0) desiredSize = 1080;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(inputFile.getPath(), options);

        int width = options.outWidth;
        int height = options.outHeight;
        float ratio;
        if (width > desiredSize) {
            ratio = (float) width / desiredSize;
            width = desiredSize;
            height *= ratio;
        }

        return new Compressor.Builder(context)
                .setMaxWidth(width)
                .setMaxHeight(height)
                .setQuality(80)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .build()
                .compressToFile(inputFile);
    }
}
