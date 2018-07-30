package edu.fsu.cs.mobile.studybuddy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PrivateAdapter extends RecyclerView.Adapter<PrivateAdapter.PrivateViewHolder>{

    interface OnPrivateClickListener{
        void onClick(PrivateChat clicked);
    }

    private OnPrivateClickListener mListener;
    private ArrayList<PrivateChat> students;

    public PrivateAdapter(ArrayList<PrivateChat> std, OnPrivateClickListener clicked){
        students = std;
        this.mListener = clicked;
    }

    @NonNull
    @Override
    public PrivateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_private_chat, parent, false);
        return new PrivateViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PrivateViewHolder holder, int position) {
        holder.bind(students.get(position));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class PrivateViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        PrivateChat chat;

        public PrivateViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.student_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(chat);
                }
            });
        }

        public  void bind(PrivateChat chat2){
            this.chat = chat2;
            name.setText(chat.getFriend_name());
        }
    }
}
