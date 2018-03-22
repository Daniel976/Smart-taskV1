package com.example.jeandan.smart_task;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class TorchActivity extends AppCompatActivity {

    private ImageButton myTorchButton;
    private boolean hasCameraFlash, flashLightActivate=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torch);

        myTorchButton = (ImageButton) findViewById(R.id.buttonTorch);
        hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        myTorchButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(hasCameraFlash){
                    flashLight(flashLightActivate);

                }else{
                    Toast.makeText(TorchActivity.this, "No flash available on your device", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void flashLight(boolean flashState){
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, !flashState);
            flashLightActivate = !flashState;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
