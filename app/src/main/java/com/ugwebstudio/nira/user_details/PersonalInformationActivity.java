package com.ugwebstudio.nira.user_details;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.ugwebstudio.nira.R;

public class PersonalInformationActivity extends AppCompatActivity {

    private TextInputEditText etFirstName, etMiddleName, etLastName, etDateOfBirth, etPlaceOfBirth;
    private Spinner spinnerGender;
    private Button btnNext;
    private static final String PREF_NAME = "UserData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etLastName = findViewById(R.id.etLastName);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etPlaceOfBirth = findViewById(R.id.etPlaceOfBirth);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnNext = findViewById(R.id.btnNext);

        // Set up gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        btnNext.setOnClickListener(view -> validateAndForward());
    }

    private void validateAndForward() {
        String firstName = etFirstName.getText().toString().trim();
        String middleName = etMiddleName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        String placeOfBirth = etPlaceOfBirth.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();

        if (validateInputs(firstName, lastName, dateOfBirth, placeOfBirth)) {
            saveUserDataToSharedPreferences(firstName, middleName, lastName, dateOfBirth, placeOfBirth, gender);

            // Inputs are valid, forward to the next intent or perform further actions
            Intent intent = new Intent(PersonalInformationActivity.this, ContactInfoActivity.class);
      
            startActivity(intent);
        } else {
            // Show a message indicating that some inputs are invalid
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserDataToSharedPreferences(String firstName, String middleName,
                                                 String lastName, String dateOfBirth,
                                                 String placeOfBirth, String gender) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("firstName", firstName);
        editor.putString("middleName", middleName);
        editor.putString("lastName", lastName);
        editor.putString("dateOfBirth", dateOfBirth);
        editor.putString("placeOfBirth", placeOfBirth);
        editor.putString("gender", gender);

        editor.apply();
    }

    private boolean validateInputs(String firstName, String lastName,
                                   String dateOfBirth, String placeOfBirth) {
        return !firstName.isEmpty() &&  !lastName.isEmpty() && !dateOfBirth.isEmpty()
                && !placeOfBirth.isEmpty();
    }
}

