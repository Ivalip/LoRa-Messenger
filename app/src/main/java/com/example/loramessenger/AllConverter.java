package com.example.loramessenger;

import androidx.room.TypeConverter;

import java.util.List;

public class AllConverter {
    @TypeConverter
    public List<ChatMessage> fromMessages(List<ChatMessage> chatMessages) {
        return chatMessages;
    }
}
