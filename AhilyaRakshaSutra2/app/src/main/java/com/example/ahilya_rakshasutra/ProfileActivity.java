package com.example.ahilya_rakshasutra;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvPhone, tvAddress;
    TableLayout tableWhatsapp, tableSms, tableUrl;
    private static final String PROFILE_URL = "http://10.0.2.2:5000/api/auth/profile"; // Replace with your endpoint
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);

        tableWhatsapp = findViewById(R.id.tableWhatsapp);
        tableSms = findViewById(R.id.tableSms);
        tableUrl = findViewById(R.id.tableUrl);

        fetchProfileData();
    }

    private void fetchProfileData() {
        new Thread(() -> {
            try {
                SharedPreferences preferences = getSharedPreferences("auth", MODE_PRIVATE);
                String token = preferences.getString("jwt_token", null);

                if (token == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Authentication token missing. Please log in again.", Toast.LENGTH_LONG).show());
                    return;
                }

                URL url = new URL(PROFILE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream()
                ));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Log.d(TAG, "API Response: " + response);

                if (responseCode >= 200 && responseCode < 300) {
                    JSONObject obj = new JSONObject(response.toString());

                    runOnUiThread(() -> {
                        try {
                            tvName.setText(obj.optString("name", "N/A"));
                            tvEmail.setText(obj.optString("email", "N/A"));
                            tvPhone.setText(obj.optString("number", "N/A"));
                            tvAddress.setText(obj.optString("address", "N/A"));

                            addTableHeader(tableWhatsapp, new String[]{"Number", "Message", "Time", "Location"});
                            addTableData(tableWhatsapp, obj.optJSONArray("whatsappReports"), new String[]{"number", "message", "time", "location"});

                            addTableHeader(tableSms, new String[]{"Number", "Message", "Time", "Location"});
                            addTableData(tableSms, obj.optJSONArray("smsReports"), new String[]{"number", "message", "time", "location"});

                            addTableHeader(tableUrl, new String[]{"URL", "Message", "Time", "Location"});
                            addTableData(tableUrl, obj.optJSONArray("urlReports"), new String[]{"url", "message", "time", "location"});
                        } catch (Exception e) {
                            Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show());
                }

                conn.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "Error: ", e);
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void addTableHeader(TableLayout table, String[] headers) {
        TableRow row = new TableRow(this);
        for (String header : headers) {
            TextView tv = new TextView(this);
            tv.setText(header);
            tv.setPadding(8, 8, 8, 8);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
            row.addView(tv);
        }
        table.addView(row);
    }

    private void addTableData(TableLayout table, JSONArray data, String[] keys) {
        if (data == null) return;
        for (int i = 0; i < data.length(); i++) {
            TableRow row = new TableRow(this);
            JSONObject item = data.optJSONObject(i);
            for (String key : keys) {
                TextView tv = new TextView(this);
                tv.setText(item.optString(key, ""));
                tv.setPadding(8, 8, 8, 8);
                row.addView(tv);
            }
            table.addView(row);
        }
    }
}
