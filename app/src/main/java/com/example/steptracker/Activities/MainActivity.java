package com.example.steptracker.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;

import com.example.steptracker.Adapters.RecordAdapter;
import com.example.steptracker.Contracts.UserContract;
import com.example.steptracker.Databases.WorkoutDBHelper;
import com.example.steptracker.Fragments.DashboardFragment;
import com.example.steptracker.Fragments.GraphFragment;
import com.example.steptracker.Fragments.ProfileFragment;
import com.example.steptracker.Interface.DayChangedListner;
import com.example.steptracker.Models.Record;
import com.example.steptracker.R;
import com.example.steptracker.Receivers.DayChangedReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private final Fragment dashboardFragment = new DashboardFragment();
    private final Fragment graphFragment = new GraphFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = dashboardFragment;
    private boolean doubleBackPressed = false;

    public ArrayList<Record> recordList;
    public RecordAdapter adapter;
    private WorkoutDBHelper dbHelper;
    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        recordList = new ArrayList<>();
        adapter = new RecordAdapter(this, recordList);
        dbHelper = new WorkoutDBHelper(this);
        mDatabase = dbHelper.getReadableDatabase();
        setupToolbar();
        setupBottomNav();
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }


    private void setupBottomNav() {
        fm.beginTransaction().add(R.id.fragmentContainer, profileFragment, "3").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.fragmentContainer, graphFragment, "2").hide(graphFragment).commit();
        fm.beginTransaction().add(R.id.fragmentContainer, dashboardFragment, "1").commit();
    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.dashboardFragment:
                if(!getSupportActionBar().isShowing()) {
                    getSupportActionBar().show();
                }
                fm.beginTransaction().hide(active).show(dashboardFragment).commit();
                active = dashboardFragment;
                return true;
            case R.id.graphFragment:
                if(!getSupportActionBar().isShowing()) {
                    getSupportActionBar().show();
                }
                fm.beginTransaction().hide(active).show(graphFragment).commit();
                active = graphFragment;
                return true;
            case R.id.profileFragment:
                if(getSupportActionBar().isShowing()) {
                    getSupportActionBar().hide();
                }
                fm.beginTransaction().hide(active).show(profileFragment).commit();
                active = profileFragment;
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(!doubleBackPressed) {
            doubleBackPressed = true;
            if(!getSupportActionBar().isShowing()) {
                getSupportActionBar().show();
            }
            getSupportFragmentManager().beginTransaction().hide(active).show(dashboardFragment).commit();
            active = dashboardFragment;
            new Handler().postDelayed(() -> doubleBackPressed = false, 2000);
        }else{
            super.onBackPressed();
        }
    }


    public void getAllData() {

        Cursor cursor = mDatabase.query(UserContract.WorkoutEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(UserContract.WorkoutEntry.COLUMN_DATE));
            String steps = cursor.getString(cursor.getColumnIndexOrThrow(UserContract.WorkoutEntry.COLUMN_STEPS));


            recordList.add(new Record(date, Long.valueOf(steps)));
        }

    }

    @Override
    protected void onPause() {
        dbHelper.close();
        super.onPause();
    }
}

