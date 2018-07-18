package edu.fsu.cs.mobile.studybuddy;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;

public class StudyBuddy extends AppCompatActivity {

    private static final String LOCATION_TAG = "LocationTag";
    public static final String TAG = StudyBuddy.class.getCanonicalName();


    public static boolean isCheckedIn = false;
    //public static int userCount = 0;

    private LocationManager mLocationManager;
    protected LatLng diracLatLng = new LatLng(30.4450, -84.2999);

    private BottomNavigationView nav;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_messages:
                    requestLocationUpdates();

                    MessagesFragment mess = new MessagesFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, mess)
                            .commit();

                    return true;

                case R.id.navigation_map:
                    requestLocationUpdates();

                    MapFragment map = new MapFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, map)
                            .commit();

                    return true;

                case R.id.navigation_study:
                    requestLocationUpdates();

                    MainFragment main = new MainFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, main)
                            .commit();

                    return true;

                case R.id.navigation_classes:
                    requestLocationUpdates();

                    ClassesFragment classes = new ClassesFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, classes)
                            .commit();
                    return true;

                case R.id.navigation_menu:
                    requestLocationUpdates();

                    MenuFragment menu = new MenuFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, menu)
                            .commit();
                    return true;
            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_buddy);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        nav = findViewById(R.id.navigation);

        disableShift(nav);
        nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //set up main fragment first

        MainFragment frag = new MainFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, frag)
                .addToBackStack(MainFragment.TAG)
                .commit();

        nav.setSelectedItemId(R.id.navigation_study);

    }

    //Found of stack overflow to fix bottom navigation disblay to correctly show 5 options
    public static void disableShift(BottomNavigationView view){
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    public static boolean getIsCheckedIn() {
        return  isCheckedIn;
    }

    public static void setIsCheckedIn(boolean val) {
        isCheckedIn = val;
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();;

            //truncate lat val
            lat *= 10000;
            lat = (int) lat;

            lat /= 10000.0;

            double lng = location.getLongitude();
            //truncate lng val
            lng *= 10000;
            lng = (int) lng;

            lng /= 10000.0;

            //set isCheckedIn if at Dirac
            if (Math.abs(lat - diracLatLng.latitude) < 1 && Math.abs(lng - diracLatLng.longitude) < 1) {
                db.collection("users")
                        .document(currentFirebaseUser.getUid())
                        .update("active", "true");
                isCheckedIn = true;
                Log.i(TAG, "YYYYYYYYYYYYYYYYYYYYEEEEEEEEEEEEEEEEEEEEEEEEEEEESSSSSSSSSSSSSSSSSSSSSSSSSS");
            }else {
                db.collection("users")
                        .document(currentFirebaseUser.getUid())
                        .update("active", "false");
                isCheckedIn = false;
                Log.i(TAG, "NONNNNNNNNNNNNNNNNNNNNNNNNNNNNNOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
            }

            //remove updates
            mLocationManager.removeUpdates(this);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void requestLocationUpdates() {
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, mLocationListener);
            Log.i(TAG, "HHHHHHHHHHHHHHHHHHHHHHEEEEEEEEEEEEEEEEEEEYYYYYYYYYYYYYYYYYYYY");
        } catch (SecurityException e) {
            Log.i(LOCATION_TAG, "GPS Location failed");
        }
    }
}
