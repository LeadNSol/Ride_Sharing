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
           "Authorization:key=AAAA3tJUmLo:APA91bFtv9AwSSfCdDTnwXoB3cBadM3-HqpTPLYCkIhl_fOpB8Nwv-CB03UYS6hd-ueqbxBsjogNVhjWPDURx6B9xzPiyq2NRtuc4DuAdKbUqZAcKDoShBP1JyhUOkurSVixu5uuwP-D"
   })
    @POST("fcm/send")
   Call<StatusResponse> sendNotification(@Body Sender sender);
}
