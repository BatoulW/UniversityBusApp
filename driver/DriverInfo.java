package com.example.secondv.Driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secondv.Manager.Manager;
import com.example.secondv.R;
import com.example.secondv.Bean.DriverInfoBean;

import java.util.ArrayList;

public class DriverInfo extends AppCompatActivity {

    EditText driverNameTV, driverPhoneTv;
    TextView driverID, driverBusTv, driverDistrictTv,managerNameTv, managerPhoneTV;
    ArrayList<TextView>  tvList= new ArrayList<>();
    Button saveChanges;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info);

        //handle toolbar and option menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        driverID = (TextView) findViewById(R.id.driver_id_driver_account_text_view);
        driverNameTV = (EditText) findViewById(R.id.driver_name_account_edit_text);
        driverPhoneTv = (EditText) findViewById(R.id.driver_phone_driver_account_edit_text);

        driverBusTv = (TextView) findViewById(R.id.driver_bus_number_driver_account_text_view);
        driverDistrictTv = (TextView) findViewById(R.id.driver_district_driver_account_text_view);
        managerNameTv = (TextView) findViewById(R.id.manager_name_driver_account_text_view);
        managerPhoneTV = (TextView) findViewById(R.id.manager_phone_driver_account_text_view);
//##############################################################################################################################
        //getting the current driver
        Driver driver = DriverSharedPref.getInstance(this).getUser();
        Manager manager = new Manager();
        final DriverCrud dCrud = new DriverCrud();
        final int dId = driver.getId();
        dCrud.readDriverInfo(dId, driver, manager);

// Ebtihal Code --- think about it later $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        //get driver information from the DB will be added here
        //DriverInfoBean driverInfotest = new DriverInfoBean("Ahmed","05123456789","34","Al-Hada","Faisal Al-Rawi","05123654987");
       // DriverInfoBean driverInfo = DriverSharedPref.getInstance(this).getUser();

        // driverBusTv.setText(driverInfo.getDriverBusNumber());
        // driverDistrictTv.setText(driverInfo.getDriverDistrict());
// Ebtihal Code  $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
        driverID.setText(driver.getId()+"");
        driverNameTV.setText(driver.getName());
        driverPhoneTv.setText(String.valueOf(driver.getPhoneNo()));
        driverDistrictTv.setText(driver.getDistrict());
        driverBusTv.setText(String.valueOf(driver.getBusNo()));
        managerNameTv.setText(manager.getName());
        managerPhoneTV.setText(String.valueOf(manager.getPhoneNo()));
//##############################################################################################################################
        saveChanges = (Button) findViewById(R.id.driver_save_change_btn);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            //Update driver will be written here [Not sure] .. update the record in the database then refresh the activity with the new data
            @Override
            public void onClick(View view) {

              int status=   dCrud.updateDriverNamePhoneNo(dId,driverNameTV, driverPhoneTv, getApplicationContext());
                if (status==1) {
                    Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                    //refresh activity
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        });

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

}