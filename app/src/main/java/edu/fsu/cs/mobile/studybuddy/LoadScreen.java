package edu.fsu.cs.mobile.studybuddy;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class LoadScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_screen);

        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mAuth.getCurrentUser() == null){
                    startActivity(new Intent(LoadScreen.this, StartScreen.class));
                    finish();
                }

                else {
                    startActivity(new Intent(LoadScreen.this, StudyBuddy.class));
                    finish();
                }
            }
        }, 3000);


    }
}

