package com.leadnsol.ride_sharing.ui.rider;

import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.leadnsol.ride_sharing.R;
import com.leadnsol.ride_sharing.app_common.AppConstant;
import com.leadnsol.ride_sharing.app_common.preference.SharedPrefHelper;
import com.leadnsol.ride_sharing.models.DriverAdapter;
import com.leadnsol.ride_sharing.models.LocationModel.LocationModel;
import com.leadnsol.ride_sharing.models.User;
import com.vanillaplacepicker.data.VanillaAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DriverListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Location riderLocation;
    private VanillaAddress dropUpLocationAddress;
    private LocationModel locationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_list);
        initViews();
        riderLocation = RiderDashboardActivity.riderCurrentLocation;
        dropUpLocationAddress = RiderDashboardActivity.dropUpLocationAddress;

        recyclerView = findViewById(R.id.rv_driver_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        /*if (getIntent() != null) {
         *//* riderLocation = (Location) getIntent().getSerializableExtra("RiderLocation");
            dropUpLocationAddress = (VanillaAddress) getIntent().getSerializableExtra("dropUpLocation");*//*
           // locationModel = (LocationModel) getIntent().getSerializableExtra("RiderLocation");
        }
*/
        getAvailableDrivers();
        /*DriverAdapter adapter = new DriverAdapter();
        recyclerView.setAdapter(adapter)*/
        ;
    }

    private TextView txtRiderName, txtRiderLocation, txtRiderDropUpLocation, txtDistanceFromRiderToDropup;

    private void initViews() {
        txtRiderDropUpLocation = findViewById(R.id.tv_riderDestination);
        txtRiderName = findViewById(R.id.tv_riderName);
        txtRiderLocation = findViewById(R.id.tv_riderClocation);
        txtDistanceFromRiderToDropup = findViewById(R.id.tv_distanceDifference);
        if (SharedPrefHelper.getPrefHelper().getUserModel() != null) {
            User user = new Gson().fromJson(SharedPrefHelper.getPrefHelper().getUserModel(), User.class);
            txtRiderName.setText(user.getName());
        }

        if (LocationModel.getInstance() != null) {
            txtRiderLocation.setText(LocationModel.getInstance().getRiderCurrentLocation().getLatitude() + "," +
                    LocationModel.getInstance().getRiderCurrentLocation().getLongitude());
            txtRiderDropUpLocation.setText(LocationModel.getInstance().getRiderVanilaAddress().getFormattedAddress());
            Location locationDropUp = new Location("RiderDropUp");
            locationDropUp.setLatitude(LocationModel.getInstance().getRiderVanilaAddress().getLatitude());
            locationDropUp.setLongitude(LocationModel.getInstance().getRiderVanilaAddress().getLongitude());

            double distance = LocationModel.getInstance().getRiderCurrentLocation().distanceTo(locationDropUp) / 1000; // dividing in km
            String locationDiff = String.format("RiderDestination: %.2f", distance, Locale.getDefault()).concat(" km");
            txtDistanceFromRiderToDropup.setText(locationDiff);
        }

    }

    private void getAvailableDrivers() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.USERS);
        dbRef.orderByChild("userType").equalTo(AppConstant.DRIVER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<User> driverList = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        assert user != null;
                        if (user.getStatus().equalsIgnoreCase(AppConstant.DRIVER_CAN_SHARE_RIDE))
                            driverList.add(user);
                    }
                    if (driverList.size() > 0) {
                        if (riderLocation != null) {
                            DriverAdapter adapter = new DriverAdapter(driverList, DriverListActivity.this);
                            recyclerView.setAdapter(adapter);

                        }
                    } else {
                        Toast.makeText(DriverListActivity.this, "No Driver Available right now!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
