package com.example.secondv.Manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.secondv.Bean.DriverInfoBean;
import com.example.secondv.Adapter.DriverAdapter;
import com.example.secondv.R;

import java.util.ArrayList;

public class ManagerAvailableDrivers extends AppCompatActivity {
    ArrayList<DriverInfoBean> driversList;
    ListView listView;
    TextView noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_available_drivers);

        //handle toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //layout components
        driversList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listt);
        noData = (TextView) findViewById(R.id.no_data);

    }

    @Override
    protected void onResume() {
        //get Manager's available drivers
        ManagerCrud mCrud = new ManagerCrud();
        driversList = mCrud.getAvailableDrivers();

        displayData(driversList);
        super.onResume();
    }

    public void displayData(ArrayList<DriverInfoBean> list){
        if(list.size() > 0 ) {
            noData.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            DriverAdapter dr = new DriverAdapter(this, list);
            listView.setAdapter(dr);
        }else {
            noData.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }


}
