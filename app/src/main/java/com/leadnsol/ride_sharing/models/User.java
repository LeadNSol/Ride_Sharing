package com.leadnsol.ride_sharing.models;

import androidx.annotation.NonNull;

public class User {
    private String UId, name, email, password, mobile, carModel, carRegistrationNumber, carColor;
    private String userType, status, driverLocation, driverDestinationLocation;

    public User() {
    }

    public User(String UId, String name, String email, String password, String mobile, String carModel,
                String carRegistrationNumber,
                String carColor, String userType, String status, String driverLocation,
                String driverDestinationLocation) {
        this.UId = UId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.carModel = carModel;
        this.carRegistrationNumber = carRegistrationNumber;
        this.carColor = carColor;
        this.userType = userType;
        this.status = status;
        this.driverLocation = driverLocation;
        this.driverDestinationLocation = driverDestinationLocation;
    }

    public User(String driverLocation, String driverDestinationLocation) {
        this.driverLocation = driverLocation;
        this.driverDestinationLocation = driverDestinationLocation;
    }
    //    /*
//        * this is for Rider signup
//        * */
//    public User(String UId, String name, String email, String password, String mobile,
//                String carModel:"-1", String carRegistrationNumber:"-1", String carColor:"-1", String userType, String status){
//
//    }


    public String getDriverDestinationLocation() {
        return driverDestinationLocation;
    }

    public void setDriverDestinationLocation(String driverDestinationLocation) {
        this.driverDestinationLocation = driverDestinationLocation;
    }

    public String getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(String driverLocation) {
        this.driverLocation = driverLocation;
    }

    public String getUId() {
        return UId;
    }

    public void setUId(String UId) {
        this.UId = UId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarRegistrationNumber() {
        return carRegistrationNumber;
    }

    public void setCarRegistrationNumber(String carRegistrationNumber) {
        this.carRegistrationNumber = carRegistrationNumber;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return email + password;
    }
}
