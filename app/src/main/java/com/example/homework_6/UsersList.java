package com.example.homework_6;

import static com.example.homework_6.Firebase.firebaseAuth;
import static com.example.homework_6.Firebase.referenceAthlete;
import static com.example.homework_6.Firebase.storageReference;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.UUID;

public class UsersList extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    EditText editTextName, editTextAge, editTextCountry;
    Button buttonAdd, buttonUploadImage;
    ListView listView;
    ArrayList<Athlete> athletes = new ArrayList<>();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextCountry = findViewById(R.id.editTextCountry);
        listView = findViewById(R.id.listView);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        listView.setOnItemClickListener(this);

        buttonAdd.setEnabled(true);
        buttonUploadImage.setEnabled(true);

        ArrayList<String> athleteNames = new ArrayList<>();

        if(firebaseAuth.getCurrentUser() != null)
        {
            userId = firebaseAuth.getCurrentUser().getUid();
        }

        buttonAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addAthlete();
            }
        });

        buttonUploadImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                gallery(v);
            }
        });

        if(getIntent().getBooleanExtra("loggedIn", true))
        {
            referenceAthlete.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                        buttonAdd.setEnabled(false);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });

            storageReference.child("images/" + userId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri uri)
                {
                    buttonUploadImage.setEnabled(false);

                }
            });
        }

        referenceAthlete.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                athleteNames.clear();
                athletes.clear();
                for(DataSnapshot athleteSnapshot : snapshot.getChildren())
                {
                    Athlete athlete = athleteSnapshot.getValue(Athlete.class);
                    if(athlete != null)
                    {
                        athletes.add(athlete);
                        String name = athlete.getName();
                        String age = String.valueOf(athlete.getAge());
                        String country = String.valueOf(athlete.getCountry());
                        athleteNames.add(name + ", " + age + " years old, from " + country);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UsersList.this, android.R.layout.simple_list_item_1, athleteNames);
                listView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
    public void addAthlete()
    {
        String name = editTextName.getText().toString();
        String age = editTextAge.getText().toString();
        String country = editTextCountry.getText().toString();


        if(name.isEmpty() || age.isEmpty() || country.isEmpty())
        {
            Toast.makeText(this, "Please make sure all of the fields are filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!checkForName(name))
        {
            Toast.makeText(this, "Please make sure to check the name field", Toast.LENGTH_SHORT).show();
            return;
        }

        if(Integer.parseInt(age) < 1)
        {
            Toast.makeText(this, "Please make sure to check the age field", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!checkForName(country))
        {
            Toast.makeText(this, "Please make sure to check the country field", Toast.LENGTH_SHORT).show();
            return;
        }

        Athlete athlete = new Athlete(name, Integer.parseInt(age), country, userId);
        String id = referenceAthlete.push().getKey();
        if(id != null)
        {
            athlete.setKeyId(id);
            referenceAthlete.child(id).setValue(athlete);
            Toast.makeText(this, "You have an added an athlete, move on to your events by clicking on your data", Toast.LENGTH_SHORT).show();
            buttonAdd.setEnabled(false);
            clearFields();
        }
    }

    public void clearFields()
    {
        editTextName.setText("");
        editTextAge.setText("");
        editTextCountry.setText("");
    }

    public boolean checkForName(String name)
    {
        for(int i = 0; i < name.length(); i++)
        {
            if((name.charAt(i) < 65 || name.charAt(i) > 90) && (name.charAt(i) < 97 || name.charAt(i) > 127))
            {
                return false;
            }
        }

        return true;
    }

    public void gallery(View view)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK && data != null)
        {
            Uri imageUri = data.getData();
            uploadImage(imageUri);
        }
    }

    private void uploadImage(Uri imageUri)
    {
        if(imageUri != null)
        {
            String fileName = userId + ".jpg";
            StorageReference referenceFile = storageReference.child("images/" + fileName);

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading image..");
            progressDialog.setMessage("Please wait for the image to finish uploading..");
            progressDialog.show();

            referenceFile.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    progressDialog.dismiss();
                    Toast.makeText(UsersList.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                    buttonUploadImage.setEnabled(false);
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    progressDialog.dismiss();
                    Toast.makeText(UsersList.this, "Image couldn't upload", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot)
                {
                    double progress = (double) (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Athlete athlete = athletes.get(position);
        if(userId.equals(athlete.getUserId()))
        {
            Intent intent = new Intent(UsersList.this, EventsParticipating.class);
            intent.putExtra("athleteKeyId", athlete.getKeyId());
            startActivity(intent);
        }
    }
}