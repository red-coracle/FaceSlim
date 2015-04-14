package org.indywidualni.fblite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyAppWebViewClient extends WebViewClient {

    // variable for onReceivedError
    private boolean refreshed;

    // it have to be static to avoid overwriting again and again
    private static String cssFile;

    // get application context from MainActivity
    private Context context = MainActivity.getContextOfApplication();

    // get shared preferences
    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // handling external links as intents
        if( Uri.parse(url).getHost().endsWith("facebook.com") || Uri.parse(url).getHost().endsWith("m.facebook.com") || Uri.parse(url).getHost().endsWith("h.facebook.com") || Uri.parse(url).getHost().endsWith("l.facebook.com") || Uri.parse(url).getHost().endsWith("0.facebook.com") || Uri.parse(url).getHost().endsWith("zero.facebook.com") || Uri.parse(url).getHost().endsWith("fb.me") ) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(intent);
        return true;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        // refresh on connection error (sometimes there is an error even when there is a network connection)
        if(!refreshed) {
            view.loadUrl(failingUrl);
            // when network error is real do not reload url again
            refreshed = true;
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // turn facebook black (highly experimental)
        if (preferences.getBoolean("dark_theme", false)) {
            cssFile = readRawTextFile(context, R.raw.black);
            view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); node.innerHTML = str; document.body.appendChild(node); } addStyleString('" + cssFile + "');");
        }
        // apply extra bottom padding for transparent navigation
        if (preferences.getBoolean("transparent_nav", false)) {
            view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); node.innerHTML = str; document.body.appendChild(node); } addStyleString('body{ padding-bottom: 47px; }');");
        }
    }

    // read raw files to string (for css files)
    private static String readRawTextFile(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();
        try {
            while (( line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return " ";
        }
        return text.toString();
    }

}