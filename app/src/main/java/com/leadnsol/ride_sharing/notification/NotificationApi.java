package com.leadnsol.ride_sharing.notification;

import com.leadnsol.ride_sharing.notification.models.Sender;
import com.leadnsol.ride_sharing.notification.models.StatusResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface NotificationApi {
   @Headers({
           "Content-Type:application/json",
           "Authorization:key=AAAARLVq4cM:APA91bHiajt2M-D_aoBujzr2R_CTJ4wYPNrLGu9dsQOTzI4S0K1VshQfeVLIzgk8EF-i9GfNBVEkDSquH3d3UVla1Dr_FWDHf7ksmTPJ67aScVaDVqkulCPEOscTMzTcSwi6RLbLnzZz"
   })
    @POST("fcm/send")
   Call<StatusResponse> sendNotification(@Body Sender sender);
}
