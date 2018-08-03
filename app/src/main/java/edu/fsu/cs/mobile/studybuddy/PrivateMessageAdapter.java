package edu.fsu.cs.mobile.studybuddy;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PrivateMessageAdapter extends RecyclerView.Adapter<PrivateMessageAdapter.PrivateMessageViewHolder>{

    private String userId;
    private ArrayList<PrivateMessage> mess;

    public PrivateMessageAdapter(ArrayList<PrivateMessage> mess,String userId){
        this.mess = mess;
        this.userId = userId;
    }

    @NonNull
    @Override
    public PrivateMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType == 0){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_sent, parent, false);
        }

        else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_received, parent, false);
        }

        return new PrivateMessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PrivateMessageViewHolder holder, int position) {
        holder.bind(mess.get(position));

    }

    @Override
    public int getItemViewType(int position){
        if(mess.get(position).getSenderId().contentEquals(userId))
            return 0;

        return 1;
    }

    @Override
    public int getItemCount() {
        return mess.size();
    }


    public class PrivateMessageViewHolder extends RecyclerView.ViewHolder{
        TextView message;
        TextView timestamp;
        ImageView image;
        ImageView profile;

        public PrivateMessageViewHolder(final View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message);
            timestamp = itemView.findViewById(R.id.timestamp);
            image = itemView.findViewById(R.id.chat_image);
            profile = itemView.findViewById(R.id.profile_image);
        }

        public void bind(final PrivateMessage chat){
            FirebaseStorage mStorage = FirebaseStorage.getInstance();
            StorageReference mStorageRef = mStorage.getReference();

            Glide.with(itemView.getContext() /* context */)
                    .using(new FirebaseImageLoader())
                    .load(mStorageRef.child("images/" + chat.getSenderId() + ".jpg"))
                    .into(profile);

            if(chat.getMessage().equals("")){
                message.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
                Uri uri = Uri.parse(chat.getImage());
                Picasso.get().load(uri).into(image);
            }
            else {
                message.setText(chat.getMessage());
            }

        }


    }
}
