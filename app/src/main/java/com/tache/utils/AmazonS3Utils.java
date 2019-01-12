package com.tache.utils;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * Created by mayank on 24/8/16.
 */
public class AmazonS3Utils {

    private static final String COGNITO_POOL_ID = "ap-northeast-1:97e75e8c-c26b-4cc0-a842-a87a2373e2ee";
    // We only need one instance of the clients and credentials provider
    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;
    private static TransferUtility sTransferUtility;
    private static String bucketUrl;
    static final String BUCKET_NAME = "tache-upload";
    private static final String ACCOUNT_ID = "232249072935";
    private static final String BASE_ARN = "arn:aws:iam::232249072935:role/Cognito_tache";
    private static final String AUTH_ARN = BASE_ARN + "Auth_Role";
    private static final String UNAUTH_ARN = BASE_ARN + "Unauth_Role";

    /**
     * Gets an instance of CognitoCachingCredentialsProvider which is
     * constructed using the given Context.
     *
     * @param context An Context instance.
     * @return A default credential provider.
     */
    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        if (sCredProvider == null) {
            sCredProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    ACCOUNT_ID,
                    COGNITO_POOL_ID,
                    UNAUTH_ARN,
                    AUTH_ARN,
                    Regions.AP_NORTHEAST_1);
        }
        return sCredProvider;
    }

    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A default S3 client.
     */
    public static AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));
            sS3Client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
        }
        return sS3Client;
    }

    /**
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     *
     * @param context
     * @return a TransferUtility instance
     */
    public static TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = new TransferUtility(getS3Client(context.getApplicationContext()),
                    context.getApplicationContext());
        }

        return sTransferUtility;
    }

    public static String getBucketUrl(){
        if (bucketUrl == null){
            bucketUrl = "https://" + BUCKET_NAME + ".s3.amazonaws.com/";
        }
        return bucketUrl;
    }
}
