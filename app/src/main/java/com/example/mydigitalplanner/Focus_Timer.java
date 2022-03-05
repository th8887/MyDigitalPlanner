package com.example.mydigitalplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Focus_Timer extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner h, m;

    TextView cd;

    Button reset, start, clear;

    Intent i;

    long START_TIME_IN_MILLIS=0 , mTimeLeftInMillis;

    private CountDownTimer mCountDownTimer;

    String [] hours= {"0","1", "2", "3", "4", "5", "6", "7", "8", "9","10","11",
            "12", "13", "14", "15" ,"16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
            "26", "27", "28", "29" ,"30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
            "40", "41", "42", "43" ,"44", "45", "46", "47", "48", "49", "50", "51", "52", "53",
            "54", "55", "56", "57" ,"58", "59","60","61","62","63","64","65","66","67","68","69",
            "70","71","72","73","74","75","76","77","78","79","80","81","82","83","84","85","86",
            "87","88","89","90","91","92","93","94","95","96","97","98","99"};

    //minutes or sec, the same number from 0 to 59
    String [] mis = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "10", "11", "12", "13", "14", "15" , "16", "17", "18", "19", "20", "21", "22", "23",
            "24", "25", "26", "27", "28", "29" , "30", "31", "32", "33", "34", "35", "36", "37",
            "38", "39", "40", "41", "42", "43" , "44", "45", "46", "47", "48", "49", "50", "51",
            "52", "53", "54", "55", "56", "57" , "58", "59"};

    boolean b=false;

    private boolean mTimerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_timer);


        h=(Spinner) findViewById(R.id.h);
        m= (Spinner) findViewById(R.id.m);
        cd=(TextView) findViewById(R.id.cd);
        reset= (Button) findViewById(R.id.reset);
        start=(Button) findViewById(R.id.start);
        clear= (Button) findViewById(R.id.clear);


        h.setOnItemSelectedListener(this);
        m.setOnItemSelectedListener(this);

        ArrayAdapter<String> adph = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item,hours);
        h.setAdapter(adph);

        ArrayAdapter<String> adpm = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item,mis);
        m.setAdapter(adpm);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                h.setVisibility(View.GONE);
                m.setVisibility(View.GONE);
                cd.setVisibility(View.VISIBLE);

                if(cd.getText().toString().equals("00:00:00")||b){
                    mTimeLeftInMillis = START_TIME_IN_MILLIS;
                }
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        //reset the time to the previous time that was chosen
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
                resetTimer();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                START_TIME_IN_MILLIS=0;
                h.setSelection(0);
                m.setSelection(0);
                clear.setVisibility(View.INVISIBLE);
                reset.setVisibility(View.INVISIBLE);
                start.setVisibility(View.VISIBLE);
                cd.setText("00:00:00");
                b=false;
            }
        });

        updateCountDownText();
    }

    /**
     * @des starts the timer and the countdown, when the timer is done the user gets
     * a notification and the spinners are visible again.
     */
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                Toast.makeText(Focus_Timer.this, "Done!", Toast.LENGTH_SHORT).show();
                mTimerRunning = false;

                START_TIME_IN_MILLIS=0;

                start.setText("Start");
                start.setVisibility(View.VISIBLE);
                reset.setVisibility(View.VISIBLE);
                clear.setVisibility(View.VISIBLE);

                h.setVisibility(View.VISIBLE);
                m.setVisibility(View.VISIBLE);

                b=false;
            }
        }.start();

        mTimerRunning = true;
        start.setText("pause");

        reset.setVisibility(View.INVISIBLE);
        clear.setVisibility(View.INVISIBLE);
    }

    /**
     * @des shows the countdown in the TextView and converting the time from milliseconds to
     * seconds, minutes and hours.
     */
    private void updateCountDownText() {
        int minutes = (int) ((mTimeLeftInMillis%3600000) / 1000) / 60;
        int seconds = (int) ((mTimeLeftInMillis%3600000) / 1000) % 60;
        int hour= (int) (mTimeLeftInMillis / 3600000);

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minutes, seconds);

        cd.setText(timeLeftFormatted);
    }


    /**
     * @des stops the timer and shows the spinners
     * again so the user can reset or change the amount of time they want.
     */
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        start.setText("Start");

        reset.setVisibility(View.VISIBLE);
        clear.setVisibility(View.VISIBLE);

        h.setVisibility(View.VISIBLE);
        m.setVisibility(View.VISIBLE);
    }


    /**
     * turns the chosen time to milliseconds and if the user wants to change the time
     * when the timer is running the user adds the new time to the previous one.
     * @param par
     * @param view
     * @param pos
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> par, View view, int pos, long l) {
        switch(par.getId()){
            case R.id.h:
                if(!cd.getText().toString().equals("00:00:00")){
                    b=true;
                }
                START_TIME_IN_MILLIS=mTimeLeftInMillis+ (long) (pos*3600000);
                break;
            case R.id.m:
                if(!cd.getText().toString().equals("00:00:00")){
                    b=true;
                }
                START_TIME_IN_MILLIS= mTimeLeftInMillis+ (long) (pos*60000);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    /**
     * @des reset the time to the time the user chose in the first place.
     */
    private void resetTimer() {
        updateCountDownText();

        reset.setVisibility(View.INVISIBLE);
        clear.setVisibility(View.INVISIBLE);

        start.setVisibility(View.VISIBLE);
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
            case R.id.ap:
                i= new Intent(this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.ui:
                i= new Intent(this, LogInOk.class);
                startActivity(i);
                break;
            case R.id.c:
                i= new Intent(this, Calendar.class);
                startActivity(i);
                break;
            case R.id.cl:
                i= new Intent(this, CheckList.class);
                startActivity(i);
                break;
            case R.id.ft:
                Toast.makeText(this, "You're already here!", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}