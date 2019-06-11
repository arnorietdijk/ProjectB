package com.example.test;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.info_window, null);

        TextView name_tv = view.findViewById(R.id.title);
        TextView details_tv = view.findViewById(R.id.des);
        ImageView img = view.findViewById(R.id.pic);

        TextView hotel_tv = view.findViewById(R.id.loc);
        /*TextView food_tv = view.findViewById(R.id.food);
        TextView transport_tv = view.findViewById(R.id.transport);*/

        name_tv.setText(marker.getTitle());
        details_tv.setText(marker.getSnippet());

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();
        Bitmap bitmap = BitmapFactory.decodeByteArray(infoWindowData.getImage(), 0, infoWindowData.getImage().length);
        img.setImageBitmap(bitmap);

        hotel_tv.setText(infoWindowData.getHotel());
        /*food_tv.setText(infoWindowData.getFood());
        transport_tv.setText(infoWindowData.getTransport());*/

        return view;
    }
}