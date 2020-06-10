package online.wandering.sfstreetparking;

import android.location.Criteria;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloadTask extends AsyncTask<String, Void, String> {
    private ArrayList<Marker> mMarkerArray;
    private Criteria criteria;
    /**
     * Distance
     */

    public static String distance(Double lat0, Double lng0, Double lat1, Double lng1) {
        Double kmtomile = 0.621371;
        Double dist = 0.000000000000;
        Double R = 6371000.0;
        Double dLat = (lat0 - lat1) * Math.PI / 180;
        Double dLon = (lng0 - lng1) * Math.PI / 180;
        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat0 * Math.PI / 180) * Math.cos(lat1 * Math.PI / 180) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double d = R * c;
        dist = dist + d;
//        Log.e("distance1!!!!!", String.valueOf((dist * kmtomile / 10) / 100));
//        return String.valueOf(Math.round((dist * kmtomile / 10) / 100));
        return String.valueOf((dist * kmtomile / 10) / 100);
    }
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
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            // Add POST parameters
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(120000);
            urlConnection.setReadTimeout(300000);
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
//            Log.e("---",data);
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

}
