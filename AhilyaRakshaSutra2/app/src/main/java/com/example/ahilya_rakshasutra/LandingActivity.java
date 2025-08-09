package com.example.ahilya_rakshasutra;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LandingActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}
