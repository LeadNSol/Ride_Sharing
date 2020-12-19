package com.leadnsol.ride_sharing.models.LocationModel;

import android.location.Location;

import com.vanillaplacepicker.data.VanillaAddress;

public interface ILocationModel {

    void setRiderCurrentLocation(Location location);

    Location getRiderCurrentLocation();

    void setRiderVanilaAddress(VanillaAddress vanilaAddress);

    VanillaAddress getRiderVanilaAddress();
}
