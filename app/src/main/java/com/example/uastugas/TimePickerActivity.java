package com.example.uastugas;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class TimePickerActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "alarmChannel";
    private TimePicker timePicker;
    private Button btnSetTimer;
    private Button btnCancelTimer;
    private EditText etNewTaskTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        // Initialize views
        timePicker = findViewById(R.id.timePicker);
        btnSetTimer = findViewById(R.id.btnTimer);
        btnCancelTimer = findViewById(R.id.btnTimer2);
        etNewTaskTitle = findViewById(R.id.NewTaskTitle);

        // Set default time on the TimePicker to current time
        Calendar now = Calendar.getInstance();
        timePicker.setHour(now.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(now.get(Calendar.MINUTE));

        // Button to set timer
        btnSetTimer.setOnClickListener(v -> setAlarm());

        // Button to cancel timer
        btnCancelTimer.setOnClickListener(v -> {
            cancelAlarm();
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void setAlarm() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String taskName = etNewTaskTitle.getText().toString().trim();

        if (taskName.isEmpty()) {
            taskName = "Unnamed Task";
        }

        Log.d("TimePickerActivity", "Task: " + taskName + ", Alarm set for " + hour + ":" + minute);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1); // Set for the next day if time is already passed
        }

        // Schedule the alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getPendingIntent(taskName);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("HOUR", hour);
            resultIntent.putExtra("MINUTE", minute);
            resultIntent.putExtra("TASK_NAME", taskName);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getPendingIntent(""); // Use an empty task name to cancel

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.d("TimePickerActivity", "Alarm cancelled");
        }
    }

    private PendingIntent getPendingIntent(String taskName) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("TASK_NAME", taskName);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.getBroadcast(this, taskName.hashCode(), intent, flags); // Use taskName hashCode for unique requestCode
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            createNotificationChannel(context);

            // Mengambil data dengan kunci "TASK_NAME"
            String taskName = intent.getStringExtra("TASK_NAME");
            if (taskName == null) {
                Log.e("AlarmReceiver", "No task name received!");
                return; // Menghentikan proses jika data tidak ada
            }

            Log.d("AlarmReceiver", "Alarm received for task: " + taskName);

            // Membuat notifikasi
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_alarm)
                    .setContentTitle("Alarm")
                    .setContentText("Reminder: " + taskName)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(taskName.hashCode(), notification); // Unique notification ID using taskName hash
        }

        private void createNotificationChannel(Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String name = "Alarm Notifications";
                String description = "Channel for alarm notifications";
                int importance = NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);

                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }
        }
    }
}
