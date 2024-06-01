package com.example.loramessenger.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.util.List;

import com.example.loramessenger.AllConverter;
import com.example.loramessenger.Database.Entity.ChatMessage;


@Dao
public interface DataDAO {
    //@Query()
    //int updateChannelById(Integer id, int content, String time);

    @Query("SELECT * FROM messages")
    List<ChatMessage> getAll();

    @Query("SELECT * FROM messages WHERE receiver = :receiver ORDER BY time ASC")
    @TypeConverters({AllConverter.class})
    List <ChatMessage> getById(int receiver);
    @Insert
    void insert(ChatMessage message);

    @Update
    void update(ChatMessage message);

    @Delete
    void delete(ChatMessage message);
    @Query("UPDATE messages SET content = :content, time = :time , " +
            "sender = :sender, receiver = :receiver")
    void updateChannelById(String content, String time, int sender, int receiver);
    @Query("DELETE FROM messages WHERE id = :id")
    void deleteById (int id);

}
