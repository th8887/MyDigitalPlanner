package com.example.mydigitalplanner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FBref {
    public static FirebaseAuth reAuth= FirebaseAuth.getInstance();

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference refDB = database.getReference("Users");
    public static DatabaseReference refDBM= database.getReference("Mission");
    /**
     * refDBUC- reference to the uncompleted root.
     *
     * refDBC- reference for the completed missions.
     */
    public static DatabaseReference refDBUC= database.getReference(
            "Mission/"+reAuth.getCurrentUser().getUid()+"/uncompleted");

    public static DatabaseReference refDBC= database.getReference(
            "Mission/"+reAuth.getCurrentUser().getUid()+"/completed");

    public static FirebaseStorage storage= FirebaseStorage.getInstance();
    public static StorageReference storageReference=storage.getReference();

}
