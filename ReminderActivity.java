package com.example.project;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {
    EditText k_title, k_desc, k_due_date;
    Spinner k_priority, k_category;
    Button k_save, k_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reminder);
        k_title = findViewById(R.id.new_task_title);
        k_desc = findViewById(R.id.new_task_desc);
        k_due_date = findViewById(R.id.due_date);
        k_priority = findViewById(R.id.priority_spinner);
        k_category = findViewById(R.id.category_spinner);
        k_save = findViewById(R.id.save_btn);
        k_date = findViewById(R.id.date_btn);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        k_save.setOnClickListener(v -> {
            String title = k_title.getText().toString().trim();
            String desc = k_desc.getText().toString().trim();
            String duedate = k_due_date.getText().toString().trim();
            String priority = k_priority.getSelectedItem().toString();
            String category = k_category.getSelectedItem().toString();

            if (title.isEmpty() || desc.isEmpty() || duedate.isEmpty()) {
                Toast.makeText(ReminderActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            } else if (!duedate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                Toast.makeText(ReminderActivity.this, "Invalid due date format. Use YYYY-MM-DD", Toast.LENGTH_LONG).show();
            } else {
                Intent i1 = new Intent(ReminderActivity.this, CategoriesActivity.class);
                i1.putExtra("task_title", title);
                i1.putExtra("task_due_date", duedate);

                startActivity(i1);
                saveTaskToDatabase(title, desc, priority, duedate, category);
            }
        });

        k_date.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(ReminderActivity.this,
                    (DatePicker view, int year, int month, int dayOfMonth) ->
                            k_due_date.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });
    }
    private void saveTaskToDatabase(String title, String description, String priority, String dueDate, String category) {

        String urlString = "http://10.0.2.2/task.php";

        new Thread(() -> {
            try {
                // Create URL and HttpURLConnection
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // Set connection properties
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Prepare POST data
                String postData = "task_title=" + title +
                        "&task_description=" + description +
                        "&priority=" + priority +
                        "&due_date=" + dueDate +
                        "&task_type=" + category +
                        "&status=pending";

                // Send data to server
                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                // Get response from server
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                runOnUiThread(() -> {
                    if (response.toString().contains("success")) {
                        Toast.makeText(ReminderActivity.this, "Task saved successfully!", Toast.LENGTH_SHORT).show();
                        clearFields();

                        // Extract task_id from the response
                        String taskId = extractTaskId(response.toString());

                        // Transfer data to subtask.php
                        transferDataToSubtask(taskId, description, "pending");

                        // Include task_id in the intent
                        Intent i1 = new Intent(ReminderActivity.this, CategoriesActivity.class);
                        i1.putExtra("task_title", title);
                        i1.putExtra("task_due_date", dueDate);
                        i1.putExtra("task_id", taskId);
                        startActivity(i1);

                    } else {
                        Toast.makeText(ReminderActivity.this, "Failed to save task: " + response, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ReminderActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private String extractTaskId(String response) {
        // Extract task_id from the response (assuming the response contains task_id)
        // This is a placeholder implementation, adjust it based on your actual response format
        return response.substring(response.indexOf("task_id") + 9, response.indexOf(",", response.indexOf("task_id")));
    }


    private void transferDataToSubtask(String taskId, String description, String status) {
        String urlString = "http://10.0.2.2/sub_task.php";

        new Thread(() -> {
            try {
                // Create URL and HttpURLConnection
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // Set connection properties
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Prepare POST data
                String postData = "task_id=" + taskId +
                        "&description=" + description +
                        "&status=" + status;

                // Send data to server
                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                // Get response from server
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                runOnUiThread(() -> {
                    if (response.toString().contains("success")) {
                        Toast.makeText(ReminderActivity.this, "Subtask saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ReminderActivity.this, "Failed to save subtask: " + response, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ReminderActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void clearFields() {
        k_title.setText("");
        k_desc.setText("");
        k_due_date.setText("");
        k_priority.setSelection(0);
        k_category.setSelection(0);
    }
}
