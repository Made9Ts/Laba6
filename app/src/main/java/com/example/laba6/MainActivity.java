package com.example.laba6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laba6.adapters.ReminderAdapter;
import com.example.laba6.models.Reminder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_REMINDER_REQUEST = 1;
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

    @Override
    protected void onDestroy() {
        // Закрытие базы данных при уничтожении активности
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}