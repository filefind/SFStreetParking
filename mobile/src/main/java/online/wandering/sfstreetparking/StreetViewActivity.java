package online.wandering.sfstreetparking;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class StreetViewActivity extends AppCompatActivity {

//    private StreetViewPanoramaView streetViewPanoramaView;
//    private static LatLng SAN_FRAN = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);

        Log.e("----", String.valueOf(getIntent().getExtras().getDouble("lat")));
        Log.e("----", String.valueOf(getIntent().getExtras().getDouble("lon")));
        String link =
                getString(R.string.streetview) + "?location=streetview&lat=" + getIntent().getExtras().getDouble("lat") + "&lon=" + getIntent().getExtras().getDouble("lon") + "";
        Log.e("----", link);

        WebView myWebView = (WebView) findViewById(R.id.streetwebview);
        myWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        myWebView.loadUrl(link);
    }

//    @Override
//    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
//// Set position with LatLng only.
//        streetViewPanorama.setPosition(SAN_FRAN);
//// Set position with LatLng and radius.
//        streetViewPanorama.setPosition(SAN_FRAN, 20);
//// Set position with LatLng and source.
//        streetViewPanorama.setPosition(SAN_FRAN, StreetViewSource.OUTDOOR);
//// Set position with LaLng, radius and source.
//        streetViewPanorama.setPosition(SAN_FRAN, 20, StreetViewSource.OUTDOOR);
//    }
}
