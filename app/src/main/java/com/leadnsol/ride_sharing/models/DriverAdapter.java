package com.leadnsol.ride_sharing.models;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.leadnsol.ride_sharing.models.LocationModel.LocationModel;
import com.leadnsol.ride_sharing.notification.NotificationApi;
import com.leadnsol.ride_sharing.notification.models.Data;
import com.leadnsol.ride_sharing.notification.models.Sender;
import com.leadnsol.ride_sharing.notification.models.StatusResponse;
import com.leadnsol.ride_sharing.notification.models.Token;
import com.leadnsol.ride_sharing.notification.retrofit.RetrofitHelper;
import com.leadnsol.ride_sharing.ui.rider.RiderDashboardActivity;
import com.vanillaplacepicker.data.VanillaAddress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.viewHolder> {
    List<User> mUserList;
    Context context;
    private Location riderLocation;
    private VanillaAddress dropUpLocationAddress;
    private List<Double> driverLocationSumUpList; // used for to adds up the current and destination of driver to find out smallest.
    private NotificationApi mNotificationApi;
    private OnDriverClickListener mClickListener;

    public DriverAdapter(List<User> mUserList, Context context) {
        this.mUserList = mUserList;
        this.context = context;
        this.mClickListener = mClickListener;

        this.riderLocation = LocationModel.getInstance().getRiderCurrentLocation();
        this.dropUpLocationAddress = LocationModel.getInstance().getRiderVanilaAddress();
        this.driverLocationSumUpList = new ArrayList<>();
        this.mNotificationApi = RetrofitHelper.getInstance().getNotificationApiClient();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_recycler_view, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        User user = mUserList.get(position);
        holder.setData(user);


    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView name, carModel, carRegistrationNumber, carColor, txtDriverLocation, txtDriverDestination, txtDistanceOfRiderDropUp;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            carModel = itemView.findViewById(R.id.tv_carModel);
            carRegistrationNumber = itemView.findViewById(R.id.tv_CarRegistration);
            carColor = itemView.findViewById(R.id.tv_carColor);
            txtDriverLocation = itemView.findViewById(R.id.tv_cdistancedriver);
            txtDriverDestination = itemView.findViewById(R.id.tv_driver_destination_address);
            txtDistanceOfRiderDropUp = itemView.findViewById(R.id.tv_driverDestination);

        }

        public void setData(User user) {
            name.setText("Name: ".concat(user.getName()));
            carColor.setText("Car Color: "+user.getCarColor());
            carRegistrationNumber.setText("CRN: " + user.getCarRegistrationNumber());
            carModel.setText("Car model: " + user.getCarModel());
            txtDriverDestination.setText("Driver Destination: " + user.getDriverDestinationLocation());


            Location locationDropUp = new Location("RiderDropUp");
            locationDropUp.setLatitude(dropUpLocationAddress.getLatitude());
            locationDropUp.setLongitude(dropUpLocationAddress.getLongitude());

            if (user.getDriverLocation() != null && user.getDriverDestinationLocation() != null) {
                double riderToDriverDistance = riderLocation.distanceTo(getLocationDetails(user.getDriverLocation(), "DriverLocation")) / 1000;
                double distanceBtwDestinations = getLocationDetails(user.getDriverDestinationLocation(), "DriverDestination")
                        .distanceTo(locationDropUp) / 1000;
                txtDriverLocation.setText(String.format("RiderToDriver: %.2f", riderToDriverDistance).concat(" Km"));
                txtDistanceOfRiderDropUp.setText(String.format("BtwDestination: % .2f", distanceBtwDestinations).concat(" Km"));

                double result = riderToDriverDistance + distanceBtwDestinations;
                driverLocationSumUpList.add(result);
            }

            itemView.setOnClickListener(view -> {
                nearestDriver = Collections.min(driverLocationSumUpList);

                if (nearestDriver != 0) {
                    Toast.makeText(context, "nearest Driver is" + user.getName() + "\n with regNum " + user.getCarRegistrationNumber(), Toast.LENGTH_SHORT).show();
                    sendNotification(user, nearestDriver);

                    Intent intent = new Intent(context, RiderDashboardActivity.class);
                    intent.putExtra("DriverDestination", user.getDriverDestinationLocation());
                    intent.putExtra("DriverCurrentLocation", user.getDriverLocation());
                    context.startActivity(intent);
                }
            });


            //wait until complete list is finished
           /* if (driverLocationSumUpList.size() == mUserList.size()) {
                //nearestDriver = pickUpNearestUser(driverLocationSumUpList);
                nearestDriver = Collections.min(driverLocationSumUpList);
                Toast.makeText(context, "nearest Driver is" + user.getName() + "\n with regNum " + user.getCarRegistrationNumber(), Toast.LENGTH_SHORT).show();
                sendNotification(user, nearestDriver);
            }*/
        }

        double nearestDriver = 0;

        private Location getLocationDetails(String locationLatLng, String type) {
            Location location = new Location(type);
            if (!locationLatLng.isEmpty()) {
                String[] value = locationLatLng.split(",");
                location.setLongitude(Double.parseDouble(value[1]));
                location.setLatitude(Double.parseDouble(value[0]));

            }
            return location;
        }

        private void sendNotification(User model, double nearestDriver) {
            if (SharedPrefHelper.getPrefHelper().getUserModel() != null) {
                User rider = new Gson().fromJson(SharedPrefHelper.getPrefHelper().getUserModel(), User.class);
                if (rider != null && model != null) {
                    String body = "You have a ride request from " + rider.getName();
                    DatabaseReference dbAllTokenRef = FirebaseDatabase.getInstance().getReference(AppConstant.TOKENS);

                    // Query query = dbAllTokenRef.orderByKey().equalTo(mSaloonModel.getId());
                    dbAllTokenRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot child : snapshot.getChildren())
                                    if (child.getKey().equalsIgnoreCase(model.getUId())) {

                                        Token token = child.getValue(Token.class);
                                        Data data = new Data(rider.getUId(), body,
                                                "Someone wants a Ride", model.getUId(), R.mipmap.ic_launcher);
                                        Sender sender = new Sender(data, token.getToken());

                                        mNotificationApi.sendNotification(sender)
                                                .enqueue(new Callback<StatusResponse>() {
                                                    @Override
                                                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                                                        if (response.isSuccessful()) {
                                                            StatusResponse statusResponse = response.body();
                                                            if (statusResponse != null) {
                                                                Toast.makeText(context, "Request is send", Toast.LENGTH_SHORT).show();

                                                                //saving data as appointments in d
                                                                //      String timeStamp = String.valueOf(System.currentTimeMillis());
                                                                //    model.setTime(timeStamp);
                                                                RideShare rideShare = new RideShare(rider.getName(),
                                                                        rider.getMobile(),
                                                                        riderLocation.getLatitude()
                                                                                + "," + riderLocation.getLongitude(),
                                                                        dropUpLocationAddress.getLatitude()
                                                                                + "," + dropUpLocationAddress.getLongitude());
                                                                FirebaseDatabase.getInstance().getReference(AppConstant.SHARING_RIDE)
                                                                        .push()
                                                                        .setValue(rideShare);

                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                                                        t.printStackTrace();
                                                    }
                                                });
                                    }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }

        }
    }

    public interface OnDriverClickListener {
        void onDriverClick(User user); //user will be driver in this case
    }
}
