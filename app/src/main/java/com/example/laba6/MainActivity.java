package com.example.laba6;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laba6.adapters.ReminderAdapter;
import com.example.laba6.models.Reminder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_REMINDER_REQUEST = 1;
    public static final String CHANNEL_ID = "REMINDER_CHANNEL";
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

            // Запланировать уведомление
            scheduleNotification(reminder);

            // Обновление списка
            loadReminders();
        }
    }

    private void loadReminders() {
        // Очистка текущего списка и загрузка напоминаний из БД
        reminderList.clear();
        reminderList.addAll(dbHelper.getAllReminders());
        reminderAdapter.notifyDataSetChanged();
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

    private void scheduleNotification(Reminder reminder) {
        if (canScheduleExactAlarms()) {
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("title", reminder.getTitle());
            intent.putExtra("text", reminder.getText());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) reminder.getReminderId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getDateTime(), pendingIntent);
            }
        } else {
            requestExactAlarmPermission();
        }
    }

    private boolean canScheduleExactAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            return appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_READ_CALL_LOG, getApplicationInfo().uid, getPackageName()) == AppOpsManager.MODE_ALLOWED;
        }
        return true;
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Разрешение на уведомления предоставлено", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Разрешение на уведомления отклонено", Toast.LENGTH_SHORT).show();
            }
        }
    }
}