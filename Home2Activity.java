package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home2Activity extends AppCompatActivity {
    ImageButton kadd_btn, kcateg_btn, kstatistics_btn, ksettings_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home2);
        kadd_btn = findViewById(R.id.add_btn);
        kcateg_btn = findViewById(R.id.categ_btn);
        kstatistics_btn = findViewById(R.id.statistics_btn);
        ksettings_btn = findViewById(R.id.settings_btn);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        kadd_btn.setOnClickListener(v -> {
            Intent i1 = new Intent(Home2Activity.this, ReminderActivity.class);
            startActivity(i1);
        });

        // Categories Button
        kcateg_btn.setOnClickListener(v -> {
            Intent i2 = new Intent(Home2Activity.this, CategoriesActivity.class);
            startActivity(i2);
        });

        // Statistics Button
        kstatistics_btn.setOnClickListener(v -> {
            Intent i3 = new Intent(Home2Activity.this, StatisticsActivity.class);
            startActivity(i3);
        });

        // Settings Button
        ksettings_btn.setOnClickListener(v -> {
            Intent intent = new Intent(Home2Activity.this, Settings.class);
            startActivity(intent);
        });
    }
}