package edu.fsu.cs.mobile.studybuddy;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

        public MessageViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message);
            timestamp = itemView.findViewById(R.id.timestamp);
        }

        public void bind(Messages chat){
            message.setText(chat.getMessage());

            Long millis = chat.getSent();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .document(chat.getSenderId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String test = documentSnapshot.getString("name");
                            timestamp.setText(test);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Toast.makeText(, "", Toast.LENGTH_SHORT).show();
                    // Log.d("Tag",e.toString());
                    Log.i("shame", "onFailure:");
                }
            });

        }

    }
}
