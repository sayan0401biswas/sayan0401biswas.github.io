package io.github.sayan0401biswas.portfolio;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public final class MainActivity extends Activity {
    private static final String HOME_URL = "https://sayan0401biswas.github.io/";
    private static final String SITE_HOST = "sayan0401biswas.github.io";

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(8, 19, 38));
        getWindow().setNavigationBarColor(Color.rgb(6, 16, 30));

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(247, 245, 238));

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(247, 245, 238));
        WebView.setWebContentsDebuggingEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAllowContentAccess(false);
        webView.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.rgb(44, 123, 229)));
        progressBar.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.TRANSPARENT));
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(ProgressBar.GONE);

        root.addView(webView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(3));
        root.addView(progressBar, progressParams);
        setContentView(root);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return routeUrl(request.getUrl());
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request,
                                        android.webkit.WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request.isForMainFrame()) showOfflinePage();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress >= 100 ? ProgressBar.GONE : ProgressBar.VISIBLE);
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture,
                                          Message resultMsg) {
                WebView popup = new WebView(MainActivity.this);
                popup.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView child, WebResourceRequest request) {
                        Uri uri = request.getUrl();
                        if (!routeUrl(uri)) webView.loadUrl(uri.toString());
                        child.destroy();
                        return true;
                    }
                });
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(popup);
                resultMsg.sendToTarget();
                return true;
            }
        });

        webView.loadUrl(HOME_URL);
    }

    private boolean routeUrl(Uri uri) {
        String scheme = uri.getScheme();
        if ("https".equalsIgnoreCase(scheme) && SITE_HOST.equalsIgnoreCase(uri.getHost())) {
            return false;
        }
        if ("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme)
                || "mailto".equalsIgnoreCase(scheme) || "tel".equalsIgnoreCase(scheme)) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (ActivityNotFoundException exception) {
                Toast.makeText(this, "No app is available to open this link.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return true;
    }

    private void showOfflinePage() {
        String html = "<!doctype html><html><head><meta name='viewport' content='width=device-width,initial-scale=1'>"
                + "<style>body{font-family:system-ui,sans-serif;margin:0;min-height:100vh;display:grid;place-items:center;"
                + "background:#f7f5ee;color:#122033;text-align:center}.box{padding:32px;max-width:330px}"
                + "h1{font-size:22px}p{color:#647084;line-height:1.6}button{border:0;border-radius:10px;"
                + "padding:13px 20px;background:#123b68;color:white;font-weight:700;font-size:15px}</style></head>"
                + "<body><div class='box'><h1>You're offline</h1><p>Connect to the internet, then try loading the portfolio again.</p>"
                + "<button onclick='location.href=\"" + HOME_URL + "\"'>Try again</button></div></body></html>";
        webView.loadDataWithBaseURL(HOME_URL, html, "text/html", "UTF-8", HOME_URL);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
