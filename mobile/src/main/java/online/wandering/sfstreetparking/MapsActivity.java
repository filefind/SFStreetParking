package online.wandering.sfstreetparking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient
        .ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private LatLng centerMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationManager mLocationManager;
    private String mLocationProvider;
    private HashMap<String, Marker> mHashMap = new HashMap<String, Marker>();
    private LocationListener mLocationListener;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private String rate = null;
    private TextView details;
    RewardedVideoAd mRewardedVideoAd = null;

    /* AdMob */

    private static final String AD_UNIT_ID = "ca-app-pub-9344202428605178/4120545669";
    private static final long COUNTER_TIME = 10;
    private static final int GAME_OVER_REWARD = 1;

    private int coinCount;
    private TextView coinCountText;
    private CountDownTimer countDownTimer;
    private boolean gameOver;
    private boolean gamePaused;

    private RewardedAd rewardedAd;
    private Button retryButton;
    private Button showVideoButton;
    private long timeRemaining;
    boolean isLoading;

    double lat;
    double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        details = findViewById(R.id.place_details);
//        if(getIntent().hasExtra("rate")){
//            rate = getIntent().getExtras().getString("rate");
//        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        FragmentManager myFragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

//        loadRewardedAd();

//        showVideoButton = findViewById(R.id.show_video_button);
//        showVideoButton.setVisibility(View.GONE);
//        showVideoButton.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        loadRewardedAd();
//                    }
//                });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();

        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if(getIntent().hasExtra("lat")){
//            Log.e("ExtraLat", String.valueOf(String.valueOf(getIntent().getExtras().get("lat"))));
            lat = Double.parseDouble(getIntent().getExtras().get("lat").toString());
//            lat = Double.parseDouble(String.valueOf(getIntent().getExtras().get("lat")));
        } else {
            lat = Double.parseDouble(String.valueOf(37.7816778));
        }
        if(getIntent().hasExtra("lon")){
            lon = Double.parseDouble(getIntent().getExtras().get("lon").toString());
//            Log.e("ExtraLon", String.valueOf(lon));
        } else {
            lon = Double.parseDouble(String.valueOf(-122.395589654));
        }

//        if(locationGPS.equals(null)){
//            Log.e("INFO", String.valueOf(locationGPS.getLatitude()));
//            Log.e("INFO", String.valueOf(locationGPS.getLongitude()));
//            centerMap = new LatLng(locationGPS.getLatitude(), locationGPS.getLongitude());
//        } else {
//            centerMap = new LatLng(0.0, 0.0);
//        }
        centerMap = new LatLng(lat, lon);
//        mMap.addMarker(new MarkerOptions().position(centerMap).title("Checking GPS location..."));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(centerMap));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
//        mMap.addTileOverlay(TileOverlayOptions.CREATOR)
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
//                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                    return;
//                }
                Intent intent = new Intent(MapsActivity.this, StreetViewActivity.class);
                intent.putExtra("title", marker.getTitle());
                intent.putExtra("lat", marker.getPosition().latitude);
                intent.putExtra("lon", marker.getPosition().longitude);
                startActivity(intent);
//                Toast.makeText(MapsActivity.this, "Dynamic Street View feature will incur charges",
//                        Toast.LENGTH_LONG).show();
//                Toast.makeText(MapsActivity.this, "Please Make Donation",
//                        Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(MapsActivity.this, DonationsActivity.class);
//                startActivity(intent);
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        mMap.setMyLocationEnabled(true);
        checkParking(lat, lon);
    }

    protected synchronized void buildGoogleApiClient() {
//        Log.e("mGoogleApiClient", "buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    protected synchronized boolean checkpermissions(){
        Boolean permsGPS = false;
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            permsGPS=true;
        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            permsGPS=false;
        }
        return permsGPS.equals(true) == true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mMap.clear();
        Toast.makeText(getApplicationContext(), "Connected! Checking location...", Toast
                .LENGTH_LONG)
                .show();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteriaReady = new Criteria();
            criteriaReady.setPowerRequirement(Criteria.POWER_LOW);
            criteriaReady.setAccuracy(Criteria.ACCURACY_FINE);
            criteriaReady.setSpeedRequired(true);
            criteriaReady.setAltitudeRequired(false);
            criteriaReady.setBearingRequired(false);
            criteriaReady.setCostAllowed(false);
            mLocationProvider = mLocationManager.getBestProvider(criteriaReady,true);
//            Log.e("Permissions", mLocationProvider);
            if (mLocationProvider == null || mLocationProvider.equals("")) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        } else {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MapsActivity.this, "GPS Status: suspended!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MapsActivity.this, "GPS Status: failed!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
//        mMap.clear();
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
//        Log.e("Location",location.getLatitude()+","+location.getLongitude());
//        checkParking(location.getLatitude(),location.getLongitude());
    }

    private void checkParking (double lat, double lng){
        Map<String, String> assoc = new HashMap<String, String>();
        assoc.put("lat", String.valueOf(lat));
        assoc.put("lng", String.valueOf(lng));
        assoc.put("zoom", "12");
        String userdata = "";
        for (Map.Entry<String, String> e : assoc.entrySet()) {
            userdata += e.getKey() + "=" + e.getValue() + "&";
        }
//        Log.e("--------",userdata);
        new DownloadTask(){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
//            Log.e("------", String.valueOf(result));
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(result);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    Double rateValue = 1000.0;
                    Marker rateMarker = null;
                    String dataValue = "";
                    mHashMap.clear();
                    if(jsonArray.length()>0){
                        Double minRate = 1000.0;
                        for (int j = 0; j < jsonArray.length(); j++) {
                            Boolean showMin = false;
                            String snippetValue="";
                            JSONObject jsonObjectUnit = new JSONObject(jsonArray.get(j).toString());
                            String post_id = jsonObjectUnit.getString
                                    ("post_id");
                            String sensor_flag = jsonObjectUnit.getString
                                    ("sensor_flag");
                            Double latitude = Double.valueOf(jsonObjectUnit.getString
                                    ("latitude"));
                            Double longitude = Double.valueOf(jsonObjectUnit.getString
                                    ("longitude"));
                            MarkerOptions mPriceMarkerOptions = new MarkerOptions();
                            LatLng latLngPrice = new LatLng(latitude,longitude);
                            mPriceMarkerOptions.position(latLngPrice);
                            mPriceMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.sad));
                            if(sensor_flag.equals("N")) mPriceMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.thumbsup));
                            if(sensor_flag.equals("E")) mPriceMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.info));
                            if(sensor_flag.equals("Y")) mPriceMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.danger));
                            if(sensor_flag.equals("X")) mPriceMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.target));
                            if(sensor_flag.equals("P")) mPriceMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pinion));
                            if(sensor_flag.equals("R")) mPriceMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.piechart));
                            if(sensor_flag.equals("C")) mPriceMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.help));
                            if(sensor_flag.equals("I")) mPriceMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.redstar));
                            if(sensor_flag.equals("H")) mPriceMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.warning));
                            Marker marker = mMap.addMarker(mPriceMarkerOptions);
                            marker.hideInfoWindow();
                            builder.include(latLngPrice);
                            mHashMap.put(marker.getId(),marker);
                            marker.setTitle("Unit #"+post_id);

                            snippetValue+="Time: "+jsonObjectUnit.getString("now")+"\n";
                            String rates = "";
                            String schedule = "";
                            if(jsonObjectUnit.has("schedule")){
                                JSONArray jsonArraySchedule = null;
                                jsonArraySchedule = new JSONArray(jsonObjectUnit.getString("schedule"));
                                for (int i = 0; i < jsonArraySchedule.length(); i++) {
                                    JSONObject jsonObjectUnitSchedule =
                                            new JSONObject(jsonArraySchedule.get(i).toString());
//                                    schedule+="\nPriority: "+jsonObjectUnitSchedule.getString(
//                                            "schedule_priority");
                                    schedule+="\n"+jsonObjectUnitSchedule.getString("schedule_type")+": " +
                                            ""+jsonObjectUnitSchedule.getString("time_limit");
                                    if(jsonObjectUnitSchedule.has("days_applied")){
                                        schedule+="\n"+jsonObjectUnitSchedule.getString("days_applied")+
                                                " ("+jsonObjectUnitSchedule.getString("from_time")+" - "+jsonObjectUnitSchedule.getString("to_time")+")";
                                    }
                                }
                            } else {
                                schedule+="\nSchedules not available!";
                            }
                            if(jsonObjectUnit.has("rates")){
                                JSONArray jsonArrayRates = null;
                                jsonArrayRates = new JSONArray(jsonObjectUnit.getString("rates"));
                                for (int i = 0; i < jsonArrayRates.length(); i++) {
                                    JSONObject jsonObjectUnitRates =
                                            new JSONObject(jsonArrayRates.get(i).toString());
                                    if(i==0) rates+=""; else rates+="\n";
//                                    rates+="Priority: "+jsonObjectUnitRates.getString(
//                                            "schedule_priority");
                                    rates+=""+jsonObjectUnitRates.getString("rate_type")+": " +
                                            "$"+jsonObjectUnitRates.getString("rate");
                                    if(jsonObjectUnitRates.has("days_applied")){
                                        rates+="\n"+jsonObjectUnitRates.getString("days_applied")+
                                                " ("+jsonObjectUnitRates.getString("from_time")+" - "+jsonObjectUnitRates.getString("to_time")+")";
                                    }
                                    if(jsonObjectUnitRates.getDouble("rate")<minRate){
                                        minRate=jsonObjectUnitRates.getDouble("rate");
                                        showMin = true;
                                        marker.showInfoWindow();
                                    }
                                }
                            } else {
                                rates+="Rates not available!";
                            }
                            marker.setSnippet(snippetValue+rates+schedule);
                            if(showMin.equals(true)){
                                marker.showInfoWindow();
                            }
                        }
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),
                                100));
                        details.setText("$"+minRate);
                    } else {
                        details.setText("There is no meters!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(getString(R.string.url)+"?action=loadMarkers", userdata);
    }

    public void loadAvailability (String post_id){
        Map<String, String> assoc = new HashMap<String, String>();
        assoc.put("post_id", String.valueOf(post_id));
        String userdata = "";
        for (Map.Entry<String, String> e : assoc.entrySet()) {
            userdata += e.getKey() + "=" + e.getValue() + "&";
        }
        DownloadTask dTask = new DownloadTask();
        dTask.execute(getString(R.string.url)+"?action=loadPlace", userdata);
        return;
    }

    public View getInfoContents(Marker marker) {

        Context context = getApplicationContext(); //or getActivity(), YourActivity.this, etc.

        LinearLayout info = new LinearLayout(context);
        info.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(context);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(marker.getTitle());

        TextView snippet = new TextView(context);
        snippet.setTextColor(Color.GRAY);
        snippet.setText(marker.getSnippet());

        info.addView(title);
        info.addView(snippet);

        return info;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
//        Log.e("Lat", String.valueOf(latLng.latitude));
//        Log.e("Lng", String.valueOf(latLng.longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latLng.latitude, latLng.longitude)));
        Toast.makeText(getApplicationContext(), "Please wait loading data...", Toast.LENGTH_SHORT)
                .show();
        details.setText("Loading. Please wait...");
        checkParking (latLng.latitude, latLng.longitude);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MAP_TYPE_HYBRID:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.MAP_TYPE_SATELLITE:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.MAP_TYPE_TERRAIN:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.MAP_TYPE_NORMAL:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    /* Admob */

    private void loadRewardedAd() {
        if (rewardedAd == null || !rewardedAd.isLoaded()) {
            rewardedAd = new RewardedAd(this, AD_UNIT_ID);
            isLoading = true;
            Log.e("TAG","loadRewardedAd");
            rewardedAd.loadAd(
                    new AdRequest.Builder().build(),
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onRewardedAdLoaded() {
                            // Ad successfully loaded.
                            MapsActivity.this.isLoading = false;
                            Toast.makeText(MapsActivity.this, "Success!",
                                    Toast.LENGTH_SHORT).show();
                            showRewardedVideo();
                        }

                        @Override
                        public void onRewardedAdFailedToLoad(int errorCode) {
                            // Ad failed to load.
                            MapsActivity.this.isLoading = false;
                            Toast.makeText(MapsActivity.this, "No ads available!",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        }
    }

    private void showRewardedVideo() {
//        showVideoButton.setVisibility(View.INVISIBLE);
        if (rewardedAd.isLoaded()) {
            RewardedAdCallback adCallback =
                    new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
//                            Toast.makeText(MapsActivity.this, "onRewardedAdOpened", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
//                            Toast.makeText(MapsActivity.this, "onRewardedAdClosed", Toast.LENGTH_SHORT).show();
                            // Preload the next video ad.
//                            MapsActivity.this.loadRewardedAd();
                        }

                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
//                            Toast.makeText(MapsActivity.this, "onUserEarnedReward", Toast.LENGTH_SHORT).show();
//                            addCoins(rewardItem.getAmount());
                        }

                        @Override
                        public void onRewardedAdFailedToShow(int errorCode) {
                            // Ad failed to display
                            Toast.makeText(MapsActivity.this, "onRewardedAdFailedToShow",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    };
            rewardedAd.show(this, adCallback);
        }
    }
}
