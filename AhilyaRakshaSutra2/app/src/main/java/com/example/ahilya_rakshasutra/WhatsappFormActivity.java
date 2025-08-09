package com.example.ahilya_rakshasutra;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class WhatsappFormActivity extends AppCompatActivity {

    EditText etWhatsappMsg, etWhatsappNumber, etWhatsappTime;
    Spinner spinnerLocation;
    Button btnSubmitWhatsapp;
    Calendar selectedTime;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatsapp_form);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etWhatsappMsg = findViewById(R.id.etWhatsappMsg);
        etWhatsappNumber = findViewById(R.id.etWhatsappNumber);
        etWhatsappTime = findViewById(R.id.etWhatsappTime);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        btnSubmitWhatsapp = findViewById(R.id.btnSubmitWhatsapp);

        selectedTime = Calendar.getInstance();

        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(
                this, R.array.locations, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationAdapter);

        etWhatsappTime.setOnClickListener(v -> {
            int hour = selectedTime.get(Calendar.HOUR_OF_DAY);
            int minute = selectedTime.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, hourOfDay, minute1) -> {
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute1);
                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                        etWhatsappTime.setText(formattedTime);
                    },
                    hour, minute, true);
            timePickerDialog.show();
        });

        btnSubmitWhatsapp.setOnClickListener(v -> {
            String msg = etWhatsappMsg.getText().toString().trim();
            String number = etWhatsappNumber.getText().toString().trim();
            String location = spinnerLocation.getSelectedItem().toString().trim();
            String timeInput = etWhatsappTime.getText().toString().trim();

            if (number.isEmpty() || msg.isEmpty() || timeInput.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String formattedTimestamp = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(selectedTime.getTime());

            sendWhatsappData(number, msg, formattedTimestamp, location);
        });
    }

    private void sendWhatsappData(String number, String msg, String time, String location) {
        new Thread(() -> {
            try {
                // ✅ Get JWT token from SharedPreferences
                SharedPreferences preferences = getSharedPreferences("auth", MODE_PRIVATE);
                String token = preferences.getString("jwt_token", null);

                if (token == null) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Authentication token missing. Please log in again.", Toast.LENGTH_LONG).show());
                    return;
                }

                URL url = new URL("http://10.0.2.2:5000/api/auth/whatsapp-form"); // backend route
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Authorization", "Bearer " + token); // ✅ Add JWT token
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("number", number);
                json.put("message", msg);
                json.put("time", time);
                json.put("location", location);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();

                runOnUiThread(() -> {
                    if (responseCode == 200 || responseCode == 201) {
                        Toast.makeText(this, "Submitted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Submission failed. Code: " + responseCode, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
