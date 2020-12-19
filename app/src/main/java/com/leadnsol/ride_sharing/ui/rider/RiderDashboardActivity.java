package com.leadnsol.ride_sharing.ui.rider;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bikcrum.locationupdate.LocationUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leadnsol.ride_sharing.R;
import com.leadnsol.ride_sharing.app_common.preference.SharedPrefHelper;
import com.leadnsol.ride_sharing.models.LocationModel.LocationModel;
import com.leadnsol.ride_sharing.ui.LoginActivity;
import com.vanillaplacepicker.data.VanillaAddress;
import com.vanillaplacepicker.presentation.builder.VanillaPlacePicker;
import com.vanillaplacepicker.utils.KeyUtils;
import com.vanillaplacepicker.utils.MapType;
import com.vanillaplacepicker.utils.PickerLanguage;
import com.vanillaplacepicker.utils.PickerType;

public class RiderDashboardActivity extends AppCompatActivity implements LocationUpdate.OnLocationUpdatedListener, OnMapReadyCallback {
    private static final int REQUEST_PLACE_PICKER = 1001;
    LocationUpdate mLocationUpdate;

    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // The fastest rate for active location updates. Exact. Updates will never be more frequent than this value.
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private String driverCurrentLocation, driverDestinationLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        mLocationUpdate = LocationUpdate.getInstance(this);
        mLocationUpdate.onCreate(savedInstanceState);

        mLocationUpdate.setLocationUpdateIntervalInMilliseconds(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationUpdate.setLocationFastestUpdateIntervalInMilliseconds(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);


    }

    private void showDriverOnMap() {
        String[] value = driverCurrentLocation.split(",");
        String[] valueDes = driverDestinationLocation.split(",");

        LatLng driverCurrLatLng = new LatLng(Double.parseDouble(value[0]), Double.parseDouble(value[1]));
        LatLng driverDestinationLatLng = new LatLng(Double.parseDouble(valueDes[0]), Double.parseDouble(valueDes[1]));

        if (mMap != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(driverCurrLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.driver))
                    .title("Driver"));
            mMap.addMarker(new MarkerOptions()
                    .position(driverDestinationLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.transportation))
                    .title("Driver Destination"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverCurrLatLng, 10));
        }

        if (LocationModel.getInstance().getRiderCurrentLocation() != null) {
            LatLng riderLocation = new LatLng(LocationModel.getInstance().getRiderCurrentLocation().getLatitude(),
                    LocationModel.getInstance().getRiderCurrentLocation().getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(riderLocation)
                    .title("Driver Destination"));
        }


    }

    private GoogleMap mMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (getIntent() != null) {
            driverCurrentLocation = getIntent().getStringExtra("DriverCurrentLocation");
            driverDestinationLocation = getIntent().getStringExtra("DriverDestination");
            if (driverDestinationLocation != null && driverCurrentLocation != null) {
                showDriverOnMap();

            }

        }

    }

    private void setLocationOnMap(LatLng latLng, String whom) {

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(whom));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
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

    public static Location riderCurrentLocation;
    public static VanillaAddress dropUpLocationAddress;

    @Override
    public void onLocationUpdated(Location mCurrentLocation, String mLastUpdateTime) {
        /*Log.v(TAG, "onLocationUpdated is called");
        long lat = mCurrentLocation.getLatitude();
        long lng = mCurrentLocation.getLongitude();*/
        //do whatever you want to do with latitude and longitude
        riderCurrentLocation = mCurrentLocation;
        setLocationOnMap(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), "Me");
    }

    /*Button click from xml*/
    public void onDropUpLocation(View view) {
        Intent intent = new VanillaPlacePicker.Builder(this)
                .with(PickerType.MAP_WITH_AUTO_COMPLETE) // Select Picker type to enable autocompelte, map or both
                .isOpenNow(true)
                //.withLocation(riderCurrentLocation.getLatitude(), riderCurrentLocation.getLongitude())
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLocationUpdate.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_PLACE_PICKER) {
                dropUpLocationAddress = (VanillaAddress) data.getSerializableExtra(KeyUtils.SELECTED_PLACE);
                if (riderCurrentLocation != null) {
                    // LocationModel locationModel = new LocationModel(riderCurrentLocation, dropUpLocationAddress);

                    setLocationOnMap(new LatLng(dropUpLocationAddress.getLatitude(), dropUpLocationAddress.getLongitude()), "Destination");
                    LocationModel.getInstance().setRiderCurrentLocation(riderCurrentLocation);
                    LocationModel.getInstance().setRiderVanilaAddress(dropUpLocationAddress);

                    Intent intent = new Intent(RiderDashboardActivity.this, DriverListActivity.class);
                    //intent.putExtra("RiderLocation", locationModel);
                    startActivity(intent);

                } else {
                    Toast.makeText(this, "Your current location is not set!", Toast.LENGTH_SHORT).show();
                }
                // Do needful with your vanillaAddressbreak;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void onLogoutClick(View view) {
        SharedPrefHelper.getPrefHelper().clearPreferences();
        startActivity(new Intent(RiderDashboardActivity.this, LoginActivity.class));
        finish();
    }


}
