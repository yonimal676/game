package com.example.game;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    AlertDialog.Builder builder;

    FirebaseAuth mAuth;
    FirebaseUser user;

    Button userButton;
    boolean canUserLogIn;

    boolean isFirstLogin = true;




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


        if (User_dialog.user != null && User_dialog.user.isIn())
            findViewById(R.id.user_btn).setBackground(ContextCompat.getDrawable(this, R.drawable.logout_icon));



        userButton = (Button) findViewById(R.id.user_btn);



        userButton.setOnClickListener(this);








        // story alert dialog
        builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.story_layout, null);
        builder.setView(dialogView);
        builder.setNegativeButton("ok", (dialog, which) -> dialog.dismiss());




        // if pressed button that is associated with R.id.story_btn -> openStoryDialog()
        findViewById(R.id.story_btn).setOnClickListener(v -> openStoryDialog());




    }



    private void openStoryDialog ()
    {
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onClick(View view)
    {


        if (view == userButton)
        {
            if (user == null)
            {

                //User_dialog.user == null || ! User_dialog.user.isIn()
                MainActivity.this.startActivity(new Intent(MainActivity.this, User_dialog.class));

                findViewById(R.id.user_btn).setBackground(ContextCompat.getDrawable(this, R.drawable.logout_icon));

            }
            else {
                FirebaseAuth.getInstance().signOut();
                user = mAuth.getCurrentUser();

                User_dialog.user = new User(false);

                findViewById(R.id.user_btn).setBackground(ContextCompat.getDrawable(this, R.drawable.user_png));

            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();

        Toast.makeText(this, "hi",Toast.LENGTH_SHORT).show();
    }
}

