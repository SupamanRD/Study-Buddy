package edu.fsu.cs.mobile.studybuddy;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrivateMessageFragment extends Fragment {

    private PrivateMessageAdapter mAdapter;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri mUri;
    private String mImageString;


    private EditText message;
    private ImageButton mButton;
    private ImageButton mButtonImage;
    private TextView mDisplay;
    private FirebaseUser currentFirebaseUser;
    private FirebaseFirestore db;
    private RecyclerView chats;

    //this is the id of the person chatting with
    private static final String ARG_PARAM1 = "student_id";
    private static final String ARG_PARAM2 = "student_name";


    private String student_id;
    private String student_name;


    public static PrivateMessageFragment newInstance(String Id, String name){
        PrivateMessageFragment frag = new PrivateMessageFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, Id);
        args.putString(ARG_PARAM2, name);
        frag.setArguments(args);


        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            student_id = getArguments().getString(ARG_PARAM1);
            student_name = getArguments().getString(ARG_PARAM2);
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
        mDisplay = rootview.findViewById(R.id.nameP);
        mButtonImage = rootview.findViewById(R.id.imageP);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images");

        mDisplay.setText(student_name);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!message.getText().toString().equals("")&& getArguments() != null){
                    sendMessage();
                }
            }
        });




        mButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
                /*

                imagePicker = new ImagePicker(getActivity());
                imagePicker.setImagePickerCallback(new ImagePickerCallback(){
                @Override
                public void onImagesChosen(List<ChosenImage> images) {
                // Display images
                Toast.makeText(getActivity(),"Text!",Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onError(String message) {
                // Do error handling
                Toast.makeText(getActivity(),"Nope",Toast.LENGTH_SHORT).show();

                }
                });
                //imagePicker.shouldGenerateThumbnails(false); // Default is true
                //.pickImage();

                */





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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mUri = data.getData();
            //Toast.makeText(getActivity(),"YEAH", Toast.LENGTH_SHORT).show();
            sendImageMessage();
        }
    }

    private void sendImageMessage() {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        final StorageReference add = storageReference.child(System.currentTimeMillis()
                + "." + mime.getExtensionFromMimeType(cR.getType(mUri)));

        add.putFile(mUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        add.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mImageString = uri.toString();

                                message.setText("");
                                mButton.setEnabled(false);
                                String sender = currentFirebaseUser.getUid();


                                Map<String, Object> chat = new HashMap<>();
                                chat.put("receiver", student_id);
                                chat.put("sender", sender);
                                chat.put("message", "");
                                chat.put("sent", System.currentTimeMillis());
                                chat.put("image", mImageString);

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
                        });
                        //Toast.makeText(getActivity(),temp, Toast.LENGTH_SHORT).show();


                    }
                });

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
                                            doc.getString("image"),
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
        chat.put("image", "");


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
