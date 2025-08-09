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

public class SMSFormActivity extends AppCompatActivity {

    EditText etSmsText, etSmsNumber, etSmsTime;
    Spinner spinnerLocation;
    Button btnSubmitSms;
    Calendar selectedTime;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_form);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etSmsText = findViewById(R.id.etSmsText);
        etSmsNumber = findViewById(R.id.etSmsNumber);
        etSmsTime = findViewById(R.id.etSmsTime);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        btnSubmitSms = findViewById(R.id.btnSubmitSms);

        selectedTime = Calendar.getInstance();

        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(
                this, R.array.locations, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationAdapter);

        etSmsTime.setOnClickListener(v -> {
            int hour = selectedTime.get(Calendar.HOUR_OF_DAY);
            int minute = selectedTime.get(Calendar.MINUTE);
            new TimePickerDialog(this, (view, h, m) -> {
                selectedTime.set(Calendar.HOUR_OF_DAY, h);
                selectedTime.set(Calendar.MINUTE, m);
                etSmsTime.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m));
            }, hour, minute, true).show();
        });

        btnSubmitSms.setOnClickListener(v -> {
            String smsText = etSmsText.getText().toString().trim();
            String number = etSmsNumber.getText().toString().trim();
            String location = spinnerLocation.getSelectedItem().toString().trim();
            String timeInput = etSmsTime.getText().toString().trim();

            if (smsText.isEmpty() || number.isEmpty() || timeInput.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String formattedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(selectedTime.getTime());

            sendSmsData(number, smsText, formattedTime, location);
        });
    }

    private void sendSmsData(String number, String smsText, String time, String location) {
        new Thread(() -> {
            try {
                SharedPreferences preferences = getSharedPreferences("auth", MODE_PRIVATE);
                String token = preferences.getString("jwt_token", null);

                if (token == null) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Authentication token missing. Please log in again.", Toast.LENGTH_LONG).show());
                    return;
                }

                URL url = new URL("http://10.0.2.2:5000/api/auth/sms-form");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("number", number);
                json.put("message", smsText);
                json.put("location", location);
                json.put("time", time);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();

                runOnUiThread(() -> {
                    if (responseCode == 200 || responseCode == 201) {
                        Toast.makeText(this, "SMS submitted successfully", Toast.LENGTH_SHORT).show();
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
