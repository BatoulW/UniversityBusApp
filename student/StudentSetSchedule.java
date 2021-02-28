package com.example.secondv.Student;
//Fatma code v9
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.secondv.Bean.StudentSchedule;
import com.example.secondv.R;

import java.util.ArrayList;

public class StudentSetSchedule extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Intent intent;
    Button submitBtn;
    Spinner sunGoSpinner, monGoSpinner, tueGoSpinner, wedGoSpinner, thuGoSpinner;
    Spinner sunReturnSpinner, monReturnSpinner,tueReturnSpinner,wedReturnSpinner,thuReturnSpinner;

    String sunGOTime , monGOTime , tueGOTime , wedGOTime , thuGOTime;
    String sunReturnTime , monReturnTime , tueReturnTime , wedReturnTime , thuReturnTime;
    StudentCrud studentCrudObject = new StudentCrud();
    int StudentId;
    String District;
    ArrayList<StudentSchedule> newSchedule = new ArrayList<StudentSchedule>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_set_schedule);

        //handle toolbar and option menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //get Student ID
        Student student = SharedPrefManager.getInstance(this).getUser();
        StudentId = student.getId();
        StudentCrud sCrud = new StudentCrud();
        sCrud.readStudentInfo(student.getId(), student);
        District = student.getDistrict();


        //initializing
        init();

        //submit schedule
        sendSubmission();


    }

    private void init(){

        //initialize buttons
        submitBtn = (Button) findViewById(R.id.submit_schedule_btn);

        //initialize all go Spinners
        sunGoSpinner = findViewById(R.id.sun_go_spinner);
        monGoSpinner = findViewById(R.id.mon_go_spinner);
        tueGoSpinner = findViewById(R.id.tue_go_spinner);
        wedGoSpinner = findViewById(R.id.wed_go_spinner);
        thuGoSpinner = findViewById(R.id.thu_go_spinner);

        //fill go spinners
        Spinner[] goSpinners = new Spinner[]{sunGoSpinner, monGoSpinner, tueGoSpinner, wedGoSpinner, thuGoSpinner};
        ArrayAdapter<CharSequence> goAdapter=ArrayAdapter.createFromResource(this , R.array.schedule_go_time_array, android.R.layout.simple_spinner_item);

        for(Spinner s : goSpinners){
            s.setAdapter(goAdapter);
            s.setOnItemSelectedListener(this);
        }

        //initialize all return Spinners
        sunReturnSpinner = findViewById(R.id.sun_return_spinner);
        monReturnSpinner = findViewById(R.id.mon_return_spinner);
        tueReturnSpinner = findViewById(R.id.tue_return_spinner);
        wedReturnSpinner = findViewById(R.id.wed_return_spinner);
        thuReturnSpinner = findViewById(R.id.thu_return_spinner);

        //fill return spinners
        Spinner[] returnSpinners = new Spinner[]{sunReturnSpinner, monReturnSpinner, tueReturnSpinner, wedReturnSpinner, thuReturnSpinner};
        ArrayAdapter<CharSequence> returnAdapter=ArrayAdapter.createFromResource(this , R.array.schedule_return_time_array, android.R.layout.simple_spinner_item);

        for(Spinner s : returnSpinners){
            s.setAdapter(returnAdapter);
            s.setOnItemSelectedListener(this);
        }

        //get the old schedule
        getOldSchedule(goAdapter,returnAdapter);

        //SetOnItemSelectListener
        sunGoSpinner.setOnItemSelectedListener(this);
        monGoSpinner.setOnItemSelectedListener(this);
        tueGoSpinner.setOnItemSelectedListener(this);
        wedGoSpinner.setOnItemSelectedListener(this);
        thuGoSpinner.setOnItemSelectedListener(this);

        sunReturnSpinner.setOnItemSelectedListener(this);
        monReturnSpinner.setOnItemSelectedListener(this);
        tueReturnSpinner.setOnItemSelectedListener(this);
        wedReturnSpinner.setOnItemSelectedListener(this);
        thuReturnSpinner.setOnItemSelectedListener(this);

    }

    //handle toolbar and option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.student_main_page:
                intent = new Intent(getApplicationContext(), StudentMainPage.class);
                startActivity(intent);
                return true;
            case R.id.student_trip_schedule:
                intent = new Intent(getApplicationContext(), StudentTripsSchedulePage.class);
                startActivity(intent);
                return true;
            case R.id.set_schedule:
                intent = new Intent(getApplicationContext(), StudentSetSchedule.class);
                startActivity(intent);
                return true;
            case R.id.student_track_trip:
                intent = new Intent(getApplicationContext(), StudentTrackTrip.class);
                startActivity(intent);
                return true;
            case R.id.student_logout:
                SharedPrefManager.getInstance(this).logout();
                return true;
            case R.id.student_account:
                intent = new Intent(getApplicationContext(), StudentInfo.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //end of handle toolbar and option menu

    //To display the old schedule of the student
    public void getOldSchedule(ArrayAdapter<CharSequence> goAdapter, ArrayAdapter<CharSequence> returnAdapter){

        ArrayList<StudentSchedule> schedules = studentCrudObject.getOldSchedule(StudentId);

        if(schedules.size() > 0){
            for (int i = 0 ; i < schedules.size() ; i++){
                String Day = schedules.get(i).getMday();
                String Time = schedules.get(i).getmTripTime();
                Log.e("check",Day+" "+Time);

                switch (Day){
                    case "Sunday":
                        if(schedules.get(i).isGoTrip(Time)){
                            sunGoSpinner.setSelection(goAdapter.getPosition(Time));
                        }else {
                            sunReturnSpinner.setSelection(returnAdapter.getPosition(Time));
                        }
                        break;
                    case "Monday":
                        if(schedules.get(i).isGoTrip(Time)){
                            monGoSpinner.setSelection(goAdapter.getPosition(Time));
                        }else {
                            monReturnSpinner.setSelection(returnAdapter.getPosition(Time));
                        }
                        break;
                    case "Tuesday":
                        if(schedules.get(i).isGoTrip(Time)){
                            tueGoSpinner.setSelection(goAdapter.getPosition(Time));
                        }else {
                            tueReturnSpinner.setSelection(returnAdapter.getPosition(Time));
                        }
                        break;
                    case "Wednesday":
                        if(schedules.get(i).isGoTrip(Time)){
                            wedGoSpinner.setSelection(goAdapter.getPosition(Time));
                        }else {
                            wedReturnSpinner.setSelection(returnAdapter.getPosition(Time));
                        }
                        break;
                    case "Thursday":
                        if(schedules.get(i).isGoTrip(Time)){
                            thuGoSpinner.setSelection(goAdapter.getPosition(Time));
                        }else {
                            thuReturnSpinner.setSelection(returnAdapter.getPosition(Time));
                        }
                        break;
                }
            }
        }
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.sun_go_spinner:
                if(position == 0){
                    sunGOTime = "null";
                } else {
                    sunGOTime = parent.getSelectedItem().toString();
                }
                break;

            case R.id.mon_go_spinner:
                if(position == 0){
                    monGOTime = "null";
                } else {
                    monGOTime = parent.getSelectedItem().toString();
                }
                break;

            case R.id.tue_go_spinner:
                if(position == 0){
                    tueGOTime = "null";
                } else {
                    tueGOTime = parent.getSelectedItem().toString();
                }
                break;

            case R.id.wed_go_spinner:
                if(position == 0){
                    wedGOTime = "null";
                } else {
                    wedGOTime = parent.getSelectedItem().toString();
                }
                break;

            case R.id.thu_go_spinner:
                if(position == 0){
                    thuGOTime = "null";
                } else {
                    thuGOTime = parent.getSelectedItem().toString();
                }
                break;

            case R.id.sun_return_spinner:
                if(position == 0){
                    sunReturnTime = "null";
                } else {
                    sunReturnTime = parent.getSelectedItem().toString();
                }
                break;

            case R.id.mon_return_spinner:
                if(position == 0){
                    monReturnTime = "null";
                } else {
                    monReturnTime = parent.getSelectedItem().toString();
                }
                break;

            case R.id.tue_return_spinner:
                if(position == 0){
                    tueReturnTime = "null";
                } else {
                    tueReturnTime = parent.getSelectedItem().toString();
                }
                break;

            case R.id.wed_return_spinner:
                if(position == 0){
                    wedReturnTime = "null";
                } else {
                    wedReturnTime = parent.getSelectedItem().toString();
                }
                break;

            case R.id.thu_return_spinner:
                if(position == 0){
                    thuReturnTime = "null";
                } else {
                    thuReturnTime = parent.getSelectedItem().toString();
                }
                break;
        }

    }
    //end of spinners' item selected handler

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //To send the schedule that student fill out
    public void sendSubmission(){

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog=new AlertDialog.Builder(StudentSetSchedule.this);
                dialog.setMessage("Save this schedule?");
                dialog.setTitle("Confirm saving");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        newSchedule.add( new StudentSchedule("Sunday",sunGOTime));
                        newSchedule.add( new StudentSchedule("Monday",monGOTime));
                        newSchedule.add( new StudentSchedule("Tuesday",tueGOTime));
                        newSchedule.add( new StudentSchedule("Wednesday",wedGOTime));
                        newSchedule.add( new StudentSchedule("Thursday",thuGOTime));

                        newSchedule.add( new StudentSchedule("Sunday",sunReturnTime));
                        newSchedule.add( new StudentSchedule("Monday",monReturnTime));
                        newSchedule.add( new StudentSchedule("Tuesday",tueReturnTime));
                        newSchedule.add( new StudentSchedule("Wednesday",wedReturnTime));
                        newSchedule.add( new StudentSchedule("Thursday",thuReturnTime));


                        studentCrudObject.StoreSchedule(StudentId, District , newSchedule);
                        Toast.makeText(getApplicationContext(),"Schedule saved.",Toast.LENGTH_LONG).show();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                AlertDialog alertDialog=dialog.create();
                alertDialog.show();

            }
        });
    }

}
/*
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.secondv.R;

public class StudentSetSchedule extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Intent intent;
    Button submitBtn;
    Spinner sunGoSpinner, monGoSpinner, tueGoSpinner, wedGoSpinner, thuGoSpinner;
    Spinner sunReturnSpinner, monReturnSpinner,tueReturnSpinner,wedReturnSpinner,thuReturnSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_set_schedule);

        //handle toolbar and option menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize buttons
        submitBtn = (Button) findViewById(R.id.submit_schedule_btn);

        //initialize all go Spinners
        sunGoSpinner = findViewById(R.id.sun_go_spinner);
        monGoSpinner = findViewById(R.id.mon_go_spinner);
        tueGoSpinner = findViewById(R.id.tue_go_spinner);
        wedGoSpinner = findViewById(R.id.wed_go_spinner);
        thuGoSpinner = findViewById(R.id.thu_go_spinner);

        //fill go spinners
        Spinner[] goSpinners = new Spinner[]{sunGoSpinner, monGoSpinner, tueGoSpinner, wedGoSpinner, thuGoSpinner};
        ArrayAdapter<CharSequence> goAdapter=ArrayAdapter.createFromResource(this , R.array.schedule_go_time_array, android.R.layout.simple_spinner_item);

        for(Spinner s : goSpinners){
            s.setAdapter(goAdapter);
            s.setOnItemSelectedListener(this);
        }

        //initialize all return Spinners
        sunReturnSpinner = findViewById(R.id.sun_return_spinner);
        monReturnSpinner = findViewById(R.id.mon_return_spinner);
        tueReturnSpinner = findViewById(R.id.tue_return_spinner);
        wedReturnSpinner = findViewById(R.id.wed_return_spinner);
        thuReturnSpinner = findViewById(R.id.thu_return_spinner);

        //fill return spinners
        Spinner[] returnSpinners = new Spinner[]{sunReturnSpinner, monReturnSpinner, tueReturnSpinner, wedReturnSpinner, thuReturnSpinner};
        ArrayAdapter<CharSequence> returnAdapter=ArrayAdapter.createFromResource(this , R.array.schedule_return_time_array, android.R.layout.simple_spinner_item);

        for(Spinner s : returnSpinners){
            s.setAdapter(returnAdapter);
            s.setOnItemSelectedListener(this);
        }

    }

    //handle toolbar and option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.student_main_page:
                intent = new Intent(getApplicationContext(), StudentMainPage.class);
                startActivity(intent);
                return true;
            case R.id.student_trip_schedule:
                intent = new Intent(getApplicationContext(), StudentTripsSchedulePage.class);
                startActivity(intent);
                return true;
            case R.id.set_schedule:
                intent = new Intent(getApplicationContext(), StudentSetSchedule.class);
                startActivity(intent);
                return true;
            case R.id.student_track_trip:
                intent = new Intent(getApplicationContext(), StudentTrackTrip.class);
                startActivity(intent);
                return true;
            case R.id.student_account:
                intent = new Intent(getApplicationContext(), StudentInfo.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//end of handle toolbar and option menu
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        /// handle selected items from spinner

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}*/