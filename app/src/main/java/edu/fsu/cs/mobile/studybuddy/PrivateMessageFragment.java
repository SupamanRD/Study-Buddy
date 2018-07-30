package edu.fsu.cs.mobile.studybuddy;


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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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
public class PrivateMessageFragment extends Fragment {

    private PrivateMessageAdapter mAdapter;

    private EditText message;
    private ImageButton mButton;
    private FirebaseUser currentFirebaseUser;
    private FirebaseFirestore db;
    private RecyclerView chats;

    //this is the id of the person chatting with
    private static final String ARG_PARAM1 = "student_id";
    private String student_id;


    public static PrivateMessageFragment newInstance(String Id){
        PrivateMessageFragment frag = new PrivateMessageFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, Id);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            student_id = getArguments().getString(ARG_PARAM1);
        }
    }


    public PrivateMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_private_message, container, false);

        message = rootview.findViewById(R.id.messageP);
        mButton = rootview.findViewById(R.id.sendP);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        db = FirebaseFirestore.getInstance();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!message.getText().toString().equals("")&& getArguments() != null){
                    sendMessage();
                }
            }
        });

        chats = rootview.findViewById(R.id.chatP);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        chats.setLayoutManager(manager);


        if(getArguments() != null){
            displayChat();
        }

        return rootview;
    }

    private void getChat(EventListener<QuerySnapshot> listener) {
        db.collection("private")
                .orderBy("sent", Query.Direction.DESCENDING)
                .addSnapshotListener(listener);
    }

    private void displayChat() {
        getChat(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("PrivateMessageFragment", "Listen failed.", e);
                    return;
                }

                ArrayList<PrivateMessage> messages = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    String sender = doc.getString("sender");
                    String receiver = doc.getString("receiver");

                    if(sender.equals(currentFirebaseUser.getUid()) || sender.equals(student_id)){
                        if(receiver.equals(currentFirebaseUser.getUid()) || receiver.equals(student_id)){
                            messages.add(
                                    new PrivateMessage(
                                            doc.getString("sender"),
                                            doc.getString("receiver"),
                                            doc.getString("message"),
                                            doc.getLong("sent")
                                    )
                            );
                        }
                    }


                }

                mAdapter = new PrivateMessageAdapter(messages, currentFirebaseUser.getUid());
                chats.setAdapter(mAdapter);
            }

        });
    }

    private void sendMessage() {
        String send = message.getText().toString();
        String sender = currentFirebaseUser.getUid();

        message.setText("");
        mButton.setEnabled(false);

        Map<String, Object> chat = new HashMap<>();
        chat.put("receiver", student_id);
        chat.put("sender", sender);
        chat.put("message", send);
        chat.put("sent", System.currentTimeMillis());

        db.collection("private")
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
