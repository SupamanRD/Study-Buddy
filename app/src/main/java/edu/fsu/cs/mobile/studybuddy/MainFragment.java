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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private ClassAdapter mAdapter;

    public static final String TAG = MainFragment.class.getCanonicalName();


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = rootview.findViewById(R.id.available);
        mAdapter = new ClassAdapter(getContext(), listener);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        loadClasses();

        return rootview;
    }

    private void loadClasses() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("class")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                ClassChat user = new ClassChat(document.getId(), document.getString("name"));
                                mAdapter.addNewUser(user);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    ClassAdapter.OnClassClickListener listener = new ClassAdapter.OnClassClickListener() {
        @Override
        public void onClick(ClassChat clicked) {
            Log.i(TAG, "onClick: " + clicked.getID());
            MessagesFragment mess = MessagesFragment.newInstance(clicked.getID(), clicked.getName());
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, mess)
                    .commit();

        }
    };

}
