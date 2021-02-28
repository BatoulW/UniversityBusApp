package com.example.secondv.Driver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.example.secondv.Bean.Passengers;
import com.example.secondv.Bean.StudentSchedule;
import com.example.secondv.Bean.Trip;
import com.example.secondv.Bean.annuncement;
import com.example.secondv.ConnectionClass;
import com.example.secondv.Crypto;
import com.example.secondv.Manager.Manager;
import com.example.secondv.Student.SharedPrefManager;
import com.example.secondv.Student.Student;
import com.example.secondv.Student.StudentMainPage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DriverCrud {


    ConnectionClass c = new ConnectionClass();
    Connection conn;
    String z;
    boolean isSuccess;
    boolean goUni;

    public void readDriverInfo(int dId, Driver driver, Manager manager) {

        try {
            conn = c.connection();// Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                String query = "select driver.name as dName, driver.phoneNo as dPhoneNo, bus.busNo, bus.district, manager.name as mName, manager.phoneNo as mPhoneNo\n" +
                        "from driver inner join manager on driver.mId = manager.mId inner join bus on bus.dId = driver.dId  where driver.dId=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, dId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    isSuccess = true;
///################################################################################################
                    //setting driver account info gotten from db
                    driver.setName(rs.getString("dname"));
                    driver.setPhoneNo(rs.getInt("dPhoneNo"));
                    driver.setBusNo(rs.getInt("busNo"));
                    driver.setDistrict(rs.getString("district"));
                    // driver.setmName(rs.getString("mName"));
                    manager.setName(rs.getString("mName"));
                    manager.setPhoneNo(rs.getInt("mPhoneNo"));
///################################################################################################
                    conn.close();
                } else {
                    isSuccess = false;
                }
            }
        } catch (Exception ex) {
            isSuccess = false;
            z = ex.getMessage();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void checkLogin(EditText userName, EditText password, final Context ctx) {
        String usernam = userName.getText().toString();
        String passwordd = password.getText().toString();

        if (TextUtils.isEmpty(usernam)) {
            userName.setError("Please enter your username");
            userName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(passwordd)) {
            password.setError("Please enter your password");
            password.requestFocus();
            return;
        } else {
            try {
                conn = c.connection(); // Connect to database
                if (conn == null) {
                    z = "Check Your Internet Access!";
                } else {
                    String query = "select * from driver where dId=? and password=?";

                    Crypto data = new Crypto();
                    byte[] md5InBytes = data.digest(passwordd.getBytes(UTF_8));
                    String cPass = data.bytesToHex(md5InBytes);

                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, usernam);// means id
                    pstmt.setString(2, cPass);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        z = "successful";
                        isSuccess = true;
                        Log.i("logged in driver ", "Yes");
///################################################################################################
                        //creating a new Driver object
                        Driver driver = new Driver(Integer.parseInt(usernam));// means id
                        //storing the driver in shared preferences
                        DriverSharedPref.getInstance(ctx).userLogin(driver);
///################################################################################################
                        Intent it = new Intent(ctx, DriverMainPage.class);
                        ctx.startActivity(it);

                        conn.close();
                    } else {
                        AlertDialog.Builder dialog=new AlertDialog.Builder(ctx);
                        dialog.setMessage("Wrong Email or Password..\nTry again..");
                        dialog.setTitle("Error Message!");
                        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ctx,
                                        "You clicked on OK", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                        AlertDialog alertDialog=dialog.create();
                        alertDialog.show();
                        z = "Invalid Credentials!";
                        isSuccess = false;
                    }
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = ex.getMessage();
            }
        }


    }
    public int updateDriverNamePhoneNo(int dId, EditText dName, EditText dPhoneNo, Context ctx) {
        String name = dName.getText().toString();
        int phoneNo= Integer.parseInt(dPhoneNo.getText().toString());
        int value=0;
        try {
            conn = c.connection();  // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
                Toast.makeText(ctx, z, Toast.LENGTH_SHORT).show();

            } else {
///################################################################################################
                String query = " update driver set name = ? ,  phoneNo=? where dId = ?";

                PreparedStatement pstmt = conn.prepareStatement(query);

                pstmt.setString(1, name);
                pstmt.setInt(2, phoneNo);
                pstmt.setInt(3, dId);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    value = 1;
                    isSuccess = true;
                    Log.i("update driver? ", "Yes");

///################################################################################################
                    conn.close();
                } else {

                    z = "Invalid Credentials!";
                    isSuccess = false;
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return value;
    }

    public ArrayList<Passengers> displayTripSchedule(String tripTime, String dayName , int dId){
        //  String tripTime=time.getText().toString();
        //String dayName=day.getText().toString();
        ArrayList<Passengers> passengersList = new ArrayList<Passengers>();
        // Student s = new Student();
        try {
            conn = c.connection();        // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                // Change below query according to your own database.
                String query = "select student.name as sName , student.phoneNo as sPhoneNo from student\n" +
                        "inner join studentTakesTrip on student.sId= studentTakesTrip.sId\n" +
                        "inner join trip on trip.tId=studentTakesTrip.tId\n" +
                        "where trip.tripTime=? and studentTakesTrip.dayName=? and trip.dId=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, tripTime);
                pstmt.setString(2, dayName);
                pstmt.setInt(3,dId); //new edit 13
                ResultSet rs = pstmt.executeQuery();

                if(rs.next()) {
                    do {
                        Passengers p = new Passengers();
                        Log.i("get students list", "Success - Passenger: " + rs.getString("sName"));
                        p.setID(String.valueOf(rs.getRow()));
                        p.setStudentName(rs.getString("sName"));
                        p.setStudentPhone(rs.getString("sPhoneNo"));
                        passengersList.add(p);
                    } while (rs.next());

                }else{

                    Log.i("students for this trip?", "No");
                }
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passengersList;
    }

    // no need for now since login will be with id itself
    private int driverId(Connection conn, String id) {
        int dId = 0;
        try {
            String query = "select dId from driver where phoneNo=? ";
            PreparedStatement pstmt2 = conn.prepareStatement(query);
            pstmt2.setString(1, id);
            ResultSet rs = pstmt2.executeQuery();
            if (rs.next()) {
                dId = rs.getInt(1);
                Log.i("get id number ", String.valueOf(dId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dId;
    }

    // no need for now since insertion will be on db admin side
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void insertDriver(EditText dName, EditText password, EditText phoneNo, EditText mId) {
        String name = dName.getText().toString();
        String pass = password.getText().toString();
        int phone = Integer.parseInt(phoneNo.getText().toString());
        int manId = Integer.parseInt(mId.getText().toString());

        try {
            conn = c.connection();// Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                String query = " insert into driver (name, password, phoneNo, mId)"
                        + " values (?, ?, ?, ?)";

                Crypto data = new Crypto();
                byte[] md5InBytes = data.digest(pass.getBytes(UTF_8));
                String cPass = data.bytesToHex(md5InBytes);

                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, cPass);
                pstmt.setInt(3, phone);
                pstmt.setInt(4, manId);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    z = "successful";
                    isSuccess = true;
                    Log.i("Insert new? ", "Yes");
                    conn.close();
                } else {

                    z = "Invalid Credentials!";
                    isSuccess = false;
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Sawsan Edit Code **/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Location> getTripLocations(int dId) throws ParseException {
        String time = getTripTime();
        String day = getTripDay();
        ArrayList<Location> locationsS = new ArrayList<>();

        try {
            conn = c.connection();// Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {

                String query ="select student.latitude , student.longitude from student inner join studentTakesTrip on student.sId = studentTakesTrip.sId inner join " +
                        "trip on trip.tId = studentTakesTrip.tId  where studentTakesTrip.dayName = ? and trip.tripTime = ? and  trip.dId = ?";

                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, day);
                pstmt.setString(2, time);
                pstmt.setInt(3, dId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    isSuccess = true;
                    do {
                        Trip trip = new Trip();
                        Location loc = new Location("");
                        trip.setLongitude((double) rs.getFloat("longitude"));
                        trip.setLatitude((double) rs.getFloat("latitude"));
                        loc.setLongitude(trip.getLongitude());
                        loc.setLatitude(trip.getLatitude());
                        locationsS.add(loc);
                        Log.e("location = ",rs.getFloat("latitude")+","+rs.getFloat("longitude"));
                    } while (rs.next());
                    conn.close();
                } else {
                    z = "Invalid Credentials!";
                    isSuccess = false;
                }
            }
        } catch (Exception ex) {
            isSuccess = false;
            z = ex.getMessage();
        }

        return locationsS;
    }

    //New Edit
    public String getTripDay(){
        // displaying full-day name
        Date now = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
        String day = simpleDateformat.format(now);
        return day;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getTripTime() throws ParseException {
        Date time = new Date();
        String strTimeFormat = "hh:mm:ss";

        //Current time
        // Get current date time
        LocalTime currentDateTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
        String formattedTime = currentDateTime.format(formatter);

        Date d = new SimpleDateFormat(strTimeFormat).parse(formattedTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.DATE, 1);

        //For go trips
        Date timeFormat1 = new SimpleDateFormat(strTimeFormat).parse("05:00:00");
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(timeFormat1);
        calendar1.add(Calendar.DATE, 1);

        Date timeFormat2 = new SimpleDateFormat(strTimeFormat).parse("08:00:00");
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(timeFormat2);
        calendar2.add(Calendar.DATE, 1);

        Date timeFormat3 = new SimpleDateFormat(strTimeFormat).parse("11:00:00");
        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTime(timeFormat3);
        calendar3.add(Calendar.DATE, 1);

        Date x = calendar.getTime();
        if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
            //checkes whether the current time is between 05:00:00 and 08:00:00.
            goUni = true;
            return "8:00"; // New Edit
        } else if(x.after(calendar2.getTime()) && x.before(calendar3.getTime())){
            //checkes whether the current time is between 08:00:00 and 11:00:00.
            goUni = true;
            return "11:00";
        }

        //For return trips
        Date timeFormat4 = new SimpleDateFormat(strTimeFormat).parse("12:45:00");
        Calendar calendar4 = Calendar.getInstance();
        calendar4.setTime(timeFormat4);
        calendar4.add(Calendar.DATE, 1);

        Date timeFormat5 = new SimpleDateFormat(strTimeFormat).parse("14:45:00");
        Calendar calendar5 = Calendar.getInstance();
        calendar5.setTime(timeFormat5);
        calendar5.add(Calendar.DATE, 1);

        Date timeFormat6 = new SimpleDateFormat(strTimeFormat).parse("16:45:00");
        Calendar calendar6 = Calendar.getInstance();
        calendar6.setTime(timeFormat6);
        calendar6.add(Calendar.DATE, 1);

        Date timeFormat7 = new SimpleDateFormat(strTimeFormat).parse("18:30:00");
        Calendar calendar7 = Calendar.getInstance();
        calendar7.setTime(timeFormat7);
        calendar7.add(Calendar.DATE, 1);

        Date y = calendar.getTime();
        if (y.after(calendar4.getTime()) && x.before(calendar5.getTime())) {
            //checkes whether the current time is between 12:45:00 and 14:45:00.
            goUni = false;
            return "13:00";
        } else if(y.after(calendar5.getTime()) && y.before(calendar6.getTime())){
            //checkes whether the current time is between 14:45:00 and 16:45:00.
            goUni = false;
            return "15:00";
        } else if(y.after(calendar6.getTime()) && y.before(calendar7.getTime())){
            //checkes whether the current time is between 16:45:00 and 18:30:00.
            goUni = false;
            return "17:00";
        }

        return "";
    }

    //Fatma 13
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getTripId(int dId) throws ParseException {

        int tripId = 0;
        String tripTime = getTripTime();

        try {
            conn = c.connection(); // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {

                String query ="select tId from trip where dId=? and tripTime =?";

                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, dId);
                pstmt.setString(2, tripTime);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    isSuccess = true;
                    tripId = rs.getInt("tId");
                    conn.close();
                    return tripId;
                } else {
                    z = "Invalid Credentials!";
                    isSuccess = false;
                }
            }
        } catch (Exception ex) {
            isSuccess = false;
            z = ex.getMessage();
        }

        return tripId;
    }
    //end of Fatma 13 edit

    public boolean isUnigo(){
        return goUni;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //Start of rawan's code V14
    public void readDriverInfo(int dId, Driver driver) {
        try {
            conn = c.connection();// Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                String query = "select driver.name, driver.phoneNo,  trip.busNo from driver inner join trip on driver.dId = trip.dId  where driver.dId=?";

                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, dId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    isSuccess = true;
                    //setting driver account info gotten from db
                    driver.setName(rs.getString("name"));
                    driver.setPhoneNo(rs.getInt("phoneNo"));
                    driver.setBusNo(rs.getInt("busNo"));
                    conn.close();
                } else {
                    isSuccess = false;
                }
            }
        } catch (Exception ex) {
            isSuccess = false;
            z = ex.getMessage();
        }
    }

    public ArrayList<annuncement> getAnnouncement(int busnum) {
        //ArrayList of announcment
        ArrayList<annuncement> annuncement = new ArrayList<annuncement>();
        Log.i("bus no is:", busnum+"");
        try {
            conn = c.connection(); // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                String query = "select * from dAnnouncement where busNo='" + busnum + "'";
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    isSuccess = true;
                    do {
                        Log.i("Ann yes:", rs.getString("announcementText"));
                        com.example.secondv.Bean.annuncement u = new annuncement(rs.getString("announcementText"),rs.getString("dateAndTime"));
                        annuncement.add(u);
                    } while (rs.next());

                    conn.close();
                } else {
                    z = "Invalid Credentials!";
                    isSuccess = false;
                }
            }
        } catch (Exception ex) {
            isSuccess = false;
            z = ex.getMessage();
        }

        return annuncement;
    }
    ///ُend of rawan's code     V!4
}


