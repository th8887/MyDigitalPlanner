package com.example.mydigitalplanner;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.example.mydigitalplanner.CreateMission.mDataSetListener;
import static com.example.mydigitalplanner.FBref.reAuth;
import static com.example.mydigitalplanner.FBref.refDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class DailyCalendar extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    EditText openDate,dueDate, timeOpen, timeDue;
    /**
     * Taking the information from firebase into the object.
     */
    User u;
    /**
     * takes the users id from firebase auth.
     */
    String uid;

    ArrayList<String> c;

    int hour, min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_calendar);

        openDate=(EditText)findViewById(R.id.openDate);
        dueDate=(EditText) findViewById(R.id.dueDate);
        timeOpen=(EditText) findViewById(R.id.timeOpen);
        timeDue=(EditText) findViewById(R.id.timeDue);
    }


    /**
     * To Display the custom dialog.
     * inside the function:
     * 1. defines the dialog and whats inside it.
     * 2. reads the information from Firebase and then updates the ArrayList category.
     * 3. closes the dialog.
     */
    void showCustomDialog(){
        final Dialog dialog = new Dialog(DailyCalendar.this);
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
        java.util.Calendar cal= java.util.Calendar.getInstance();
        int year= cal.get(java.util.Calendar.YEAR);
        int month= cal.get(java.util.Calendar.MONTH);
        int day= cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog= new DatePickerDialog(
                DailyCalendar.this,
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

    public void cc(View view) {
        showCustomDialog();
    }

    public void saveEvent(View view) {

    }
}