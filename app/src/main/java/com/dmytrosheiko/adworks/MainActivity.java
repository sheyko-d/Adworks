package com.dmytrosheiko.adworks;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private EditText mEditTxt;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        initWebView();
    }

    private void bindViews() {
        mEditTxt = findViewById(R.id.editText);
        mWebView = findViewById(R.id.webView);
        mProgressBar = findViewById(R.id.progressBar);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mEditTxt.getText().toString())
                        && URLUtil.isValidUrl(mEditTxt.getText().toString())) {
                    mWebView.loadUrl(mEditTxt.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "Invalid URL",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        mWebView.setWebViewClient(new HttpsWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {
                mProgressBar.setProgress(progress);
                if (progress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private class HttpsWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(view.getUrl())));
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("http://")) {
                mWebView.loadUrl("https://" + url.replace("http://", ""));
                return true;
            } else {
                view.loadUrl(url);
                return false;
            }
        }

        public void onReceivedError(WebView view, int errorCode, String description,
                                    String failingUrl) {
            Toast.makeText(MainActivity.this, "Error, opening in browser instead...",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + failingUrl
                    .replace("https://", ""))));
        }
    }
}
