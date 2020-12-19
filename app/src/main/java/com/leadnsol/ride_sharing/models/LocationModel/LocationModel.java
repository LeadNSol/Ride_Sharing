package com.leadnsol.ride_sharing.models.LocationModel;

import android.location.Location;

import com.vanillaplacepicker.data.VanillaAddress;

public class LocationModel implements ILocationModel {
    private Location riderCurrentLocation;
    private VanillaAddress riderDestinationAddress;

    static LocationModel locationModel;

    public static LocationModel getInstance() {
        if (locationModel == null) {
            locationModel = new LocationModel();
        }
        return locationModel;
    }

    private LocationModel() {
    }

    private LocationModel(Location riderCurrentLocation, VanillaAddress riderDestinationAddress) {
        this.riderCurrentLocation = riderCurrentLocation;
        this.riderDestinationAddress = riderDestinationAddress;
    }

    @Override
    public void setRiderCurrentLocation(Location location) {
        this.riderCurrentLocation = location;
    }

    @Override
    public Location getRiderCurrentLocation() {
        return this.riderCurrentLocation;
    }


    @Override
    public void setRiderVanilaAddress(VanillaAddress vanilaAddress) {
        this.riderDestinationAddress = vanilaAddress;
    }

    @Override
    public VanillaAddress getRiderVanilaAddress() {
        return this.riderDestinationAddress;
    }


}
