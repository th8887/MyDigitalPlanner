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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DailyCalendar extends AppCompatActivity {

    EditText t, des, start, end;
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


        start=(EditText) findViewById(R.id.start);
        end=(EditText) findViewById(R.id.end);


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



    public void cc(View view) {
        showCustomDialog();
    }

    public void saveEvent(View view) {

    }

    public void startM(View view) {
        showDateTimeDialog(start);
    }

    public void endM(View view) {
        showDateTimeDialog(end);
    }


    public void showDateTimeDialog(final EditText date_time_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd HH:mm");

                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(DailyCalendar.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(DailyCalendar.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }
}