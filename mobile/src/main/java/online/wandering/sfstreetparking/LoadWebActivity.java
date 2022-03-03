package online.wandering.sfstreetparking;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class LoadWebActivity extends AppCompatActivity {

    String link;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private WebView generateWebView(){
        WebView wv = new WebView(getApplicationContext());
        wv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        wv.setFitsSystemWindows(false); // your preferences
        wv.setVerticalScrollBarEnabled(false); // your preferences
        wv.setPadding(15,15,15,15); // your preferences
        wv.getSettings().setMediaPlaybackRequiresUserGesture(true);
        wv.getSettings().setLoadsImagesAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            wv.getSettings().setOffscreenPreRaster(true);
        }
        wv.getSettings().setAppCacheEnabled(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl(link);
        return wv;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_web);
        ConstraintLayout scrollContainer = findViewById(R.id.scrollContainer);

        link = getIntent().getExtras().getString("link");
Log.e("Link",link);
        scrollContainer.addView(generateWebView());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}