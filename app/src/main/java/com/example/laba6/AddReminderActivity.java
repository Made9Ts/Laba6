package com.example.laba6;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddReminderActivity extends AppCompatActivity {
    private EditText etTitle, etText;
    private Button btnSetDateTime, btnSave;
    private long dateTime;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        // Инициализация компонентов
        initializeViews();

        // Настройка календаря
        calendar = Calendar.getInstance();

        // Установка листенеров
        setupListeners();
    }

    private void initializeViews() {
        etTitle = findViewById(R.id.etTitle);
        etText = findViewById(R.id.etText);
        btnSetDateTime = findViewById(R.id.btnSetDateTime);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupListeners() {
        // Обработчик выбора даты и времени
        btnSetDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        // Обработчик сохранения напоминания
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReminder();
            }
        });
    }

    private void showDateTimePicker() {
        // Диалог выбора даты
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Листенер для установки даты
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // Установка выбранной даты
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Показ диалога выбора времени
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    AddReminderActivity.this,  timeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            );
            timePickerDialog.show();
        }
    };

    // Листенер для установки времени
    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Установка выбранного времени
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            dateTime = calendar.getTimeInMillis();

            // Обновление текста кнопки
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            btnSetDateTime.setText(sdf.format(calendar.getTime()));
        }
    };

    private void saveReminder() {
        // Получение данных из полей ввода
        String title = etTitle.getText().toString().trim();
        String text = etText.getText().toString().trim();

        if (title.isEmpty() || text.isEmpty() || dateTime == 0) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Возврат данных в MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("text", text);
        resultIntent.putExtra("dateTime", dateTime);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}