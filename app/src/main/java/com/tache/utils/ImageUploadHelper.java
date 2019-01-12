package com.tache.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayank on 14/10/16.
 */

public class ImageUploadHelper {

    private Context context;
    private int uploadIndex;
    private int desiredSize = -1;

    public ImageUploadHelper(Context context, int desiredSize) {
        this.context = context;
        this.desiredSize = desiredSize;
    }

    public ImageUploadHelper(Context context) {
        this.context = context;
    }

    private List<String> mImageUrls = new ArrayList<>();

    public void uploadImagesOnS3(@NonNull final ArrayList<String> filePaths, @NonNull final ImageUploadListener imageUploadListener) {

        final int totalFiles = filePaths.size();

        final ImageUploadHelper.ImageUploadListener uploadListener = new ImageUploadHelper.ImageUploadListener() {
            @Override
            public void onUploadCompleted(List<String> imageUrls) {
                if (uploadIndex < totalFiles) {
                    uploadImage(filePaths.get(uploadIndex), this);
                } else if (uploadIndex == totalFiles) {
                    imageUploadListener.onUploadCompleted(mImageUrls);
                }
            }


            @Override
            public void onError() {
                imageUploadListener.onError();
            }
        };

        uploadImage(filePaths.get(uploadIndex = 0), uploadListener);
    }

    private void uploadImage(String filePath, final ImageUploadListener imageUploadListener) {
        TransferUtility transferUtility = AmazonS3Utils.getTransferUtility(context);

        SharedPrefsUtils sharedPrefsUtils = new SharedPrefsUtils(context);
        final File imageFile;
        if (desiredSize == -1) imageFile = new ImageCompressorHelper(context).compressImage(new File(filePath));
        else imageFile = new ImageCompressorHelper(context).compressImage(new File(filePath), desiredSize);
        final String key = "images/"
                + String.valueOf(sharedPrefsUtils.getIntegerPreference(SharedPrefsUtils.USER_ID, 0))
                + System.currentTimeMillis()
                + imageFile.getName();

        TransferObserver transferObserver = transferUtility.upload(
                AmazonS3Utils.BUCKET_NAME,
                key,
                imageFile
        );

        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    String imageUrl = AmazonS3Utils.getBucketUrl() + key;
                    mImageUrls.add(imageUrl);
                    uploadIndex++;
                    imageUploadListener.onUploadCompleted(null);
                    Log.d("upload", "images: completed" + uploadIndex);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                Log.d("upload", "images: error = " + ex.getCause());
                Toast.makeText(context, "Error uploading Image", Toast.LENGTH_SHORT).show();
                imageUploadListener.onError();
            }
        });
    }

    public interface ImageUploadListener {
        void onUploadCompleted(List<String> imageUrls);
        void onError();
    }
}
