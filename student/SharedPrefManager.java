package com.example.secondv.Student;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.secondv.Login;
import com.example.secondv.MainActivity;

/**
 * Created by Belal on 9/5/2017.
 */

//here for this class we are using a singleton pattern

public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "pnuBussharedpref";
    private static final String KEY_Name = "keyname";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_District = "keydistricr";
    private static final String KEY_ID = "keyid";
    private static final String KEY_phoneNo = "keyphoneNo";
    private static final String KEY_College = "keycollege";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the student login
    //this method will store the student data in shared preferences
    public void userLogin(Student student) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
///################################################################################################
       editor.putInt(KEY_ID, student.getId());
       // editor.putString(KEY_Name, student.getName());
       // editor.putString(KEY_EMAIL, student.getEmail());
        //editor.putInt(KEY_phoneNo, student.getPhoneNo());
      //  editor.putString(KEY_District, student.getDistrict());
        //editor.putString(KEY_College, student.getCollege());
        editor.apply();
    }

    //this method will checker whether student is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ID, -1) != -1;// i think i should change email== I CHANGED TO ID
    }

    //this method will give the logged in student
    public Student getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
///################################################################################################
        return new Student(sharedPreferences.getInt(KEY_ID, -1));
      /*  return new Student(
             //   sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getString(KEY_Name, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getInt(KEY_phoneNo, -1),
                sharedPreferences.getString(KEY_College, null)
        );
        */

        /*  return new Student(
                sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getInt(KEY_phoneNo, -1),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_District, null),
                sharedPreferences.getString(KEY_College, null)
        );*/
    }

    //this method will logout the student
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, MainActivity.class));
    }
}

