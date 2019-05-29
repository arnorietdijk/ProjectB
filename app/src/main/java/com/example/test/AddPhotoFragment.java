package com.example.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class AddPhotoFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView buckysImageView;
    Button buckysButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_camera,
                container, false);

        buckysButton = (Button) rootView.findViewById(R.id.buckysButton);
        buckysImageView = (ImageView) rootView.findViewById(R.id.buckysImageView);


        buckysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,
                        REQUEST_IMAGE_CAPTURE);

            }
        });
       /* memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap mBitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
// Pass it to intent to send in NextActitivy
                Intent intent = new Intent(getActivity(), AddActivity.class);
                intent.putExtra("captured_image", byteArray);
                startActivity(intent);
            }
        });*/



        return rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {

                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap

                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);

                buckysImageView.setImageBitmap(bitmap);

                Intent intent = new Intent(getActivity(), AddPhotoActivity.class);
                intent.putExtra("captured_image", byteArray);
                startActivity(intent);
            }
        }

    }
}