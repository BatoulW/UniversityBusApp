package com.example.secondv;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secondv.Student.StudentCrud;
import com.example.secondv.Student.StudentLocationMap;

public class signup extends AppCompatActivity implements  AdapterView.OnItemSelectedListener {

    Intent intent;
    EditText studentNameTv,studentEmail, studentPassTv, studentConfirmPassTv, studentPhoneTV, studentCollegeTV;
    Button pickLocationBtn, signUpBtn;
    TextView loginTv,studentLocationTV;
    Spinner spinner;

    Double addressLatitude , addressLongitude;
    String district;

    /// make Student Crud obj
    StudentCrud sCRUD = new StudentCrud();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

//##############################################################################################################################
        studentNameTv= (EditText)findViewById(R.id.txtFullName);
        studentPassTv= (EditText)findViewById(R.id.txtPassword);
        studentPhoneTV= (EditText)findViewById(R.id.txtPhoneNo);
        studentCollegeTV= (EditText)findViewById(R.id.txtCollege);
        studentEmail =(EditText)findViewById(R.id.email_signup_edit_text);
        studentConfirmPassTv=(EditText) findViewById(R.id.txtConfPassword) ;
        signUpBtn= (Button) findViewById(R.id.sign_up_btn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                sCRUD.insertStudent(studentNameTv,studentEmail, studentPassTv,studentConfirmPassTv, studentPhoneTV, district, addressLatitude, addressLongitude,studentCollegeTV, signup.this);// add district att -Fatma's Code and comment V7
 // ##############################################################################################################################
            }
        });
        ///////

        //Bundle extras = getIntent().getExtras();

        pickLocationBtn= (Button) findViewById(R.id.pick_location_btn);
        pickLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent= new Intent(getApplicationContext(), StudentLocationMap.class);
                startActivityForResult(intent, 1);
            }
        });

        studentLocationTV = (TextView) findViewById(R.id.student_location_text_view);


        //handle district spinner
        spinner= findViewById(R.id.studentDiscritTV);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this , R.array.district_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        loginTv = (TextView) findViewById(R.id.login_text_view);
        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Selected district is "+district, Toast.LENGTH_LONG).show();// just to test getting the right selected district -Ebtihal
                intent = new Intent(signup.this, Login.class);
                startActivity(intent);
            }
        });
    }

    //Fatma's Code -V7
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                addressLatitude = data.getDoubleExtra("locationForDataLatitude",0); //To store in the database
                addressLongitude = data.getDoubleExtra("locationForDataLongitude",0); //To store in the database
                studentLocationTV.setText(data.getStringExtra("StudentAddress"));
            }else{
                studentLocationTV.setText("No location is picked");
            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //set the district to the student sign up here
        district = spinner.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //set the
    }
}