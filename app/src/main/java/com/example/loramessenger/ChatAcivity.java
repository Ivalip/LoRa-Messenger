package com.example.loramessenger;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.loramessenger.Database.Entity.ChatMessage;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class ChatAcivity extends AppCompatActivity implements SerialInputOutputManager.Listener {
    int numberOfChat;
    DateFormat formatter;
    ViewModel viewModel;
    ChatRecyclerViewAdapter adapter;
    private boolean connected = false;
    private SerialInputOutputManager usbIoManager;
    Button edit;
    EditText text;
    RecyclerView recyclerView;
    String message = "";
    UsbSerialPort usbSerialPort;
    BroadcastReceiver broadcastReceiver;
    Handler mainLooper;
    Button backBut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_acivity);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            }
        };
        backBut = findViewById(R.id.Back);
        backBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                finish();
            }
        });
        mainLooper = new Handler(Looper.getMainLooper());
        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            System.out.println("Drivers empty");
            Log.d("Driver empty", availableDrivers.toString());
        }
        else {
            // Open a connection to the first available driver.
            UsbSerialDriver driver = availableDrivers.get(0);
            UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
            if (connection == null) {
                // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
                System.out.println("Connection empty");
                Log.d("Connection empty", connection.toString());
                return;
            }
            usbSerialPort = driver.getPorts().get(0);
            Log.d("PORTS", driver.getPorts().toString());
            // Most devices have just one port (port 0)
            try {
                usbSerialPort.open(connection);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                usbSerialPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            usbIoManager = new SerialInputOutputManager(usbSerialPort, this);
            usbIoManager.start();

            connected = true;
        }
        Intent intent = getIntent();
        recyclerView = findViewById(R.id.MESSAGES);
        edit = findViewById(R.id.button);
        text = findViewById(R.id.editText);
        viewModel = new ViewModel();
        numberOfChat = intent.getIntExtra("Number", -1);
        viewModel.createByID(getApplicationContext(), numberOfChat);
        List<ChatMessage> messageList = new ArrayList<>();
        viewModel.mutableLiveData.observe(this, new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(List<ChatMessage> messages) {
                if (messages.size() == 0) {
                    Log.d("EMPTY", "ENPTY");
                } else {
                    messageList.clear();
                    for (int i = 0; i < messages.size(); i++) {
                        messageList.add(messages.get(i));
                        Log.d("MESSAGE", messageList.get(i).content);
                    }
                    adapter.newAddeddata(messageList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        adapter = new ChatRecyclerViewAdapter(getApplicationContext(), messageList, viewModel);
        recyclerView.setAdapter(adapter);
        if (connected) {
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (text.getText().equals("")) {
                    } else {
                        Log.d("EDIT TEXT", text.getText().toString());
                        try {
                            writeMessage(text.getText().toString());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        viewModel.insert(new ChatMessage(text.getText().toString(), formater(System.currentTimeMillis() + ""),
                                0, numberOfChat));
                        Log.d("INSERT", "GET By ID");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                viewModel.mutableLiveData = viewModel.getById(numberOfChat);
                            }
                        }).start();

                        Log.d("INSERT", "OBSERVE");
                        text.setText("");
                    }
                }
            });
        }
    }
    @Override
    public void onNewData(byte[] data) {
            runOnUiThread(() -> {
               createMessage(new String(data));
            });
    }
    public void createMessage (String msg) {
        if (!msg.isEmpty()) {
            message += msg;
        }
        if (message.contains("<END>")) {

            String time = message.substring(message.indexOf("<TIME>") + 6, message.length() - 5);
            int id = Integer.parseInt(message.substring(message.indexOf("<ID>") + 4, message.indexOf("<START>")));
            String text = message.substring(message.indexOf("<START>") + 7, message.indexOf("<TIME>"));
            viewModel.insert(new ChatMessage(text, formater(time), 1, id));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    viewModel.mutableLiveData = viewModel.getById(numberOfChat);
                }
            }).start();
            message = "";
        }
    }
    public void writeMessage (String msg) throws IOException {
        msg = "<ID>" + numberOfChat + "<START>" + msg + "<TIME>" + System.currentTimeMillis() + "<END>";
        usbSerialPort.write(msg.getBytes(), 2000);
    }

    @Override
    public void onRunError(Exception e) {

    }
        public String formater(String time){
            Long inttime = Long.parseLong(time);
            inttime = inttime / 1000;
            long seconds = inttime % 60;
            inttime = inttime / 60;
            long day = inttime / 1440;
            long hour = (inttime  % 1440) / 60;
            long minutes = (inttime % 1440) % 60;

            return day + ":" + String.format("%2s", hour).replace(' ', '0') + ":" +
                    String.format("%2s", minutes).replace(' ', '0') + ":" +
                    String.format("%2s", seconds).replace(' ', '0');
        }

}