package com.example.mydigitalplanner;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.mydigitalplanner.FBref.reAuth;
import static com.example.mydigitalplanner.FBref.refDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

//can't read from database...

public class LogInOk extends AppCompatActivity {

    EditText n, e, uID,p;
    CheckBox cBconnectview;

    String name, email, uid,phone;
    Boolean newuser;
    Intent i;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_ok);

        n= (EditText) findViewById(R.id.nameu);
        e= (EditText) findViewById(R.id.emailu);
        uID= (EditText) findViewById(R.id.uidu);
        p=(EditText) findViewById(R.id.phoneu);
        cBconnectview=(CheckBox)findViewById(R.id.cBconnectview);





        Intent gi=getIntent();
        newuser=gi.getBooleanExtra("newuser",false);


    }

    ValueEventListener VEL = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dS) {
            if (dS.exists()) {
                for(DataSnapshot data : dS.getChildren()) {
                    user = data.getValue(User.class);
                    n.setText(user.getName());
                    p.setText(user.getPhone());
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };


    /**
     * ON start the activity shows the information about the user.
*/
    @Override
    public void onStart() {
        super.onStart();


        FirebaseUser fbuser = reAuth.getCurrentUser();
        uid = fbuser.getUid();
        Query query = refDB
                .orderByChild("uID")
                .equalTo(uid);

        query.addListenerForSingleValueEvent(VEL);
        email = fbuser.getEmail();
        e.setText(email);
        uID.setText(uid);
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        Boolean isChecked = settings.getBoolean("stayConnect", false);
        cBconnectview.setChecked(isChecked);
    }

    public void update(View view) {
        if (!cBconnectview.isChecked()){
            reAuth.signOut();
        }
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();
        editor.putBoolean("stayConnect",cBconnectview.isChecked());
        editor.commit();
        Intent si= new Intent(LogInOk.this,MainActivity.class);
        startActivity(si);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.page1:
                Toast.makeText(this, "You're already here!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.page2:
                i= new Intent(this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.page3:
                i= new Intent(this, CreateMission.class);
                startActivity(i);
                break;
            case R.id.page4:
                i= new Intent(this, com.example.mydigitalplanner.Calendar.class);
                startActivity(i);
                break;
        }
        return true;
    }
}