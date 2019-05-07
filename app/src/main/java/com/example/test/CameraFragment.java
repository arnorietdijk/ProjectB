package com.example.test;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import static android.app.Activity.RESULT_OK;


public class CameraFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView buckysImageView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState){
        Button buckysButton = (Button)getView().findViewById(R.id.buckysButton);
        ImageView imageView = (ImageView) getView().findViewById(R.id.buckysImageView);
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!hasCamera())
            buckysButton.setEnabled(false);

    }

    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void launchCamera(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            buckysImageView.setImageBitmap(photo);
        }
    }

}