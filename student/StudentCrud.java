package com.example.secondv.Student;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ShareCompat;

import com.example.secondv.Bean.StudentSchedule;
import com.example.secondv.Bean.Trip;
import com.example.secondv.Bean.annuncement;
import com.example.secondv.ConnectionClass;
import com.example.secondv.Crypto;
import com.example.secondv.Driver.DriverCrud;
import com.example.secondv.R;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class StudentCrud {

    ConnectionClass c = new ConnectionClass();
    Connection conn;
    String z;
    boolean isSuccess;

    public void readStudentInfo(int sId , Student student){
        try
        {
            conn = c.connection();        // Connect to database
            if (conn == null)
            {
              z=  "Check your internet connection";
            }
            else
            {
                String query = "select * from student where sId=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1,sId);
                ResultSet rs = pstmt.executeQuery();
                if(rs.next())
                {
                    isSuccess=true;
///################################################################################################
                    //setting student account info gotten from db
                    student.setName(rs.getString("name"));
                    student.setEmail(rs.getString("email"));
                    student.setDistrict(rs.getString("district"));
                    student.setCollege(rs.getString("collage"));
                    student.setLongitude(rs.getString("longitude"));
                    student.setLatitude(rs.getString("latitude"));
                    student.setPhoneNo(rs.getInt("phoneNo"));
///################################################################################################
                    conn.close();
                }
                else
                {
                    isSuccess = false;
                }
            }
        }
        catch (Exception ex)
        {
            isSuccess = false;
            z = ex.getMessage();
        }
    }

    //insertStudent : BatoulV6 and Fatma's Code V7 - to sign up new student.
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void insertStudent(EditText sName, EditText sEmail, EditText password, EditText confirm, EditText phoneNo, String district, Double latitude
            , Double longitude, EditText college, Context ctx) {

        String name = sName.getText().toString();
        String email = sEmail.getText().toString();
        String pass = password.getText().toString();
        String confPass= confirm.getText().toString();
        String coll = college.getText().toString();
        int phone = Integer.parseInt(phoneNo.getText().toString());
        String stringPhone = phoneNo.getText().toString();
        // validation
        if (TextUtils.isEmpty(name)) {
            sName.setError("Please enter Full name");
            sName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            sEmail.setError("Please enter your email");
            sEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            sEmail.setError("Enter a valid email");
            sEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            password.setError("Enter a password");
            password.requestFocus();
            return;
        } if (!pass.equals(confPass)){
            confirm.setError("Password does not match!");
            confirm.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(coll)) {
            college.setError("Enter a college");
            college.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(stringPhone)) {
            phoneNo.setError("Enter a phone number");
            phoneNo.requestFocus();
            return;
        }

        if( latitude == 0 && longitude == 0 ){ //Fatma
            Toast.makeText(ctx, "Please pick your home location.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            conn = c.connection();  // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
                Toast.makeText(ctx, z, Toast.LENGTH_SHORT).show();

            } else {
///################################################################################################
                String query = " insert into student (name,email, password, phoneNo, district, longitude, latitude, collage)"
                        + " values (?, ?, ?, ?, ?, ?, ?, ?)";
                Crypto data = new Crypto();
                byte[] md5InBytes = data.digest(pass.getBytes(UTF_8));
                String cPass = data.bytesToHex(md5InBytes);

                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, cPass);
                pstmt.setInt(4, phone);
                pstmt.setString(5, district);
                pstmt.setDouble(6, longitude);
                pstmt.setDouble(7, latitude);
                pstmt.setString(8, coll);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    z = "successful";
                    isSuccess = true;
                    Log.i("Insert new? ", "Yes");
///################################################################################################
                    int returnedId = studentId(conn, email);//pstmt.getResultSet().getString(2));
///################################################################################################
                    //creating a new student object //
                    Student student = new Student(returnedId);// only id
                    //storing the student in shared preferences
                    SharedPrefManager.getInstance(ctx).userLogin(student);
///################################################################################################
                    Intent it = new Intent(ctx, StudentMainPage.class);
                    ctx.startActivity(it);

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
    public  int updateStudentInfo(int sId , EditText sName, EditText sEmail, EditText phoneNo, String district, Double latitude
            , Double longitude, EditText college, Context ctx) {
        String name = sName.getText().toString();
        String email = sEmail.getText().toString();
     //   String pass = password.getText().toString();
        String coll = college.getText().toString();
        int phone = Integer.parseInt(phoneNo.getText().toString());
        String stringPhone = phoneNo.getText().toString();

        int value=0;

        // validation
        if (TextUtils.isEmpty(name)) {
            sName.setError("Please enter Full name");
            sName.requestFocus();
            return 0;
        }

        if (TextUtils.isEmpty(email)) {
            sEmail.setError("Please enter your email");
            sEmail.requestFocus();
            return 0;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            sEmail.setError("Enter a valid email");
            sEmail.requestFocus();
            return 0;
        }

       /* if (TextUtils.isEmpty(pass)) {
            password.setError("Enter a password");
            password.requestFocus();
            return 0;
        }*/

        if (TextUtils.isEmpty(coll)) {
            college.setError("Enter a college");
            college.requestFocus();
            return 0;
        }

        if (TextUtils.isEmpty(stringPhone)) {
            phoneNo.setError("Enter a phone number");
            phoneNo.requestFocus();
            return 0;
        }

        if( latitude == 0 && longitude == 0 ){ //Fatma
            Toast.makeText(ctx, "Please pick your home location.", Toast.LENGTH_LONG).show();
            return 0;
        }

        try {
            conn = c.connection();  // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
                Toast.makeText(ctx, z, Toast.LENGTH_SHORT).show();

            } else {
///################################################################################################
                String query = " update student set name = ? , email=?, collage=?, district=?, longitude=?, latitude=? , phoneNo=? where sId = ?";
                                                    // 1        //2     //3           //4       //5         //6             //7           //8
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, coll);
                pstmt.setString(4, district);
                pstmt.setDouble(5, longitude);
                pstmt.setDouble(6, latitude);
                pstmt.setInt(7, phone);

                pstmt.setInt(8, sId);


                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    value = 1;
                    isSuccess = true;
                    Log.i("update student? ", "Yes");

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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void checkLogin(EditText userName, EditText password, final Context ctx) {
///################################################################################################
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
                conn = c.connection();// Connect to database
                if (conn == null) {
                    z = "Check Your Internet Access!";
                } else {
                    String query = "select * from student where email=? and password=?";// replaced sId by email

                    Crypto data = new Crypto();
                    byte[] md5InBytes = data.digest(passwordd.getBytes(UTF_8));
                    String cPass = data.bytesToHex(md5InBytes);

                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, usernam);
                    pstmt.setString(2, cPass);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        z = "successful";
                        isSuccess = true;
                        Log.i("logged in Student ", "Yes");
                        Log.i("email retrieved ", usernam);
///################################################################################################
                        int returnedId = studentId(conn, usernam);
                        //creating a new student object
                        Student student = new Student(returnedId);
                        //storing the student in shared preferences
                        SharedPrefManager.getInstance(ctx).userLogin(student);
///################################################################################################
                        Intent it = new Intent(ctx, StudentMainPage.class);
                        ctx.startActivity(it);

                        conn.close();
                    } else {
                        // prompt student' wrong username or password
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
    public int studentId(Connection conn, String email){// method to return sId-used in insert method
        int idReturned = 0;
    try {
        String query = "select sId from student where email=? ";
        PreparedStatement pstmt2 = conn.prepareStatement(query);
        pstmt2.setString(1, email);
        ResultSet rs = pstmt2.executeQuery();
        if (rs.next()) {
            idReturned = rs.getInt(1);
            Log.i("get id number ", String.valueOf(idReturned));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
        return idReturned;
    }
    public StudentSchedule goScheduleDisplay(int sId, String tripDay, String tripType) {
        StudentSchedule goSchedule = new StudentSchedule("", "", "", "", "", "");
        try {
            conn = c.connection(); // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                String query = "select studentTakesTrip.dayName, trip.tripTime, trip.busNo, trip.tripType , driver.name, driver.phoneNo\n" +
                        "from studentTakesTrip inner join trip on studentTakesTrip.tId = trip.tId inner join driver on trip.dId = driver.dId\n" +
                        "where studentTakesTrip.sId = ? and studentTakesTrip.dayName=? and trip.tripType= ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, sId);
                pstmt.setString(2, tripDay);
                pstmt.setString(3, tripType);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    isSuccess = true;
                    Log.i("schedule info ", "Yes");
///################################################################################################
                    goSchedule.setMday(rs.getString("dayName"));
                    goSchedule.setmDriverName(rs.getString("name"));
                    goSchedule.setmDriverPhone(rs.getString("phoneNo"));
                    goSchedule.setmBusNumber(rs.getString("busNo"));
                    goSchedule.setmTripTime(rs.getString("tripTime"));
                    goSchedule.setTripType(rs.getString("tripType"));
 ///################################################################################################
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

        return goSchedule;
    }
    public StudentSchedule returnScheduleDisplay(int sId, String tripDay, String tripType) {
        StudentSchedule returnSchedule = new StudentSchedule("", "", "", "", "", "");
        try {
            conn = c.connection(); // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                String query = "select studentTakesTrip.dayName, trip.tripTime, trip.busNo, trip.tripType , driver.name, driver.phoneNo\n" +
                        "from studentTakesTrip inner join trip on studentTakesTrip.tId = trip.tId inner join driver on trip.dId = driver.dId\n" +
                        "where studentTakesTrip.sId = ? and studentTakesTrip.dayName=? and trip.tripType= ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, sId);
                pstmt.setString(2, tripDay);
                pstmt.setString(3, tripType);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    isSuccess = true;
                    Log.i("schedule info ", "Yes");
///################################################################################################
                    returnSchedule.setMday(rs.getString("dayName"));
                    returnSchedule.setmDriverName(rs.getString("name"));
                    returnSchedule.setmDriverPhone(rs.getString("phoneNo"));
                    returnSchedule.setmBusNumber(rs.getString("busNo"));
                    returnSchedule.setmTripTime(rs.getString("tripTime"));
                    returnSchedule.setTripType(rs.getString("tripType"));
///################################################################################################
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

        return returnSchedule;
    }
    public int cancelCertainTrip(int sId, String dayName, String tripType, Context ctx){
    int value=0;
        try {
            conn = c.connection();  // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
                Toast.makeText(ctx, z, Toast.LENGTH_SHORT).show();

            } else {
///################################################################################################
                int tId = sTripId(conn,sId, tripType);
                String query = " DELETE FROM studentTakesTrip WHERE sId = ? and dayName=? and tId=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, sId);
                pstmt.setString(2, dayName);
                pstmt.setInt(3, tId);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    value = 1;
                    isSuccess = true;
                    Log.i("delete student's trip? ", "Yes");
///################################################################################################
                    conn.close();
                } else {
                    Toast.makeText(ctx, "Something goes wrong!", Toast.LENGTH_SHORT).show();
                    Log.i("delete student? ", "No");
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return value;
    }
    public int sTripId(Connection conn,int sId, String tripType){
        int tId=0;
        try {
            String query = "select distinct studentTakesTrip.tId from studentTakesTrip \n" +
                    "inner join trip on studentTakesTrip.tId = trip.tId\n" +
                    "where studentTakesTrip.sId= ? and trip.tripType=?";
            PreparedStatement pstmt2 = conn.prepareStatement(query);
            pstmt2.setInt(1,sId);
            pstmt2.setString(2, tripType);
            ResultSet rs = pstmt2.executeQuery();
            if (rs.next()) {
                tId = rs.getInt(1);
                Log.i("get trip id number ", String.valueOf(tId));
            } else {
                Log.i("get trip id number ? ", "No");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tId;
    }
    // Start of Fatma code - v9
    public ArrayList<StudentSchedule> getOldSchedule(int sId) {

        //ArrayList of student day and time schedule
        ArrayList<StudentSchedule> OldSchedule = new ArrayList<StudentSchedule>();

        try {
            conn = c.connection(); // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                String query = "select studentTakesTrip.dayName, trip.tripTime from studentTakesTrip inner join trip on studentTakesTrip.tId = trip.tId where studentTakesTrip.sId = ? ";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, sId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    isSuccess = true;
                    do {
                        StudentSchedule schedule = new StudentSchedule(rs.getString("dayName"), rs.getString("tripTime"));
                        OldSchedule.add(schedule);
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

        return OldSchedule;
    }
    public ArrayList<Trip> getTripForStudent(int sId) {

        ArrayList<Trip> TripsForStudent = new ArrayList<Trip>();

        //will get the tripId and time (trip table) for the busNo(bus table) of the student's district (student table)
        try {
            conn = c.connection(); // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                String query = "select trip.tId, trip.tripTime from bus inner join trip on trip.busNo = bus.busNo where bus.district = (select district from student where sId = ?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, sId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    isSuccess = true;
                    do {
                        Trip studentTrips = new Trip(rs.getInt("tId"), rs.getString("tripTime"));
                        TripsForStudent.add(studentTrips);
                    } while (rs.next());
                    conn.close();
                    return TripsForStudent;
                } else {
                    z = "Invalid Credentials!";
                    isSuccess = false;
                }
            }
        } catch (Exception ex) {
            isSuccess = false;
            z = ex.getMessage();
        }
        return TripsForStudent;
    }
    public void StoreSchedule(int StudentId, String District, ArrayList<StudentSchedule> newSchedule) {

        // Log.i("Check", StudentId+": "+sunGO+","+monGO+","+tueGO+","+wedGO+","+thuGO+","+sunReturn+","+monReturn+","
        // +tueReturn+","+wedReturn+","+thuReturn);


        //delete all student's scheduled trips (if exist) from studentTakesTrip table
        try {
            conn = c.connection(); // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                String query = "if EXISTS(select * from studentTakesTrip where sId = ?) \n" +
                        "delete from studentTakesTrip where sId = ?";

                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, StudentId);
                pstmt.setInt(2, StudentId);

                int rs = pstmt.executeUpdate();

                if (rs != 0) {
                    isSuccess = true;
                    Log.i("Delete", rs + " rows");
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

        //get all trips for student
        ArrayList<Trip> TripsForStudent = this.getTripForStudent(StudentId);


        // insert new records (matching the time student select with trips time) in studentTakesTrip table for this student id
        Trip trip = new Trip();

        for (int i = 0; i < newSchedule.size(); i++) {
            String Day = newSchedule.get(i).getMday();
            String Time = newSchedule.get(i).getmTripTime();
            String query = "";

            if(!Time.equals("null")) {
                int tId = trip.TripIdForTime(Time, TripsForStudent);
                query = "insert into studentTakesTrip (sId,tId,dayName) values (?, ?, ?)";
                this.insertTripForStudent(query, StudentId, tId, Day);
                //   Log.e("check", Day + " " + Time+" "+tId); //****************

            }

   /*         if (!Time.equals(null)) {
                switch (Day) {
                    case "Sunday":
                        query = "insert into studentTakesTrip (sId,tId,dayName) values (?, ?, ?)";
                        this.insertTripForStudent(query, StudentId, tId, Day);
                        break;
                    case "Monday":
                        query = "insert into studentTakesTrip (sId,tId,dayName) values (?, ?, ?)";
                        this.insertTripForStudent(query, StudentId, tId, Day);
                        break;
                    case "Tuesday":
                        if (newSchedule.get(i).isGoTrip(Time)) {
                            // tueGoSpinner.setSelection(goAdapter.getPosition(Time));
                        } else {
                            // tueReturnSpinner.setSelection(returnAdapter.getPosition(Time));
                        }
                        break;
                    case "Wednesday":
                        if (newSchedule.get(i).isGoTrip(Time)) {
                            //  wedGoSpinner.setSelection(goAdapter.getPosition(Time));
                        } else {
                            //  wedReturnSpinner.setSelection(returnAdapter.getPosition(Time));
                        }
                        break;
                    case "Thursday":
                        if (newSchedule.get(i).isGoTrip(Time)) {
                            //  thuGoSpinner.setSelection(goAdapter.getPosition(Time));
                        } else {
                            //  thuReturnSpinner.setSelection(returnAdapter.getPosition(Time));
                        }
                        break;
                }


            }*/
        }
    }
    public void insertTripForStudent(String query , int sId, int tId, String dayName) {

        try {
            conn = c.connection();  // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";

            } else {
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, sId);
                pstmt.setInt(2, tId);
                pstmt.setString(3, dayName);

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
        } catch(SQLException e){
            e.printStackTrace();
        }


    }
    // end of Fatma code - v9

    //Fatma 13
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int tripToTrack(int sId) throws ParseException {
        DriverCrud driverCrud = new DriverCrud();
        String tripTime = driverCrud.getTripTime();
        String tripDay = driverCrud.getTripDay();
        int tripId = 0;
        try {
            conn = c.connection(); // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                String query = "select trip.tId from trip inner join studentTakesTrip on trip.tId = studentTakesTrip.tId " +
                        "where studentTakesTrip.sId =? and studentTakesTrip.dayName =? and trip.tripTime =?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, sId);
                pstmt.setString(2,tripDay);
                pstmt.setString(3,tripTime);
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

///////////////////////////////////////////////////////////////////////////////////////////////////////
    //Start of rawan's code for sending announcement V14
    public ArrayList<Integer> getStudentbusNum(int sId){
        ArrayList<Integer> BusNoList = new ArrayList<Integer>();

        try {
            conn = c.connection();        // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                // Change below query according to your own database.
                String query = "select DISTINCT trip.busNo from \n" +
                        "studentTakesTrip inner join trip on studentTakesTrip.tId=trip.tId\n" +
                        "where sId=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, sId);
                ResultSet rs = pstmt.executeQuery();
                if(rs.next()) {
                    Log.i("get available BusNO: ", "Yes" + rs.getInt("busNo"));
                    do {
                        BusNoList.add(rs.getInt("busNo"));
                    } while (rs.next());
                }else{
                    Log.i("get available BusNO:", "No retrieved data" + rs.getInt("busNo"));
                }
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BusNoList;

    }

    public ArrayList<annuncement>  getAnnouncement(int sId ) {
        //ArrayList of announcment
        ArrayList<annuncement> annuncement = new ArrayList<annuncement>();

        try {
            conn = c.connection(); // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                String query = "select * from sAnnouncement where busNo in (select DISTINCT trip.busNo from \n" +
                        "studentTakesTrip inner join trip on studentTakesTrip.tId=trip.tId\n" +
                        "where sId=?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, sId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    isSuccess = true;
                    do {
                        Log.i("Ann yes:", rs.getString("announcementText")+" "+rs.getString("dateAndTime"));
                        com.example.secondv.Bean.annuncement u = new annuncement(rs.getString("announcementText"),rs.getString("dateAndTime"));
                        annuncement.add(u);
                    } while (rs.next());
                    conn.close();
                    return annuncement;

                } else {
                    z = "Invalid Credentials!";
                    isSuccess = false;
                }
            }
        } catch (Exception ex) {
            isSuccess = false;
            z = ex.getMessage();
        }

        annuncement.clear();
        return annuncement;
    }

    //end of rawan's code for sending announcement    V14

}
