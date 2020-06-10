package online.wandering.sfstreetparking;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PlaceSelectionListener {

//    PlaceAutocompleteFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Underconstruction", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView content = findViewById(R.id.content);
//        content.setText("Good Luck!");

        Map<String, String> assoc = new HashMap<String, String>();
        assoc.put("offset", String.valueOf(0));
        assoc.put("limit", String.valueOf(10));
        String userdata = "";
        for (Map.Entry<String, String> e : assoc.entrySet()) {
            userdata += "&" + e.getKey() + "=" + URLEncoder.encode(e.getValue());
        }
//        Log.e("+++", String.valueOf(userdata));
        new DownloadTask(){

            private ArrayList getListData(JSONArray items) {
                ArrayList<SharelistItem> results = new ArrayList<SharelistItem>();
//                    Log.e(TAG, String.valueOf(items));
                try {
                    for (int i = 0; i < items.length(); i++) {
                        JSONArray item = items.getJSONArray(i);
                        SharelistItem newsData = new SharelistItem();
                        newsData.setHeadline(item.getString(0));
                        newsData.setStatus(item.getString(1));
                        newsData.setLat(item.getString(2));
                        newsData.setLng(item.getString(3));
                        results.add(newsData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Add some more dummy data for testing
                return results;
            }


            // Executes in UI thread, after the execution of doInBackground();
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Server connected!", Toast.LENGTH_SHORT)
                    .show();
//                Log.e("+++", String.valueOf(result));

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    if(jsonObject.has("action") && jsonObject.getString("action").equals("loadLast")) {
                        JSONArray list = jsonObject.getJSONArray("data");
                        ArrayList image_details = getListData(list);
                        final ListView lv1 = findViewById(R.id.shareListView);
//                        lv1.setVisibility(View.VISIBLE);
                        lv1.setAdapter(new CustomListAdapter(MainActivity.this,
                                image_details));
                        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                                Object o = lv1.getItemAtPosition(position);
                                SharelistItem shareListData = (SharelistItem) o;
//                            autocompleteFragment.setText(shareListData.getHeadline());
//                                LinearLayout saveLayout = findViewById(R.id.saveLayout);
                                Intent intent = new Intent(MainActivity.this, MapsActivity
                                        .class);
//                                intent.putExtra("token", token);
                                intent.putExtra("name", shareListData.getHeadline());
                                intent.putExtra("lat", shareListData.getLat());
                                intent.putExtra("lon", shareListData.getLng());
//                                intent.putExtra("mapLL", shareListData.getStatus());
                                startActivity(intent);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }.execute(getString(R.string.url)+"?action=loadLast", userdata);

//        if (!Places.isInitialized()) {
//            Places.initialize(getApplicationContext(),"");
//        }

// Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(this);
        // Retrieve the PlaceAutocompleteFragment.
//        autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
//        autocompleteFragment.setOnPlaceSelectedListener(this);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            return true;
        }
        switch (id) {
            case R.id.action_settings:
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                return true;
//            case MENU_LocationCar:
//                startActivity(new Intent(this, Gps.class));
//                return(true);
//            case MENU_Satellite:
//                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//                mMap.invalidate();
//                return(true);
//            case MENU_Terrain:
//                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//                mMap.invalidate();
//                return(true);
        }

       return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_settings) {
//            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
//            startActivity(intent);
//        } else
        if (id == R.id.donations) {
            Intent intent = new Intent(MainActivity.this, DonationsActivity.class);
            startActivity(intent);
//            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Callback invoked when a place has been selected from the PlaceAutocompleteFragment.
     */
    @Override
    public void onPlaceSelected(Place place) {
//        Log.e("111", String.valueOf(place.getAddress()));
//        Log.e("111", Base64.encodeToString(new String(place.getAddress().toString())
//                .getBytes(), Base64.DEFAULT));
//        Log.e("111", String.valueOf(StandardCharsets.UTF_8.decode(Charset.forName("UTF-8").encode("Dodd\u2013Frank"))));
        Map<String, String> assoc = new HashMap<String, String>();



        assoc.put("name", place.getAddress().toString().trim());
//        assoc.put("name", Base64.encodeToString(new String(place.getAddress().toString())
//                .getBytes(), Base64.DEFAULT));
        assoc.put("lat", String.valueOf(place.getLatLng().latitude));
        assoc.put("lng", String.valueOf(place.getLatLng().longitude));
        String userdata = "";
        for (Map.Entry<String, String> e : assoc.entrySet()) {
            userdata += "&" + e.getKey() + "=" + URLEncoder.encode(e.getValue());
        }
        Log.e("111", userdata);
        new DownloadTask().execute(getString(R.string.url) +"?action=saveLast", userdata);

        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putExtra("lat", place.getLatLng().latitude);
        intent.putExtra("lon", place.getLatLng().longitude);
        startActivity(intent);
    }

    /**
     * Callback invoked when PlaceAutocompleteFragment encounters an error.
     */
    @Override
    public void onError(Status status) {
        Log.e("Error created", "onError: Status = " + status.toString());

        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}
