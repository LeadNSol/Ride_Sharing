package com.leadnsol.ride_sharing.app_common.preference;

public interface ISharedPref {

   void setUserModel(String user);
   String getUserModel();

   void clearPreferences();
}
