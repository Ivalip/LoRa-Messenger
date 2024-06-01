package com.example.loramessenger;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.loramessenger.Database.Entity.ChatMessage;

import java.util.List;

public class ViewModel extends androidx.lifecycle.ViewModel {
    Repository repository;
    MutableLiveData<List<ChatMessage>> mutableLiveData = new MutableLiveData<>();
    public void create(Context context) {
        repository = new Repository(context);
        getAll();
    }
    public void createByID(Context context, int receiver) {
        repository = new Repository(context);
        mutableLiveData = getById(receiver);
    }

    public MutableLiveData<List<ChatMessage>> getAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
               mutableLiveData.postValue(repository.getAll());
            }
        }).start();
        return mutableLiveData;
    }
    public MutableLiveData<List<ChatMessage>> getById(int number) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mutableLiveData.postValue(repository.getById(number));
            }
        }).start();
        return mutableLiveData;
    }
    public void insert(ChatMessage message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                repository.insert(message);
            };
        }).start();
    }
    public void deleteById (int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                repository.deleteById(id);
            };
        }).start();
    }
    public void update(ChatMessage message) {
        repository.update(message);
    }

}
