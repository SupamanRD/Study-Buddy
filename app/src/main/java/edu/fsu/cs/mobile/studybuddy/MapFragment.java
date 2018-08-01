package edu.fsu.cs.mobile.studybuddy;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private View rootView;
    private TextView txtView;

    private LatLng[] libLatLongs = {
            new LatLng(30.4450, -84.2999), //dirac
            new LatLng(30.4431, -84.2950)  //strozier
    };
    private int userCount = 0;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        txtView = rootView.findViewById(R.id.map_txt_view);

        db = FirebaseFirestore.getInstance();
        SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFrag.getMapAsync(this);

        setClassCount();

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //sets the marker to the closer library
        setMarkerToLib(map);

        Location libLocation = new Location(LocationManager.NETWORK_PROVIDER);

        LatLng libLatLong = libLatLongs[0];
        libLocation.setLatitude(libLatLong.latitude);
        libLocation.setLongitude(libLatLong.longitude);

        float dist = libLocation.distanceTo(StudyBuddy.currentLocation);

        dist = convertToMiles(dist);

        String str = "Distances:\n";
        str += ("\n" + truncateNum(dist, 2) + " miles from Dirac");

        libLatLong = libLatLongs[1];
        libLocation.setLatitude(libLatLong.latitude);
        libLocation.setLongitude(libLatLong.longitude);

        dist = libLocation.distanceTo(StudyBuddy.currentLocation);
        dist = convertToMiles(dist);

        str += ("\n" + truncateNum(dist, 2) + " miles from Strozier");
        updateTxtView(str);

        map.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        String str;

        str = "# of users found: " + userCount;
        marker.setTitle(str);
        str = "The number of users found:\n";
        str += userCount;

        updateTxtView(str);

        return false;
    }

    private void updateTxtView(String str) {
        txtView.setText(str);
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
    }

    public void setMarkerToLib(GoogleMap map) {
        float distToDirac = getLibDist(libLatLongs[0]);
        float distToStrozier = getLibDist(libLatLongs[1]);

        //add marker for Strozier
        map.addMarker(new MarkerOptions().position(libLatLongs[1]).title("Marker in Strozier Library"));

        //add marker for Dirac
        map.addMarker(new MarkerOptions().position(libLatLongs[0]).title("Marker in Dirac Library"));

        LatLng libLatLong;

        if (distToStrozier < distToDirac) {
            libLatLong = libLatLongs[1];
        }else {
            libLatLong = libLatLongs[0];
        }

        //move the camera to the closest marker
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(libLatLong, 18));
    }

    public float getLibDist(LatLng libLatLong) {
        //get the distance to the library passed in

        Location libLocation = new Location(LocationManager.NETWORK_PROVIDER);

        libLocation.setLatitude(libLatLong.latitude);
        libLocation.setLongitude(libLatLong.longitude);
        float dist = libLocation.distanceTo(StudyBuddy.currentLocation);

        return dist;
    }

    public float convertToMiles(float metersDist) {
        /*
         * convert from meters to miles
         * 1 meter = 0.000621371 miles
         */

        float milesDist = (float) (metersDist * 0.000621371);
        return milesDist;
    }

    public double truncateNum(float num, int decimalPlace) {
        double newNum;
        double multOfTen = Math.pow(10, decimalPlace);

        newNum = (int) (num * multOfTen);

        newNum /= multOfTen;

        return newNum;
    }
}