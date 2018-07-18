package edu.fsu.cs.mobile.studybuddy;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private String userId;
    private ArrayList<Messages> mess;

    public MessageAdapter(ArrayList<Messages> mess, String userId){
        this.mess = mess;
        this.userId = userId;
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType == 0){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_sent, parent, false);
        }

        else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_received, parent, false);
        }

        return new MessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(mess.get(position));
    }

    @Override
    public int getItemCount() {
        return mess.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView message;

        public MessageViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message);
        }

        public void bind(Messages chat){
            message.setText(chat.getMessage());
        }

    }
}
