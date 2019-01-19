package com.tache.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.tache.R;
import com.tache.utils.Constants;
import com.tache.utils.Helper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StartSurveyMissionActivity extends AppCompatActivity {
    private String what, url;
    private boolean reload = false;
    private TextView empty_view;
    private WebView webView;
    private ProgressDialog progressDialog;
    private CoordinatorLayout coordinatorLayout;
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_survey_mission);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        empty_view = (TextView) findViewById(R.id.empty_view);
        webView = (WebView) findViewById(R.id.webview);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        what = intent.getStringExtra("what");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading " + what);
        progressDialog.setMessage("just a moment..");

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setPluginState(WebSettings.PluginState.ON);

        if (Build.VERSION.SDK_INT >= 26) {
           // settings.setMixedContentMode(0);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 19) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.setWebChromeClient(new MyWebViewClient());
        webView.setWebViewClient(webViewClient);
        webView.canGoBack();

        loadUrl();
    }





    private void loadUrl() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "No internet connection", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUrl();
            }
        }).setActionTextColor(Color.YELLOW);

        if (Helper.isConnected(this)) {
            webView.loadUrl(url);
            progressDialog.show();
            webView.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
            snackbar.dismiss();
        } else {
            snackbar.show();
            empty_view.setVisibility(View.VISIBLE);
        }
    }


    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("/callback/")) {
                Uri uri = Uri.parse(url);
                System.out.println("Mission URL mil gaya hai........  "+uri);
                String code = uri.getQueryParameter("code");
                String status = uri.getQueryParameter("status");
                if (code.equals("200")) {
                    reload = true;
                    switch (status) {
                        case "complete":
                            dialogStatus(what, what + " " + "completed!");
                            break;
                        case "quota_full":
                            dialogStatus(what, what + " " + "quota full!");
                            break;
                        case "terminate":
                            dialogStatus(what, what + " " + "terminated!");
                            break;
                    }
                } else {
                    dialogStatus(what, "Something went wrong!");
                }
            }else {
                Toast.makeText(StartSurveyMissionActivity.this, "ssssddddddd", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    };

    private void dialogStatus(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(message).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onBackPressed();
            }
        }).show();
    }

    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (progressDialog != null) {
                if (newProgress == 100) {
                    webView.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }
        }
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        //For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUM = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            StartSurveyMissionActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), FCR);
        }

        // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            mUM = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            StartSurveyMissionActivity.this.startActivityForResult(
                    Intent.createChooser(i, "File Browser"),
                    FCR);
        }

        //For Android 4.1+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUM = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            StartSurveyMissionActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), StartSurveyMissionActivity.FCR);
        }

        //For Android 5.0+
        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                WebChromeClient.FileChooserParams fileChooserParams) {
            if (mUMA != null) {
                mUMA.onReceiveValue(null);
            }
            mUMA = filePathCallback;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(StartSurveyMissionActivity.this.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCM);
                } catch (IOException ex) {
                    Log.e("WebView", "Image file creation failed", ex);
                }
                if (photoFile != null) {
                    mCM = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("*/*");
            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(chooserIntent, FCR);
            return true;
        }
    }

    // Create an image file
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            //Check if response is positive
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null) {
                        //Capture Photo if no image available
                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        } else {
            if (requestCode == FCR) {
                if (null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        webView.canGoBack();
                        finish();
                    }
                    return true;
            }
        }
      // return false;
         return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        Constants.RELOAD_MISSION = reload;
        Constants.RELOAD_SURVEY = reload;
        super.onBackPressed();
        /*Intent moveback =
                new Intent(getApplicationContext(), StartSurveyMissionActivity.class);
        startActivity(moveback);
        finish();*/
    }
}
