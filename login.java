package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class login extends AppCompatActivity {
    EditText u_nm, passwd;
    Button lg_btn;
    ImageButton eye;
    boolean isPasswordVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login2);
        u_nm = findViewById(R.id.username);
        passwd = findViewById(R.id.pwd);
        lg_btn = findViewById(R.id.login_btn);
        eye=findViewById(R.id.eye_btn);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide the password
                    passwd.setInputType(129); // 129 is for Password
                    eye.setImageResource(R.drawable.visibility_off); // Set eye-off icon
                } else {
                    // Show the password
                    passwd.setInputType(1); // 1 is for normal text
                    eye.setImageResource(R.drawable.visibility); // Set eye-on icon
                }
                isPasswordVisible = !isPasswordVisible;

                // Move the cursor to the end of the text after toggling the visibility
                passwd.setSelection(passwd.getText().length());
            }
        });

        lg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginInput = u_nm.getText().toString().trim();
                String password = passwd.getText().toString().trim();

                if (loginInput.isEmpty() || password.isEmpty()) {
                    Toast.makeText(login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Make a network request to the PHP script
                new Thread(() -> {
                    try {
                        String urlString = "http://10.0.2.2/login.php";
                        URL url = new URL(urlString);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                        conn.setRequestMethod("POST");
                        conn.setDoOutput(true);
                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                        String postData = "login_input=" + loginInput + "&password=" + password;

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

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        runOnUiThread(() -> {
                            if (status.equals("success")) {
                                Toast.makeText(login.this, message, Toast.LENGTH_SHORT).show();
                                try {
                                    int userId = jsonResponse.getJSONObject("user").getInt("id");

                                    // Navigate to the next activity
                                    Intent intent = new Intent(login.this, Home2Activity.class);
                                    intent.putExtra("user_id", userId);

                                    startActivity(intent);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(login.this, "Error: Failed to parse user details", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(login.this, message, Toast.LENGTH_LONG).show();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(login.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }).start();
            }
        });


    }
}


