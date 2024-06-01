package com.example.loramessenger;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.loramessenger.Database.Entity.ChatMessage;


public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    ViewModel viewModel = new ViewModel();
    private LayoutInflater mInflater;
    private List<ChatMessage> chatMessages = new ArrayList<>();

    public ChatRecyclerViewAdapter(Context context, List<ChatMessage> messageList, ViewModel
                                   viewModel) {
        this.viewModel = viewModel;
        chatMessages = messageList;
        this.context = context;
    }
    public void newAddeddata(List <ChatMessage> messages){
        Log.d ("Messages", messages.get(0).content);
        chatMessages = messages;
        notifyDataSetChanged();
    }


    @Override
    public NewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        this.mInflater = LayoutInflater.from(parent.getContext());
        View view = mInflater.inflate(R.layout.message_item, parent, false);
        return new NewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final NewViewHolder viewHolder = (NewViewHolder) holder;
        viewHolder.content.setText(chatMessages.get(position).content);
        String tm = chatMessages.get(position).time;
        viewHolder.time.setText(tm.substring(tm.indexOf(":") + 1, tm.length()-3));
        viewHolder.deleteBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteById(chatMessages.get(position).id);
                removeAt(position);
            }
        });
        if (chatMessages.get(position).sender == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#808080"));
        }
        else {
            holder.itemView.setBackgroundColor(Color.parseColor("#008000"));
        }
    }
    public void removeAt(int position) {
        Log.d("MESSAGES REMOVE", "PERED");
        chatMessages.remove(position);
        Log.d("MESSAGES NOTIFY", "PERED");
        notifyItemRemoved(position);
        Log.d("MESSAGES NOTIFY2", "POSLE");
        notifyItemRangeChanged(position, getItemCount());
        Log.d("MESSAGES NOTIFY3", "POSLE");
    }

    public class NewViewHolder extends RecyclerView.ViewHolder {
        public TextView content;
        public TextView time;
        public TextView deleteBut;
        public NewViewHolder(View itemView) {
                super(itemView);
                deleteBut = itemView.findViewById(R.id.DeleteBut);
                content = itemView.findViewById(R.id.TEXT);
                time = itemView.findViewById(R.id.TIME);
            }
        }
        @Override
        public int getItemCount () {
            return chatMessages.size();
        }
}
