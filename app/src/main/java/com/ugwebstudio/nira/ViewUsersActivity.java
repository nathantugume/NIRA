package com.ugwebstudio.nira;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewUsersActivity extends AppCompatActivity {

    private ListView usersListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> userNames; // For simplicity, assuming users have only names

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        usersListView = findViewById(R.id.usersListView);
        userNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNames);
        usersListView.setAdapter(adapter);

        // Query Firestore to get user data
        queryUserData();

        // Set click listener for list items
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userName = userNames.get(position);
                // Implement actions for approve and decline based on userName
                // For simplicity, display a toast message
                Toast.makeText(ViewUsersActivity.this, "User: " + userName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void queryUserData() {
        // Assuming you have a collection named "user_data" in Firestore
        firestore.collection("user_data")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Assuming 'firstname' and 'lastname' are fields in your Firestore document
                                String firstName = document.getString("firstname");
                                String lastName = document.getString("lastname");
                                String userName = firstName + " " + lastName;

                                userNames.add(userName);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            // Handle errors
                            Toast.makeText(ViewUsersActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
