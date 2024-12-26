package com.example.project;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
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

public class register extends AppCompatActivity {
    EditText k_user, kemail, kpasswd, kdob;
    Button k_registerbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        k_user = findViewById(R.id.user_name);
        kemail = findViewById(R.id.email);
        kpasswd = findViewById(R.id.passwd);
        kdob = findViewById(R.id.dob);
        k_registerbtn = findViewById(R.id.register_btn);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        k_registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement registration logic here (validation and saving the data)
                String username = k_user.getText().toString().trim();
                String email = kemail.getText().toString().trim();
                String password = kpasswd.getText().toString().trim();
                String dob = kdob.getText().toString().trim();

                // Check if fields are not empty
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || dob.isEmpty()) {
                    Toast.makeText(register.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    sendDataToServer(username, email, password, dob);
                }
            }
        });
    }
        private void sendDataToServer(String username, String email, String password, String dob) {
            String urlString = "http://10.0.2.2/register.php";

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
                String postData = "username=" + username + "&email=" + email + "&password=" + password + "&dob=" + dob;

                // Send data to server
                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                // Get the response from server
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Log response
                Log.d("Server Response", response.toString());

                // Handle response
                if (response.toString().contains("success")) {
                    runOnUiThread(() -> Toast.makeText(register.this, "Registration successful!", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(register.this, "Registration failed: " + response, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(register.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

        }
}


