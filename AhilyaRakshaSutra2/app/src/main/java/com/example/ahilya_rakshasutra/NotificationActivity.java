package com.example.ahilya_rakshasutra;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private ListView listViewNotifications;
    private ArrayList<String> notificationsList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        listViewNotifications = findViewById(R.id.listViewNotifications);
        notificationsList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationsList);
        listViewNotifications.setAdapter(adapter);

        // Example: Adding alerts from outside (this could be from BroadcastReceiver or API)
        addNotification("Emergency Alert: Suspicious activity reported in Indore.");
        addNotification("Reminder: Cyber awareness webinar today at 5 PM.");
    }

    public void addNotification(String message) {
        notificationsList.add(0, message); // Add at top
        adapter.notifyDataSetChanged();
    }
}
