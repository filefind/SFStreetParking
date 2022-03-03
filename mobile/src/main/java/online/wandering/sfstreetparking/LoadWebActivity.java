package online.wandering.sfstreetparking;

import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class LoadWebActivity extends AppCompatActivity {

    String link;

    private WebView generateWebView(){
        WebView wv = new WebView(getApplicationContext());
        wv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        wv.setFitsSystemWindows(false); // your preferences
        wv.setVerticalScrollBarEnabled(false); // your preferences
        wv.setPadding(15,15,15,15); // your preferences
        wv.loadUrl(link);
        return wv;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_web);
        LinearLayout scrollContainer = findViewById(R.id.scrollContainer);

        link = getIntent().getExtras().getString("link");

        scrollContainer.addView(generateWebView());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}