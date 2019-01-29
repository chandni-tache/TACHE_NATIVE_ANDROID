package com.tache.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;

import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tache.R;
import com.tache.adapter.UniversalStatePagerAdapter;
import com.tache.fragments.MissionDetailFragment;
import com.tache.fragments.WebViewFragment;
import com.tache.rest.ApiUtils;
import com.tache.rest.models.response.Mission;
import com.tache.rest.services.LinksService;
import com.tache.utils.Constants;
import com.tache.utils.Helper;
import com.tache.utils.TimeFormatHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class TaskDetailActivity extends AppCompatActivity implements WebViewFragment.OnWebViewComplete, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
                                                                                                            GoogleApiClient.OnConnectionFailedListener, LocationListener{
    private View detailContainer;
    private Button start;
    private Mission mission;
    private FrameLayout frameLayout;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;




   /* Button btn_start;
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission ;
    TextView tv_latitude, tv_longitude, tv_address,tv_area,tv_locality;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    Double latitude,longitude;
    Geocoder geocoder;
*/
   private Location location;
    private TextView locationTv;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    CircleOptions circleOptions;
    LatLng latlng23;
    LatLng latLngA;
    LatLng latLngB;
    String address;
    LatLng myLocation23;
    Circle circle2, circle5;
    double distance;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//---------------------------------------------------------------------------------------------

       /* btn_start = (Button) findViewById(R.id.btn_start);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_latitude = (TextView) findViewById(R.id.tv_latitude);
        tv_longitude = (TextView) findViewById(R.id.tv_longitude);
        tv_area = (TextView)findViewById(R.id.tv_area);
        tv_locality = (TextView)findViewById(R.id.tv_locality);
        geocoder = new Geocoder(this, Locale.getDefault());
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();*/


        locationTv = findViewById(R.id.location);
        // we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= 26) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();


//---------------------------------------------------------------------------------------------

        Intent intent = getIntent();
        mission = new Gson().fromJson(intent.getStringExtra("data"), Mission.class);
        final String fragmentName = intent.getStringExtra("name");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(mission.getCompany_name());


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        detailContainer = findViewById(R.id.detailContainer);
        ImageView imageView = (ImageView) findViewById(R.id.slider);
        TextView daysAllowed = (TextView) findViewById(R.id.daysAllowed);
        TextView timeAllowed = (TextView) findViewById(R.id.timeAllowed);
        TextView price = (TextView) findViewById(R.id.price);
        TextView date = (TextView) findViewById(R.id.dates);
        TextView address = (TextView) findViewById(R.id.address);
        TextView aboutText = (TextView) findViewById(R.id.aboutText);
        TextView ensureText = (TextView) findViewById(R.id.ensureText);
        TextView pointsText = (TextView) findViewById(R.id.pointsText);
        start = (Button) findViewById(R.id.start);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        findViewById(R.id.viewInMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Mission> missions = new ArrayList<Mission>();
                missions.add(mission);
                startActivity(MapsActivity.getNewIntent(TaskDetailActivity.this, missions));
            }
        });




        Glide.with(this).load(mission.getCompany_logo()).into(imageView);
        System.out.println("Image == "+mission.getCompany_logo());
        System.out.println("Mission Id = "+mission.getId());
        System.out.println("Mission Survey ====  "+mission.getSurvey()+ " mission Location ==== "+mission.getLocation_lat()+" ttt "+mission.getLocation_long()+" mmm "+mission.getLocation());
        daysAllowed.setText(mission.getDay_allowed());
        date.setText(TimeFormatHelper.getInDM(mission.getDate_from()) + " - " + TimeFormatHelper.getInDMY(mission.getDate_to()));
        price.setText(String.valueOf(mission.getPrice()));
        address.setText(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? Html.fromHtml(mission.getLocation(), Html.FROM_HTML_MODE_COMPACT) : Html.fromHtml(mission.getLocation()).toString());
        aboutText.setText(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? Html.fromHtml(mission.getAbout(), Html.FROM_HTML_MODE_COMPACT) : Html.fromHtml(mission.getAbout()).toString());
        ensureText.setText(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? Html.fromHtml(mission.getEnsure_things(), Html.FROM_HTML_MODE_COMPACT) : Html.fromHtml(mission.getEnsure_things()).toString());
        pointsText.setText(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? Html.fromHtml(mission.getKey_points(), Html.FROM_HTML_MODE_COMPACT) : Html.fromHtml(mission.getKey_points()).toString());

        System.out.println("My Get Location ====  "+mission.getLocation());

        latlng23 = new LatLng(mission.getLocation_lat(),mission.getLocation_long());
       // latlng23 = String.valueOf(mission.getLocation_lat()+","+mission.getLocation_long());
        System.out.println("Default location of event = "+latlng23);

//------------------------------------------------------------------------------------------------

       /* btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {*/
               /* if (boolean_permission) {

                    if (mPref.getString("service", "").matches("")) {
                        medit.putString("service", "service").commit();

                        Intent inten = new Intent(getApplicationContext(), GoogleService.class);
                        startService(inten);

                    } else {
                        Toast.makeText(getApplicationContext(), "Service is already running", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enable the gps", Toast.LENGTH_SHORT).show();
                }
             //   Toast.makeText(TaskDetailActivity.this, "Start Server", Toast.LENGTH_SHORT).show();
                fn_permission();*/
           /* }
        });*/




//------------------------------------------------------------------------------------------------

        if (TextUtils.isEmpty(fragmentName))
            start.setVisibility(View.GONE);
        else {
            switch (fragmentName.toLowerCase()) {
                case "available":
                    start.setText("Apply now");
                    break;
                case "search":
                    start.setText("Apply now");
                    break;
                case "assigned":
                    start.setText("Start now");
                    break;
            }
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AlertDialog.Builder alert = new AlertDialog.Builder(TaskDetailActivity.this);
                    alert.setTitle("Survey");
                    alert.setMessage("Not in range of survey");
                    alert.setPositiveButton("OK",null);
                    alert.show();




                    /*switch (start.getText().toString().toLowerCase()) {
                        case "apply now":
                            applyMission(mission.getId());
                            break;
                        case "start now":
//                            FragmentManager fm = getSupportFragmentManager();
//                            FragmentTransaction ft = fm.beginTransaction();
//                            Fragment fragment = WebViewFragment.newInstance(String.format(LinksService.TASK_URL, mission.getId(), Helper.getAuthKey(TaskDetailActivity.this)));
//                            ft.replace(frameLayout.getId(), fragment, TAG_MISSION);
//                            ft.commit();
                            //using Activity for now!

                            Intent start = new Intent(TaskDetailActivity.this, StartSurveyMissionActivity.class);
                            start.putExtra("url", String.format(LinksService.TASK_URL, mission.getId(), Helper.getAuthKey(TaskDetailActivity.this)));
                            start.putExtra("what", "Task");
                            startActivity(start);
                            break;
                    }*/
                }
            });
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }


    }

    //--------------------------waist code start---------------------------------------------------------------------------------

    LocationCallback mLocationCallback = new LocationCallback() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();


            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("TaskDetailActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                //  Log.i("MapsActivity", "Location: " + 28.5445 + " " + 77.2642);
                mLastLocation = location;

                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
               /* LatLng latLng = new LatLng(28.536957, 77.271521);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Tache Technologies");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);


                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));*/


                latLngA = new LatLng(location.getLatitude(),location.getLongitude());
                latLngB = new LatLng(28.537094, 77.271434);

                Location locationA = new Location("point A");
                locationA.setLatitude(latLngA.latitude);
                locationA.setLongitude(latLngA.longitude);
                Location locationB = new Location("point B");
                locationB.setLatitude(latLngB.latitude);
                locationB.setLongitude(latLngB.longitude);
                Geocoder gcd = new Geocoder(TaskDetailActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = gcd.getFromLocation(latLngA.latitude, latLngA.longitude, 1);
                    if (addresses.size() > 0)
                        System.out.println(addresses.get(0).getLocality());

                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();


                }catch (Exception e){
                    e.printStackTrace();
                }



                distance = locationA.distanceTo(locationB);



                circle5 = mMap.addCircle(new CircleOptions()
                        //             28.537094, 77.271434    mission.getLocation_lat(),mission.getLocation_long()
                        .center(new LatLng(28.537094, 77.271434))
                        .radius(50)
                        .strokeColor(Color.RED)
                );

                if(distance<=circle5.getRadius())
                {
                    start.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (start.getText().toString().toLowerCase()) {
                                case "apply now":
                                    applyMission(mission.getId());
                                    break;
                                case "start now":
//                            FragmentManager fm = getSupportFragmentManager();
//                            FragmentTransaction ft = fm.beginTransaction();
//                            Fragment fragment = WebViewFragment.newInstance(String.format(LinksService.TASK_URL, mission.getId(), Helper.getAuthKey(TaskDetailActivity.this)));
//                            ft.replace(frameLayout.getId(), fragment, TAG_MISSION);
//                            ft.commit();
                                    //using Activity for now!

                                    Intent start = new Intent(TaskDetailActivity.this, StartSurveyMissionActivity.class);
                                    start.putExtra("url", String.format(LinksService.TASK_URL, mission.getId(), Helper.getAuthKey(TaskDetailActivity.this)));
                                    start.putExtra("what", "Task");
                                    startActivity(start);
                                    break;
                            }
                        }
                    });
                }


            }
        }
    };


//--------------------waist code end---------------------------------------------------------------------------------------


    private void switchToWebView() {
        detailContainer.setVisibility(View.GONE);
        start.setVisibility(View.GONE);
        frameLayout.setVisibility(View.VISIBLE);
    }

    private void switchToDetailView() {
        detailContainer.setVisibility(View.VISIBLE);
        start.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.GONE);
    }

    @Override
    public void closeWebView(Fragment fragment) {
        switchToDetailView();
        frameLayout.removeAllViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        if (frameLayout.getVisibility() == View.VISIBLE)
//            closeWebView(null);
//        else
        super.onBackPressed();
    }

    private void applyMission(int id) {
        final LinksService linksService = ApiUtils.retrofitInstance().create(LinksService.class);
        System.out.println("vvvvvvvvvvv = = = "+id);
        linksService.missionApply(Helper.getAuthHeader(this), id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("My URL ====   "+linksService.toString().length());
                System.out.println("Hello user three = "+call.toString());
                System.out.println("Hello user two = "+response.toString());
                if (response.isSuccessful()) {
                    start.setVisibility(View.GONE);

                    snackbar = Snackbar.make(coordinatorLayout, "Applied for audit. You can start audit when approved", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (snackbar != null && snackbar.isShown())
                                snackbar.dismiss();
                        }
                    }).setActionTextColor(Color.YELLOW);
                    snackbar.show();

                    Constants.RELOAD_MISSION = true;
                } else
                    Toast.makeText(TaskDetailActivity.this, "Already applied", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

//----------------------------------------------------------------------------------------------------


    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

   /* private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }*/

    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            locationTv.setText("You need to install Google Play Services to use the App properly");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();




            //stop location updates when Activity is no longer active
            if (mFusedLocationClient != null) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }


        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }





    @Override
    public void onLocationChanged(Location location) {

//--------------------------------------------------------------------------------------------------


//--------------------------------------------------------------------------------------------------
        if (location != null) {
            locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
            myLocation23 = new LatLng(location.getLatitude(),location.getLongitude());
          //  myLocation23 = String.valueOf(location.getLatitude()+location.getLongitude());28.536989, 77.271538

            Location loc1 = new Location("");
            loc1.setLatitude(28.536941);
            loc1.setLongitude(77.271450);

            /*loc1.setLatitude(location.getLatitude());
            loc1.setLongitude(location.getLongitude());*/

            Location loc2 = new Location("");
            /*loc2.setLatitude(location.getLatitude());
            loc2.setLongitude(location.getLongitude());*/
            loc2.setLatitude(28.536941);
            loc2.setLongitude(77.271450);

            /*loc2.setLatitude(mission.getLocation_lat());
            loc2.setLongitude(mission.getLocation_long());*/

            float distanceInMeters = loc1.distanceTo(loc2);

            if (mFusedLocationClient != null) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }


            if(distanceInMeters<=50)
            {
                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (start.getText().toString().toLowerCase()) {
                            case "apply now":
                                applyMission(mission.getId());
                                break;
                            case "start now":
//                            FragmentManager fm = getSupportFragmentManager();
//                            FragmentTransaction ft = fm.beginTransaction();
//                            Fragment fragment = WebViewFragment.newInstance(String.format(LinksService.TASK_URL, mission.getId(), Helper.getAuthKey(TaskDetailActivity.this)));
//                            ft.replace(frameLayout.getId(), fragment, TAG_MISSION);
//                            ft.commit();
                                //using Activity for now!

                                Intent start = new Intent(TaskDetailActivity.this, StartSurveyMissionActivity.class);
                                start.putExtra("url", String.format(LinksService.TASK_URL, mission.getId(), Helper.getAuthKey(TaskDetailActivity.this)));
                                start.putExtra("what", "Task");
                                startActivity(start);
                                break;
                        }
                    }
                });
            }


        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(TaskDetailActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {


                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }

            }


        }






    }




    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(TaskDetailActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }


//----------------------------------------------------------------------------------------------------

}
