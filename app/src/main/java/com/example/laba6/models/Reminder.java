package com.example.laba6.models;

import java.io.Serializable;

public class Reminder implements Serializable {
    private long reminderId; // Изменено название поля
    private String title;
    private String text;
    private long dateTime;

    // Конструктор без ID (для создания нового напоминания)
    public Reminder(String title, String text, long dateTime) {
        this.title = title;
        this.text = text;
        this.dateTime = dateTime;
    }

    // Геттеры и сеттеры с использованием reminderId
    public long getReminderId() {
        return reminderId;
    }

    public void setReminderId(long reminderId) {
        this.reminderId = reminderId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
}