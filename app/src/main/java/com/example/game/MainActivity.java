package com.example.game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN); // make it fullscreen


        findViewById(R.id.play_btn).setOnClickListener(view ->
                startActivity(new Intent(this, GameActivity.class)));



        Button openScrollButton = findViewById(R.id.story_btn);
        openScrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStoryDialog();
            }
        });


        Button openDialogButton = findViewById(R.id.user_btn);
        openDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomDialog();
            }
        });


    }





    private void openStoryDialog ()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        View dialogView = LayoutInflater.from(this).inflate(R.layout.story_layout, null);

        builder.setView(dialogView);
        builder.setNegativeButton("ok", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void openCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Values");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        final EditText editText1 = dialogView.findViewById(R.id.editText1);
        final EditText editText2 = dialogView.findViewById(R.id.editText2);



        builder.setView(dialogView);


        builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String value1 = editText1.getText().toString();
                String value2 = editText2.getText().toString();
                // Do something with the entered values

                MainActivity.this.addUser(value1, value2);

            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }



    public void addUser (String name, String password)
    {
        // add user to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference saveUser = database.getReference("users").push();


        User user = new User(name, password);

        saveUser.setValue(user);

    }

}

