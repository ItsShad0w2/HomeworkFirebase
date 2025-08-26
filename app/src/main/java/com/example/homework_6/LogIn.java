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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity
{
    EditText editTextEmail, editTextPassword;
    Button buttonLogIn, buttonToSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogIn = findViewById(R.id.buttonLogIn);
        buttonToSignIn = findViewById(R.id.buttonToSignIn);

        buttonLogIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                logIn();
            }
        });

        buttonToSignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LogIn.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public void logIn()
    {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this, "Make sure all of the fields are filled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Connecting..");
            progressDialog.setMessage("Logging in..");
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    progressDialog.dismiss();
                    if(task.isSuccessful())
                    {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Toast.makeText(LogIn.this, "Welcome to the application", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LogIn.this, UsersList.class);
                        intent.putExtra("usersId", user.getUid());
                        intent.putExtra("loggedIn", true);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Exception exception = task.getException();
                        if(exception instanceof FirebaseAuthInvalidUserException)
                        {
                            Toast.makeText(LogIn.this, "Invalid email address, please change it", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if(exception instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Toast.makeText(LogIn.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(LogIn.this, "An error occured. Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
    }
}