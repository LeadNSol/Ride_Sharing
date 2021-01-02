package com.leadnsol.ride_sharing.models;

public class RideShare {

    private String riderName, riderPhone, riderLocation, riderDestination,riderID;

    public RideShare() {
    }

    public RideShare(String riderName, String riderPhone, String riderLocation, String riderDestination,String riderID) {
        this.riderName = riderName;
        this.riderPhone = riderPhone;
        this.riderLocation = riderLocation;
        this.riderDestination = riderDestination;
        this.riderID = riderID;
    }

    public String getRiderID() {
        return riderID;
    }

    public void setRiderID(String riderID) {
        this.riderID = riderID;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public String getRiderPhone() {
        return riderPhone;
    }

    public void setRiderPhone(String riderPhone) {
        this.riderPhone = riderPhone;
    }

    public String getRiderLocation() {
        return riderLocation;
    }

    public void setRiderLocation(String riderLocation) {
        this.riderLocation = riderLocation;
    }

    public String getRiderDestination() {
        return riderDestination;
    }

    public void setRiderDestination(String riderDestination) {
        this.riderDestination = riderDestination;
    }
}

