package com.example.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

/*
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
*/


public class MainActivity extends AppCompatActivity
{

    EditText nameEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN); // make it fullscreen


        findViewById(R.id.play_btn).setOnClickListener(view ->
                startActivity(new Intent(this, GameActivity.class)));



//        nameEditText = findViewById(R.id)


    }


/*    public void saveName(View view)
    {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("name");

        myRef.setValue("Hello, World!");
    }*/


}