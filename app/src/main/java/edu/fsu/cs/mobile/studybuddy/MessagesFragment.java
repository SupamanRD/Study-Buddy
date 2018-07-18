package edu.fsu.cs.mobile.studybuddy;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {

    private MessageAdapter mAdapter;
    private EditText message;
    private ImageButton mButton;
    private FirebaseUser currentFirebaseUser;
    private FirebaseFirestore db;
    private RecyclerView chats;


    private String class_id;
    private String class_name;

    //to be removed later
    private String library_id = "t8iSBrMSXftkT0qBFcDb";

    private static final String ARG_PARAM1 = "class_id";
    private static final String ARG_PARAM2 = "class_name";

    public static MessagesFragment newInstance(String Id, String Name){
        MessagesFragment frag = new MessagesFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, Id);
        args.putString(ARG_PARAM2, Name);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            class_id = getArguments().getString(ARG_PARAM1);
            class_name = getArguments().getString(ARG_PARAM2);
        }
    }

    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_messages, container, false);
        message = rootview.findViewById(R.id.message);
        mButton = rootview.findViewById(R.id.send);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        db = FirebaseFirestore.getInstance();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!message.getText().toString().equals("") && getArguments() != null){
                    sendMessage();
                }
            }
        });

        chats = rootview.findViewById(R.id.chat);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        chats.setLayoutManager(manager);


        if(getArguments() != null){
            displayChat();
        }



        return rootview;
    }

    private void getChat(EventListener<QuerySnapshot> listener) {
        db.collection("chats")
                .whereEqualTo("class_id", class_id)
                .orderBy("sent", Query.Direction.DESCENDING)
                .addSnapshotListener(listener);
    }

    private void displayChat(){
        getChat(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("MessageFragment", "Listen failed.", e);
                    return;
                }

                ArrayList<Messages > messages = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    messages.add(
                            new Messages(
                                    doc.getId(),
                                    doc.getString("library_id"),
                                    doc.getString("class_id"),
                                    doc.getString("sender_id"),
                                    doc.getString("message"),
                                    doc.getLong("sent")
                            )
                    );
                }

                mAdapter = new MessageAdapter(messages, currentFirebaseUser.getUid());
                chats.setAdapter(mAdapter);
            }

        });
    }

    private void sendMessage() {
        String send = message.getText().toString();
        String userId = currentFirebaseUser.getUid();

        message.setText("");
        mButton.setEnabled(false);

        Map<String, Object> chat = new HashMap<>();
        chat.put("class_id", class_id);
        chat.put("library_id", library_id);
        chat.put("sender_id", userId);
        chat.put("message", send);
        chat.put("sent", System.currentTimeMillis());

        db.collection("chats")
                .add(chat)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        mButton.setEnabled(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"message failed to send",Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
