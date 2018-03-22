package com.example.jeandan.smart_task;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeclenchorActivity extends AppCompatActivity  implements SensorEventListener, View.OnClickListener {

    private static final String TAG = "Storage#MainActivity";

    float xAccel, yAccel, zAccel;
    float xPreviousAccel, yPreviousAccel, zPreviousAccel;


    private static final int RC_TAKE_PICTURE = 101;


    boolean firstUpdate = true;
    boolean shakeInitiated = false;
    float shakeThreshold = 12.5F;
    private TextView text1;
    private TextView text2;
    private TextView text3;
    private ImageView img;
    private Bitmap bit;
    private Boolean camera = true;
    private StorageReference mStorageRef;


    private BroadcastReceiver mBroadcastReceiver;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;

    private Uri photoURI=null;

    String mCurrentPhotoPath;

    Sensor accelerometer;
    SensorManager sm;

    public IBinder onBind(Intent intent) {
        return null;
    }

        public void onCreate(Bundle savedInstanceState)
        {
            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            super.onCreate(savedInstanceState);
            setContentView(R.layout.declenchor_activity);
            // Click listeners
            findViewById(R.id.button_upload).setOnClickListener(this);

            sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sm.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);


            text1 = (TextView) findViewById(R.id.textView1);
            text2 = (TextView) findViewById(R.id.textView2);
            text3 = (TextView) findViewById(R.id.textView3);


            /*text1.setText("x = "+Float.toString(xAccel));
            text2.setText("y = "+Float.toString(yAccel));
            text3.setText("z = "+Float.toString(zAccel));*/

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {

        }




        @Override
        public void onSensorChanged(SensorEvent event)
        {
            updateAccelParameters (event.values[0], event.values[1], event.values[2]);

            if ((!shakeInitiated)  && isAccelerationChanged())
            {
                shakeInitiated = true;
            }
            else if ((shakeInitiated) && isAccelerationChanged())
            {
                executeShakeAction();
            }
            else if ((shakeInitiated) && !isAccelerationChanged())
            {
                shakeInitiated = false;
            }




        }

        private void executeShakeAction()
        {
            Intent ii = new Intent(this, DeclenchorActivity.class);
            ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(ii);
        }

        private boolean isAccelerationChanged()
        {
            float deltaX = Math.abs(xPreviousAccel - xAccel);
            float deltaY = Math.abs(yPreviousAccel - yAccel);
            float deltaZ = Math.abs(zPreviousAccel - zAccel);

            return (deltaX > shakeThreshold && deltaY > shakeThreshold)
                    || (deltaX > shakeThreshold && deltaZ > shakeThreshold)
                    || (deltaY > shakeThreshold && deltaZ > shakeThreshold);

        }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


        private void updateAccelParameters(float xNewAccel, float yNewAccel, float zNewAccel) {
            if (firstUpdate) {
                xPreviousAccel = xNewAccel;
                yPreviousAccel = yNewAccel;
                zPreviousAccel = zNewAccel;
                firstUpdate = false;
            } else {
                xPreviousAccel = xAccel;
                yPreviousAccel = yAccel;
                zPreviousAccel = zAccel;
            }
            xAccel = xNewAccel;
            yAccel = yNewAccel;
            zAccel = zNewAccel;

            if (yNewAccel > 8 && camera == true) {
                //On prend la photo

                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
                camera = false;*/
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 0);
                        StorageReference riversRef = mStorageRef.child("images/rivers.jpg");


                        /*signInAnonymously();
                        Uri mFileUri = takePictureIntent.getParcelableExtra(MyUploadService.EXTRA_FILE_URI);

                        uploadFromUri(mFileUri);*/

                        riversRef.putFile(photoURI)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
                                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        //uploadFromUri(downloadUrl);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        // ...
                                    }
                                });
                    }

                        //startActivityForResult(intent, 0);
                        camera = false;

                    }
                }
                text1.setText("x = " + Float.toString(Math.round(xNewAccel)));
                text2.setText("y = " + Float.toString(Math.round(yNewAccel)));
                text3.setText("z = " + Float.toString(Math.round(zNewAccel)));

        }


    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    private void signInAnonymously() {
        // Sign in anonymously. Authentication is required to read or write from Firebase Storage.
        showProgressDialog("connexion");
        // Sign in anonymously. Authentication is required to read or write from Firebase Storage.
        mAuth.signInAnonymously()
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "signInAnonymously:SUCCESS");
                        hideProgressDialog();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "signInAnonymously:FAILURE", exception);
                        hideProgressDialog();
                    }
                });
    }
    private void launchCamera() {
        Log.d(TAG, "launchCamera");

        // Pick an image from storage
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_TAKE_PICTURE);
    }
    private void uploadFromUri(Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());



        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        startService(new Intent(this, MyUploadService.class)
                .putExtra(MyUploadService.EXTRA_FILE_URI, fileUri)
                .setAction(MyUploadService.ACTION_UPLOAD));

        // Show loading spinner
        showProgressDialog("Upload photo...");
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

         protected void onActivityResult(int requestCode, int resultCode, Intent data) {
             Toast.makeText(this, "Photo sauvegardée dans la tablette", Toast.LENGTH_SHORT).show();
            //super.onActivityResult(requestCode, resultCode, data);
             /*bit = (Bitmap) data.getExtras().get("data");
             img.setImageBitmap(bit);*/
             /*if (requestCode == 1 && resultCode == RESULT_OK) {
                 bit = (Bitmap) data.getExtras().get("data");
                 img.setImageBitmap(bit);
             }*/
            //galleryAddPic();
            //camera = true
             
        }

    @Override
    public void onClick(View v) {

        signInAnonymously();
        //uploadFromUri(photoURI);
    }
}


