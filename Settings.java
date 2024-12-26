package com.example.project;

import android.icu.util.Output;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
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

public class Settings extends AppCompatActivity {
    EditText k_update_usernm, k_email, k_update_dob;
    Button save_btn;
    String user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        k_update_usernm = findViewById(R.id.username_edit);
        k_email = findViewById(R.id.email_edit);
        k_update_dob = findViewById(R.id.dob_edit);
        save_btn = findViewById(R.id.save_button);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
       // userEmail = "user_registered_email@example.com"; // Replace with actual fetched value
     //   emailEdit.setText(userEmail); // Set email as non-editable
      //  emailEdit.setEnabled(false);

        save_btn.setOnClickListener(v -> {
            String usernm = k_update_usernm.getText().toString().trim();
            String email = k_email.getText().toString().trim();
            String dob = k_update_dob.getText().toString().trim();

            if (usernm.isEmpty() || dob.isEmpty()) {
                Toast.makeText(Settings.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate email (client-side check)
           // if (!email.equals(userEmail)) {
           //     Toast.makeText(Settings.this, "Email doesn't match the one used during registration!", Toast.LENGTH_SHORT).show();
          //      return;
          //  }

            // Call method to update user data
            updateUser(usernm, dob);
        });
    }

    private void updateUser(String username, String dob) {
        String urlString = "http://10.0.2.2/update_user.php";

        new Thread(() -> {
            try {
                // Create URL and HttpURLConnection
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // Set connection properties
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Prepare POST data
                String postData = "username=" + username + "&dob=" + dob + "&email=" ;//+ userEmail;

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

                // Handle response
                runOnUiThread(() -> {
                    if (response.toString().contains("success")) {
                        Toast.makeText(Settings.this, "User updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Settings.this, "Failed to update user: " + response, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Settings.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
