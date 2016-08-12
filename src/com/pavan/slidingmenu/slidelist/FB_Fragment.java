package com.pavan.slidingmenu.slidelist;


import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.pavan.slidingmenu.MainActivity;
import com.pavan.slidingmenu.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View.OnClickListener;
import com.pavan.slidingmenu.mapActivity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.google.android.gms.location.LocationClient;


@SuppressLint("NewApi")
public class FB_Fragment extends Fragment implements OnClickListener,LocationListener {

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    String latt,provider;
    double latitude,longitude;
    protected boolean gps_enabled,network_enabled;
    double firstvalueLong,firstvalueLat;
    int tester=0;


    CountDownTimer countDownTimer;
    private int userIcon, foodIcon, drinkIcon, shopIcon, otherIcon;
    private GoogleMap theMap;
    private LocationManager locMan;
    private Marker userMarker;
    private Marker[] placeMarkers;
    private final int MAX_PLACES = 20;//most returned from google
    private MarkerOptions[] places;
    double lat, lng;
    double latloc,lngloc;
    int check=0;

    ImageButton musicBtn;
    Button btn, sessionStart,lon,latit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fb_fragment, container, false);

        userIcon = R.drawable.yellow_point;
        foodIcon = R.drawable.red_point;
        drinkIcon = R.drawable.blue_point;
        shopIcon = R.drawable.green_point;
        otherIcon = R.drawable.purple_point;

       // lat = 51.876570;
        //lng = 0.945994;

      //  latloc = 27.175015;
       // lngloc = 78.042155;

        if (theMap == null) {
            //get the map
            theMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.the_map)).getMap();
            //check in case map/ Google Play services not available
            if (theMap != null) {
                //ok - proceed
                theMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                //create marker array
                placeMarkers = new Marker[MAX_PLACES];
                //update location

            }

        }


        lon=(Button) rootView.findViewById(R.id.lngt);
        latit=(Button) rootView.findViewById(R.id.lat);
        sessionStart = (Button) rootView.findViewById(R.id.sessionStart);
        musicBtn = (ImageButton) rootView.findViewById(R.id.imageButton);
        btn = (Button) rootView.findViewById(R.id.DistressBtn);


        musicBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                System.out.println("From the fb-fragmentfor music player.");
                MainActivity activity = (MainActivity) getActivity();
                activity.onMusic(v);


            }

        });


        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                System.out.println("From the fb-fragment for Distress Button.");
                MainActivity activity = (MainActivity) getActivity();
                activity.onDistressClicked(v);

            }
        });


        sessionStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                MainActivity activity = (MainActivity) getActivity();
                activity.onSessionStart(v);

            }
        });


       // startcountdown();
       // countDownTimer.start();
      // updatePlaces();

        return rootView;
    }

    @Override
    public void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);

        locationManager=(LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }

    private void startcountdown() {

        countDownTimer=new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                int s=(int)millisUntilFinished/1000;

                System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&" + s);


            }

            @Override
            public void onFinish() {

                check=1;
                updatePlaces();

            }
        };
    }

    @Override
    public void onClick(View v) {

    }

    public void onSessionStart(View view) {

    }


    @Override
    public void onLocationChanged(Location location) {

        Log.v("MyMapActivity", "location changed");
        latitude=location.getLatitude();
        longitude=location.getLongitude();


        lon.setText(String.valueOf(longitude));
        latit.setText(String.valueOf(latitude));
        updatePlaces();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.v("MyMapActivity", "provider disabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.v("MyMapActivity", "provider enabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.v("MyMapActivity", "status changed");
    }

    /*
     * update the place markers
     */
    private void updatePlaces() {
        //get location manager
        //locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
       // locMan = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //get last location
      //  Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);



            lat=latitude;
            lng=longitude;

        //create LatLng
        LatLng lastLatLng = new LatLng(lat, lng);

        //remove any existing marker
        if (userMarker != null) userMarker.remove();
        //create and set marker properties
        userMarker = theMap.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .title("You are here")
                .icon(BitmapDescriptorFactory.fromResource(userIcon))
                .snippet("Your last recorded location"));
        //move to location
        theMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);

        //build places query string
        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?location=" + lat + "," + lng +
                "&radius=1000&sensor=true" +
                "&types=food|bar|store|museum|art_gallery" +
                "&key=AIzaSyA9gBN_LgaXoRmbL543Pq_rBd8QPgmTUr4";//ADD KEY

        //execute query
        new GetPlaces().execute(placesSearchStr);

//        locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 100, this);

        marker();
    }

    private void marker() {


        if (tester == 0) {
            firstvalueLong = longitude;
            firstvalueLat = latitude;
            tester = 1;
        } else {

        }

        Polyline line=theMap.addPolyline(new PolylineOptions().add(new LatLng(firstvalueLong,firstvalueLat),new LatLng(longitude,latitude)).width(5).color(Color.RED));
    }


    private class GetPlaces extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... placesURL) {
            //fetch places

            //build result as string
            StringBuilder placesBuilder = new StringBuilder();
            //process search parameter string(s)
            for (String placeSearchURL : placesURL) {
                HttpClient placesClient = new DefaultHttpClient();
                try {
                    //try to fetch the data

                    //HTTP Get receives URL string
                    HttpGet placesGet = new HttpGet(placeSearchURL);
                    //execute GET with Client - return response
                    HttpResponse placesResponse = placesClient.execute(placesGet);
                    //check response status
                    StatusLine placeSearchStatus = placesResponse.getStatusLine();
                    //only carry on if response is OK
                    if (placeSearchStatus.getStatusCode() == 200) {
                        //get response entity
                        HttpEntity placesEntity = placesResponse.getEntity();
                        //get input stream setup
                        InputStream placesContent = placesEntity.getContent();
                        //create reader
                        InputStreamReader placesInput = new InputStreamReader(placesContent);
                        //use buffered reader to process
                        BufferedReader placesReader = new BufferedReader(placesInput);
                        //read a line at a time, append to string builder
                        String lineIn;
                        while ((lineIn = placesReader.readLine()) != null) {
                            placesBuilder.append(lineIn);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return placesBuilder.toString();
        }

        //process data retrieved from doInBackground
        protected void onPostExecute(String result) {
            //parse place data returned from Google Places
            //remove existing markers
            if (placeMarkers != null) {
                for (int pm = 0; pm < placeMarkers.length; pm++) {
                    if (placeMarkers[pm] != null)
                        placeMarkers[pm].remove();
                }
            }
            try {
                //parse JSON

                //create JSONObject, pass stinrg returned from doInBackground
                JSONObject resultObject = new JSONObject(result);
                //get "results" array
                JSONArray placesArray = resultObject.getJSONArray("results");
                //marker options for each place returned
                places = new MarkerOptions[placesArray.length()];
                //loop through places
                for (int p = 0; p < placesArray.length(); p++) {
                    //parse each place
                    //if any values are missing we won't show the marker
                    boolean missingValue = false;
                    LatLng placeLL = null;
                    String placeName = "";
                    String vicinity = "";
                    int currIcon = otherIcon;
                    try {
                        //attempt to retrieve place data values
                        missingValue = false;
                        //get place at this index
                        JSONObject placeObject = placesArray.getJSONObject(p);
                        //get location section
                        JSONObject loc = placeObject.getJSONObject("geometry")
                                .getJSONObject("location");
                        //read lat lng
                        placeLL = new LatLng(Double.valueOf(loc.getString("lat")),
                                Double.valueOf(loc.getString("lng")));
                        //get types
                        JSONArray types = placeObject.getJSONArray("types");
                        //loop through types
                        for (int t = 0; t < types.length(); t++) {
                            //what type is it
                            String thisType = types.get(t).toString();
                            //check for particular types - set icons
                            if (thisType.contains("food")) {
                                currIcon = foodIcon;
                                break;
                            } else if (thisType.contains("bar")) {
                                currIcon = drinkIcon;
                                break;
                            } else if (thisType.contains("store")) {
                                currIcon = shopIcon;
                                break;
                            }
                        }
                        //vicinity
                        vicinity = placeObject.getString("vicinity");
                        //name
                        placeName = placeObject.getString("name");
                    } catch (JSONException jse) {
                        Log.v("PLACES", "missing value");
                        missingValue = true;
                        jse.printStackTrace();
                    }
                    //if values missing we don't display
                    if (missingValue) places[p] = null;
                    else
                        places[p] = new MarkerOptions()
                                .position(placeLL)
                                .title(placeName)
                                .icon(BitmapDescriptorFactory.fromResource(currIcon))
                                .snippet(vicinity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (places != null && placeMarkers != null) {
                for (int p = 0; p < places.length && p < placeMarkers.length; p++) {
                    //will be null if a value was missing
                    if (places[p] != null)
                        placeMarkers[p] = theMap.addMarker(places[p]);
                }
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
       /* if (theMap != null) {
            locMan.removeUpdates(this);
        }*/
    }


    @Override
    public void onResume() {
        super.onResume();

        if (check == 1) {

            if (theMap != null) {
                locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 100, this);

            }
        } else {

        }

    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        Fragment fragment=(getFragmentManager().findFragmentById(R.id.the_map));
        FragmentTransaction ft=getActivity().getFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }
}

