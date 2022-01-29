package com.example.mydigitalplanner;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.mydigitalplanner.FBref.reAuth;
import static com.example.mydigitalplanner.FBref.refDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
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
//לסדר את המסך שלו!!!
//לבדוק למה מציג את השעה בדיליי
public class CreateMission extends AppCompatActivity  implements TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {
    TextView openDate,dueDate, timeOpen, timeDue;
    EditText t, des;
    Spinner catS;
    RadioButton v;
    /**
     * a plus to add links for pictures.
     */
    FloatingActionButton fab;
    /**
     * Taking the information from firebase into the object.
     */
    User user;
    /**
     * takes the users id from firebase auth.
     */
    String uid,n;
    /**
     * Strings, boolean and arrayList for the update user.
     */
    String name, phone, uID, email;
    ArrayList<String> c;
    boolean a;
    /**
     * params for a new Mission.
     * @param- oD- opendate
     * @paran- dD- dueDate
     * @param- oT- openTime
     * @param- dT- dueTime
     * @param- importance:
     *          0-very important
     *          1-less important
     *          2-not important
     */
    int category;
    int importance;
    String title, description, oD, dD,oT, dT;
    ArrayList<String> images= new ArrayList<String>();

    /**
     * for the time picker.
     */
    int hour, min;
    /**
     * Adapter for the spinner.
     */
    ArrayAdapter<String> adp;

    public static DatePickerDialog.OnDateSetListener mDataSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mission);
        fab = findViewById(R.id.fab);
        openDate=(TextView)findViewById(R.id.openDate);
        dueDate=(TextView) findViewById(R.id.dueDate);
        timeOpen=(TextView) findViewById(R.id.timeOpen);
        timeDue=(TextView) findViewById(R.id.timeDue);
        t=(EditText) findViewById(R.id.title);
        des=(EditText) findViewById(R.id.des);
        catS=(Spinner) findViewById(R.id.catS);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateMission.this, Camera_or_Gallery.class));

                //images.add();
            }
        });


        catS.setOnItemSelectedListener(this);

        FirebaseUser fbuser = reAuth.getCurrentUser();
        uid = fbuser.getUid();
        Query q = refDB.orderByChild("uID").equalTo(uid);
        q.addListenerForSingleValueEvent(VEL);
    }

    ValueEventListener VEL = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dS) {
            if (dS.exists()) {
                for(DataSnapshot data : dS.getChildren()) {
                    user = data.getValue(User.class);

                    c= user.getCategory();
                    name=user.getName();
                    phone= user.getPhone();
                    uID= user.getuID();
                    email= user.getEmail();
                    a= user.getActive();
                }
                adp = new ArrayAdapter<String>(CreateMission.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        c);
                catS.setAdapter(adp);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };

    /**
     * OnClick for the button "create category"- creates a new category and adds it to Firebase Database.
     * To Display the custom dialog.
     * inside the function:
     * 1. defines the dialog and whats inside it.
     * 2. reads the information from Firebase and then updates the ArrayList category.
     * 3. closes the dialog.
     */
    public void showCustomDialog(View view){
        final Dialog dialog = new Dialog(CreateMission.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.create_category);

        final EditText nameC= dialog.findViewById(R.id.nameC);
        Button add= dialog.findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                n= nameC.getText().toString();
                c.add(n);

                ArrayAdapter<String> adp2= new ArrayAdapter<String>(CreateMission.this
                        ,R.layout.support_simple_spinner_dropdown_item
                        ,c);
                catS.setAdapter(adp2);

                User newU= new User(name,email,phone,uID,a);
                newU.setCategory(c);
                refDB.child(uID).setValue(newU);


                dialog.dismiss();
            }
        });

        dialog.show();
    }


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
    public void date(TextView o){
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
                //Log.d(TAG,"OnDateSet : dd/mm/yyyy "+ day +"/"+ month +"/"+ year);
                String date= day+"/"+month+"/"+year;
                o.setText(date);
            }
        };
    }

    /**
     * In order to not make things twice, creates the TimePicker for each EditText.
     * @param t- the EditText that the info will go to.
     */
    public void time(TextView t){
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
    public void onItemSelected(AdapterView<?> parent, View v, int pos, long rowid)
    {
        category= pos;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /**
     * Saves the mission into Firebase Database according to users ID and
     * completed/ uncompleted missions.
     * @param view
     */
    public void saveMission(View view)
    {
        if(t.getText().equals(null)){
            Toast.makeText(CreateMission.this, "You must have a title", Toast.LENGTH_SHORT).show();
        }
        else{
            title= t.getText().toString();
            oD= openDate.getText().toString();
            dD= dueDate.getText().toString();
            description= des.getText().toString();
            oT= timeOpen.getText().toString();
            dT= timeDue.getText().toString();
            switch (v.getId()){
                case R.id.i0: importance=0; break;
                case R.id.i1: importance=1; break;
                case R.id.i2: importance=2; break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i;
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


