package com.ugwebstudio.nira;// RegisterActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        TextInputEditText editTextEmail = findViewById(R.id.editTextEmail);
        TextInputEditText editTextPassword = findViewById(R.id.editTextPassword);

        // Set click listener for creating an account
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validate the inputs
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    // Display an error if any field is empty
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create an account with the specified email and password
                createUserAccount(email, password);
            }
        });
    }

    private void createUserAccount(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Account creation success
                        // Add logic to set user role as 'user' in Firestore
                        setDefaultUserRole();

                        // Navigate to the main activity or any other screen
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);

                        // Finish the current activity to prevent going back to the registration screen
                        finish();
                    } else {
                        // If account creation fails, display an error message
                        handleRegistrationError(task.getException());
                    }
                });
    }

    private void setDefaultUserRole() {
        // Get the user ID
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Create a map to store user details
        Map<String, Object> user = new HashMap<>();
        user.put("userRole", "user"); // Set the user role as 'user'

        // Add the user details to Firestore
        firestore.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // User role set successfully
                    Toast.makeText(this, "User role set to 'user'", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Error setting user role
                    Toast.makeText(this, "Failed to set user role. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void handleRegistrationError(Exception exception) {
        // Handle specific registration errors
        if (exception instanceof FirebaseAuthUserCollisionException) {
            // The email address is already in use by another account
            Toast.makeText(RegisterActivity.this, "Email is already in use.", Toast.LENGTH_SHORT).show();
        } else {
            // Other registration errors
            Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
