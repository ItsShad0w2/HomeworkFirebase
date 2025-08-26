package com.example.homework_6;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class Firebase
{
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    public static DatabaseReference referenceAthlete = firebaseDatabase.getReference("FBAT").child("Athlete");
    public static FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    public static StorageReference storageReference = firebaseStorage.getReference();
}
