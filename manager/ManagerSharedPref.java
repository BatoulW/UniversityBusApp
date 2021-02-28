package com.example.secondv.Manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.secondv.Driver.Driver;
import com.example.secondv.Driver.DriverSharedPref;
import com.example.secondv.Login;
import com.example.secondv.MainActivity;

public class ManagerSharedPref {
    //the constants
    private static final String SHARED_PREF_NAME = "managersharedpref";
    private static final String KEY_Name = "keyname";
    private static final String KEY_ID = "keymId";
    private static final String KEY_phoneNo = "keyphoneNo";


    private static ManagerSharedPref mInstance;
    private static Context mCtx;

    private ManagerSharedPref(Context context) {
        mCtx = context;
    }

    public static synchronized ManagerSharedPref getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ManagerSharedPref(context);
        }
        return mInstance;
    }

    //method to let the manager login
    //this method will store the manager data in shared preferences
    public void userLogin(Manager manager) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
///################################################################################################
        editor.putInt(KEY_ID, manager.getId());
        editor.apply();
    }

    //this method will checker whether manager is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ID, -1) != -1;//
    }

    //this method will give the logged in manager
    public Manager getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
///################################################################################################
        return new Manager(sharedPreferences.getInt(KEY_ID, -1));

    }

    //this method will logout the manager
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, MainActivity.class));
    }
}
