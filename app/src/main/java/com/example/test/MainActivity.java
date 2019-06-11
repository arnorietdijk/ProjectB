package com.example.test;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    public static SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.main_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        sqLiteHelper = new SQLiteHelper(this, "MemoryDB.sqlite", null, 1);

        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS FOOD(Id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR, description VARCHAR, location VARCHAR, image BLOB)");

        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,
                    new HomeFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_map:
                            selectedFragment = new MapFragment();
                            break;
                        case R.id.nav_memories:
                            selectedFragment = new MemoriesFragment();
                            break;
                        case R.id.nav_settings:
                            selectedFragment = new AboutFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,
                            selectedFragment).commit();

                    return true;
                }
            };
}