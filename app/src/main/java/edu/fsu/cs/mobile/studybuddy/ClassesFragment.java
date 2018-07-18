package edu.fsu.cs.mobile.studybuddy;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClassesFragment extends Fragment {

    private FloatingActionButton add;
    private String getEntry;
    private ClassAdapter mAdapter;
    //private Map<String> schedule = new HashMap<String, String>();
    public static final String TAG = ClassesFragment.class.getCanonicalName();

    private FirebaseUser currentFirebaseUser;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;


    public ClassesFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_classes, container, false);

        add = v.findViewById(R.id.addButton);
        recyclerView = v.findViewById(R.id.classList);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //recyclerView.setHasFixedSize(true);

        Log.i(TAG, "onCreateView: "+ currentFirebaseUser.getUid());

        loadMyClasses();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Class");
                builder.setMessage("Enter the course code of the class you would like to add.");
                final EditText classEntry = new EditText(getActivity());
                builder.setView(classEntry);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getEntry = classEntry.getText().toString();

                        Map<String, Object> classAdd = new HashMap<>();
                        classAdd.put("name", getEntry);
                        classAdd.put("student", currentFirebaseUser.getUid());
                        classAdd.put("active", "false");

                        db.collection("class")
                                .add(classAdd)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.i(TAG, "onSuccess: ");
                                        loadMyClasses();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(),"failed to add class",Toast.LENGTH_SHORT).show();
                                    }
                                });

                        /*
                        getEntry = classEntry.getText().toString();
                        for(int i = 0; i < classArr.length; i++){
                            if(classArr[i].equals("")){
                                classArr[i] = getEntry;
                                Toast.makeText(getActivity(), classArr[i],
                                        Toast.LENGTH_SHORT).show();
                                mRef.push().setValue(classArr[i]);
                                //schedule.put(String.valueOf(i), classArr[i]);
                                break;
                            }
                            else if(!classArr[i].equals("")){
                                //schedule.put(String.valueOf(i), classArr[i]);
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_list_item_1, classArr);
                        cList.setAdapter(adapter);
                        //mRef.push().setValue(schedule);
                        */
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_list_item_1, classArr);
                        cList.setAdapter(adapter);*/
            }
        });


        return v;
    }

    private void getClasses(EventListener<QuerySnapshot> listener2) {
        db.collection("class")
                .whereEqualTo("student", currentFirebaseUser.getUid())
                .orderBy("name")
                .addSnapshotListener(listener2);
    }

    private void loadMyClasses() {
        getClasses(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("ClassesFragment", "Listen failed.", e);
                    return;
                }
                ArrayList<ClassChat>  cls = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    cls.add(
                            new ClassChat(
                                    doc.getId(),
                                    doc.getString("name"),
                                    doc.getString("student"),
                                    doc.getString("active")
                            )
                    );
                }

                mAdapter = new ClassAdapter(cls, listener);
                recyclerView.setAdapter(mAdapter);
            }

        });
    }

    ClassAdapter.OnClassClickListener listener = new ClassAdapter.OnClassClickListener() {
        @Override
        public void onClick(ClassChat clicked) {
            Log.i(TAG, "onClick: " + clicked.getID());
        }
    };



}
