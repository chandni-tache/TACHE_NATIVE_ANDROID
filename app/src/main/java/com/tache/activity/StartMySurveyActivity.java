package com.tache.activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.tache.R;
import com.tache.adapter.SurveysRecyclerAdapter;
import com.tache.fragments.WebViewFragment;
import com.tache.rest.models.response.Surveys;
import com.tache.rest.services.LinksService;
import com.tache.utils.Helper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class StartMySurveyActivity extends AppCompatActivity {
    ArrayList<Surveys> dataList;
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_my_survey);

        webView = (WebView)this.findViewById(R.id.myWebOne);


        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setBuiltInZoomControls(false);
        webView.setWebViewClient(new MyBrowser());
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webView.canGoBack();

        /*if (Build.VERSION.SDK_INT >= 26) {
            // settings.setMixedContentMode(0);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 19) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }*/



        Intent start = new Intent(Intent.ACTION_VIEW);
        start.putExtra("url", String.format(LinksService.TASK_URL, dataList.get(1).getId(), Helper.getAuthKey(getApplicationContext())));
        start.putExtra("what", "Survey");

       startActivity(start);
       // System.out.println("Hello Sonu ke titu ki sweeti == "+String.format(LinksService.TASK_URL, dataList.get(getAdapterPosition()).getId(), Helper.getAuthKey(context)));

    //    Toast.makeText(context, "Jai ho.....", Toast.LENGTH_SHORT).show();
        //using Activity for webView for now..
        EventBus.getDefault().post(WebViewFragment.newInstance(String.format(LinksService.TASK_URL, dataList.get(1).getId(), Helper.getAuthKey(getApplicationContext()))));
    }


    class MyBrowser extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
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
                        finish();
                    }
                    return true;
            }
        }
        return false;
        //   return super.onKeyDown(keyCode, event);
    }

}
