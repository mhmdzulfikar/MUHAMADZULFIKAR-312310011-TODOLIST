package com.example.uastugas;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private TextView timeDisplay;
    private TextView dateDisplay;
    private LinearLayout taskContainer;

    private Handler handler = new Handler(); // Handler to update time every second
    private Runnable updateTimeRunnable;

    private static final String PREFS_NAME = "task_prefs";
    private static final String TASKS_KEY = "tasks";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI components
        timeDisplay = view.findViewById(R.id.time_display);
        dateDisplay = view.findViewById(R.id.tvDate); // Date display reference
        taskContainer = view.findViewById(R.id.taskContainer);

        // Set up the time update task
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(updateTimeRunnable);

        // Load saved tasks
        loadTasks();

        // Button to add time
        view.findViewById(R.id.btnAddTime).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TimePickerActivity.class);
            timePickerLauncher.launch(intent);
        });

        return view;
    }

    private final ActivityResultLauncher<Intent> timePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        int hour = data.getIntExtra("HOUR", -1);
                        int minute = data.getIntExtra("MINUTE", -1);
                        String taskNameReceived = data.getStringExtra("TASK_NAME");

                        if (hour != -1 && minute != -1 && taskNameReceived != null && !taskNameReceived.isEmpty()) {
                            String formattedTime = String.format("%02d:%02d", hour, minute);

                            // Add task to the list
                            addTask(taskNameReceived, formattedTime);

                            // Save tasks
                            saveTasks();

                            // Send data to AlarmReceiver to set alarm
                            scheduleNotification(taskNameReceived, hour, minute);
                        } else {
                            Log.e("HomeFragment", "Invalid task name or time received");
                        }
                    }
                } else {
                    Log.d("HomeFragment", "No time set");
                }
            }
    );

    private void scheduleNotification(String taskName, int hour, int minute) {
        // Create intent for AlarmReceiver
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("TASK_NAME", taskName);
        intent.putExtra("HOUR", hour);
        intent.putExtra("MINUTE", minute);

        // PendingIntent to be triggered by AlarmManager
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                taskName.hashCode(), // Unique requestCode based on taskName
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Schedule alarm with AlarmManager
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1); // Schedule for next day if time has passed
            }

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );

            Log.d("HomeFragment", "Alarm scheduled for " + taskName + " at " + hour + ":" + minute);
        }
    }


    // Method to update time and date every second
    private void updateDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss"); // 24-hour time format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy"); // Date format

        String currentTime = timeFormat.format(calendar.getTime());
        String currentDate = dateFormat.format(calendar.getTime());

        timeDisplay.setText(currentTime); // Update time
        dateDisplay.setText(currentDate); // Update date
    }

    // Method to add a task to the task container
    private void addTask(String taskName, String time) {
        if (taskContainer.getChildCount() >= 6) {
            Toast.makeText(getContext(), "You can only add up to 6 tasks", Toast.LENGTH_SHORT).show();
            return; // Don't add more tasks if there are already 6
        }

        View taskView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, taskContainer, false);

        TextView taskNameView = taskView.findViewById(R.id.tvTaskName);
        TextView timeView = taskView.findViewById(R.id.tvTime);

        taskNameView.setText(taskName);
        timeView.setText(time);

        // Set listener for the edit button
        taskView.findViewById(R.id.ivEditTask).setOnClickListener(v -> {
            showEditDialog(taskNameView, timeView);
        });

        // Set listener for the delete button
        taskView.findViewById(R.id.ivDeleteTask).setOnClickListener(v -> {
            taskContainer.removeView(taskView);
            saveTasks(); // Save the task list after deletion
            Toast.makeText(getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
        });

        taskContainer.addView(taskView); // Add the new task view to the container
    }

    // Method to show the dialog for editing a task
    private void showEditDialog(TextView taskNameView, TextView taskNoteView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Task");

        // Set up custom layout for the dialog
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_edit_task, null);

        final EditText newTaskName = customLayout.findViewById(R.id.editTaskName);
        final EditText newNote = customLayout.findViewById(R.id.editNote);

        newTaskName.setText(taskNameView.getText().toString());
        newNote.setText(taskNoteView.getText().toString());

        builder.setView(customLayout);

        Button saveButton = customLayout.findViewById(R.id.btnSaveTask);
        saveButton.setOnClickListener(v -> {
            String updatedTaskName = newTaskName.getText().toString();
            String updatedNote = newNote.getText().toString();

            taskNameView.setText(updatedTaskName);
            taskNoteView.setText(updatedNote);

            saveTasks();
            Toast.makeText(getContext(), "Task updated", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    // Method to save tasks to SharedPreferences
    private void saveTasks() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray taskArray = new JSONArray();

        for (int i = 0; i < taskContainer.getChildCount(); i++) {
            View taskView = taskContainer.getChildAt(i);
            TextView taskNameView = taskView.findViewById(R.id.tvTaskName);
            TextView timeView = taskView.findViewById(R.id.tvTime);

            JSONObject taskObject = new JSONObject();
            try {
                taskObject.put("taskName", taskNameView.getText().toString());
                taskObject.put("time", timeView.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            taskArray.put(taskObject);
        }

        editor.putString(TASKS_KEY, taskArray.toString());
        editor.apply();
    }

    // Method to load tasks from SharedPreferences
    private void loadTasks() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, 0);
        String tasksJson = prefs.getString(TASKS_KEY, "");

        if (!tasksJson.isEmpty()) {
            try {
                JSONArray taskArray = new JSONArray(tasksJson);
                for (int i = 0; i < taskArray.length(); i++) {
                    JSONObject taskObject = taskArray.getJSONObject(i);
                    String taskNameLoaded = taskObject.getString("taskName");
                    String time = taskObject.getString("time");

                    addTask(taskNameLoaded, time); // Add each loaded task
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
