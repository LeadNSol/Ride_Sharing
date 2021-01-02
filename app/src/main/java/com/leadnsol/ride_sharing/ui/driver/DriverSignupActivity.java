package com.leadnsol.ride_sharing.ui.driver;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.leadnsol.ride_sharing.R;
import com.leadnsol.ride_sharing.app_common.AppConstant;
import com.leadnsol.ride_sharing.models.User;
import com.leadnsol.ride_sharing.ui.LoginActivity;

public class DriverSignupActivity extends AppCompatActivity {
    private DatabaseReference dbRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_signup);
        initViews();
    }

    private EditText etName, etEmail, etPassword, etMobile, etCarmodel, etCarRegistrationNumber, etCarcolor;

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etMobile = findViewById(R.id.et_mobile);
        etCarmodel = findViewById(R.id.et_carModel);
        etCarRegistrationNumber = findViewById(R.id.et_carRegistrationNumber);
        etCarcolor = findViewById(R.id.et_carColor);

        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.USERS);

    }

    public void onDriverClick(View view) {
        if (etName.getText().toString().isEmpty() &&
                etEmail.getText().toString().isEmpty() &&
                etPassword.getText().toString().isEmpty() &&
                etMobile.getText().toString().isEmpty() &&
                etCarmodel.getText().toString().isEmpty() &&
                etCarRegistrationNumber.getText().toString().isEmpty() &&
                etCarcolor.getText().toString().isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
        } else {

            firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            signUp();
                        }
                    }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });


        }
    }

    private void signUp() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        String firebaseUId = firebaseUser.getUid();
        User user = new User(firebaseUId, etName.getText().toString().trim(),
                etEmail.getText().toString().trim(),
                etPassword.getText().toString().trim(),
                etMobile.getText().toString().trim(),
                etCarmodel.getText().toString().trim(),
                etCarRegistrationNumber.getText().toString().trim(),
                etCarcolor.getText().toString().trim(), AppConstant.DRIVER, AppConstant.DRIVER_CANNOT_SHARE_RIDE, "", "");
        dbRef.child(firebaseUId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "You are Registered Successfully!", Toast.LENGTH_SHORT).show();
                Log.d("FirebaseUID", firebaseUId);
                Intent intent = new Intent(this, LoginActivity.class);
                //intent.putExtra("FirebaseUserId", firebaseUId);
                startActivity(intent);
            }
        });
    }
}

