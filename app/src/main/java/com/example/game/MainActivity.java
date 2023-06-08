package com.example.game;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    // for story
    AlertDialog.Builder builder;

    FirebaseAuth mAuth;
    FirebaseUser user;
    Button userButton;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // show in fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN); // make it fullscreen




        // start GameActivity
        findViewById(R.id.play_btn).setOnClickListener(view ->
                startActivity(new Intent(this, GameActivity.class)));



        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        if (User_dialog.user != null)
            findViewById(R.id.user_btn).setBackground(ContextCompat.getDrawable(this, R.drawable.logout_icon));


        userButton = (Button) findViewById(R.id.user_btn);
        userButton.setOnClickListener(this);





        // if pressed button that is associated with R.id.story_btn -> openStoryDialog()
        findViewById(R.id.story_btn).setOnClickListener(v -> openStoryDialog());

    }



    private void openStoryDialog ()
    {
        // story alert dialog
        builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.story_layout, null);
        builder.setView(dialogView);
        builder.setNegativeButton("ok", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onClick(View view)
    {

        if (view == userButton)
        {
            // will happen if never logged in or if user logged out
            if (user == null)
            {
                MainActivity.this.startActivity(new Intent(MainActivity.this, User_dialog.class));

                findViewById(R.id.user_btn).setBackground(ContextCompat.getDrawable(this, R.drawable.logout_icon));

            }

            else {
                FirebaseAuth.getInstance().signOut();

                // NOTICE: these two lines below are supremely important, otherwise the user would never be null
                user = mAuth.getCurrentUser();
//                User_dialog.user = new User(false);

                // when logged out show the user he can login again.
                findViewById(R.id.user_btn).setBackground(ContextCompat.getDrawable(this, R.drawable.user_png));

            }

        }
    }
/*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();

        Toast.makeText(this, "hi",Toast.LENGTH_SHORT).show();
    }*/
}

