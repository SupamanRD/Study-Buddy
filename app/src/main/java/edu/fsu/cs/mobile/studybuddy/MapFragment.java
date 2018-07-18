package edu.fsu.cs.mobile.studybuddy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback{

    private FirebaseFirestore db;
    private DatabaseReference dbRef;
    public static final String FIREBASE_TABLE = "Users";
    public static final String TAG = MapFragment.class.getCanonicalName();

    private int userCount = 0;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        db = FirebaseFirestore.getInstance();
        SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFrag.getMapAsync(this);

        setClassCount();

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng libLocation = new LatLng(30.4450, -84.2999);
        map.addMarker(new MarkerOptions().position(libLocation).title("Marker in Dirac Library"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(libLocation, 18));

        map.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        String str;

        /*
        if (StudyBuddy.getIsCheckedIn()) {
            str = "# of users found: " + userCount;
        }else {
            str = "please go to Dirac";
        }*/
        str = "# of users found: " + userCount;
        marker.setTitle(str);

        return false;
    }

    private void getCount(EventListener<QuerySnapshot> listener2) {
        db.collection("users")
                .whereEqualTo("active", "true")
                .addSnapshotListener(listener2);
    }

    public void setClassCount() {

        getCount(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("ClassesFragment", "Listen failed.", e);
                    return;
                }
                for (QueryDocumentSnapshot doc : snapshots) {
                    userCount++;
                }
            }

        });


        /*
        final String currentClass = "COP3014";

        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference(FIREBASE_TABLE);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    //checks if "Class" exists
                    if (userSnapshot.child("Class").exists()) {
                        String classStr = userSnapshot.child("Class").getValue() + "";

                        classStr = classStr.substring(classStr.indexOf(currentClass), classStr.indexOf(currentClass) + 7);
                        Log.i("UserClass", classStr);
                        //compare classStr to currentClass
                        //increment user count if matches
                        if (classStr.equals(currentClass)) {
                            userCount++;
                        }
                    }

                    Log.i("UserCount", userCount + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });*/
    }
}