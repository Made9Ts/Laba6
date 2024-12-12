package com.example.laba6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.laba6.models.Reminder;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Константы базы данных
    private static final String DATABASE_NAME = "reminders.db";
    private static final int DATABASE_VERSION = 1;

    // Таблица напоминаний
    private static final String TABLE_REMINDERS = "reminders";
    private static final String COLUMN_ID = "reminderId";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_DATETIME = "datetime";

    // SQL запрос создания таблицы
    private static final String CREATE_TABLE_REMINDERS =
            "CREATE TABLE " + TABLE_REMINDERS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_TITLE + " TEXT, "
                    + COLUMN_TEXT + " TEXT, "
                    + COLUMN_DATETIME + " INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы при первом запуске
        db.execSQL(CREATE_TABLE_REMINDERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Удаление старой таблицы при обновлении
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
        onCreate(db);
    }

    // Метод добавления напоминания
    public long addReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, reminder.getTitle());
        values.put(COLUMN_TEXT, reminder.getText());
        values.put(COLUMN_DATETIME, reminder.getDateTime());

        // Возвращаем ID вставленной записи
        long id = db.insert(TABLE_REMINDERS, null, values);
        db.close();
        return id;
    }

    // Метод получения всех напоминаний
    public List<Reminder> getAllReminders() {
        List<Reminder> reminderList = new ArrayList<>();

        // Запрос для выборки всех напоминаний
        String selectQuery = "SELECT * FROM " + TABLE_REMINDERS +
                " ORDER BY " + COLUMN_DATETIME + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Обработка результатов запроса
        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATETIME))
                );

                // Устанавливаем ID напоминания
                reminder.setReminderId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));

                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }

        // Закрываем курсор и базу данных
        cursor.close();
        db.close();

        return reminderList;
    }

    // Метод удаления напоминания
    public void deleteReminder(long reminderId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Удаление по ID
        db.delete(TABLE_REMINDERS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(reminderId)});

        db.close();
    }
}