package qiminl.lifaryupdate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by liuqimin on 2016-01-06.
 */
public class LocationData {
    private static final String DEBUG = "location";
    private Location loc;
    private Context context;
    String locName = null;

    LocationData(Context con){
        this.context = con;
        loc = getLastBestLocation();
        checkConnection();
    }

    public double getLatitude(){
        return loc.getLatitude();
    }
    public double getLongitude(){
        return loc.getLongitude();
    }

    public Location getLoc(){
        return loc;
    }

    public String getAddress(){
        return getLatitude() + ", " + getLongitude();

    }

    // TODO: get location name data
    public String getLocName(){

        return null;
    }

    // TODO: get location name by geoname
    public void getLocNameByGeoname(){

        new ReadGeonameJSONTask().execute(
                context.getResources().getString(R.string.geoname_locName) +
                getLatitude() +
                "&lng=" +
                getLongitude() +
                "&username=qiminl");
    }

    // get location name by geoname
    // @params: lat: latitude
    // @params: lon: longitude

    public void getLocNameByGeoname(double lat, double lon){

        new ReadGeonameJSONTask().execute(
                context.getResources().getString(R.string.geoname_locName) +
                        lat +
                        "&lng=" +
                        lon +
                        "&username=qiminl");
    }

    // TODO: show location in google map
    public void showLocMap(){

    }

    /*
    * Get the latest known location based on gps and network
    * if gps time later than network time, then use gps data
    *
    * */
    private Location getLastBestLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // if cannot get location
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false
                && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == false){

            buildAlertMessageNoGps();
            return null;
        }
        else {

            // get location
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            long GPSLocationTime = 0;
            if (locationGPS != null) {
                GPSLocationTime = locationGPS.getTime();
                Log.d(DEBUG, "GPS location != null");
            } else                 Log.d(DEBUG, "GPS location == null");


            long NetLocationTime = 0;

            if (locationNet != null) {
                NetLocationTime = locationNet.getTime();
                Log.d(DEBUG, "Net location != null");

            }else Log.d(DEBUG, "Net location == null");

            if (GPSLocationTime - NetLocationTime > 0) {
                Log.d(DEBUG, "return location GPS");
                return locationGPS;
            } else {
                Log.d(DEBUG, "return location Net");
                return locationNet;
            }
        }
    }


    /*
    * If gps is not enabled, show up a dialog to turn on gps
    * if yes selected, then go to setting page
    * else otherwise
    * */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void checkConnection(){
        Log.d(DEBUG, "checkConnection");

        ConnectivityManager connectMgr =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.d(DEBUG, "checkConnection 2");
        NetworkInfo networkInfo = null;
        try {
            networkInfo = connectMgr.getActiveNetworkInfo();

        }catch (Exception e){
            Log.d(DEBUG, "ERROR: " + e.getLocalizedMessage());
        }
        Log.d("Lifary", "checkConnection 3");

        if(networkInfo != null && networkInfo.isConnected()){
            //fetch data

            String networkType = networkInfo.getTypeName().toString();
        }
        else {
            //display error
            Log.d("Lifary", "CONNECTION ERROR");
        }
    }


    private String readJSONData(String myurl) throws IOException {

        Log.d("Lab11", "readJsondata");

        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 2500;

        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
                conn.disconnect();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {

        Log.d("Lab11", "readIt");

        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }


    /*
    * Read data from Geonames
    * */
    private class ReadGeonameJSONTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            try{
                return readJSONData(urls[0]);
            }catch (IOException e){
                e.printStackTrace();
                Log.d(DEBUG, "ERROR: failed to read JSON data");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {

                Log.d(DEBUG, "jsonObject");
                JSONObject jsonObject = new JSONObject(result);
                Log.d(DEBUG, "weatherObservationItems");

                JSONArray locArray = new JSONArray(jsonObject.getString("geonames"));

                Log.d(DEBUG, "try to get location name");
                JSONObject observObject = locArray.getJSONObject(0);

                locName = observObject.getString("name");
                Log.d(DEBUG, "locName = " + locName);

            } catch (Exception e) {
                Log.d(DEBUG, "ERROR" + e.getLocalizedMessage());
                if(e.getLocalizedMessage().equals("No value for geonames")){
                    try {
                        JSONObject statusMsg = new JSONObject(result);
                        Toast.makeText(context, "" + statusMsg.getString("status"), Toast.LENGTH_LONG).show();
                    }catch (Exception err){
                        Log.d(DEBUG, "ERROR: " + err.getLocalizedMessage());
                    }
                }
            }


            MediaCommunication mediaComm = (MediaCommunication) context;
            mediaComm.locCom(getLatitude(), getLongitude(), locName);
        }
    }


}
