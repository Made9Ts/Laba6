package com.example.laba6.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.laba6.R;

public class ReminderDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_detail);

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvText = findViewById(R.id.tvDetailText);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");

        tvTitle.setText(title);
        tvText.setText(text);
    }
}