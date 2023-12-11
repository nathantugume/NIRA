package com.ugwebstudio.nira;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.ugwebstudio.nira.user_details.ContactInfoActivity;
import com.ugwebstudio.nira.user_details.PersonalInformationActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnContact = findViewById(R.id.btnRegister);
        Button chkStatus = findViewById(R.id.btnCheckStatus);
        Button logout = findViewById(R.id.btnLogout);

        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();

            // Redirect the user to the login or splash screen
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();  //
        });

        chkStatus.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, CheckStatusActivity.class)));

        btnContact.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PersonalInformationActivity.class)));
    }
}