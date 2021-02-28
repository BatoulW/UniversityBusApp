package com.example.secondv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.example.secondv.Student.StudentMainPage;// added

public class ConnectionClass {

    // This is default if you are using JTDS driver.
    String classs = "net.sourceforge.jtds.jdbc.Driver";

    @SuppressLint("NewApi")
    public Connection connection() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL;
        try {
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://pnubusserver.database.windows.net:1433;DatabaseName=PNUBus;" +
                    "user=PNUBUS@pnubusserver;password=server_PNU;encrypt=true;trustServerCertificate=false;" +
                    "hostNameInCertificate=*.database.windows.net;loginTimeout=30";
            /*  "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";*/
            conn = DriverManager.getConnection(ConnURL);
            if (conn != null) {
                Log.i("hello", "connected");
            }
        } catch (SQLException se) {
            Log.e("safiya", se.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        return conn;
    }
}
/////////
/*
    String z = "";
    Boolean isSuccess = false;
    Connection conn;
    EditText userName , password;
    Context ctx;
*/
/// constructor for login -from me-
    /*public ConnectionClass(EditText userName , EditText password , Context ctx){
    this.userName = userName;
    this.password=password;
    this.ctx=ctx;
    }*/
///
   /* @Override
    protected void onPreExecute()
    {
       //progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String r)
    {
      //  progressBar.setVisibility(View.GONE);
       // Toast.makeText(MainActivity.this, r, Toast.LENGTH_SHORT).show();
        if(isSuccess)
        {
            Intent it = new Intent(ctx, StudentMainPage.class);
            ctx.startActivity(it);
         //   Toast.makeText(MainActivity.this , "Login Successfull" , Toast.LENGTH_LONG).show();
            //finish();
        }
    }
    @Override
    protected String doInBackground(String... params)
    {
        String usernam = userName.getText().toString();
        String passwordd = password.getText().toString();
        if(usernam.trim().equals("")|| passwordd.trim().equals(""))
            z = "Please enter Username and Password";
        else
        {
            try
            {
                conn = connection();        // Connect to database
                if (conn == null)
                {
                    z = "Check Your Internet Access!";
                }
                else
                {
                    // Change below query according to your own database.
                    String query = "select * from student where sId=? and password=?";
                      //  Statement stmt = conn.createStatement();
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1,usernam);
                    pstmt.setString(2,passwordd);
                    ResultSet rs = pstmt.executeQuery();
                    if(rs.next())
                    {
                        z = "successful";
                        isSuccess=true;
                        Log.i("logged in","Yes");
                        conn.close();
                    }
                    else
                    {
                        z = "Invalid Credentials!";
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
        return z;
    }

*/



