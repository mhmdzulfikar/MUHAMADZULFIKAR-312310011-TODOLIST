package com.example.uastugas;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve task details from the intent
        String taskName = intent.getStringExtra("TASK_NAME");

        if (taskName != null && !taskName.isEmpty()) {
            // Create a notification to display the task reminder
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            String channelId = "task_reminder_channel";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Task Reminder",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Notifications for task reminders");
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app's icon
                    .setContentTitle("Task Reminder")
                    .setContentText("It's time for: " + taskName)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            // Show the notification
            notificationManager.notify(taskName.hashCode(), builder.build());
        }
    }
}
