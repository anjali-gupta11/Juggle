package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CategoriesActivity extends AppCompatActivity {

    TextView taskTitle, dueDate;
    CheckBox taskCheckbox;
    String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        taskTitle = findViewById(R.id.task_title);
        dueDate = findViewById(R.id.due_date);
        taskCheckbox = findViewById(R.id.task_checkbox);

        // Retrieve task data from the intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("task_title");
        String dueDateStr = intent.getStringExtra("task_due_date");
        taskId = intent.getStringExtra("task_id");

        // Set the task data to the TextViews
        taskTitle.setText(title);
        dueDate.setText(dueDateStr);

        // Set a click listener on the checkbox
        taskCheckbox.setOnClickListener(v -> {
            if (taskCheckbox.isChecked()) {
                updateTaskStatus("fulfilled");
                Toast.makeText(CategoriesActivity.this, "Task Completed", Toast.LENGTH_SHORT).show();
            } else {
                updateTaskStatus("pending");
                Toast.makeText(CategoriesActivity.this, "Task Not Completed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTaskStatus(String status) {
        new Thread(() -> {
            try {
                // Update task status in task.php
                String taskUrl = "http://10.0.2.2/task.php";
                sendStatusUpdate(taskUrl, status);

                // Update task status in subtask.php
                String subtaskUrl = "http://10.0.2.2/sub_task.php";
                sendStatusUpdate(subtaskUrl, status);

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(CategoriesActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void sendStatusUpdate(String urlString, String status) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String postData = "task_id=" + taskId + "&status=" + status;

        OutputStream os = conn.getOutputStream();
        os.write(postData.getBytes());
        os.flush();
        os.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        if (!response.toString().contains("success")) {
            throw new Exception("Failed to update status: " + response);
        }
    }
}
