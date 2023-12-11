package com.ugwebstudio.nira;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        CardView cardViewViewUsers = findViewById(R.id.cardViewViewUsers);
        CardView cardViewLogout = findViewById(R.id.cardViewLogout);

        cardViewLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();

            // Redirect the user to the login or splash screen
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();  //
        });

        cardViewViewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this, ViewUsersActivity.class));
            }
        });


    }
}