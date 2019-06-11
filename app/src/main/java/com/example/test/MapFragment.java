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
    List<Address> addresses = null;

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

        final CameraUpdate zoom = CameraUpdateFactory.zoomTo(5);
        final MarkerOptions mp = new MarkerOptions();
        final Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM FOOD;");
        // get all data from sqlite
            if(cursor.getCount() > 0)
            {
                if(cursor.moveToFirst())
                {
                    do
                    {
                        try
                        {
                            final int id = cursor.getInt(0);
                            final String title = cursor.getString(1);
                            final String description = cursor.getString(2);
                            final String loc = cursor.getString(3);
                            final byte[] image = cursor.getBlob(4);


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

                                Log.e("Inserted Marker", "inserted latitude " + latitude + ", inserted Longitude " + longitude);

                                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));

                                InfoWindowData info = new InfoWindowData();
                                info.setImage(image);
                                info.setHotel(title);
                                info.setFood(description);
                                info.setTransport(loc);

                                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity());
                                mMap.setInfoWindowAdapter(customInfoWindow);

                                Marker m = mMap.addMarker(mp);
                                m.setTag(info);
                                mMap.moveCamera(center);
                                mMap.animateCamera(zoom);

                                m.showInfoWindow();
                            }
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

        }

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

