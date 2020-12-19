package com.leadnsol.ride_sharing.notification.retrofit;

import com.leadnsol.ride_sharing.notification.NotificationApi;

public class RetrofitHelper implements IRetrofitHelper {

    private static RetrofitHelper mHelper;

    public static RetrofitHelper getInstance() {
        if (mHelper == null)
            mHelper = new RetrofitHelper();
        return mHelper;
    }


    @Override
    public NotificationApi getNotificationApiClient() {
        return RetrofitClient.getClient().create(NotificationApi.class);
    }
}
