package com.example.homework_6;

import static com.example.homework_6.Firebase.firebaseAuth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
{
    EditText email, numberPassword;
    Button buttonSignUp, buttonLogIn;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        numberPassword = findViewById(R.id.numberPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonLogIn = findViewById(R.id.buttonToLogIn);

        buttonSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createUser();
            }
        });

        buttonLogIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, LogIn.class);
                startActivity(intent);
            }
        });

    }

    public void createUser()
    {
        String email = this.email.getText().toString();
        String password = this.numberPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this, "Please make sure all of the fields are filled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Connecting..");
            progressDialog.setMessage("Creating the user..");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    progressDialog.dismiss();
                    if(task.isSuccessful())
                    {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if(user != null)
                        {
                            Toast.makeText(MainActivity.this, "Welcome to the application", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, UsersList.class);
                            startActivity(intent);
                        }
                    }
                    else
                    {
                        Exception exception = task.getException();

                        if(exception instanceof FirebaseAuthInvalidUserException)
                        {
                            Toast.makeText(MainActivity.this, "Invalid email address, please change it", Toast.LENGTH_SHORT).show();
                        }
                        else if(exception instanceof FirebaseAuthWeakPasswordException)
                        {
                            Toast.makeText(MainActivity.this, "The password entered is too weak, please change it", Toast.LENGTH_SHORT).show();
                        }
                        else if(exception instanceof FirebaseAuthUserCollisionException)
                        {
                            Toast.makeText(MainActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                        }
                        else if(exception instanceof FirebaseAuthInvalidCredentialsException)
                        {
                            Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                        else if(exception instanceof FirebaseNetworkException)
                        {
                            Toast.makeText(MainActivity.this, "Make sure you're connected to an internet service", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "An error has occurred, please try later to sign up", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }



}