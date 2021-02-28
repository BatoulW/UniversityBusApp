package com.example.secondv.Manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.example.secondv.Bean.DriverInfoBean;
import com.example.secondv.Bean.Passengers;
import com.example.secondv.ConnectionClass;
import com.example.secondv.Crypto;
import com.example.secondv.Driver.Driver;
import com.example.secondv.Driver.DriverMainPage;
import com.example.secondv.Driver.DriverSharedPref;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ManagerCrud {

    ConnectionClass c = new ConnectionClass();
    Connection conn;
    String z;
    boolean isSuccess;


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
                conn = c.connection();        // Connect to database
                if (conn == null) {
                    z = "Check Your Internet Access!";
                } else {
                    // Change below query according to your own database.
                    String query = "select * from manager where mId=? and password=?";

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
                        Log.i("logged in manager ", "Yes");
                        ///################################################################################################
                        //creating a new Manager object
                        Manager manager = new Manager(Integer.parseInt(usernam));
                        //storing the student in shared preferences
                        ManagerSharedPref.getInstance(ctx).userLogin(manager);
///################################################################################################
                        Intent it = new Intent(ctx, ManagerMainPage.class);
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
    public int updateManager(int mId, EditText dName, EditText dPhoneNo, Context ctx) {
        String name = dName.getText().toString();
        String phoneNo = dPhoneNo.getText().toString();
        int value=0;

        // validation
        if (TextUtils.isEmpty(name)) {
            dName.setError("Please enter your Full name");
            dName.requestFocus();
            return 0;
        }

        if (TextUtils.isEmpty(phoneNo)) {
            dPhoneNo.setError("Please enter your phone number");
            dPhoneNo.requestFocus();
            return 0;
        }

        try {
            conn = c.connection();  // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
                Toast.makeText(ctx, z, Toast.LENGTH_SHORT).show();

            } else {
///################################################################################################
                String query = " update manager set name = ? , phoneNo=? where mId = ?";

                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, phoneNo);
                pstmt.setInt(3, mId);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    value = 1;

                    Log.i("update manager? ", "Yes");

///################################################################################################
                    conn.close();
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return value;
    }
    public void readManagerInfo(int mId, Manager manager) {
        try {
            conn = c.connection(); // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                String query = "select * from manager where mId=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, mId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    isSuccess = true;
                    ///################################################################################################
                    //setting manger values gotten form db
                    manager.setName(rs.getString("name"));
                    manager.setPhoneNo(rs.getInt("phoneNo"));
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

//*********************************************************************************************************
//Ebtihal Codes to retrieve data for ManagerTripsSchedule

    //populate bus numbers from the data base   -Ebtihal
    public List<Integer> populateBusNumbers(int mId) {
        List<Integer> busNoList = new ArrayList<Integer>();

        try {
            conn = c.connection();        // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                // Change below query according to your own database.
                String query = "select busNo from bus where mId=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, mId);
                ResultSet rs = pstmt.executeQuery();
                if(rs.next()) {
                    do {
                        Log.i("get Bus numbers ", "Yes" + rs.getInt("busNo"));
                        busNoList.add(rs.getInt("busNo"));
                    } while (rs.next());
                }else{
                    busNoList.add(0);//set bus number to 0 if the manager is not Managing to any bus    -Ebtihal
                    Log.i("get Bus numbers ", "No retrieved data" + rs.getInt("busNo"));
                }
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return busNoList;
    }

    //get driver info for each trip         -Ebtihal
    public Driver getTripDriver(String tripTime, int busNum){
        Driver d= new Driver();
        try {
            conn = c.connection();        // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                // Change below query according to your own database.
                String query = "select distinct driver.name, driver.phoneNo, bus.district\n" +
                        "from driver inner join trip on driver.dId= trip.dId\n" +
                        "inner join bus on bus.busNo= trip.busNo\n" +
                        "where trip.tripTime='"+tripTime+"' and trip.busNo="+busNum;
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery();
                if(rs.next()) {
                    Log.i("get Driver trip info", "Yes");
                    d.setName(rs.getString("name"));
                    d.setPhoneNo(rs.getInt("phoneNo"));
                    d.setDistrict(rs.getString("district"));
                }else{
                    Log.i("get Driver Trip info", "No retrieved data");
                }
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return d;
    }

    //get list of passengers        -Ebtihal
    public ArrayList<Passengers> getTripPassengersList(String tripTime, int busNum, String dayName) {
        ArrayList<Passengers> passengersList = new ArrayList<Passengers>();

        try {
            conn = c.connection();        // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                // Change below query according to your own database.
                String query = "select student.name , student.phoneNo\n" +
                        "from student inner join studentTakesTrip on student.sId=studentTakesTrip.sId\n" +
                        "inner join trip on studentTakesTrip.tId = trip.tId \n" +
                        "where trip.tripTime='"+tripTime+"' and trip.busNo="+busNum+" and studentTakesTrip.dayName='"+dayName+"'";
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery();
                if(rs.next()) {
                    do {
                        Passengers p = new Passengers();
                        Log.i("get students list", "Success - Passenger: " + rs.getString("name"));
                        p.setID(rs.getRow()+"");
                        p.setStudentName(rs.getString("name"));
                        p.setStudentPhone(rs.getString("phoneNo"));
                        passengersList.add(p);
                    } while (rs.next());
                }else{
                    Log.i("get students list ", "No retrieved data" );
                }
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passengersList;
    }
//End of Ebtihal's Code for ManagerTripsSchedule

    //Ebtihal Code's for ManagerDriversInfo
    public ArrayList<DriverInfoBean> getDriversList(int mId) {
        ArrayList<DriverInfoBean> driverList = new ArrayList<DriverInfoBean>();

        try {
            conn = c.connection();        // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                // Change below query according to your own database.
                String query ="select driver.name, driver.phoneNo, bus.district, bus.busNo\n"+
                        "from driver inner join bus on driver.dId=bus.dId where bus.mId=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, mId);
                ResultSet rs = pstmt.executeQuery();
                if(rs.next()) {
                    do {
                        DriverInfoBean d = new DriverInfoBean();
                        Log.i("get Drivers list:", "Success - Driver: " + rs.getString("name"));
                        d.setID(rs.getRow()+"");
                        d.setDriverName(rs.getString("name"));
                        d.setDriverPhone(rs.getInt("phoneNo")+"");
                        d.setDriverDistrict(rs.getString("district"));
                        d.setDriverBusNumber(rs.getInt("busNo")+"");
                        driverList.add(d);
                    } while (rs.next());
                }else{
                    Log.i("get Drivers list:", "No retrieved data" );
                }
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return driverList;
    }
//end of Ebtihal Code's for ManagerDriversInfo

//*********************************************************************************************************

    //Ebtihal's code for ManagerAvailableDrivers
    public List<Integer> populateAvailableBusNumbers(int mId) {
        List<Integer> availableBusNoList = new ArrayList<Integer>();

        try {
            conn = c.connection();        // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                // Change below query according to your own database.
                String query = "select busNo from bus where dId is NULL and mId=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, mId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    Log.i("get available BusNO: ", "Yes" + rs.getInt("busNo"));
                    do {
                        availableBusNoList.add(rs.getInt("busNo"));
                    } while (rs.next());
                } else {
                    Log.i("get available BusNO:", "No retrieved data" + rs.getInt("busNo"));
                }
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availableBusNoList;
    }

    public ArrayList<DriverInfoBean> getAvailableDrivers() {
        ArrayList<DriverInfoBean> availableDriversList = new ArrayList<>();

        try {
            conn = c.connection();        // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                // Change below query according to your own database.
                String query = "select dId, name, phoneNo from driver where mId is NULL";
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    Log.i("get available Driver: ", "Yes" + String.valueOf(rs.getString("dId")));
                    do {
                        DriverInfoBean d = new DriverInfoBean();
                        Log.i("get Drivers list:", "Success - Driver: " + rs.getString("name"));
                        d.setID(String.valueOf(rs.getString("dId")));
                        d.setDriverName(rs.getString("name"));
                        d.setDriverPhone(rs.getInt("phoneNo") + "");
                        availableDriversList.add(d);
                    } while (rs.next());
                } else {
                    Log.i("get available Drivers:", "No retrieved data" );
                }
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availableDriversList;
    }

    public int assignDriverToManager(Context context, int mId,  int dId){
        int value= 0;
        try {
            conn = c.connection();  // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
                Toast.makeText(context, z, Toast.LENGTH_SHORT).show();
            } else {
                String query = " update driver set mId= ? where dId=?";

                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, mId);
                pstmt.setInt(2, dId);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    value = 1;

                    Log.i("Assign driver? ", "Yes");

                    conn.close();
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return value;
    }

    public int assignBusToDriver(Context context, int dId, int busNum, int mId){
        int value= 0;
        try {
            conn = c.connection();  // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
                Toast.makeText(context, z, Toast.LENGTH_SHORT).show();
            } else {
                String query = "update bus set dId=? where busNo=? and mId=?";

                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, dId);
                pstmt.setInt(2, busNum);
                pstmt.setInt(3, mId);


                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    value = 1;

                    Log.i("update bus? ", "Yes");

                    conn.close();
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return value;
    }
    //end of Ebtihal's code for ManagerAvailableDrivers
//*********************************************************************************************************

    //Start of rawan's code for sending announcement
    public void sendAnnouncementStudent(String announcementText, int busNo ,String dateAndTime) {

        try {
            conn = c.connection();        // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                // Change below query according to your own database.
                String query = "insert into sAnnouncement ( announcementText, busNo ,dateAndTime ) values (?, ? ,?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, announcementText);
                pstmt.setInt(2, busNo);
                pstmt.setString(3, dateAndTime);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    z = "successful";
                    isSuccess = true;
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

    public void sendAnnouncementDriver(String announcementText, int busNo, String dateAndTime) {

        try {
            conn = c.connection();        // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                // Change below query according to your own database.
                String query = "insert into dAnnouncement ( announcementText, busNo,dateAndTime ) values (?, ?,? )";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, announcementText);
                pstmt.setInt(2, busNo);
                pstmt.setString(3, dateAndTime);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    z = "successful";
                    isSuccess = true;
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


//rawan's code for sending announcement

//*********************************************************************************************************

    // no need for it since insertion will be on db admin side
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void insertManager(EditText sName, EditText password, EditText phoneNo) {
        String name = sName.getText().toString();
        String pass = password.getText().toString();
        int phone = Integer.parseInt(phoneNo.getText().toString());

        try {
            conn = c.connection();        // Connect to database
            if (conn == null) {
                z = "Check Your Internet Access!";
            } else {
                String query = " insert into manager (name, password, phoneNo)"
                        + " values (?, ?, ?)";

                Crypto data = new Crypto();
                byte[] md5InBytes = data.digest(pass.getBytes(UTF_8));
                String cPass = data.bytesToHex(md5InBytes);

                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, cPass);
                pstmt.setInt(3, phone);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    z = "successful";
                    isSuccess = true;
                    Log.i("Insert new? ", "Yes");
                    // to be added Manager obj + shared pref
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

}


