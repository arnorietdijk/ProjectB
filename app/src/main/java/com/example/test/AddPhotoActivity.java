package com.example.test;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import java.util.List;
import java.util.Locale;

public class AddPhotoActivity extends AppCompatActivity implements LocationListener {

    private static final String FILE_NAME = "memories.txt";
    private LocationManager locationManager;
    private Location onlyOneLocation;
    private final int REQUEST_FINE_LOCATION = 1234;
    LocationTrack locationTrack;
    TextInputEditText addTitle,addDescription,addLocation;
    String title, description, location ;

    Boolean CheckEditTextEmpty ;
    String SQLiteQuery;
    SQLiteDatabase SQLITEDATABASE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        Paint mTextPaint = new Paint();

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle mBundle = getIntent().getExtras();
        byte[] mBytes = mBundle.getByteArray("captured_image");

        Bitmap mBitmap = BitmapFactory.decodeByteArray(mBytes, 0, mBytes.length);
        ImageView mImageView = (ImageView) findViewById(R.id.imageView2);

        mImageView.setImageBitmap(mBitmap);


        Button Cancel = (Button) findViewById(R.id.Cancel);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);

        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxLocation);
        addTitle = (TextInputEditText) findViewById(R.id.addTitle);
        addDescription = (TextInputEditText) findViewById(R.id.addDescription);
        addLocation = (TextInputEditText) findViewById(R.id.addLocation);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationTrack = new LocationTrack(AddPhotoActivity.this);
                boolean checked = ((CheckBox) v).isChecked();
                // Check which checkbox was clicked
                if (checked) {

                    if (locationTrack.canGetLocation()) {
                        double longitude = locationTrack.getLongitude();
                        double latitude = locationTrack.getLatitude();
                        final String address = getAddress(AddPhotoActivity.this,latitude,longitude);
                        //Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
                        addLocation.setText(address);
                    } else {

                        locationTrack.showSettingsAlert();
                    }
                } else {
                    // Do your coding
                }
            }
        });

        Button Save = (Button) findViewById(R.id.saveButton);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBCreate();

                SubmitData2SQLiteDB();
            }
        });
    }

    public void DBCreate(){

        SQLITEDATABASE = openOrCreateDatabase("DemoDataBase", Context.MODE_PRIVATE, null);

        SQLITEDATABASE.execSQL("CREATE TABLE IF NOT EXISTS demoTable(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title String, description String, location VARCHAR);");
    }

    public void SubmitData2SQLiteDB(){

        title = addTitle.getText().toString();

        description = addDescription.getText().toString();

        location = addLocation.getText().toString();

        CheckEditTextIsEmptyOrNot( title,description, location);

        if(CheckEditTextEmpty == true)
        {

            SQLiteQuery = "INSERT INTO demoTable (title,description,location) VALUES('"+title+"', '"+description+"', '"+location+"');";

            SQLITEDATABASE.execSQL(SQLiteQuery);

            Toast.makeText(AddPhotoActivity.this,"Data Submit Successfully", Toast.LENGTH_LONG).show();

            ClearEditTextAfterDoneTask();

        }
        else {

            Toast.makeText(AddPhotoActivity.this,"Please Fill All the Fields", Toast.LENGTH_LONG).show();
        }
    }

    public void CheckEditTextIsEmptyOrNot(String Name,String PhoneNumber, String subject ){

        if(TextUtils.isEmpty(Name) || TextUtils.isEmpty(PhoneNumber) || TextUtils.isEmpty(subject)){

            CheckEditTextEmpty = false ;

        }
        else {
            CheckEditTextEmpty = true ;
        }
    }

    public void ClearEditTextAfterDoneTask(){

        addTitle.getText().clear();
        addDescription.getText().clear();
        addLocation.getText().clear();

    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    public String getAddress(Context ctx, double lat, double lng){
        String fullAdd=null;
        try{
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<android.location.Address> addresses = geocoder.getFromLocation(lat,lng,1);
            if(addresses.size()>0){
                Address address = addresses.get(0);
                fullAdd = address.getLocality();

                String Location = address.getLocality();
                String Zip = address.getPostalCode();
                String Country = address.getCountryName();
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
        return fullAdd;
    }

    @Override
    public void onLocationChanged(Location location) {
        onlyOneLocation = location;
        locationManager.removeUpdates(this);
    }
    @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
    @Override public void onProviderEnabled(String provider) { }
    @Override public void onProviderDisabled(String provider) { }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("gps", "Location permission granted");
                    try {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        locationManager.requestLocationUpdates("gps", 0, 0, this);
                    } catch (SecurityException ex) {
                        Log.d("gps", "Location permission did not work!");
                    }
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
}
