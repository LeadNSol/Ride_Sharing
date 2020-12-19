package com.leadnsol.ride_sharing.app_common.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper implements ISharedPref {

    private static final String PREF_KEY = "Pref key";
    private static final String PREF_USER_KEY = "Pref user key";

    private static SharedPrefHelper prefHelper;
    private SharedPreferences mSharedPreferences;

    public static SharedPrefHelper getPrefHelper() {
        if (prefHelper == null) {
            prefHelper = new SharedPrefHelper();
        }
        return prefHelper;
    }

    private SharedPrefHelper() {
        mSharedPreferences = AppContext.getContext().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
    }

    @Override
    public void setUserModel(String user) {
        mSharedPreferences.edit().putString(PREF_USER_KEY, user).apply();
    }

    @Override
    public String getUserModel() {
        return mSharedPreferences.getString(PREF_USER_KEY, null);
    }

    @Override
    public void clearPreferences() {
        mSharedPreferences.edit().clear().apply();
    }
}
