package com.example.loramessenger;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.loramessenger.Database.Dao.DataDAO;
import com.example.loramessenger.Database.Entity.ChatMessage;
@Database(entities = { ChatMessage.class }, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "messages")
                    .build();
        }
        Log.d("DATABASE", INSTANCE.toString());
        return INSTANCE;
    }
    public abstract DataDAO getDataDao();
}
