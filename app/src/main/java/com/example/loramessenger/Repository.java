package com.example.loramessenger;

import android.content.Context;
import android.util.Log;

import com.example.loramessenger.Database.Dao.DataDAO;
import com.example.loramessenger.Database.Entity.ChatMessage;

import java.util.List;

public class Repository {

    AppDatabase db;
    DataDAO dataDAO;
    public Repository(Context context) {
        db = AppDatabase.getInstance(context);
        dataDAO = db.getDataDao();
    }
    public List<ChatMessage> getAll() {
        return dataDAO.getAll();
    }
    public List<ChatMessage> getById(int receiver) {
        Log.d("POLUCHENIYE PO ID", "NE POLUCHIL");
        return dataDAO.getById(receiver);
    }
    public void insert(ChatMessage message) {
        dataDAO.insert(message);
    }
    public void update(ChatMessage message) {
        dataDAO.update(message);
    }
    public void deleteById (int id) {
        dataDAO.deleteById(id);
    }
}
