package com.example.laba6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laba6.R;
import com.example.laba6.models.Reminder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    // Интерфейс объявлен внутри класса
    public interface OnDeleteClickListener {
        void onDeleteClick(Reminder reminder);
    }

    private List<Reminder> reminderList;
    private OnDeleteClickListener deleteClickListener;

    // Конструктор с передачей слушателя
    public ReminderAdapter(List<Reminder> reminderList, OnDeleteClickListener deleteClickListener) {
        this.reminderList = reminderList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.bind(reminder);
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    // Внутренний класс ViewHolder
    class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvText, tvDateTime;
        ImageButton btnDelete;

        ReminderViewHolder(@NonNull View itemView) {
            super(itemView);

            // Инициализация компонентов
            tvTitle = itemView.findViewById(R.id.tvReminderTitle);
            tvText = itemView.findViewById(R.id.tvReminderText);
            tvDateTime = itemView.findViewById(R.id.tvReminderDateTime);
            btnDelete = itemView.findViewById(R.id.btnDeleteReminder);

            // Установка слушателя удаления
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && deleteClickListener != null) {
                        deleteClickListener.onDeleteClick(reminderList.get(position));
                    }
                }
            });
        }

        // Метод привязки данных к элементу списка
        void bind(Reminder reminder) {
            tvTitle.setText(reminder.getTitle());
            tvText.setText(reminder.getText());

            // Форматирование даты
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvDateTime.setText(sdf.format(reminder.getDateTime()));
        }
    }

}