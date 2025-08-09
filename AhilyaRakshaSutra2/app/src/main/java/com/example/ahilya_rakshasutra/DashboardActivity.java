package com.example.ahilya_rakshasutra;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    Button btnWhatsapp,btnProfile, btnSMS, btnURL, btnNotification;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnWhatsapp = findViewById(R.id.btnWhatsapp);
        btnSMS = findViewById(R.id.btnSMS);
        btnURL = findViewById(R.id.btnURL);
        btnProfile = findViewById(R.id.btnProfile);
        btnNotification = findViewById(R.id.btnNotification);

        btnWhatsapp.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, WhatsappFormActivity.class));
        });
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
        });
        btnSMS.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, SMSFormActivity.class));
        });
        btnNotification.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, NotificationActivity.class));
        });

        btnURL.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, URLFormActivity.class));
        });
    }
}
