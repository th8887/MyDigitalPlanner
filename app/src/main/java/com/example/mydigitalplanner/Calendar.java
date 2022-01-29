package com.example.mydigitalplanner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Calendar extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;

    Intent i;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        initWidgets();
        selectedDate= LocalDate.now();
        setMonthView();

    }

    private void initWidgets() {

        calendarRecyclerView= findViewById(R.id.calendarRecyclerView);
        monthYearText= findViewById(R.id.monthYearTV);


    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth= daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),7 );
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

    }

    /**
     * builds the calendar according to the dates.
     * @param date
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray= new ArrayList<>();
        YearMonth yearMonth= YearMonth.from(date);
        int daysInMonth= yearMonth.lengthOfMonth();

        LocalDate firstOfMonth= selectedDate.withDayOfMonth(1);
        int dayOfWeek= firstOfMonth.getDayOfWeek().getValue();
        for (int i=1; i<=42; i++){
            if(i<= dayOfWeek || i>daysInMonth+dayOfWeek){
                daysInMonthArray.add("");
            }
            else{
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }

        return daysInMonthArray;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date){
        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    /**
     * shows previous month
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousMonthAction(View view) {
        selectedDate= selectedDate.minusMonths(1);
        setMonthView();
    }

    /**
     * shows the month after the present one
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextMonthAction(View view) {
        selectedDate= selectedDate.plusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.equals("")){
            startActivity(new Intent(this, DailyCalendar.class));
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
                i= new Intent(this, CreateMission.class);
                startActivity(i);
                break;
            case R.id.page4:
                Toast.makeText(this, "You're already here!", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}