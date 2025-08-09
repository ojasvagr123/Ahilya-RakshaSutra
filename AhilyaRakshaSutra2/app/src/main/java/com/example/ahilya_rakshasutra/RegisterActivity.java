package com.example.ahilya_rakshasutra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    EditText nameInput, emailInput, numberInput, addressInput, passwordInput;
    Button registerButton;

    private static final String REGISTER_URL = "http://10.0.2.2:5000/api/auth/register";
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        numberInput = findViewById(R.id.number_input);
        addressInput = findViewById(R.id.address_input);
        passwordInput = findViewById(R.id.password_input);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String number = numberInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || number.isEmpty() || address.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                try {
                    URL url = new URL(REGISTER_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);

                    JSONObject requestBody = new JSONObject();
                    requestBody.put("name", name);
                    requestBody.put("email", email);
                    requestBody.put("number", number);
                    requestBody.put("address", address);
                    requestBody.put("password", password);

                    String jsonInputString = requestBody.toString();
                    Log.d(TAG, "Sending JSON: " + jsonInputString);

                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("UTF-8");
                        os.write(input, 0, input.length);
                    }

                    int responseCode = conn.getResponseCode();
                    Log.d(TAG, "Response Code: " + responseCode);

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    responseCode >= 200 && responseCode < 300 ?
                                            conn.getInputStream() : conn.getErrorStream(),
                                    "UTF-8"
                            )
                    );

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line.trim());
                    }
                    reader.close();

                    Log.d(TAG, "Response: " + response.toString());

                    runOnUiThread(() -> {
                        if (responseCode >= 200 && responseCode < 300) {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, DashboardActivity.class));
                        } else {
                            Toast.makeText(this, "Failed: " + response, Toast.LENGTH_LONG).show();
                        }
                    });

                    conn.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "Exception: ", e);
                    runOnUiThread(() ->
                            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
                }
            }).start();
        });
    }
}
