package com.example.test;

import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Cursor cursor2;

    public MapFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_map, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        List<Address> addresses = null;
        final CameraUpdate zoom = CameraUpdateFactory.zoomTo(5);
        final MarkerOptions mp = new MarkerOptions();
        // get all data from sqlite
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT location FROM FOOD;");
        if(cursor.getCount() > 0)
        {
            if(cursor.moveToFirst())
            {
                do
                {
                    try
                    {
                        String loc = cursor.getString(0);

                        Geocoder geocoder = new Geocoder(getActivity());

                        try {
                            addresses = geocoder.getFromLocationName(loc, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (addresses.size() > 0) {
                            double latitude = addresses.get(0).getLatitude();
                            double longitude = addresses.get(0).getLongitude();

                            mp.position(new LatLng(latitude, longitude));

                            Log.e("teste map2", "inserted latitude " + latitude + ", inserted Longitude " + longitude);

                            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
                            final Marker m = mMap.addMarker(mp);
                            mMap.moveCamera(center);
                            mMap.animateCamera(zoom);

                            cursor2 = MainActivity.sqLiteHelper.getData("SELECT image FROM FOOD");
                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                                @Override
                                public void onInfoWindowClick(Marker arg0) {
                                    if(cursor2.getCount() > 0)
                                    {
                                        if(cursor2.moveToFirst())
                                        {
                                            do
                                            {
                                                try
                                                {
                                                    byte[] image = cursor2.getBlob(0);
                                                    InfoWindowData info = new InfoWindowData();
                                                    info.setImage(image);
                                                    info.setHotel("Hotel : excellent hotels available");
                                                    info.setFood("Food : all types of restaurants available");
                                                    info.setTransport("Reach the site by bus, car and train.");

                                                    CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity());
                                                    mMap.setInfoWindowAdapter(customInfoWindow);


                                                    m.showInfoWindow();
                                                }
                                                catch (IllegalStateException e)
                                                {
                                                    //Do Nothing
                                                }
                                                catch (NullPointerException e)
                                                {
                                                    //Do Nothing
                                                }
                                            }
                                            while(cursor2.moveToNext());

                                        }
                                    }
                                    cursor2.close();
                                    /*Intent intent = new Intent(getBaseContext(), Activity.class);
                                    String reference = mMarkerPlaceLink.get(arg0.getId());
                                    intent.putExtra("reference", reference);

                                    // Starting the  Activity
                                    startActivity(intent);
                                    Log.d("mGoogleMap1", "Activity_Calling");*/
                                }
                            });
                        }

                        /*Geocoder geocoder = new Geocoder(<your context>);
                        List<Address> addresses;
                        addresses = geocoder.getFromLocationName(<String address>, 1);
                        if(addresses.size() > 0) {
                            double latitude= addresses.get(0).getLatitude();
                            double longitude= addresses.get(0).getLongitude();
                        }


                        LatLng sydney = new LatLng(-34, 151);
                        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.latitude + " : " + latLng.longitude));
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            }
                        });*/
                        //list.add(Food.getLocation(loc));
                    }
                    catch (IllegalStateException e)
                    {
                        //Do Nothing
                    }
                    catch (NullPointerException e)
                    {
                        //Do Nothing
                    }
                }
                while(cursor.moveToNext());

            }
        }
        cursor.close();

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.latitude + " : " + latLng.longitude));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });*/
    }
}

