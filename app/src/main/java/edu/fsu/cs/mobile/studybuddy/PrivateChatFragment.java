package edu.fsu.cs.mobile.studybuddy;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class PrivateChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private PrivateAdapter mAdapter;

    private FirebaseFirestore db;
    private FirebaseUser currentFirebaseUser;



    public PrivateChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_private_chat, container, false);
        recyclerView = rootview.findViewById(R.id.messageList);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        db = FirebaseFirestore.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        loadFriends();


        return rootview;
    }


    private void getFriends(EventListener<QuerySnapshot> listener2) {
        db.collection("users")
                .document(currentFirebaseUser.getUid())
                .collection("friend")
                .addSnapshotListener(listener2);
    }

    private void loadFriends() {
        getFriends(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("ClassesFragment", "Listen failed.", e);
                    return;
                }

                ArrayList<PrivateChat> std = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    std.add(
                            new PrivateChat(
                                    doc.getId(),
                                    doc.getString("name")
                            )
                    );
                }

                mAdapter = new PrivateAdapter(std, listener);
                recyclerView.setAdapter(mAdapter);
            }

        });
    }

    PrivateAdapter.OnPrivateClickListener listener = new PrivateAdapter.OnPrivateClickListener() {
        @Override
        public void onClick(PrivateChat clicked) {
            //Log.i(TAG, "onClick: " + clicked.getID());
            PrivateMessageFragment mess = PrivateMessageFragment.newInstance(clicked.getFriend_id(), clicked.getFriend_name());
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, mess)
                    .commit();

        }
    };

}
