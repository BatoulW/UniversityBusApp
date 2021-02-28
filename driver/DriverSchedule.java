package com.example.secondv.Driver;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.secondv.Adapter.PassengerAdapter;
import com.example.secondv.Bean.Passengers;
import com.example.secondv.Manager.ManagerCrud;
import com.example.secondv.R;

import java.util.ArrayList;

public class DriverSchedule extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Intent intent;
    ArrayList<Passengers> sunStudentsList, emptyList;

    ListView listView;
    Spinner  tripTimeSpinner, tripDaySpinner;
    TextView dayTV, noDataTV;
    LinearLayout resultLayout;
    Button getScheduleBtn;

    int dId;
    String day, time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_schedule);

        //handle toolbar and option menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get driver id :
        Driver driver = DriverSharedPref.getInstance(this).getUser();
        dId = driver.getId();

        //Initialize all the buttons and textViews in the Activity
        dayTV = (TextView) findViewById(R.id.driver_schedule_day_text_view);
        noDataTV =(TextView) findViewById(R.id.driver_schedule_no_result_text_view);
        resultLayout = (LinearLayout) findViewById(R.id.driver_schedule_result_linear_view);

        //handle trip time spinner [all times]
        //you may also populate the trip time spinner with ONLY the assigned trips times of the driver
        tripTimeSpinner = findViewById(R.id.driver_trip_time_spinner);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this , R.array.all_times_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tripTimeSpinner.setAdapter(adapter);
        tripTimeSpinner.setOnItemSelectedListener(this);

        //handle trip day Spinner
        tripDaySpinner= findViewById(R.id.driver_trip_day_spinner);
        ArrayAdapter<CharSequence> daysAdapter=ArrayAdapter.createFromResource(this , R.array.days, android.R.layout.simple_spinner_item);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tripDaySpinner.setAdapter(daysAdapter);
        tripDaySpinner.setOnItemSelectedListener(this);

        //handle passengers list
        sunStudentsList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list);

        emptyList = new ArrayList<>();

        getScheduleBtn=(Button) findViewById(R.id.get_driver_trip_btn);
        getScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                displayTripInfo(day, time,dId);
                //implement this code after retrieving data from database     -Ebtihal
                //list = getPassengers(time,day, dId);
               //displayData(list);
            }
        });
    }
    public void displayTripInfo(String dayName,  String tripTime, int dId){
        dayTV.setText(dayName); //set trip day
       // getPassengers(time,busNo, dayName);
        DriverCrud dCrud= new DriverCrud();
        noDataTV.setVisibility(View.GONE);
        resultLayout.setVisibility(View.GONE);
        ArrayList<Passengers> list=  dCrud.displayTripSchedule(tripTime, dayName,dId);

        if(list.size() >0 ){
            noDataTV.setVisibility(View.GONE);
            resultLayout.setVisibility(View.VISIBLE);
            PassengerAdapter pa = new PassengerAdapter(this, list);
            listView.setAdapter(pa);
        } else{
            noDataTV.setVisibility(View.VISIBLE);
            resultLayout.setVisibility(View.GONE);
        }
    }

    //To handle options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.driver_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.driver_main_page:
                intent = new Intent(getApplicationContext(), DriverMainPage.class);
                startActivity(intent);
                return true;
            case R.id.driver_trips_schedule:
                intent = new Intent(getApplicationContext(), DriverSchedule.class);
                startActivity(intent);
                return true;
            case R.id.driver_start_trip:
                intent = new Intent(getApplicationContext(), DriverStartTrip.class);
                startActivity(intent);
                return true;
            case R.id.driver_logout:
                DriverSharedPref.getInstance(this).logout();
                return true;
            case R.id.driver_account:
                intent = new Intent(getApplicationContext(), DriverInfo.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//end of handle toolbar and option menu
    //handle spinner selected item
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        time = tripTimeSpinner.getSelectedItem().toString();
        day = tripDaySpinner.getSelectedItem().toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //set deafult values to day and time trip
        time="8:00";
        day = "Sunday";
    }
//end of handle spinner items
    //handle passengers list
    @Override
    protected void onResume() {
        noDataTV.setVisibility(View.GONE);
        resultLayout.setVisibility(View.GONE);
        //display sunday schedule by default
        dayTV.setText("Sunday");
        displayData(sunStudentsList);//emptyList
        super.onResume();
    }
    //display list of passengers
    public void displayData(ArrayList<Passengers> list) {
        if(list.size()>0) {
            noDataTV.setVisibility(View.GONE);
            resultLayout.setVisibility(View.VISIBLE);
            dayTV.setText(day);
            PassengerAdapter pa = new PassengerAdapter(this, list);
            listView.setAdapter(pa);
        }else{
            noDataTV.setVisibility(View.VISIBLE);
            resultLayout.setVisibility(View.GONE);
        }
    }

    /*public ArrayList<Passengers> getPassengers(String trip, String day, int dId){
        //implement getting the passengers list from tha database here
        dayTV.setText(day);

        return ;
    }*/
}