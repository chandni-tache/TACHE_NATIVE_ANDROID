package com.tache.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tache.R;
import com.tache.adapter.UniversalPagerAdapter;
import com.tache.rest.models.response.Mission;
import com.tache.utils.Helper;
import com.tache.utils.LocationHelper;
import com.tache.utils.TimeFormatHelper;
import com.tache.views.WrapContentViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    private static final float ZOOM_LEVEL = 13;
    private static final String TAG = "MapsActivity";
    private static final int MY_LOCATION_REQUEST_CODE = 0;
    private static final String EXTRA_ITEMS = MapsActivity.class.getPackage().getName() + ".items";

    @BindView(R.id.activity_maps_view_pager)
    WrapContentViewPager viewPager;
    @BindColor(R.color.colorAccent)
    int accentColor;

    private GoogleMap mMap;
    private LatLng[] latLng;
    private Marker currMarker;
    private LocationHelper locationHelper;
    private ProgressDialog progressDialog;
    private LatLng currLocation;
    private List<Polyline> polylines;
    private ArrayList<Mission> missionArrayList;


    public static Intent getNewIntent(Context context, ArrayList<Mission> missions) {
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_ITEMS, missions);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        missionArrayList = getIntent().getParcelableArrayListExtra(EXTRA_ITEMS);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) supportFragmentManager.findFragmentById(R.id.activity_maps_map);
        mapFragment.getMapAsync(this);


        int listSize = missionArrayList == null ? 0 : missionArrayList.size();
        latLng = new LatLng[listSize];

        System.out.println("Latitude ===== "+listSize);
        UniversalPagerAdapter universalPagerAdapter = new UniversalPagerAdapter();

        for (int i = 0; i < listSize; i++) {
                try {
                    Mission mission = missionArrayList.get(i);
                    System.out.println("eeeeeee === "+missionArrayList.get(i));

                  //  latLng[i] = new LatLng(28.536957, 77.271521);
                    latLng[i] = new LatLng(mission.getLocation_lat(), mission.getLocation_long());
                   universalPagerAdapter.addView(getItemView(mission));
           System.out.println("ffffffffff  ==v "+mission);
            System.out.println("ttttttttt  ==v "+latLng[i]);
                }catch (Exception e){
                    e.printStackTrace();
                }



        }

        viewPager.setAdapter(universalPagerAdapter);
        viewPager.setPageMargin((int) (getResources().getDisplayMetrics().widthPixels * -0.3));
        viewPager.setOffscreenPageLimit(5);
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                float absPosition = Math.abs(position);
                float scale = 0.75f - (absPosition * 0.25f);
                page.setScaleX(scale);
                page.setScaleY(scale);
                page.setAlpha(1.0f - (absPosition * 0.5f));
            }
        });
    }

    private View getItemView(Mission mission) {
        View view = getLayoutInflater().inflate(R.layout.item_map, null);

        Glide.with(this).load(mission.getCompany_logo()).dontAnimate()
                .into((CircularImageView) view.findViewById(R.id.item_mission_profile_image));
        ((TextView) view.findViewById(R.id.item_mission_title)).setText(mission.getSurvey().getTitle());
        ((TextView) view.findViewById(R.id.item_mission_brand_name)).setText(mission.getCompany_name());
        String[] locationSplit = mission.getLocation().split(",");
        System.out.println("jjjjjjjjj=========  "+locationSplit[locationSplit.length - 1]);
        ((TextView) view.findViewById(R.id.item_mission_location)).setText(locationSplit[locationSplit.length - 1]);
        ((TextView) view.findViewById(R.id.item_mission_date_range)).setText(TimeFormatHelper.getInDMY(mission.getDate_from()) + " - " + TimeFormatHelper.getInDMY(mission.getDate_to()));
        ((TextView) view.findViewById(R.id.item_mission_price)).setText(String.valueOf(mission.getPrice()));


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json);
        mMap.setMapStyle(style);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        MarkerOptions[] markerOptions = new MarkerOptions[latLng.length];
        final Marker[] markers = new Marker[latLng.length];

        System.out.println("ssssssss ====  "+latLng.length);

        for (int i = 0; i < latLng.length; i++) {
            markerOptions[i] = new MarkerOptions().position(latLng[i]).title("title: " + i);
            markers[i] = mMap.addMarker(markerOptions[i]);
            markers[i].setTag(i);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], ZOOM_LEVEL));
        markers[0].setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currMarker = markers[0];

        System.out.println(" yyyyyyyyyyyyy ==== "+currMarker);
        System.out.println(" zzzzzzzzzzzzz ==== "+currLocation);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                moveTo(markers[position]);
            }
        });

        viewPager.setOnItemClickListener(new WrapContentViewPager.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (polylines != null) {
                    for (int i = polylines.size() - 1; i >= 0; i--) {
                        polylines.remove(i).remove();
                    }
                } else {
                    polylines = new ArrayList<>();
                }
                requestLatLong();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                viewPager.setCurrentItem((Integer) marker.getTag());
                return true;
            }
        });
    }

    private void moveTo(Marker marker) {
        currMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
        currMarker = marker;
        currMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currMarker.getPosition(), ZOOM_LEVEL));
    }

    private void showRoute(LatLng latLng1, LatLng latLng2) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latLng1).include(latLng2);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int padding = (displayMetrics.heightPixels / 6);  // offset from edges of the map
        // in pixels
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), padding));
        mMap.moveCamera(CameraUpdateFactory.scrollBy(0, viewPager.getHeight() / 3));
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(latLng1, latLng2)
                .key(getString(R.string.geo_api_key))
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Log.d(TAG, "failed");
    }

    @Override
    public void onRoutingStart() {
        Log.d(TAG, "start");
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
        Log.d(TAG, "success");
        Route route = arrayList.get(0);
        if (route != null) {
            animateRoute(route.getPoints());
        } else {
            Toast.makeText(this, "No route found", Toast.LENGTH_SHORT).show();
        }
    }

    private void animateRoute(final List<LatLng> points) {
        int totalPoints;
        if (points == null || (totalPoints = points.size()) == 0) return;

        final PolylineOptions polylineOptions = new PolylineOptions().color(accentColor);

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setIntValues(0, totalPoints - 1);
        valueAnimator.setDuration(Math.min(totalPoints * 4, 2500));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int index = (int) animation.getAnimatedValue();
                polylineOptions.add(points.get(index));
                polylines.add(mMap.addPolyline(polylineOptions));
            }
        });
        valueAnimator.start();
    }

    @Override
    public void onRoutingCancelled() {
        Log.d(TAG, "cancelled");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            requestLatLong();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_LOCATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            initLocationHelper();
        } else {
            //Toast.makeText(this, "Location permissions are needed in order to function properly", Toast.LENGTH_SHORT).show();
            requestLatLong();
        }
    }

    private void requestLatLong() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initLocationHelper();
        } else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_LOCATION_REQUEST_CODE);
    }

    private void initLocationHelper() {

        if (progressDialog == null) {
           try {
               progressDialog = new ProgressDialog(this);
               progressDialog.setMessage("Obtaining current location");
               locationHelper.buildClient();

           }catch (Exception e){
                e.printStackTrace();
           }

        }

        if (locationHelper != null) {
            locationHelper.buildClient();
        } else {
            locationHelper = new LocationHelper(this);
            locationHelper.build();
        }
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
        mMap.setMyLocationEnabled(true);
        if (!canGetLocation()) {
            if (!progressDialog.isShowing()) progressDialog.show();
            locationHelper.setOnLocationReceivedListener(new LocationHelper.OnLocationReceivedListener() {
                @Override
                public void onLocationReceivedSuccess(LatLng latLng) {
                    progressDialog.dismiss();
                    currLocation = latLng;
                    showRoute(latLng, currMarker.getPosition());
                }

                @Override
                public void onLocationReceivedFailed() {
                    if (!canGetLocation()) {
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private boolean canGetLocation() {

        Location location = mMap.getMyLocation();
        if (location != null) {
            currLocation = new LatLng(location.getLatitude(), location.getLongitude());
//            mMap.setMyLocationEnabled(false);
        }
        if (currLocation != null) {
            progressDialog.dismiss();
            showRoute(currLocation, currMarker.getPosition());
            return true;
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationHelper != null) locationHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationHelper != null) locationHelper.onPause();
    }

    @Override
    protected void onStart() {
        if (locationHelper != null) {
            locationHelper.onStart();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (locationHelper != null) {
            locationHelper.onStop();
        }
        super.onStop();
    }
}