package online.wandering.sfstreetparking;

import android.location.Criteria;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RatesSchedulesActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private String title;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            title = getIntent().getExtras().getString("title");
            Map<String, String> assoc = new HashMap<String, String>();
            assoc.put("post_id", title);
            String userdata = "";
            for (Map.Entry<String, String> e : assoc.entrySet()) {
                userdata += e.getKey() + "=" + e.getValue() + "&";
            }
            Log.e("userdata", userdata);
            RatesSchedulesActivity.DownloadTask dTask = new RatesSchedulesActivity.DownloadTask();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText("Loading...");
                    dTask.execute(getString(R.string.url)+"?action=loadPlace", userdata);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText("Loading...");
                    dTask.execute(getString(R.string.url)+"?action=loadRates", userdata);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText("Loading...");
                    dTask.execute(getString(R.string.url)+"?action=loadSchedules",
                            userdata);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates_schedules);

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        RatesSchedulesActivity.DownloadTask dTask = new RatesSchedulesActivity.DownloadTask();
        title = getIntent().getExtras().getString("title");
        Map<String, String> assoc = new HashMap<String, String>();
        assoc.put("post_id", title);
        String userdata = "";
        for (Map.Entry<String, String> e : assoc.entrySet()) {
            userdata += e.getKey() + "=" + e.getValue() + "&";
        }
        Log.e("userdata", userdata);
        dTask.execute(getString(R.string.url)+"?action=loadPlace", userdata);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        private ArrayList<Marker> mMarkerArray;
        private Criteria criteria;

        /**
         * A method to download json data from url
         */
        private String downloadUrl(String strUrl, String strPost) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);
                // Open Connection
                urlConnection = (HttpURLConnection) url.openConnection();
                // Creating an http connection to communicate with url
//                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
                urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                // Add POST parameters
                urlConnection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(strPost);
                wr.flush();
                wr.close();
                // Connecting to url
                urlConnection.connect();
                // Reading data from url
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                // Writing data to string
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();
            } catch (Exception e) {
                Log.e("Except downloading url", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0], url[1]);
                //Log.e("res",data);
            } catch (Exception e) {
                Log.e("Background Task", e.toString());
            }
            //Log.e("Result","DATA: "+data);
            return data;
        }

        // Executes in UI thread, after the execution of doInBackground();
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            Toast.makeText(getApplicationContext(), "Server connected!", Toast.LENGTH_SHORT)
//                    .show();
            JSONObject jsonObject = null;
            Log.e("Res", result);
            try {
                Calendar c = Calendar.getInstance();
                int utcOffset = c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET);
                Long utcMilliseconds = c.getTimeInMillis() + utcOffset;

                jsonObject = new JSONObject(result);
                mTextMessage.setText(title+"\n");
                if(jsonObject.has("dataRate")) {
                    JSONArray d = jsonObject.getJSONArray("dataRate");
                    if(!jsonObject.has("data")) mTextMessage.setText(title+"\n");
                    if(jsonObject.getJSONArray
                            ("dataRate").length()>0){
                        for (int i = 0; i < d.length(); i++) {
                            JSONObject e = d.getJSONObject(i);
                            mTextMessage.append("\n");
                            if(e.has("rate")) mTextMessage.append("Rate: $"+e.getString("rate")+"\n");
                            mTextMessage.append("Rate type: "+e.getString("rate_type")+"\n");
                            mTextMessage.append("Schedule priority: "+e.getString("schedule_priority")+"\n");
                            if(e.has("days_applied")) {
                                mTextMessage.append("Days applied: " + e.getString("days_applied")+"\n");
                            }
                            if(e.has("from_time") && e.has("to_time")) {
                                mTextMessage.append("From "+e.getString("from_time") + " To " + e.getString("to_time")+"\n");
                            }
                        }
                    } else jsonObject.remove("dataRate");
                }
                if(jsonObject.has("data")){
                    JSONArray a = jsonObject.getJSONArray("data");
                    if(jsonObject.getJSONArray("data")
                            .length()>0){
                        for (int i = 0; i < a.length(); i++) {
                            JSONObject b = a.getJSONObject(i);
                            mTextMessage.append("\n");
                            mTextMessage.append("Status: "+b.getString("active_meter_status")+"\n");
                            mTextMessage.append("Rules: "+b.getString("applied_color_rule")+"\n");
                            mTextMessage.append("Block side: "+b.getString("block_side")+"\n");
                            mTextMessage.append("Cap Color: "+b.getString("cap_color")+"\n");
                            mTextMessage.append("Days applied: "+b.getString("days_applied")+"\n");
                            mTextMessage.append("From: "+b.getString("from_time")+" to "+b.getString("to_time")+"\n");
                            mTextMessage.append("Shedule type: "+b.getString("schedule_type")+"\n");
                            mTextMessage.append("Street and block: "+b.getString("street_and_block")+"\n");
                            mTextMessage.append("Priority: "+b.getString("priority")+"\n");
                            mTextMessage.append("Time Limit: "+b.getString("time_limit")+"\n");
                        }
                    } else jsonObject.remove("data");
                }
                mTextMessage.append("\n");
                mTextMessage.append("\n");
                mTextMessage.append("\n");
                mTextMessage.append("\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
