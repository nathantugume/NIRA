package com.ugwebstudio.nira.user_details;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.ugwebstudio.nira.R;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EducationEmploymentActivity extends AppCompatActivity {
    private String firstName, middleName, lastName, dateOfBirth, placeOfBirth, gender, email, phone, district, constituency, subcounty, parish, imagePath;
    private TextInputEditText editTextEducation, editTextOccupation, editTextEmployer;
    private FirebaseHandler firebaseHandler;
    Intent intent = getIntent();
    String base64EncodedString;
    private static final String PREF_NAME = "UserData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_employment);
        SignaturePad signaturePad = findViewById(R.id.signaturePad);

        Button btnClear = findViewById(R.id.signaturePadClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signaturePad.clear();
            }
        });

        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
              //  Bitmap signatureBitmap = signaturePad.getSignatureBitmap(); // Get the signature bitmap

                Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                base64EncodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);



            }

            @Override
            public void onClear() {

            }
        });


        // Initialize UI elements
        editTextEducation = findViewById(R.id.editTextEducation);
        editTextOccupation = findViewById(R.id.editTextOccupation);
        editTextEmployer = findViewById(R.id.editTextEmployer);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(view -> saveDataToFirestore());

        firebaseHandler = new FirebaseHandler();
    }
    private void fetchUserData() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        firstName = preferences.getString("firstName", "");
        middleName = preferences.getString("middleName", "");
        lastName = preferences.getString("lastName", "");
        dateOfBirth = preferences.getString("dateOfBirth", "");
        placeOfBirth = preferences.getString("placeOfBirth", "");
        gender = preferences.getString("gender", "");
        email = preferences.getString("email", "");
        phone = preferences.getString("phone", "");
        district = preferences.getString("district", "");
        constituency = preferences.getString("constituency", "");
        subcounty = preferences.getString("subcounty", "");
        parish = preferences.getString("parish", "");
        imagePath = preferences.getString("image", "");

        // Update UI or perform any other actions with the retrieved data
    }
    private void saveDataToFirestore() {
        fetchUserData();
        String education = editTextEducation.getText().toString().trim();
        String occupation = editTextOccupation.getText().toString().trim();
        String employer = editTextEmployer.getText().toString().trim();
        FirebaseAuth  firebaseAuth = FirebaseAuth.getInstance();
        String currentUser = firebaseAuth.getCurrentUser().getUid();



        // Validate the inputs
        if (validateInputs(education, occupation, employer)) {
            // Create a map to store data
            Map<String, Object> userData = new HashMap<>();
            userData.put("firstName", firstName);
            userData.put("middleName", middleName);
            userData.put("lastName", lastName);
            userData.put("dateOfBirth", dateOfBirth);
            userData.put("placeOfBirth", placeOfBirth);
            userData.put("gender", gender);
            userData.put("email", email);
            userData.put("phone", phone);
            userData.put("district", district);
            userData.put("constituency", constituency);
            userData.put("subcounty", subcounty);
            userData.put("parish", parish);
            userData.put("image", imagePath);
            userData.put("education", education);
            userData.put("occupation", occupation);
            userData.put("employer", employer);
            userData.put("userId",currentUser);

            userData.put("signatureImage", base64EncodedString);

            // Retrieve fingerprints data



            // Save data to Firebase Firestore
            firebaseHandler.saveUserData(userData,EducationEmploymentActivity.this);
        }
    }

    private boolean validateInputs(String education, String occupation, String employer) {
        // Implement your validation logic
        if (education.isEmpty()) {
            // Display an error message for empty education field
            showToast("Education field cannot be empty");
            return false;
        }

        if (occupation.isEmpty()) {
            // Display an error message for empty occupation field
            showToast("Occupation field cannot be empty");
            return false;
        }

        if (employer.isEmpty()) {
            // Display an error message for empty employer field
            showToast("Employer field cannot be empty");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
