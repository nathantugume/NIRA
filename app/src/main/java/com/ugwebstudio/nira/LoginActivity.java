package com.ugwebstudio.nira;// LoginActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ugwebstudio.nira.MainActivity;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView textViewCreateAccount = findViewById(R.id.textViewCreateAccount);

        TextInputEditText editTextUsername = findViewById(R.id.editTextUsername);
        TextInputEditText editTextPassword = findViewById(R.id.editTextPassword);

        // Set click listeners
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validate the inputs
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    // Display an error if any field is empty
                    Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Perform the login
                loginUser(email, password);
            }
        });

        textViewCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        // Check the user role in Firestore
                        checkUserRole(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRole(FirebaseUser user) {
        if (user != null) {
            String userId = user.getUid();
            firestore.collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Get the user role from Firestore
                                String userRole = document.getString("userRole");

                                // Check the user role and navigate accordingly
                                if ("user".equals(userRole)) {
                                    // User is a regular user, navigate to MainActivity
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish(); // Finish the LoginActivity to prevent going back
                                } else if ("admin".equals(userRole)) {
                                    // User is an admin, navigate to AdminDashboardActivity
                                    startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                                    finish(); // Finish the LoginActivity to prevent going back
                                } else {
                                    // User role not defined, set it to "admin" and navigate to AdminDashboardActivity
                                    setDefaultUserRole();
                                }
                            } else {
                                // Document does not exist

                                Toast.makeText(LoginActivity.this, "User document does not exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Failed to get user role from Firestore
                            Toast.makeText(LoginActivity.this, "Failed to get user role", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setDefaultUserRole() {
        // Set the default user role to "admin" in Firestore
        String userId = firebaseAuth.getCurrentUser().getUid();
        firestore.collection("users").document(userId)
                .update("userRole", "admin")
                .addOnSuccessListener(aVoid -> {
                    // Navigate to AdminDashboardActivity
                    startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                    finish(); // Finish the LoginActivity to prevent going back
                })
                .addOnFailureListener(e -> {
                    // Failed to set user role
                    Toast.makeText(LoginActivity.this, "Failed to set user role", Toast.LENGTH_SHORT).show();
                });
    }

}
