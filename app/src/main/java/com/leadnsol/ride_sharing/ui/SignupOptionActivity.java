package com.leadnsol.ride_sharing.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.leadnsol.ride_sharing.ui.driver.DriverSignupActivity;
import com.leadnsol.ride_sharing.R;
import com.leadnsol.ride_sharing.ui.rider.RiderSignUpActivity;

public class SignupOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_option);
    }

    public void onRiderClick(View view) {
    startActivity(new Intent(this, RiderSignUpActivity.class));
    }

    public void onDriverClick(View view) {
        startActivity(new Intent(this,DriverSignupActivity.class));


    }
}
