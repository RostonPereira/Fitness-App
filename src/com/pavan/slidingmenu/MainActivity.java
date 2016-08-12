package com.pavan.slidingmenu;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.content.Context;
import android.content.Intent;
import android.widget.ListView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pavan.slidingmenu.slidelist.FB_Fragment;
import com.pavan.slidingmenu.slidelist.GP_Fragment;
import com.pavan.slidingmenu.slidelist.DB;
import com.pavan.slidingmenu.slidelist.TB_Fragment;
import com.pavan.slidingmenu.slidelist.settings;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.preference.PreferenceManager;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.content.ComponentName;

public class MainActivity extends FragmentActivity implements NumberPicker.OnValueChangeListener,LocationListener {

    public static FragmentManager fragmentManager;
    private int userIcon, foodIcon, drinkIcon, shopIcon, otherIcon;
    private GoogleMap theMap;
    private LocationManager locMan;
    private Marker userMarker;
    private Marker[] placeMarkers;
    private final int MAX_PLACES = 20;//most returned from google
    private MarkerOptions[] places;


    private static final String TAG = "Pedometer";
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private Utils mUtils;
    private TextView mStepValueView;
    private TextView mPaceValueView;
    private TextView mDistanceValueView;
    private TextView mSpeedValueView;
    private TextView mCaloriesValueView;
    TextView mDesiredPaceView;
    private int mStepValue;
    private int mPaceValue;
    private float mDistanceValue;
    private float mSpeedValue;
    private int mCaloriesValue;
    private float mDesiredPaceOrSpeed;
    private int mMaintain;
    private boolean mIsMetric;
    private float mMaintainInc;
    private boolean mQuitting = false;
    private boolean mIsRunning;

    private static final int MENU_SETTINGS = 8;
    private static final int MENU_QUIT     = 9;

    private static final int MENU_PAUSE = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_RESET = 3;

    String[] menutitles;
	TypedArray menuIcons;
    static Dialog d,dmusic;
    String s;

    String theNumber="7459001520";
    String theMessage="This is to test.";
    Bitmap myBitmap;
    String message="This is from the Integrated App.";

	// nav drawer title
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private List<RowItem> rowItems;
	private CustomAdapter adapter;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        fragmentManager = getFragmentManager();
		mTitle = mDrawerTitle = getTitle();

		menutitles = getResources().getStringArray(R.array.titles);
		menuIcons = getResources().obtainTypedArray(R.array.icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.slider_list);

		rowItems = new ArrayList<RowItem>();

		for (int i = 0; i < menutitles.length; i++) {
			RowItem items = new RowItem(menutitles[i], menuIcons.getResourceId(
					i, -1));
			rowItems.add(items);
		}

		menuIcons.recycle();

		adapter = new CustomAdapter(getApplicationContext(), rowItems);

		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new SlideitemListener());

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			updateDisplay(0);
		}

        userIcon = R.drawable.yellow_point;
        foodIcon = R.drawable.red_point;
        drinkIcon = R.drawable.blue_point;
        shopIcon = R.drawable.green_point;
        otherIcon = R.drawable.purple_point;


	}


    class SlideitemListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			updateDisplay(position);
		}

	}

	private void updateDisplay(int position) {
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new FB_Fragment();
			break;
		case 1:
			fragment = new DB();
			break;
		case 2:
			fragment = new TB_Fragment();
			break;
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			setTitle(menutitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		   getMenuInflater().inflate(R.menu.main, menu);

        return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {



		    // toggle nav drawer on selecting action bar app icon/title
		    if (mDrawerToggle.onOptionsItemSelected(item)) {

			       return true;
		      }
		    // Handle action bar actions click
		   switch (item.getItemId()) {

		   case R.id.action_settings:
		   return true;

        }

            return super.onOptionsItemSelected(item);

	}

	/***
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

            // if nav drawer is opened, hide the action items
            boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
            menu.findItem(R.id.action_settings).setVisible(!drawerOpen);

		    return super.onPrepareOptionsMenu(menu);

	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}


    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {


        System.out.println("The age value =" + newVal);
        s=String.valueOf(newVal);

    }


    public void onAge(View view){

        show();
    }

    private void show() {

        final Dialog d=new Dialog(MainActivity.this);
        d.setContentView(R.layout.age_picker);
        Button btn=(Button)d.findViewById(R.id.ageOk);

        final NumberPicker numberPicker=(NumberPicker)d.findViewById(R.id.numberPicker1);
        numberPicker.setMaxValue(100);
        numberPicker.setValue(25);
        numberPicker.setMinValue(10);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(this);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

              /*  s=String.valueOf(numberPicker.getValue());*/

                try
                {
                    System.out.println("iNSIDE TRY BLOCK");
                    System.out.println("The age value on button="+s);




                }
                catch (Exception ae)
                {

                }
                /*editText.setText(String.valueOf(numberPicker.getValue()));*/
                d.dismiss();

            }
        });

        d.show();
    }

    /*  ===================================================== Distress Button =================================================================  */


    public void onDistressClicked(View view)
    {

        System.out.println("From the distress button method.");

        //sm.sendSMS();
       // em.Screenshot(view);
        sendSMS();
        Screenshot(view);
    }


    public void sendSMS()
    {
        String SENT="Message Sent.";
        String DELIVERED="Message Delivered.";

           try
           {

        PendingIntent sentPI=PendingIntent.getBroadcast(this,0,new Intent(SENT),0);
        PendingIntent deliveredPI=PendingIntent.getBroadcast(this,0,new Intent(DELIVERED),0);

        registerReceiver(new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {

                switch(getResultCode())
                {
                    case Activity.RESULT_OK:  Toast.makeText(getBaseContext(), "SMS Sent.", Toast.LENGTH_LONG).show(); break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE: Toast.makeText(getBaseContext(),"Generic Failure",Toast.LENGTH_LONG).show(); break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE: Toast.makeText(getBaseContext(),"No Service",Toast.LENGTH_LONG).show(); break;

                }

            }
        },new IntentFilter(SENT));


        registerReceiver(new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {

                switch(getResultCode())
                {
                    case Activity.RESULT_OK: Toast.makeText(getBaseContext(),"SMS Delivered.",Toast.LENGTH_LONG).show(); break;

                    case Activity.RESULT_CANCELED: Toast.makeText(getBaseContext(),"SMS not Delivered.",Toast.LENGTH_LONG).show(); break;

                }

            }
        },new IntentFilter(DELIVERED));



        SmsManager sms=SmsManager.getDefault();

        sms.sendTextMessage(theNumber,null, theMessage ,sentPI,deliveredPI);



            }
            catch (Exception e)
            {
                System.out.println("The error for the App via sms:"+e.getMessage());
            }
    }


    public void Screenshot(View view)
    {
        View v1=getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        myBitmap=v1.getDrawingCache();
        saveBitmap(myBitmap);

    }



    private void saveBitmap(Bitmap myBitmap)
    {

        String filePath = Environment.getExternalStorageDirectory() + File.separator + "Pictures/screenshot.png";
        File imagePath = new File(filePath);
        FileOutputStream fos;

        try
        {
            fos = new FileOutputStream(imagePath);
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            sendEmail(filePath, message);

        }

        catch (FileNotFoundException e)
        {
            Log.e("GREC", e.getMessage(), e);
        }

        catch (IOException e)
        {
            Log.e("GREC", e.getMessage(), e);
        }
    }


    public void sendEmail(String path, String message)
    {
        try
        {
        String[] to=new String[]{"roston.pereira9@gmail.com"};
        String subject=("A message from your App.");
        Intent emailIntent=new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        emailIntent.setType("image/png");
        Uri myUri=Uri.parse("file://"+path);
        emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);

        startActivity(Intent.createChooser(emailIntent,"Email"));
        }
        catch (Exception e)
        {

        }

    }


    public void onMusic(View view){

        setContentView(R.layout.song_main);
        Intent intent=new Intent(this,SongActivity.class);
        startActivity(intent);
        this.finish();

    }


    @Override
    public void onBackPressed()
    {
        setContentView(R.layout.cover);
        Intent intent=new Intent(this,cover.class);
        startActivity(intent);
        this.finish();
    }


    /*  ===================================================== SQLite Database =================================================================  */

    public void DataBase(View view){

        // DB variable=(DB)getFragmentManager().findFragmentById(R.id.list);
        setContentView(R.layout.me);
        Intent intent=new Intent(this,Main_Screen.class);
        startActivity(intent);
        this.finish();

    }


/*  =====================================================Pedometer=================================================================  */



    public void onSessionStart(View view)
    {

        mUtils = Utils.getInstance();

    }

    @Override
    protected void onStart() {

        Log.i(TAG, "[ACTIVITY] onStart");
        super.onStart();



    }

    private void displayDesiredPaceOrSpeed() {
        if (mMaintain == PedometerSettings.M_PACE) {
            mDesiredPaceView.setText("" + (int)mDesiredPaceOrSpeed);
        }
        else {
            mDesiredPaceView.setText("" + mDesiredPaceOrSpeed);
        }
    }




    @Override
    protected void onStop() {
        Log.i(TAG, "[ACTIVITY] onStop");
        super.onStop();
    }

    protected void onDestroy() {
        Log.i(TAG, "[ACTIVITY] onDestroy");
        super.onDestroy();
    }

    protected void onRestart() {
        Log.i(TAG, "[ACTIVITY] onRestart");
        super.onDestroy();
    }

    private void setDesiredPaceOrSpeed(float desiredPaceOrSpeed) {
        if (mService != null) {
            if (mMaintain == PedometerSettings.M_PACE) {
                mService.setDesiredPace((int)desiredPaceOrSpeed);
            }
            else
            if (mMaintain == PedometerSettings.M_SPEED) {
                mService.setDesiredSpeed(desiredPaceOrSpeed);
            }
        }
    }

    private void savePaceSetting() {
        mPedometerSettings.savePaceOrSpeedSetting(mMaintain, mDesiredPaceOrSpeed);
    }

    private StepService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();

        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };


    private void startStepService() {
        if (! mIsRunning) {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            startService(new Intent(MainActivity.this,
                    StepService.class));
        }
    }

    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        bindService(new Intent(MainActivity.this,
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        unbindService(mConnection);
    }

    private void stopStepService() {
        Log.i(TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(TAG, "[SERVICE] stopService");
            stopService(new Intent(MainActivity.this,
                    StepService.class));
        }
        mIsRunning = false;
    }

    private void resetValues(boolean updateDisplay) {
        if (mService != null && mIsRunning) {
            mService.resetValues();
        }
        else {
            mStepValueView.setText("0");
            mPaceValueView.setText("0");
            mDistanceValueView.setText("0");
            mSpeedValueView.setText("0");
            mCaloriesValueView.setText("0");
            SharedPreferences state = getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);
                stateEditor.putInt("pace", 0);
                stateEditor.putFloat("distance", 0);
                stateEditor.putFloat("speed", 0);
                stateEditor.putFloat("calories", 0);
                stateEditor.commit();
            }
        }
    }

    // TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void paceChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
        public void speedChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
        }
    };

    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;

    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int)msg.arg1;
                    mStepValueView.setText("" + mStepValue);
                    break;
                case PACE_MSG:
                    mPaceValue = msg.arg1;
                    if (mPaceValue <= 0) {
                        mPaceValueView.setText("0");
                    }
                    else {
                        mPaceValueView.setText("" + (int)mPaceValue);
                    }
                    break;
                case DISTANCE_MSG:
                    mDistanceValue = ((int)msg.arg1)/1000f;
                    if (mDistanceValue <= 0) {
                        mDistanceValueView.setText("0");
                    }
                    else {
                        mDistanceValueView.setText(
                                ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
                        );
                    }
                    break;
                case SPEED_MSG:
                    mSpeedValue = ((int)msg.arg1)/1000f;
                    if (mSpeedValue <= 0) {
                        mSpeedValueView.setText("0");
                    }
                    else {
                        mSpeedValueView.setText(
                                ("" + (mSpeedValue + 0.000001f)).substring(0, 4)
                        );
                    }
                    break;
                case CALORIES_MSG:
                    mCaloriesValue = msg.arg1;
                    if (mCaloriesValue <= 0) {
                        mCaloriesValueView.setText("0");
                    }
                    else {
                        mCaloriesValueView.setText("" + (int)mCaloriesValue);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };

  @Override
  protected void onResume(){
      super.onResume();

     /* if(theMap!=null){
          locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 100, this);
      }

      setUpMap();*/
  }

    private void setUpMap() {

        if(theMap==null){
            //get the map
            theMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.the_map)).getMap();
            //check in case map/ Google Play services not available
            if(theMap!=null){
                //ok - proceed
                theMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                //create marker array
                placeMarkers = new Marker[MAX_PLACES];
                //update location
                updatePlaces();
            }

        }

    }


    @Override
    public void onLocationChanged(Location location) {
        Log.v("MyMapActivity", "location changed");
        updatePlaces();
    }
    @Override
    public void onProviderDisabled(String provider){
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
    private void updatePlaces(){
        //get location manager
        locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //get last location
        Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double lat = lastLoc.getLatitude();
        double lng = lastLoc.getLongitude();
        //create LatLng
        LatLng lastLatLng = new LatLng(lat, lng);

        //remove any existing marker
        if(userMarker!=null) userMarker.remove();
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
                "json?location="+lat+","+lng+
                "&radius=1000&sensor=true" +
                "&types=food|bar|store|museum|art_gallery"+
                "&key=AIzaSyA9gBN_LgaXoRmbL543Pq_rBd8QPgmTUr4";//ADD KEY

        //execute query
        new GetPlaces().execute(placesSearchStr);

        locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 100, this);
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
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            return placesBuilder.toString();
        }
        //process data retrieved from doInBackground
        protected void onPostExecute(String result) {
            //parse place data returned from Google Places
            //remove existing markers
            if(placeMarkers!=null){
                for(int pm=0; pm<placeMarkers.length; pm++){
                    if(placeMarkers[pm]!=null)
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
                for (int p=0; p<placesArray.length(); p++) {
                    //parse each place
                    //if any values are missing we won't show the marker
                    boolean missingValue=false;
                    LatLng placeLL=null;
                    String placeName="";
                    String vicinity="";
                    int currIcon = otherIcon;
                    try{
                        //attempt to retrieve place data values
                        missingValue=false;
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
                        for(int t=0; t<types.length(); t++){
                            //what type is it
                            String thisType=types.get(t).toString();
                            //check for particular types - set icons
                            if(thisType.contains("food")){
                                currIcon = foodIcon;
                                break;
                            }
                            else if(thisType.contains("bar")){
                                currIcon = drinkIcon;
                                break;
                            }
                            else if(thisType.contains("store")){
                                currIcon = shopIcon;
                                break;
                            }
                        }
                        //vicinity
                        vicinity = placeObject.getString("vicinity");
                        //name
                        placeName = placeObject.getString("name");
                    }
                    catch(JSONException jse){
                        Log.v("PLACES", "missing value");
                        missingValue=true;
                        jse.printStackTrace();
                    }
                    //if values missing we don't display
                    if(missingValue)	places[p]=null;
                    else
                        places[p]=new MarkerOptions()
                                .position(placeLL)
                                .title(placeName)
                                .icon(BitmapDescriptorFactory.fromResource(currIcon))
                                .snippet(vicinity);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if(places!=null && placeMarkers!=null){
                for(int p=0; p<places.length && p<placeMarkers.length; p++){
                    //will be null if a value was missing
                    if(places[p]!=null)
                        placeMarkers[p]=theMap.addMarker(places[p]);
                }
            }

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


