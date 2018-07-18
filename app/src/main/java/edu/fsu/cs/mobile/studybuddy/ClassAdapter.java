package edu.fsu.cs.mobile.studybuddy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder>{

    interface OnClassClickListener{
        void onClick(ClassChat clicked);
    }

    private OnClassClickListener mListener;
    private ArrayList<ClassChat> classes;
    private Context mContext;

    public ClassAdapter(ArrayList<ClassChat> cls, OnClassClickListener clicked){
        classes = cls;
        this.mListener = clicked;
    }

    /*public void addNewUser(ClassChat add2){
        classes.add(add2);
    }*/

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class_room, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        holder.bind(classes .get(position));
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    class ClassViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        ClassChat chat;


        public ClassViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.class_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(chat);
                }
            });
        }

        public  void bind(ClassChat chat2){
            this.chat = chat2;
            name.setText(chat.getName());
        }
    }


}
