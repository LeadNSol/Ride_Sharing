package com.leadnsol.ride_sharing.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.leadnsol.ride_sharing.R;
import com.leadnsol.ride_sharing.app_common.AppConstant;
import com.leadnsol.ride_sharing.app_common.preference.SharedPrefHelper;
import com.leadnsol.ride_sharing.models.User;
import com.leadnsol.ride_sharing.notification.models.Token;
import com.leadnsol.ride_sharing.ui.driver.DriverDashboardActivity;
import com.leadnsol.ride_sharing.ui.rider.RiderDashboardActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
    }


    private EditText etEmail, etPassword;
    DatabaseReference dbRef;

    private void initViews() {

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);


        dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.USERS);
    }

    private FirebaseAuth mAuth;

    public void onLoginClick(View view) {
        mAuth = FirebaseAuth.getInstance();
        if (etEmail.getText().toString().isEmpty() &&
                etPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Incorrect email or password!", Toast.LENGTH_SHORT).show();
        } else {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            signIn(email, password);
                        }
                    });


        }
    }

    private void signIn(String email, String password) {
        dbRef.orderByChild("email").equalTo(email).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getPassword() != null && user.getPassword().equalsIgnoreCase(password)) {
                            String userInString = new Gson().toJson(user);
                            SharedPrefHelper.getPrefHelper().setUserModel(userInString);

                            if (user.getUserType().equalsIgnoreCase(AppConstant.RIDER)) {
                                startActivity(new Intent(LoginActivity.this, RiderDashboardActivity.class));
                            } else {
                                Log.d("TAG", "onDataChange: " + user.getUId());
                                //Toast.makeText(LoginActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, DriverDashboardActivity.class));
                            }
                            Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "email or password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String
                    previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String
                    previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful())
                return;
            updateToken(task.getResult());
        });

        /*Task<InstallationTokenResult> token = FirebaseInstallations.getInstance().getToken(true);
        token.addOnSuccessListener(instanceIdResult ->
                updateToken(instanceIdResult.getToken()));*/
    }

    private void updateToken(String newToken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.TOKENS);
        Token token = new Token(newToken);
        assert firebaseUser != null;
        dbRef.child(firebaseUser.getUid()).setValue(token);
    }

    public void onSignUpClick(View view) {
        startActivity(new Intent(this, SignupOptionActivity.class));
    }
}


