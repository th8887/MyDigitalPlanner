package com.example.mydigitalplanner;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.mydigitalplanner.FBref.reAuth;
import static com.example.mydigitalplanner.FBref.refDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateMission extends AppCompatActivity  implements TimePickerDialog.OnTimeSetListener {
    EditText title,openDate,dueDate, timeOpen, timeDue;

    /**
     * a plus to add links for pictures.
     */
    FloatingActionButton fab;
    /**
     * cc- create category.
     */
    Button cc;
    /**
     * Taking the information from firebase into the object.
     */
    User u;
    /**
     * takes the users id from firebase auth.
     */
    String uid;

    ArrayList<String> c;

    Intent i;

    int hour, min;

    private DatePickerDialog.OnDateSetListener mDataSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mission);
        fab = findViewById(R.id.fab);
        openDate=(EditText)findViewById(R.id.openDate);
        dueDate=(EditText) findViewById(R.id.dueDate);
        timeOpen=(EditText) findViewById(R.id.timeOpen);
        timeDue=(EditText) findViewById(R.id.timeDue);
        cc=(Button) findViewById(R.id.cc);
        /**
         * if the user wants to create a new category.
         */

        cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    /**
     * To Display the custom dialog.
     * inside the function:
     * 1. defines the dialog and whats inside it.
     * 2. reads the information from Firebase and then updates the ArrayList category.
     * 3. closes the dialog.
     */
    void showCustomDialog(){
        final Dialog dialog = new Dialog(CreateMission.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.create_category);
        //Initializing the views of the dialog.
        final EditText nameC= dialog.findViewById(R.id.nameC);
        Button add= dialog.findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FirebaseUser fbuser = reAuth.getCurrentUser();
                uid = fbuser.getUid();
                Query query = refDB
                        .orderByChild("uID")
                        .equalTo(uid);

                query.addListenerForSingleValueEvent(VEL);
                String n= nameC.getText().toString();
                c.add(n);
                refDB.child(u.getuID()).setValue(c);
                dialog.dismiss();
            }
        });

        dialog.show();
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
                    u = data.getValue(User.class);
                    c=u.getCategory();
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };




    /**
     * TimePicker of the open date of a mission.
     * @param view
     */
    public void openDate(View view) {
        date(openDate);
    }

    /**
     * TimePicker for the finish day of the mission
     * @param view
     */
    public void dueDate(View view) {
        date(dueDate);
    }

    /**
     * TimePicker for the start time of the mission.
     * @param view
     */
    public void timeopen(View view) {
        time(timeOpen);
    }

    /**
     * TimePIcker for the finish time of the mission.
     * @param view
     */

    public void timedue(View view) {
        time(timeDue);
    }
    /**
     * In order to not make things twice, creates the DatePicker for each EditText.
     * @param o- the EditText that the info will go to.
     */
    public void date(EditText o){
        Calendar cal= Calendar.getInstance();
        int year= cal.get(Calendar.YEAR);
        int month= cal.get(Calendar.MONTH);
        int day= cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog= new DatePickerDialog(
                CreateMission.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDataSetListener,
                year,month, day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        mDataSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month= month+1;
                Log.d(TAG,"OnDateSet : dd/mm/yyyy "+ day +"/"+ month +"/"+ year);
                String date= day+"/"+month+"/"+year;
                o.setText(date);
            }
        };
    }

    /**
     * In order to not make things twice, creates the TimePicker for each EditText.
     * @param t- the EditText that the info will go to.
     */
    public void time(EditText t){
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
        t.setText(hour+":"+min);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hour=hourOfDay;
        min=minute;
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
                i= new Intent(this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.page2:
                i= new Intent(this, LogInOk.class);
                startActivity(i);
                break;
            case R.id.page3:
                Toast.makeText(this, "You're already here!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.page4:
                i= new Intent(this, com.example.mydigitalplanner.Calendar.class);
                startActivity(i);
                break;
        }
        return true;
    }

}


