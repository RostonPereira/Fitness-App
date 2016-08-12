package com.pavan.slidingmenu;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Base64;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Location;
import android.location.LocationManager;
import android.content.Context;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import android.os.AsyncTask;

/**
 * Created by Roston on 08/07/2014.
 */
public class mapActivity extends Activity implements LocationListener {

    private int userIcon, foodIcon, drinkIcon, shopIcon, otherIcon;
    private GoogleMap theMap;
    private LocationManager locMan;
    private Marker userMarker;
    private Marker[] placeMarkers;
    private final int MAX_PLACES = 20;
    private MarkerOptions[] places;
    boolean missingValue=false;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmap);

        userIcon = R.drawable.yellow_point;
        foodIcon = R.drawable.red_point;
        drinkIcon = R.drawable.blue_point;
        shopIcon = R.drawable.green_point;
        otherIcon = R.drawable.purple_point;

        if(theMap==null) {
            //map not instantiated yet
            theMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.the_map)).getMap();
        }

        if(theMap != null){

                theMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                placeMarkers = new Marker[MAX_PLACES];
                //ok - proceed
        }

        updatePlaces();

    }
    private void updatePlaces(){

      //update location
        locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        double lat = lastLoc.getLatitude();
        double lng = lastLoc.getLongitude();

        LatLng lastLatLng = new LatLng(lat, lng);

        if(userMarker!=null) {

            userMarker.remove();
        }

        userMarker = theMap.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .title("You are here")
                .icon(BitmapDescriptorFactory.fromResource(userIcon))
                .snippet("Your last recorded location"));

        theMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);

        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?location="+lat+","+lng+
                "&radius=1000&sensor=true" +
                "&types=food|bar|store|museum|art_gallery"+
                "&key=AIzaSyA9gBN_LgaXoRmbL543Pq_rBd8QPgmTUr4";

        new GetPlaces().execute(placesSearchStr);
        locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 100, this);

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.v("MyMapActivity", "location changed");
        updatePlaces();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        Log.v("MyMapActivity", "status changed");

    }

    @Override
    public void onProviderEnabled(String provider) {

        Log.v("MyMapActivity", "provider enabled");

    }

    @Override
    public void onProviderDisabled(String provider) {

        Log.v("MyMapActivity", "provider disabled");

    }

    private class GetPlaces extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... placesURL) {

            StringBuilder placesBuilder = new StringBuilder();
            //process search parameter string(s)

            for (String placeSearchURL : placesURL) {

                 //execute search
                HttpClient placesClient = new DefaultHttpClient();

                try
                {
                    //try to fetch the data
                    HttpGet placesGet = new HttpGet(placeSearchURL);
                    HttpResponse placesResponse = placesClient.execute(placesGet);
                    StatusLine placeSearchStatus = placesResponse.getStatusLine();

                    if (placeSearchStatus.getStatusCode() == 200) {
                        //we have an OK response
                        HttpEntity placesEntity = placesResponse.getEntity();

                        InputStream placesContent = placesEntity.getContent();
                        InputStreamReader placesInput = new InputStreamReader(placesContent);
                        BufferedReader placesReader = new BufferedReader(placesInput);

                        String lineIn;
                        while ((lineIn = placesReader.readLine()) != null)
                        {
                            placesBuilder.append(lineIn);
                        }
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }

            return placesBuilder.toString();
        }
         //fetch and parse place data


    }

    protected void onPostExecute(String result) {

        LatLng placeLL=null;
        String placeName="";
        String vicinity="";
        int currIcon = otherIcon;

        //parse place data returned from Google Places
        if(placeMarkers!=null){

            for(int pm=0; pm<placeMarkers.length; pm++){

                if(placeMarkers[pm]!=null)
                    placeMarkers[pm].remove();
            }
        }

        try
        {
            //parse JSON
            JSONObject resultObject = new JSONObject(result);
            JSONArray placesArray = resultObject.getJSONArray("results");

            places = new MarkerOptions[placesArray.length()];

            //loop through places
            for (int p=0; p<placesArray.length(); p++) {

                try
                {
                    //attempt to retrieve place data values
                    missingValue=false;
                    JSONObject placeObject = placesArray.getJSONObject(p);
                    JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");

                    placeLL = new LatLng(
                            Double.valueOf(loc.getString("lat")),
                            Double.valueOf(loc.getString("lng")));

                    JSONArray types = placeObject.getJSONArray("types");

                    for(int t=0; t<types.length(); t++){
                        //what type is it
                        String thisType=types.get(t).toString();

                        if(thisType.contains("food"))
                        {
                            currIcon = foodIcon;
                            break;
                        }
                        else if(thisType.contains("bar"))
                        {
                            currIcon = drinkIcon;
                            break;
                        }
                        else if(thisType.contains("store"))
                        {
                            currIcon = shopIcon;
                            break;
                        }

                    }

                    vicinity = placeObject.getString("vicinity");
                    placeName = placeObject.getString("name");



                }
                catch(JSONException jse)
                {
                    missingValue=true;
                    jse.printStackTrace();
                }

                if(missingValue)
                {
                    places[p]=null;
                }
                else
                {
                    places[p]=new MarkerOptions()
                            .position(placeLL)
                            .title(placeName)
                            .icon(BitmapDescriptorFactory.fromResource(currIcon))
                            .snippet(vicinity);
                }
                //parse each place
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(places!=null && placeMarkers!=null)
        {
            for(int p=0; p<places.length && p<placeMarkers.length; p++)
            {
                //will be null if a value was missing
                if(places[p]!=null)
                    placeMarkers[p]=theMap.addMarker(places[p]);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(theMap!=null){
            locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 100, this);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        if(theMap!=null){
            locMan.removeUpdates(this);
        }
    }

}


