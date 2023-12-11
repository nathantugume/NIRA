package com.ugwebstudio.nira.user_details;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ugwebstudio.nira.MainActivity;

import java.util.Map;

public class FirebaseHandler {

    private static final String COLLECTION_NAME = "user_data"; // Change this to your desired collection name
    private FirebaseFirestore firestore;

    public FirebaseHandler() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void saveUserData(Map<String, Object> userData, Context context) {
        firestore.collection(COLLECTION_NAME)
                .add(userData)
                .addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    Toast.makeText(context, "Data saved successfully!!", Toast.LENGTH_SHORT).show();

                    // Navigate to MainActivity
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}
