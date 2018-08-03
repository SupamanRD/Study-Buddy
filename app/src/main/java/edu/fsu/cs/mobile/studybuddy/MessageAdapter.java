package edu.fsu.cs.mobile.studybuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private String userId;
    private ArrayList<Messages> mess;

    interface OnMessageClickListener{
        void onClick(Messages clicked);
    }

    private OnMessageClickListener clicked;

    public MessageAdapter(ArrayList<Messages> mess, OnMessageClickListener clicked,String userId){
        this.mess = mess;
        this.userId = userId;
        this.clicked = clicked;
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
    public int getItemViewType(int position){
        if(mess.get(position).getSenderId().contentEquals(userId))
            return 0;

        return 1;
    }

    @Override
    public int getItemCount() {
        return mess.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView message;
        TextView timestamp;
        Messages recieved;
        ImageView image;
        ImageView profile;


        public MessageViewHolder(final View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message);
            timestamp = itemView.findViewById(R.id.timestamp);
            image = itemView.findViewById(R.id.chat_image);
            profile = itemView.findViewById(R.id.profile_image);

            //if message is clicked it allows user to send private chat

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicked.onClick(recieved);
                }
            });

        }

        public void bind(final Messages chat){

            FirebaseStorage mStorage = FirebaseStorage.getInstance();
            StorageReference mStorageRef = mStorage.getReference();

            Glide.with(itemView.getContext() /* context */)
                    .using(new FirebaseImageLoader())
                    .load(mStorageRef.child("images/" + chat.getSenderId() + ".jpg"))
                    .into(profile);

            recieved = chat;
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();


            db.collection("users")
                    .document(chat.getSenderId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            final String test = documentSnapshot.getString("name");
                            timestamp.setText(test);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Toast.makeText(, "", Toast.LENGTH_SHORT).show();
                    // Log.d("Tag",e.toString());
                    Log.i("shame", "onFailure:" );
                }
            });

            if(chat.getMessage().equals("")){
                message.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
                Uri uri = Uri.parse(chat.getImage());
                Picasso.get().load(uri).fit().into(image);
            }

            else {
                message.setText(chat.getMessage());
            }



        }

    }
}
