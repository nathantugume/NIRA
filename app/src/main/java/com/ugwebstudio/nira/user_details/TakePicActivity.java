package com.ugwebstudio.nira.user_details;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Grid;
import com.otaliastudios.cameraview.controls.Mode;
import com.ugwebstudio.nira.R;

import java.io.FileOutputStream;
import java.io.IOException;

public class TakePicActivity extends AppCompatActivity {

    private CameraView cameraView;
    private boolean isBackCamera = true;
    private ImageView imagePreview;
    private String image;
    private static final String PREF_NAME = "UserData";
    private  String imageString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pic);
        Button btnNext = findViewById(R.id.btnNext);



        btnNext.setOnClickListener(view -> {
            Intent intent = getIntent();

            // Forward data to the next activity
            Intent nextIntent = new Intent(TakePicActivity.this, FingerprintCaptureActivity.class);
            saveUserDataToSharedPreferences( imageString);
             nextIntent.putExtra("image",image);
            startActivity(nextIntent);
                }

        );

        imagePreview = findViewById(R.id.imageViewPreview);
        // Initialize CameraView
        cameraView = findViewById(R.id.cameraView);
        cameraView.open();
        cameraView.setMode(Mode.PICTURE);
        cameraView.setFacing(Facing.FRONT);
        cameraView.setSnapshotMaxHeight(200);
        cameraView.setSnapshotMaxWidth(200);

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                super.onPictureTaken(result);
                handleCapturedImage(result);
            }
        });




        // Camera Toggle Button
        Button btnToggleCamera = findViewById(R.id.btnToggleCamera);
        btnToggleCamera.setOnClickListener(v -> {
            // Toggle between front and back cameras
            toggleCamera();
        });

        // Capture Button
        Button btnCapture = findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(v -> {
            // Handle the capture button click (e.g., save the photo)

            cameraView.takePicture();

        });
    }

    private void saveUserDataToSharedPreferences(String imageString) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();


        editor.putString("image", imageString);

        editor.apply();
    }

    private void handleCapturedImage(PictureResult result) {

        result.toBitmap(bitmapResult -> {
            // Display the captured image in an ImageView or any other view
            Log.d("image", bitmapResult.toString());
            imagePreview.setImageBitmap(bitmapResult);
            imageString = bitmapResult.toString();



            // Save the image to a file
            String imagePath = saveImageToFile(bitmapResult);
            if (imagePath != null) {
                // Display the captured image in an ImageView or any other view
                Log.d("image", imagePath);
                imagePreview.setImageBitmap(bitmapResult);
                image = imagePath;
            } else {
                Log.e("image", "Failed to save image to file");
            }
        });
    }

    private String saveImageToFile(Bitmap bitmapResult) {
        String filePath = getFilesDir() + "/captured_image.jpg";
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            bitmapResult.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void toggleCamera() {
        // Toggle between front and back cameras
        isBackCamera = !isBackCamera;
        cameraView.toggleFacing();
    }





}
