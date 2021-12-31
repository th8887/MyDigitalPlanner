package com.example.mydigitalplanner;

import static com.example.mydigitalplanner.FBref.reAuth;
import static com.example.mydigitalplanner.FBref.refDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LogInOk extends AppCompatActivity {

    TextView n, e, uID;
    CheckBox cBconnectview;

    String name, email, uid;
    Boolean newuser;
    User user;
    long count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_ok);

        n=(TextView)findViewById(R.id.tVnameview);
        e=(TextView)findViewById(R.id.tVemailview);
        uID=(TextView)findViewById(R.id.tVuidview);
        cBconnectview=(CheckBox)findViewById(R.id.cBconnectview);

        Intent gi=getIntent();
        newuser=gi.getBooleanExtra("newuser",false);
        //refDB.addListenerForSingleValueEvent(VELUpdateSNum);
    }

    /**
     * ON start the activity shows the information about the user.
     */
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser fbuser = reAuth.getCurrentUser();
        uid = fbuser.getUid();
        Query query = refDB
                .orderByChild("uid")
                .equalTo(uid);
        query.addListenerForSingleValueEvent(VEL);
        email = fbuser.getEmail();
        e.setText(email);
        uID.setText(uid);
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        Boolean isChecked = settings.getBoolean("stayConnect", false);

    }

    /**
     * Gets the information about the user from the database.
     */
    com.google.firebase.database.ValueEventListener VEL = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dS) {
            if (dS.exists()) {
                long count=dS.getChildrenCount();
                for(DataSnapshot data : dS.getChildren()) {
                    user = data.getValue(User.class);
                    n.setText(user.getName());
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    public void update(View view) {
        if (!cBconnectview.isChecked()){
            reAuth.signOut();
        }
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();
        editor.putBoolean("stayConnect",cBconnectview.isChecked());
        editor.commit();
        finish();
    }
}