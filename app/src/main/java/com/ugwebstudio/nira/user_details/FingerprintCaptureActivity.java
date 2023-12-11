package com.ugwebstudio.nira.user_details;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ugwebstudio.nira.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FingerprintCaptureActivity extends AppCompatActivity {

    private LinearLayout layoutCapturedFingerprints;
    private TextView textNextFinger;
    private Button btnCaptureLeftFingerprint;
    private Button btnCaptureRightFingerprint;
    private static final String PREF_NAME = "UserData";
    private int currentFingerIndex = 0;
    private List<Bitmap> capturedFingerprints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_capture);

        // Initialize UI elements
        layoutCapturedFingerprints = findViewById(R.id.layoutCapturedFingerprints);
        textNextFinger = findViewById(R.id.textNextFinger);
        btnCaptureLeftFingerprint = findViewById(R.id.btnCaptureLeftFingerprint);
        btnCaptureRightFingerprint = findViewById(R.id.btnCaptureRightFingerprint);

        Button btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(view -> {

            // Convert the list of bitmaps to a list of byte arrays
            List<byte[]> fingerprintByteArrays = new ArrayList<>();
            for (Bitmap fingerprint : capturedFingerprints) {
                fingerprintByteArrays.add(bitmapToByteArray(fingerprint));
            }

            Intent intent;

            // Forward data to the next activity
            intent = new Intent(FingerprintCaptureActivity.this, FamilyInformationActivity.class);


            saveUserDataToSharedPreferences( capturedFingerprints);
            startActivity(intent);
        });

        btnCaptureLeftFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent("left");
            }
        });

        btnCaptureRightFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent("right");
            }
        });

        updateNextFingerText();
    }

    private void saveUserDataToSharedPreferences(List<Bitmap> capturedFingerprints) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();


        editor.putString("capturedFingerprints", capturedFingerprints.toString());

        editor.apply();
    }


    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent(String hand) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Pass the hand information as an extra to the camera intent
            takePictureIntent.putExtra("hand", hand);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Retrieve the hand information
            String hand = data.getStringExtra("hand");

            // Save the bitmap as an image file
            saveBitmapAsImageFile(imageBitmap, hand);

            // Add the captured fingerprint to the list
            capturedFingerprints.add(imageBitmap);

            // Update UI to display captured fingerprints
            updateCapturedFingerprints();

            // Move to the next finger
            currentFingerIndex++;

            // Update UI to indicate the next finger
            updateNextFingerText();
        }
    }

    private void saveBitmapAsImageFile(Bitmap bitmap, String hand) {
        // Save the bitmap as an image file using FileOutputStream
        File imageFile = new File(getExternalFilesDir(null), "fingerprint_image_" + hand + "_" + currentFingerIndex + ".jpg");
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateCapturedFingerprints() {
        // Clear existing views
        layoutCapturedFingerprints.removeAllViews();

        // Add image views for each captured fingerprint
        for (Bitmap fingerprint : capturedFingerprints) {
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(fingerprint);
            layoutCapturedFingerprints.addView(imageView);
        }
    }

    private void updateNextFingerText() {
        String[] fingerNames = {"Thumb", "Index Finger", "Middle Finger", "Ring Finger", "Little Finger"};

        if (currentFingerIndex < fingerNames.length) {
            textNextFinger.setText("Next Finger: " + fingerNames[currentFingerIndex]);
        } else {
            textNextFinger.setText("All fingerprints captured");
            btnCaptureLeftFingerprint.setEnabled(false);
            btnCaptureRightFingerprint.setEnabled(false);
            Toast.makeText(this, "Fingerprint capture completed", Toast.LENGTH_SHORT).show();
        }
    }
}
