package com.leadnsol.ride_sharing.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.leadnsol.ride_sharing.R;
import com.leadnsol.ride_sharing.app_common.AppConstant;
import com.leadnsol.ride_sharing.app_common.preference.SharedPrefHelper;
import com.leadnsol.ride_sharing.models.User;
import com.leadnsol.ride_sharing.ui.driver.DriverDashboardActivity;
import com.leadnsol.ride_sharing.ui.rider.RiderDashboardActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (SharedPrefHelper.getPrefHelper().getUserModel() != null) {
            User user = new Gson().fromJson(SharedPrefHelper.getPrefHelper().getUserModel(), User.class);
            if (user != null) {
                if (user.getUserType().equalsIgnoreCase(AppConstant.RIDER)) {
                    startActivity(new Intent(this, RiderDashboardActivity.class));
                } else {
                    Log.d("TAG", "onDataChange: " + user.getUId());
                    //Toast.makeText(LoginActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, DriverDashboardActivity.class));
                }
            }
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
