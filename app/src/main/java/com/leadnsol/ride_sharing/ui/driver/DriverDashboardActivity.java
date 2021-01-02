package com.leadnsol.ride_sharing.ui.driver;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bikcrum.locationupdate.LocationUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.leadnsol.ride_sharing.R;
import com.leadnsol.ride_sharing.app_common.AppConstant;
import com.leadnsol.ride_sharing.app_common.preference.SharedPrefHelper;
import com.leadnsol.ride_sharing.models.RideShare;
import com.leadnsol.ride_sharing.models.User;
import com.leadnsol.ride_sharing.ui.LoginActivity;
import com.vanillaplacepicker.data.VanillaAddress;
import com.vanillaplacepicker.presentation.builder.VanillaPlacePicker;
import com.vanillaplacepicker.utils.KeyUtils;
import com.vanillaplacepicker.utils.MapType;
import com.vanillaplacepicker.utils.PickerLanguage;
import com.vanillaplacepicker.utils.PickerType;

import static com.vanillaplacepicker.utils.KeyUtils.REQUEST_PLACE_PICKER;

public class DriverDashboardActivity extends AppCompatActivity implements LocationUpdate.OnLocationUpdatedListener, OnMapReadyCallback {
    LocationUpdate mLocationUpdate;
    private User mUser;

    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // The fastest rate for active location updates. Exact. Updates will never be more frequent than this value.
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private String notificationMapIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        if (SharedPrefHelper.getPrefHelper().getUserModel() != null) {
            mUser = new Gson().fromJson(SharedPrefHelper.getPrefHelper().getUserModel(), User.class);
        }

        if (getActionBar() != null && mUser != null)
            getActionBar().setTitle(mUser.getUserType());

        if (getIntent() != null) {
            notificationMapIntent = getIntent().getStringExtra("Notification");
            if (notificationMapIntent != null) {
                populateRideShareOfRiderOnMap();
            }
            //Toast.makeText(this, notification, Toast.LENGTH_SHORT).show();
        }

        mLocationUpdate = LocationUpdate.getInstance(this);
        mLocationUpdate.onCreate(savedInstanceState);

        mLocationUpdate.setLocationUpdateIntervalInMilliseconds(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationUpdate.setLocationFastestUpdateIntervalInMilliseconds(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

    }

    private void populateRideShareOfRiderOnMap() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.SHARING_RIDE);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        RideShare rideShare = child.getValue(RideShare.class);
                        if (rideShare != null && rideShare.getRiderID().equalsIgnoreCase(notificationMapIntent)) {
                            String[] value = rideShare.getRiderLocation().split(",");
                            String[] valueDest = rideShare.getRiderDestination().split(",");
                            LatLng latLng = new LatLng(Double.parseDouble(value[0]), Double.parseDouble(value[1]));
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title("RiderLocation"));
                            LatLng destinationLatLng = new LatLng(Double.parseDouble(valueDest[0]), Double.parseDouble(valueDest[1]));
                            mMap.addMarker(new MarkerOptions()
                                    .position(destinationLatLng)
                                    .title("RiderDestination"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private GoogleMap mMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //don't forget to call this
        mLocationUpdate.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        //don't forget to call this
        mLocationUpdate.onDestroy();
        super.onDestroy();
    }

    private VanillaAddress driverDestinationAddress;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLocationUpdate.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_PLACE_PICKER) {
                driverDestinationAddress = (VanillaAddress) data.getSerializableExtra(KeyUtils.SELECTED_PLACE);
                setLocationOnMap(new LatLng(driverDestinationAddress.getLatitude(), driverDestinationAddress.getLongitude()), "Destination");
                /*Intent intent = new Intent(this, DriverListActivity.class);
                intent.putExtra("RiderLocation", riderCurrentLocation);
                intent.putExtra("dropUpLocation", dropUpLocationAddress);
                startActivity(intent);*/
                // Do needful with your vanillaAddressbreak;
            }
        }
    }

    private void setLocationOnMap(LatLng latLng, String whom) {

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(whom));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
    }

    private Location driverLocation;

    @Override
    public void onLocationUpdated(Location mCurrentLocation, String mLastUpdateTime) {
        driverLocation = mCurrentLocation;

        setLocationOnMap(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), "Me");
    }

    private boolean isClicked = false;

    public void onDriverStatusClick(View view) {

        if (driverDestinationAddress != null) {
            Log.d("VanilaAddress", driverDestinationAddress.getFormattedAddress());
            Button btn = findViewById(view.getId());
            if (isClicked) {
                Toast.makeText(this, "i can share a ride", Toast.LENGTH_SHORT).show();
                btn.setText(AppConstant.DRIVER_CANNOT_SHARE_RIDE);
                mUser.setStatus(AppConstant.DRIVER_CAN_SHARE_RIDE);
                if (driverLocation != null) {
                    String location = driverLocation.getLatitude() + "," + driverLocation.getLongitude();
                    mUser.setDriverLocation(location);
                    Log.d("VanilaAddress", location);
                } else {
                    Toast.makeText(this, "Location is null!", Toast.LENGTH_SHORT).show();
                }
                mUser.setDriverDestinationLocation(driverDestinationAddress.getLatitude() + "," + driverDestinationAddress.getLongitude());

            } else {
                Toast.makeText(this, "i can't share a ride", Toast.LENGTH_SHORT).show();
                btn.setText(AppConstant.DRIVER_CAN_SHARE_RIDE);
                mUser.setStatus(AppConstant.DRIVER_CANNOT_SHARE_RIDE);
                mUser.setDriverLocation("");
                mUser.setDriverDestinationLocation("");
            }
            isClicked = !isClicked;
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.USERS).child(mUser.getUId());
            dbRef.setValue(mUser).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Location and status is updated!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Pick up Destination First!", Toast.LENGTH_SHORT).show();
        }

    }


    public void onLogoutClick(View view) {
        SharedPrefHelper.getPrefHelper().clearPreferences();
        startActivity(new Intent(DriverDashboardActivity.this, LoginActivity.class));
        finish();
    }

    public void onDriverPickUpDestination(View view) {
        Intent intent = new VanillaPlacePicker.Builder(this)
                .with(PickerType.MAP_WITH_AUTO_COMPLETE) // Select Picker type to enable autocompelte, map or both
                .isOpenNow(true)
                .setPickerLanguage(PickerLanguage.ENGLISH) // Apply language to picker
                // .enableShowMapAfterSearchResult(true) // To show the map after selecting the
                // place from place picker only for PickerType.MAP_WITH_AUTO_COMPLETE

                /*
                 * Configuration for Map UI
                 */
                .setMapType(MapType.TERRAIN) // Choose map type (Only applicable for map screen)
                //.setMapStyle(R.raw.style_json) // Containing the JSON style declaration for night-mode styling
                .setMapPinDrawable(android.R.drawable.ic_menu_mylocation) // To give custom pin image for map marker

                .build();

        startActivityForResult(intent, REQUEST_PLACE_PICKER);
    }


}
