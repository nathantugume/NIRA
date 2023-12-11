package com.ugwebstudio.nira;



import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CheckStatusActivity extends AppCompatActivity {

    private TextView textStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_status);

        // Initialize UI elements
        textStatus = findViewById(R.id.textStatus);

        // Retrieve the currently logged-in user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Get the user ID
            String userId = currentUser.getUid();

            // Query Firestore to get user data
            FirebaseFirestore.getInstance().collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Get the user data status from Firestore
                                String status = document.getString("status");

                                // Display the status in the UI
                                textStatus.setText("User Data Status: " + status);
                            } else {
                                // Document does not exist
                                Toast.makeText(CheckStatusActivity.this, "User document does not exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Failed to get user data from Firestore
                            Toast.makeText(CheckStatusActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // User not logged in
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
