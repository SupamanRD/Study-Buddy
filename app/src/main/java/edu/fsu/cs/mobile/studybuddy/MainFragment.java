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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private ClassAdapter mAdapter;
    private RecyclerView recyclerView;

    public static final String TAG = MainFragment.class.getCanonicalName();
    private FirebaseFirestore db;
    private FirebaseUser currentFirebaseUser;



    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = rootview.findViewById(R.id.available);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        db = FirebaseFirestore.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        db.collection("users")
                .document(currentFirebaseUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String test = documentSnapshot.getString("active");
                        if(test.equals("true")){
                            loadMyClasses();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Toast.makeText(, "", Toast.LENGTH_SHORT).show();
                // Log.d("Tag",e.toString());
                Log.i(TAG, "onFailure:");
            }
        });


        return rootview;
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
                ArrayList<ClassChat> cls = new ArrayList<>();
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

            ClassRoomFragment mess = ClassRoomFragment.newInstance(clicked.getID(), clicked.getName());
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, mess)
                    .commit();

        }
    };

}
