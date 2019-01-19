package com.tache.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.tache.R;

public class WebViewFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
   // WebView webView;
    // TODO: Rename and change types of parameters
    private String url;
    private String mParam2;

    private WebView webview;
    private OnWebViewComplete mListener;


    public WebViewFragment() {
        // Required empty public constructor
    }

    public static WebViewFragment newInstance(String url) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, url);

        fragment.setArguments(args);
        System.out.println("My URL ==== "+fragment);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWebViewComplete) {
            mListener = (OnWebViewComplete) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement OnDetailFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        webview = (WebView) view.findViewById(R.id.webview);
        return view;
    }


    /*@Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webview.canGoBack()) {
                        webview.goBack();
                    } else {
                        onBackPressed();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }*/



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);

        settings.setAppCacheEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setPluginState(WebSettings.PluginState.ON);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(webViewClient);

        webview.loadUrl(url);

        System.out.println("My URL 2 ====  "+url);
    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("CHECK", url);
            if (url.contains("/callback/")) {
                Uri uri = Uri.parse(url);
                String code = uri.getQueryParameter("code");
                String status = uri.getQueryParameter("status");
                if (code.equals("200")) {
                    switch (status) {
                        case "complete":
                            Toast.makeText(getContext(), "Survey completed!", Toast.LENGTH_SHORT).show();
                            break;
                        case "quota_full":
                            Toast.makeText(getContext(), "Survey quota full!", Toast.LENGTH_SHORT).show();
                            break;
                        case "terminate":
                            Toast.makeText(getContext(), "Survey terminated!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                mListener.closeWebView(WebViewFragment.this);
            }
            return false;
        }
    };

    public interface OnWebViewComplete {
        void closeWebView(Fragment fragment);
    }




}
