package online.wandering.sfstreetparking;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewSource;

public class StreetViewActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {

    private StreetViewPanoramaView streetViewPanoramaView;
    private static LatLng SAN_FRAN = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);

        Log.e("----", String.valueOf(getIntent().getExtras().getDouble("lat")));
        Log.e("----", String.valueOf(getIntent().getExtras().getDouble("lon")));

        SAN_FRAN = new LatLng(getIntent().getExtras().getDouble("lat"),
                getIntent().getExtras().getDouble("lon"));

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
// Set position with LatLng only.
        streetViewPanorama.setPosition(SAN_FRAN);
// Set position with LatLng and radius.
        streetViewPanorama.setPosition(SAN_FRAN, 20);
// Set position with LatLng and source.
        streetViewPanorama.setPosition(SAN_FRAN, StreetViewSource.OUTDOOR);
// Set position with LaLng, radius and source.
        streetViewPanorama.setPosition(SAN_FRAN, 20, StreetViewSource.OUTDOOR);
    }
}