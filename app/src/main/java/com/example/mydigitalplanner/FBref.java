package com.example.mydigitalplanner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBref {
    public static FirebaseAuth reAuth= FirebaseAuth.getInstance();

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference refDB = database.getReference("Users");
    public static DatabaseReference refDB1 = database.getReference("message");

}
