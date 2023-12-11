package com.ugwebstudio.nira.user_details;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.ugwebstudio.nira.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ContactInfoActivity extends AppCompatActivity {
    private TextInputEditText etEmail, etPhone;
    private Spinner spinnerDistrict, spinnerConstituency, spinnerSubcounty, spinnerParish;
    private List<Geodata> geodataList;
    private static final String JSON_FILE_NAME = "data.json";
    private static final String PREF_NAME = "UserData";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerConstituency = findViewById(R.id.spinnerConstituency);
        spinnerSubcounty = findViewById(R.id.spinnerSubcounty);
        spinnerParish = findViewById(R.id.spinnerParish);


        // Read and parse JSON file asynchronously
        new LoadJsonDataTask().execute();

        // Additional code...
    }

    private class LoadJsonDataTask extends AsyncTask<Void, Void, List<Geodata>> {

        @Override
        protected List<Geodata> doInBackground(Void... voids) {
            return readAndParseJsonFile();
        }

        @Override
        protected void onPostExecute(List<Geodata> result) {
            geodataList = result;

            // Populate the district spinner dynamically
            Spinner spinnerDistrict = findViewById(R.id.spinnerDistrict);
            List<String> districtOptions = getUniqueValues("DISTRICT");
            Log.d("district list", String.valueOf(districtOptions.isEmpty()));

            ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(ContactInfoActivity.this,
                    android.R.layout.simple_spinner_item, districtOptions);
            districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDistrict.setAdapter(districtAdapter);

            // Set a listener to handle district selection
            spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selectedDistrict = spinnerDistrict.getSelectedItem().toString();
                    // Update the other spinners based on the selected district
                    updateOtherSpinners(selectedDistrict);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Do nothing here
                }
            });


            // Next Button
            Button btnNext = findViewById(R.id.btnNext);
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validateAndForward();
                }
            });
        }
    }
    private void validateAndForward() {
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String district = spinnerDistrict.getSelectedItem().toString();
        String constituency = spinnerConstituency.getSelectedItem().toString();
        String subcounty = spinnerSubcounty.getSelectedItem().toString();
        String parish = spinnerParish.getSelectedItem().toString();

        if (validateInputs(email, phone, district, constituency, subcounty, parish)) {
            // Inputs are valid, forward to the next intent or perform further actions
            Intent intent = new Intent(ContactInfoActivity.this, TakePicActivity.class);
            // Put the validated inputs as extras to pass to the next activity

            saveUserDataToSharedPreferences( email, phone, district, constituency, subcounty, parish);

            startActivity(intent);
        }
    }

    private void saveUserDataToSharedPreferences(
                                                 String email, String phone,
                                                 String district, String constituency,
                                                 String subcounty, String parish) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.putString("district", district);
        editor.putString("constituency", constituency);
        editor.putString("subcounty", subcounty);
        editor.putString("parish", parish);

        editor.apply();
    }


    private boolean validateInputs(String email, String phone, String district,
                                   String constituency, String subcounty, String parish) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            return false;
        }

        if (phone.isEmpty() || !android.util.Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Enter a valid phone number");
            return false;
        }

        if (district.isEmpty()) {
            // Handle district validation, e.g., show an error for an empty district
            return false;
        }

        if (constituency.isEmpty()) {
            // Handle constituency validation
            return false;
        }

        if (subcounty.isEmpty()) {
            // Handle subcounty validation
            return false;
        }

        if (parish.isEmpty()) {
            // Handle parish validation
            return false;
        }

        // All validations passed
        return true;
    }

    private List<String> getUniqueValues(String field) {
        List<String> options = new ArrayList<>();

        for (Geodata data : geodataList) {
            if (data != null) {

                Log.d("geodata", "Geodata: " + data.toString());

                switch (field) {
                    case "DISTRICT":
                        if (data.getDISTRICT() != null && !options.contains(data.getDISTRICT())) {
                            options.add(data.getDISTRICT());
                        }
                        break;
                    case "CONSTITUENCY":
                        if (data.getCONSTITUENCY() != null && !options.contains(data.getCONSTITUENCY())) {
                            options.add(data.getCONSTITUENCY());
                        }
                        break;
                    case "SUBCOUNTY":
                        if (data.getSUBCOUNTY() != null && !options.contains(data.getSUBCOUNTY())) {
                            options.add(data.getSUBCOUNTY());
                        }
                        break;
                    case "PARISH":
                        if (data.getPARISH() != null && !options.contains(data.getPARISH())) {
                            options.add(data.getPARISH());
                        }
                        break;
                    // Add cases for other fields
                }
            }
            else {
                Log.d("geodata", "null");
            }
        }

        return options;
    }

    // Additional methods for other fields...

    private void updateOtherSpinners(String selectedDistrict) {
        // Update Constituency Spinner
        Spinner spinnerConstituency = findViewById(R.id.spinnerConstituency);
        List<String> constituencyOptions = getUniqueValues("CONSTITUENCY", selectedDistrict);

        ArrayAdapter<String> constituencyAdapter = new ArrayAdapter<>(ContactInfoActivity.this,
                android.R.layout.simple_spinner_item, constituencyOptions);
        constituencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConstituency.setAdapter(constituencyAdapter);

        // Set a listener to handle constituency selection
        spinnerConstituency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedConstituency = spinnerConstituency.getSelectedItem().toString();

                // Update the subcounty spinner based on the selected constituency
                updateSubcountySpinner(selectedDistrict, selectedConstituency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    private List<String> getUniqueValues(String field, String selectedDistrict) {
        List<String> options = new ArrayList<>();

        for (Geodata data : geodataList) {
            if (data != null && selectedDistrict.equals(data.getDISTRICT())) {
                switch (field) {
                    case "DISTRICT":
                        if (data.getDISTRICT() != null && !options.contains(data.getDISTRICT())) {
                            options.add(data.getDISTRICT());
                            Log.d("SpinnerData", "DISTRICT: " + data.getDISTRICT());
                        }
                        break;
                    case "CONSTITUENCY":
                        if (data.getCONSTITUENCY() != null && !options.contains(data.getCONSTITUENCY())) {
                            options.add(data.getCONSTITUENCY());
                            Log.d("SpinnerData", "CONSTITUENCY: " + data.getCONSTITUENCY());
                        }
                        break;
                    case "SUBCOUNTY":
                        if (data.getSUBCOUNTY() != null && !options.contains(data.getSUBCOUNTY())) {
                            options.add(data.getSUBCOUNTY());
                            Log.d("SpinnerData", "SUBCOUNTY: " + data.getSUBCOUNTY());
                        }
                        break;
                    case "PARISH":
                        if (data.getPARISH() != null && !options.contains(data.getPARISH())) {
                            options.add(data.getPARISH());
                            Log.d("SpinnerData", "PARISH: " + data.getPARISH());
                        }
                        break;
                    // Add cases for other fields
                }
            }
        }

        return options;
    }


    private List<String> getUniqueValues(String field, String selectedDistrict, String selectedConstituency) {
        List<String> options = new ArrayList<>();

        for (Geodata data : geodataList) {
            if (data != null && selectedDistrict.equals(data.getDISTRICT())
                    && selectedConstituency.equals(data.getCONSTITUENCY())) {
                switch (field) {
                    case "CONSTITUENCY":
                        if (data.getCONSTITUENCY() != null && !options.contains(data.getCONSTITUENCY())) {
                            options.add(data.getCONSTITUENCY());
                        }
                        break;
                    case "SUBCOUNTY":
                        if (data.getSUBCOUNTY() != null && !options.contains(data.getSUBCOUNTY())) {
                            options.add(data.getSUBCOUNTY());
                        }
                        break;
                    case "PARISH":
                        if (data.getPARISH() != null && !options.contains(data.getPARISH())) {
                            options.add(data.getPARISH());
                        }
                        break;
                    // Add cases for other fields
                }
            }
        }

        return options;
    }


    private void updateSubcountySpinner(String selectedDistrict, String selectedConstituency) {
        // Update Subcounty Spinner
        Spinner spinnerSubcounty = findViewById(R.id.spinnerSubcounty);
        List<String> subcountyOptions = getUniqueValues("SUBCOUNTY", selectedDistrict, selectedConstituency);

        ArrayAdapter<String> subcountyAdapter = new ArrayAdapter<>(ContactInfoActivity.this,
                android.R.layout.simple_spinner_item, subcountyOptions);
        subcountyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubcounty.setAdapter(subcountyAdapter);

        // Set a listener to handle subcounty selection
        spinnerSubcounty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSubcounty = spinnerSubcounty.getSelectedItem().toString();
                // Update the parish spinner based on the selected subcounty
                updateParishSpinner(selectedDistrict, selectedConstituency, selectedSubcounty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    private void updateParishSpinner(String selectedDistrict, String selectedConstituency, String selectedSubcounty) {
        // Update Parish Spinner


        Log.d("SpinnerData", "Before setting adapter in updateParishSpinner");

        Spinner spinnerParish = findViewById(R.id.spinnerParish);
        List<String> parishOptions = getUniqueValues("PARISH", selectedDistrict, selectedConstituency, selectedSubcounty);


        Log.d("SpinnerData", "Parish Options Size before setAdapter: " + parishOptions.size());

        ArrayAdapter<String> parishAdapter = new ArrayAdapter<>(ContactInfoActivity.this,
                android.R.layout.simple_spinner_item, parishOptions);
        parishAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParish.setAdapter(parishAdapter);
        parishAdapter.notifyDataSetChanged();

    }

    private List<String> getUniqueValues(String field, String selectedDistrict, String selectedConstituency, String selectedSubcounty) {
        List<String> options = new ArrayList<>();

        for (Geodata data : geodataList) {
            if ("PARISH".equals(field) && selectedDistrict.equals(data.getDISTRICT())
                    && selectedConstituency.equals(data.getCONSTITUENCY())
                    && selectedSubcounty.equals(data.getSUBCOUNTY())) {
                if (!options.contains(data.getPARISH())) {
                    options.add(data.getPARISH());


                }
            }
        }



        return options;
    }



    private List<Geodata> readAndParseJsonFile() {
        List<Geodata> geodataList = new ArrayList<>();

        try {
            InputStream inputStream = getAssets().open(JSON_FILE_NAME);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            // Use Gson to parse the JSON file into a GeodataContainer object
            Gson gson = new Gson();
            Geodata.GeodataContainer geodataContainer = gson.fromJson(inputStreamReader, Geodata.GeodataContainer.class);

            if (geodataContainer != null && geodataContainer.getData() != null) {
                geodataList = geodataContainer.getData();
            }

            inputStream.close();
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return geodataList;
    }
}
