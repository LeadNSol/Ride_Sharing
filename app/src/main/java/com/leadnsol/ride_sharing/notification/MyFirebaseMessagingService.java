package com.leadnsol.ride_sharing.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.leadnsol.ride_sharing.app_common.AppConstant;
import com.leadnsol.ride_sharing.notification.models.OreoAndAboveNotification;
import com.leadnsol.ride_sharing.notification.models.Token;
import com.leadnsol.ride_sharing.ui.driver.DriverDashboardActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String CHANNEL_ID = "RIDESHaring_channel";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("New Token",s);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful())
                return;
            updateToken(task.getResult(),user);
            Log.d("New Token",task.getResult());
        });

        /*FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener(instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            if (user != null)
                updateToken(newToken, user);
        });*/
    }

    private void updateToken(String newToken, FirebaseUser user) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(AppConstant.TOKENS);
        Token token = new Token(newToken);
        dbRef.child(user.getUid()).setValue(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && sent != null && sent.equalsIgnoreCase(firebaseUser.getUid())) {
            if (user != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoAndAboveNotification(remoteMessage);
                } else {
                    sendNotification(remoteMessage);
                }
            }
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String body = remoteMessage.getData().get("body");
        String title = remoteMessage.getData().get("title");
        String icon = remoteMessage.getData().get("icon");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        assert user != null;
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, DriverDashboardActivity.class); // will be set according to
        Bundle bundle = new Bundle();
        bundle.putString("Notification", AppConstant.DRIVER);
        intent.putExtras(bundle);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder notiBuilder = new Notification.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int j = 0;
        if (i > 0) {
            j = i;
        }
        notificationManager.notify(j, notiBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOreoAndAboveNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String body = remoteMessage.getData().get("body");
        String title = remoteMessage.getData().get("title");
        String icon = remoteMessage.getData().get("icon");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, DriverDashboardActivity.class); // will be set according to
        Bundle bundle = new Bundle();
        bundle.putString("Notification", user);
        intent.putExtras(bundle);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        OreoAndAboveNotification oreoAndAboveNotification = new OreoAndAboveNotification(getApplicationContext());
        Notification.Builder notiBuilder = oreoAndAboveNotification
                .getOreoAboveNotification(title,
                        body,
                        pendingIntent,
                        soundUri,
                        Integer.parseInt(icon));

        int j = 0;
        if (i > 0) {
            j = i;
        }
        oreoAndAboveNotification.getNotificationManager().notify(j, notiBuilder.build());
    }
}
