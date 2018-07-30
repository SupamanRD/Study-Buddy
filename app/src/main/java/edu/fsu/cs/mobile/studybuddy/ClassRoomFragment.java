package edu.fsu.cs.mobile.studybuddy;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
public class ClassRoomFragment extends Fragment {

    private MessageAdapter mAdapter;
    private EditText message;
    private ImageButton mButton;
    private FirebaseUser currentFirebaseUser;
    private FirebaseFirestore db;
    private RecyclerView chats;

    private String temp;


    private String class_id;
    private String class_name;

    //to be removed later
    private String library_id = "t8iSBrMSXftkT0qBFcDb";

    private static final String ARG_PARAM1 = "class_id";
    private static final String ARG_PARAM2 = "class_name";

    public static ClassRoomFragment newInstance(String Id, String Name){
        ClassRoomFragment frag = new ClassRoomFragment();

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

    public ClassRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_class_room, container, false);
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

        db.collection("users")
                .document(currentFirebaseUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        temp = documentSnapshot.getString("name");
                    }
                });


        if(getArguments() != null){
            displayChat();
        }



        return rootview;
    }

    private void getChat(EventListener<QuerySnapshot> listener) {
        db.collection("chats")
                .whereEqualTo("class_name", class_name)
                .orderBy("sent", Query.Direction.DESCENDING)
                .addSnapshotListener(listener);
    }

    private void displayChat(){
        getChat(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("PrivateChatFragment", "Listen failed.", e);
                    return;
                }

                ArrayList<Messages > messages = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    messages.add(
                            new Messages(
                                    doc.getId(),
                                    doc.getString("library_id"),
                                    doc.getString("class_name"),
                                    doc.getString("sender_id"),
                                    doc.getString("sender_name"),
                                    doc.getString("message"),
                                    doc.getLong("sent")
                            )
                    );
                }

                mAdapter = new MessageAdapter(messages, listener, currentFirebaseUser.getUid());
                chats.setAdapter(mAdapter);
            }

        });
    }


    MessageAdapter.OnMessageClickListener listener = new MessageAdapter.OnMessageClickListener() {
        @Override
        public void onClick(final Messages clicked) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Send Message");
            builder.setMessage("Send private message to "+ clicked.getSenderName());
            builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //creates the private user to user chat

                    Map<String, Object> createPrivate = new HashMap<>();
                    createPrivate.put("name", clicked.getSenderName());

                    //adds friend to current user
                    db.collection("users")
                            .document(currentFirebaseUser.getUid())
                            .collection("friend")
                            .document(clicked.getSenderId())
                            .set(createPrivate);


                    String name = currentFirebaseUser.getUid();
                    Map<String, Object> createPrivate2 = new HashMap<>();
                    createPrivate2.put("name", temp);
                    Log.i("IIIIIIIIIIIIIIIIIIIIII", "onClick: " + name);

                    //adds friend to the other user as well
                    db.collection("users")
                            .document(clicked.getSenderId())
                            .collection("friend")
                            .document(currentFirebaseUser.getUid())
                            .set(createPrivate2);
                }
            });


            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();

        }
    };


    private void sendMessage() {
        String send = message.getText().toString();
        String userId = currentFirebaseUser.getUid();
        String userName = temp;

        message.setText("");
        mButton.setEnabled(false);

        Map<String, Object> chat = new HashMap<>();
        chat.put("class_name", class_name);
        chat.put("library_id", library_id);
        chat.put("sender_id", userId);
        chat.put("sender_name", userName);
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
