package com.ugwebstudio.nira.user_details;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ugwebstudio.nira.R;

import java.util.ArrayList;
import java.util.List;

public class FamilyInformationActivity extends AppCompatActivity {

    private TextInputEditText editTextParents, editTextSiblings;
    private static final String PREF_NAME = "UserData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_information);
        // Retrieve previous inputs from the intent

        // Initialize UI elements
        editTextParents = findViewById(R.id.editTextParents);
        editTextSiblings = findViewById(R.id.editTextSiblings);


        Button btnNext =  findViewById(R.id.btnNext);
        btnNext.setOnClickListener(view ->    validateAndForward());
    }

    private void validateAndForward() {
        String parents = editTextParents.getText().toString().trim();
        String siblings = editTextSiblings.getText().toString().trim();

        // Validate the new inputs
        if (validateNewInputs(parents, siblings)) {
            // Inputs are valid, forward to the next intent or perform further actions
            saveUserDataToSharedPreferences( parents,siblings);
            Intent nextIntent = new Intent(FamilyInformationActivity.this, EducationEmploymentActivity.class);


            startActivity(nextIntent);
        } else {
            // Show a message indicating that some inputs are invalid
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserDataToSharedPreferences(String parents, String siblings) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();


        editor.putString("parents",parents);
        editor.putString("siblings",siblings);
        editor.apply();
    }

    private boolean validateNewInputs(String parents, String siblings) {
        return !parents.isEmpty() && !siblings.isEmpty(); // You can add more validation as needed
    }
}