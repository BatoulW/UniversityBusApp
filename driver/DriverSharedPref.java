package com.example.secondv.Driver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.secondv.Login;
import com.example.secondv.MainActivity;


public class DriverSharedPref {

    //the constants
    private static final String SHARED_PREF_NAME = "driversharedpref";
    private static final String KEY_Name = "keyname";
    private static final String KEY_ID = "keydId";
    private static final String KEY_phoneNo = "keyphoneNo";


    private static DriverSharedPref mInstance;
    private static Context mCtx;

    private DriverSharedPref(Context context) {
        mCtx = context;
    }

    public static synchronized DriverSharedPref getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DriverSharedPref(context);
        }
        return mInstance;
    }

    //method to let the driver login
    //this method will store the driver data in shared preferences
    public void userLogin(Driver driver) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
///################################################################################################
        editor.putInt(KEY_ID, driver.getId());
        editor.apply();
    }

    //this method will checker whether driver is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ID, -1) != -1;//
    }

    //this method will give the logged in driver
    public Driver getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
///################################################################################################
        return new Driver(sharedPreferences.getInt(KEY_ID, -1));

    }

    //this method will logout the driver
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, MainActivity.class));
    }
}
