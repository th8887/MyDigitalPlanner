package com.example.mydigitalplanner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

//location
public class CreateEvent extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    EditText title, desEvent, locEvent;
    TextView dateEvent, timeEvent;
    Switch allDay;

    LocalDate ld;

    boolean a;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        title = (EditText) findViewById(R.id.t);
        allDay = (Switch) findViewById(R.id.allDay);
        dateEvent = (TextView) findViewById(R.id.dateEvent);
        timeEvent = (TextView) findViewById(R.id.timeEvent);
        locEvent = (EditText) findViewById(R.id.locEvent);
        desEvent = (EditText) findViewById(R.id.desEvent);

        Intent gi= getIntent();
        if(gi.getIntExtra("check",0)  == 2){
            ld= LocalDate.parse(gi.getStringExtra("date"));
            dateEvent.setText(String.valueOf(ld));
        }
        else if (gi.getIntExtra("check", 0) == 1){
            dateEvent.setText(gi.getStringExtra("date"));
        }

    }

    /**
     * saves the event in googleCalendar.
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveEvent(View view) {
        if (!title.getText().toString().isEmpty()&& !locEvent.getText().toString().isEmpty()) {
            if (allDay.isChecked())
                a=true;
            else
                a=false;

            String t = title.getText().toString();
            String l = locEvent.getText().toString();
            String ti = timeEvent.getText().toString();
            String des = desEvent.getText().toString();

            LocalDate ld0 = LocalDate.parse(dateEvent.getText().toString());

            Event e;

            if(!desEvent.getText().toString().isEmpty()) {
                e = new Event(t, ld0, ti, l, des, a);
            }
            else {
                e = new Event(t, ld0, ti, l, a);
            }
            Event.eventList.add(e);

            java.util.Calendar cal = java.util.Calendar.getInstance();
            Intent calintent = new Intent(Intent.ACTION_EDIT);
            calintent.setType("vnd.android.cursor.item/event");
            calintent.putExtra("eventLocation", locEvent.getText().toString());
            calintent.putExtra("title", title.getText().toString());
            calintent.putExtra("description", desEvent.getText().toString());
            calintent.putExtra("beginTime",  dateEvent.getText().toString());
            startActivity(calintent);
            finish();
        }
        else
            Toast.makeText(this, "You must have a name", Toast.LENGTH_SHORT).show();
    }

    /**
     * creates the time picker from TimePickerFragment class
     * @param view
     */
    public void dateEvent(View view) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timeEvent.setText(hourOfDay + " : " + minute);
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
            case R.id.ap:
                i= new Intent(this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.ui:
                i= new Intent(this, LogInOk.class);
                startActivity(i);
                break;
            case R.id.c:
                i= new Intent(CreateEvent.this, com.example.mydigitalplanner.Calendar.class);
                startActivity(i);
                break;
            case R.id.cl:
                i= new Intent(this, CheckList.class);
                startActivity(i);
                break;
            case R.id.ft:
                i= new Intent(this, Focus_Timer.class);
                startActivity(i);
                break;
        }
        return true;
    }
}