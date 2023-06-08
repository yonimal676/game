package com.example.game;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

public class User_dialog extends AppCompatActivity
{

    FirebaseAuth mAuth;

    static User user;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_layout);



        mAuth = FirebaseAuth.getInstance();

        final EditText email_editText = findViewById(R.id.editText1);
        final EditText password_editText = findViewById(R.id.editText2);



        findViewById(R.id.cancel_button).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });



        // registration
        findViewById(R.id.register_btn).setOnClickListener(v -> {

            if (TextUtils.isEmpty(email_editText.getText().toString()))
                Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show();

            else if (TextUtils.isEmpty(password_editText.getText().toString()))
                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();

            else
                register(email_editText.getText().toString(), password_editText.getText().toString());
        });




        // login
        findViewById(R.id.login_btn).setOnClickListener(v -> {

            if (TextUtils.isEmpty(email_editText.getText().toString()))
                Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show();

            else if (TextUtils.isEmpty(password_editText.getText().toString()))
                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();


            else
                login(email_editText.getText().toString(), password_editText.getText().toString());
        });



    }





    public void register (String email, String password)
    {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
//                            Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();

                            user = new User(email, password);



                            // Close the dialog
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        else // registration failed
                            Toast.makeText(User_dialog.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });

    }



    //TODO: HE REMOVED 'THIS'

    public void login (String email, String password)
    {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
//                            Toast.makeText(MainActivity.this, "Welcome Back :)", Toast.LENGTH_SHORT).show();

                            user = new User(email, password);
                            // Close the dialog
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        else //  sign in failed
                            Toast.makeText(User_dialog.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                    }
                });


    }



}
