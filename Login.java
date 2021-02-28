package com.example.secondv;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.secondv.Driver.DriverCrud;
import com.example.secondv.Manager.ManagerCrud;
import com.example.secondv.Student.StudentCrud;

public class Login extends AppCompatActivity{

    EditText passwordET, userET;
    TextView signupTV, loginTV;
    ImageView userIcon;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize all textViews and EditTexts
        loginTV = (TextView) findViewById(R.id.lin);
        userET = (EditText) findViewById(R.id.usrusr);
        userIcon = (ImageView) findViewById(R.id.user_ic);
        passwordET = (EditText) findViewById(R.id.pswrdd);
        signupTV = (TextView) findViewById(R.id.sup);

        //get user type
        Bundle extra = getIntent().getExtras();
        final String user = extra.getString("user");

        //prevent non students from sign up
        if(!user.equals("student")) {
            signupTV.setVisibility(View.GONE);
        }else{
            userET.setHint("Email");
            userIcon.setImageResource(R.drawable.ic__email_24);
        }

        signupTV.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                intent = new Intent(getApplicationContext(), signup.class);
                startActivity(intent);
            }
        });

        //login based on the user type
        loginTV.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                switch(user){
                    case "student":
                        //search on students table for valid username and password then go to StudentMainPage
                        StudentCrud c = new StudentCrud();
                        c.checkLogin(userET, passwordET,Login.this);
                        /*
                        intent = new Intent(getApplicationContext(), com.example.secondv.Student.StudentMainPage.class);
                        startActivity(intent);*/
                        break;
                    case "driver":
                        //search on drivers table for valid username and password then go to DriverMainPage
                        DriverCrud d = new DriverCrud();
                        d.checkLogin(userET, passwordET,Login.this);
                        /*
                        intent = new Intent(getApplicationContext(), com.example.secondv.Driver.DriverMainPage.class);
                        startActivity(intent);*/
                        break;
                    case "manager":
                        //search on managers table for valid username and password then go to ManagerMainPage
                        ManagerCrud m = new ManagerCrud();
                        m.checkLogin(userET, passwordET,Login.this);
                       /* intent = new Intent(getApplicationContext(), com.example.secondv.Manager.ManagerMainPage.class);
                        startActivity(intent); */
                        break;
                }
            }
        });
    }
}