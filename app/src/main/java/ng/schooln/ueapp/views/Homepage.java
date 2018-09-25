package ng.schooln.ueapp.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ng.schooln.ueapp.R;
import ng.schooln.ueapp.adapters.EtypeAdapter;
import ng.schooln.ueapp.adapters.HistoryAdapter;
import ng.schooln.ueapp.controllers.DbHelper;
import ng.schooln.ueapp.models.Alert;
import ng.schooln.ueapp.models.StaffModel;
import ng.schooln.ueapp.models.StudentModel;
import ng.schooln.ueapp.utils.BounceEffect;
import ng.schooln.ueapp.utils.Variables;

public class Homepage extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMapClickListener, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;
    private ImageView userimg, option;
    private GoogleApiClient googleApiClient;
    // Setting Location Permissions
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private  LocationRequest locationRequest;
    private Boolean mLocationPermissionsGranted = false;
    private Location mlocation;
    private static final String TAG = "Homepage";
    String staff, student;
    private Handler handler;
    FirebaseAuth auth;
    //private GPSTracker gps;
    HistoryAdapter historyAdapter;
    EtypeAdapter etypeAdapter;
    boolean doubleBackToExitPressedOnce = false;
    ArrayList<Alert> alertdata = new ArrayList<>();
    ArrayList<StaffModel> ettypedata = new ArrayList<>();
    ArrayList<StudentModel> stuettypedata = new ArrayList<>();
    Geocoder geocoder;
    private TextView name, email, address, nohistory, dept, alerttex;
    private LatLng userlocation;
    private DbHelper dbHelper;
    List<Address> addresses;
    private RecyclerView historyrecycler, emerrecycler;
    private Variables variables;
    private RelativeLayout historylay, emerlay, alertbg;
    private StaffModel staffModel;
    private StudentModel studentModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getLocationPermission();
        initViews();
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

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                return;
            }
        }

        if (googleApiClient == null) {
            buildgoogleApiClient();
        }


        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setPadding(6,6,6,6);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

    }

    protected synchronized void buildgoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();
        googleApiClient.connect();
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionsGranted = true;
                    initMap();

                } else {
                    ActivityCompat.requestPermissions(this,
                            permissions,
                            LOCATION_PERMISSION_REQUEST_CODE);
                }
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }


    }

    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onClick(View view) {
        if (view ==  alertbg){
            if (emerlay.getVisibility() != View.VISIBLE){
                historylay.setVisibility(View.GONE);
                emerlay.setVisibility(View.VISIBLE);
                alertbg.setVisibility(View.GONE);
            }
        }
        if (view == userimg){
            Intent intent = new Intent(Homepage.this, Userprofile.class);
            intent.putExtra(variables.ID, auth.getCurrentUser().getUid());
            if (staff != null){
                intent.putExtra(variables.Users, variables.Staffs);
            }else {
                intent.putExtra(variables.Users, variables.Student);
            }
            startActivity(intent);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        // Draw back is that this accuracy draws down a lot of battery
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
      if (Build.VERSION.SDK_INT >= 23 ){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                return;
            }
     }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

            mlocation  = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (mlocation != null){

            userlocation = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userlocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12f));

            try
            {

                addresses = geocoder.getFromLocation(mlocation.getLatitude(), mlocation.getLongitude(), 1);
                String ess = addresses.get(0).getAddressLine(0);
                address.setText(ess);

            }
            catch (IOException e) {
                Toast.makeText(Homepage.this,e.getMessage(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        }else {
            Toast.makeText(Homepage.this,"Enable location ",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(Homepage.this, String.valueOf(i),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(Homepage.this, connectionResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        if (mlocation == null){
            mlocation  = location;
            LatLng latLng = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12f));
            getCurrentuserAddress(mlocation);
        }else {
            getCurrentuserAddress(mlocation);
        }




    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    private void getCurrentuserAddress(Location location){
        if (location != null){
            userlocation = new LatLng(location.getLatitude(), location.getLongitude());

            // if the address is not shown on the toolbar
            if (address.getText().equals("")){
                try {
                    addresses = geocoder.getFromLocation(userlocation.latitude, userlocation.longitude, 1);
                    String addr = addresses.get(0).getAddressLine(0);
                    address.setText(addr);
                }
                catch (IOException e) {
               //     Toast.makeText(Homepage.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private void initViews(){
        auth = FirebaseAuth.getInstance();
        variables = new Variables();
        userimg = findViewById(R.id.userimg);
        userimg.setOnClickListener(this);
        name = findViewById(R.id.name);
        alertbg = findViewById(R.id.alertbg);
        alertbg.setOnClickListener(this);
        nohistory = findViewById(R.id.nohis);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        historylay = findViewById(R.id.historylay);
        dept = findViewById(R.id.dept);
        emerlay = findViewById(R.id.newalertlay);
        alerttex = findViewById(R.id.alerttex);
        staff = getIntent().getStringExtra(variables.Staffs);
        student = getIntent().getStringExtra(variables.Student);
        dbHelper = new DbHelper(auth, this);
        geocoder = new Geocoder(this, Locale.getDefault());
       Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Lato-Black.ttf");
        alerttex.setTypeface(custom_font);
        setupUserViews();

        historyrecycler = findViewById(R.id.historyrecy);
        emerrecycler = findViewById(R.id.emergencylist);
        handler = new Handler();
        final int delay = 4000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                popButton();
                handler.postDelayed(this, delay);
            }
        }, delay);

    }

    private void popButton(){
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.pop);
        BounceEffect bounceEffect = new BounceEffect(0.2, 20);
        animation.setInterpolator(bounceEffect);
        alertbg.startAnimation(animation);
    }
    private void setupUserViews() {
        if (student != null) {
            dbHelper.studentref(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        studentModel = dataSnapshot.getValue(StudentModel.class);
                        Glide.with(Homepage.this).load(String.valueOf(studentModel.getImg())).into(userimg);
                        name.setText(studentModel.getNam());
                        email.setText(studentModel.getEmail());
                        dept.setText(studentModel.getDept());

                        dbHelper.stuentEmer().getRef().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null){
                                    stuettypedata.clear();
                                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                                        StudentModel  odel = ds.getValue(StudentModel.class);
                                        stuettypedata.add(0,odel);

                                        etypeAdapter = new EtypeAdapter(Homepage.this,stuettypedata,null,auth,null,studentModel, mlocation);
                                        emerrecycler.setLayoutManager(new LinearLayoutManager(Homepage.this));
                                        emerrecycler.setHasFixedSize(true);
                                        emerrecycler.addItemDecoration(new DividerItemDecoration(Homepage.this,DividerItemDecoration.HORIZONTAL));
                                        emerrecycler.setAdapter(etypeAdapter);
                                        etypeAdapter.notifyDataSetChanged();
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        dbHelper.Depthistorystudentref(studentModel.getDept()).getRef().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null){
                                    alertdata.clear();
                                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                                        Alert alert = ds.getValue(Alert.class);
                                        alertdata.add(0, alert);

                                        historyAdapter = new HistoryAdapter(Homepage.this, alertdata, auth,null,studentModel);
                                        historyrecycler.setHasFixedSize(true);
                                        historyrecycler.setLayoutManager(new LinearLayoutManager(Homepage.this));
                                        historyrecycler.setAdapter(historyAdapter);
                                        historyAdapter.notifyDataSetChanged();
                                        nohistory.setVisibility(View.GONE);
                                        historyrecycler.setVisibility(View.VISIBLE);


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else if (staff != null){
            dbHelper.staffref(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        staffModel = dataSnapshot.getValue(StaffModel.class);
                        Glide.with(Homepage.this).load(String.valueOf(staffModel.getImg())).into(userimg);
                        name.setText(staffModel.getNam());
                        email.setText(staffModel.getEmail());
                        dept.setText(staffModel.getDept());

                        dbHelper.stafemer().getRef().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ettypedata.clear();
                                if (dataSnapshot.getValue() != null) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        StaffModel odel = ds.getValue(StaffModel.class);
                                        ettypedata.add(0, odel);

                                        etypeAdapter = new EtypeAdapter(Homepage.this, null, ettypedata, auth, odel, null,  mlocation);
                                        emerrecycler.setLayoutManager(new LinearLayoutManager(Homepage.this));
                                        emerrecycler.setHasFixedSize(true);
                                        emerrecycler.addItemDecoration(new DividerItemDecoration(Homepage.this, DividerItemDecoration.HORIZONTAL));
                                        emerrecycler.setAdapter(etypeAdapter);
                                        etypeAdapter.notifyDataSetChanged();

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        dbHelper.Depthistorystudentref(staffModel.getDept()).getRef().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null){
                                    alertdata.clear();
                                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                                        Alert alert = ds.getValue(Alert.class);
                                        alertdata.add(0, alert);

                                        historyAdapter = new HistoryAdapter(Homepage.this, alertdata, auth,staffModel,null);
                                        historyrecycler.setHasFixedSize(true);
                                        historyrecycler.setLayoutManager(new LinearLayoutManager(Homepage.this));
                                        historyrecycler.setAdapter(historyAdapter);
                                        historyAdapter.notifyDataSetChanged();


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            }

        }

    @Override
    public void onBackPressed() {
        if (emerlay.getVisibility() == View.VISIBLE){
            emerlay.setVisibility(View.GONE);
            historylay.setVisibility(View.VISIBLE);
            alertbg.setVisibility(View.VISIBLE);
        }else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }
}
