package com.example.ahilya_rakshasutra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button buttonLogin, buttonGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.email_input);
        editTextPassword = findViewById(R.id.password_input);
        buttonLogin = findViewById(R.id.login_button);
        buttonGoToRegister = findViewById(R.id.go_to_register); // 🔄 Hooking the Register button

        buttonLogin.setOnClickListener(v -> loginUser());

        // 🔁 Switch to RegisterActivity
        buttonGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:5000/api/auth/login"); // Adjust URL if needed
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject requestBody = new JSONObject();
                requestBody.put("email", email);
                requestBody.put("password", password);

                OutputStream os = conn.getOutputStream();
                os.write(requestBody.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Scanner scanner = new Scanner(conn.getInputStream());
                StringBuilder response = new StringBuilder();

                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                runOnUiThread(() -> {
                    try {
                        JSONObject responseJson = new JSONObject(response.toString());
                        if (responseCode == 200) {
                            String token = responseJson.optString("token", null);
                            if (token != null) {
                                SharedPreferences preferences = getSharedPreferences("auth", MODE_PRIVATE);
                                preferences.edit().putString("jwt_token", token).apply();

                                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, DashboardActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Token missing in response", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing login response", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
