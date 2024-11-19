package com.example.laba6;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laba6.adapters.ReminderAdapter;
import com.example.laba6.models.Reminder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_REMINDER_REQUEST = 1;
    private static final String CHANNEL_ID = "REMINDER_CHANNEL";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;
    private DatabaseHelper dbHelper;
    private ReminderAdapter reminderAdapter;
    private List<Reminder> reminderList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация компонентов
        dbHelper = new DatabaseHelper(this);
        reminderList = new ArrayList<>();

        // Настройка адаптера с обработчиком удаления
        setupReminderAdapter();

        // Настройка RecyclerView
        setupRecyclerView();

        // Настройка кнопки добавления напоминания
        setupAddReminderButton();

        // Загрузка существующих напоминаний
        loadReminders();

        // Создание канала уведомлений
        createNotificationChannel();

        // Проверка разрешения на уведомления
        checkNotificationPermission();
    }

    private void setupReminderAdapter() {
        reminderAdapter = new ReminderAdapter(reminderList, new ReminderAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(Reminder reminder) {
                // Удаление напоминания
                dbHelper.deleteReminder(reminder.getReminderId());
                loadReminders();
                Toast.makeText(MainActivity.this, "Напоминание удалено", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewReminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reminderAdapter);
    }

    private void setupAddReminderButton() {
        Button btnAddReminder = findViewById(R.id.btnAddReminder);
        btnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
                startActivityForResult(intent, ADD_REMINDER_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Обработка результата добавления напоминания
        if (requestCode == ADD_REMINDER_REQUEST && resultCode == RESULT_OK && data != null) {
            // Извлечение данных из интента
            String title = data.getStringExtra("title");
            String text = data.getStringExtra("text");
            long dateTime = data.getLongExtra("dateTime", 0);

            // Создание и сохранение нового напоминания
            Reminder reminder = new Reminder(title, text, dateTime);
            long reminderId = dbHelper.addReminder(reminder);
            reminder.setReminderId(reminderId);

            // Отправка уведомления
            sendNotification(reminder);

            // Обновление списка
            loadReminders();
        }
    }

    private void loadReminders() {
        // Очистка текущего списка и загрузка напоминаний из БД
        reminderList.clear();
        reminderList.addAll(dbHelper.getAllReminders());
        reminderAdapter.notifyDataSetChanged(); // Исправлено
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder Channel";
            String description = "Channel for reminder notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendNotification(Reminder reminder) {
        // Проверка разрешения на отправку уведомлений
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(reminder.getTitle())
                    .setContentText(reminder.getText())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify((int) reminder.getReminderId(), builder.build());
        } else {
            // Если разрешение не предоставлено, запросите его
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
        }
    }
}