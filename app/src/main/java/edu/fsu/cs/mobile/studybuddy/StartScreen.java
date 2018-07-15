package edu.fsu.cs.mobile.studybuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartScreen extends AppCompatActivity {

    private Button login;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to login activity
                startActivity(new Intent(StartScreen.this, Login.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to signup activity
                startActivity(new Intent(StartScreen.this, SignUpActivity.class));
            }
        });


    }
}
