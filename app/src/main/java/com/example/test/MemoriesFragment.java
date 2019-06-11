package com.example.test;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class MemoriesFragment extends Fragment {

    EditText edtTitle, edtDes, edtLoc;
    Button btnAdd, btnList, btnPic;
    ImageView imageView;
    CheckBox chkLoc;
    LocationTrack locationTrack;

    final int REQUEST_CODE_GALLERY = 999;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private LocationManager locationManager;
    private Location onlyOneLocation;
    private final int REQUEST_FINE_LOCATION = 1234;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_memories,
                container, false);

        edtTitle = (EditText) rootView.findViewById(R.id.edtTitle);
        edtDes = (EditText) rootView.findViewById(R.id.edtDes);
        edtLoc = (EditText) rootView.findViewById(R.id.edtLoc);
        Button btnChoose = (Button) rootView.findViewById(R.id.btnChoose);
        btnAdd = (Button) rootView.findViewById(R.id.btnAdd);
        btnList = (Button) rootView.findViewById(R.id.btnList);
        btnPic = (Button) rootView.findViewById(R.id.btnPic);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        chkLoc = (CheckBox) rootView.findViewById(R.id.chkLoc);
        final String strTitle = edtTitle.getText().toString().trim();
        final String strDes = edtDes.getText().toString().trim();
        final String strLoc = edtLoc.getText().toString().trim();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions( //Method of Fragment
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_GALLERY
                    );
                }
            }
        });
        btnPic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,
                        REQUEST_IMAGE_CAPTURE);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (TextUtils.isEmpty(strTitle) || TextUtils.isEmpty(strDes) || TextUtils.isEmpty(strLoc)) {
                        Toast.makeText(getActivity(), "please fill something in!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        MainActivity.sqLiteHelper.insertData(
                                edtTitle.getText().toString().trim(),
                                edtDes.getText().toString().trim(),
                                edtLoc.getText().toString().trim(),
                                imageViewToByte(imageView)
                        );

                        Toast.makeText(getActivity().getApplicationContext(), "Added successfully!", Toast.LENGTH_SHORT).show();

                        edtTitle.setText("");
                        edtDes.setText("");
                        edtLoc.setText("");
                        imageView.setImageResource(R.mipmap.ic_launcher);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MemoryList.class);
                startActivity(intent);
            }
        });

        chkLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationTrack = new LocationTrack(getActivity());
                boolean checked = ((CheckBox) v).isChecked();
                // Check which checkbox was clicked
                if (checked) {

                    if (locationTrack.canGetLocation()) {
                        double longitude = locationTrack.getLongitude();
                        double latitude = locationTrack.getLatitude();
                        final String address = getAddress(getActivity(),latitude,longitude);
                        //Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
                        edtLoc.setText(address);
                    } else {

                        locationTrack.showSettingsAlert();
                    }
                } else {
                    // Do your coding
                }
            }
        });

        return rootView;
    }

    public String getAddress(Context ctx, double lat, double lng){
        String fullAdd=null;
        try{
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat,lng,1);
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


    public void onLocationChanged(Location location) {
        onlyOneLocation = location;
        locationManager.removeUpdates((LocationListener) this);
    }
    public void onStatusChanged(String provider, int status, Bundle extras) { }
    public void onProviderEnabled(String provider) { }
    public void onProviderDisabled(String provider) { }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("gps", "Location permission granted");
                    try {
                        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        locationManager.requestLocationUpdates("gps", 0, 0, (LocationListener) this);
                    } catch (SecurityException ex) {
                        Log.d("gps", "Location permission did not work!");
                    }
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try {
                InputStream inputStream = getActivity().getApplicationContext().getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {

                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap

                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);

                imageView.setImageBitmap(bitmap);

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}