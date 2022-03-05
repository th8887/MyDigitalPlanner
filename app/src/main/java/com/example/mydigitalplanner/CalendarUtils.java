package com.example.mydigitalplanner;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarUtils {

    public static LocalDate selectedDate;

    /**
     * creates the date of the month and the year for the TextView that shows the name of the year and month
     * @param date
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String monthYearFromDate(LocalDate date){
        DateTimeFormatter form = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(form);
    }


    /**
     * creates an Array with the dates of the month and them adds them to the adapter
     * in order for it to be in the calendar view
     * @param date
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth ym = YearMonth.from(date);

        int daysInMonth = ym.lengthOfMonth();

        LocalDate firstDayOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();

        for (int i=1; i<=42; i++)
        {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek){
                daysInMonthArray.add(null);
            }
            else{
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));
            }
        }

        return daysInMonthArray;
    }

    /**
     * creates the list with the days in the chosen week
     * @param selectedDate
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = sundayFromDate(selectedDate);

        LocalDate endDay = current.plusWeeks(1);

        while(current.isBefore(endDay)){
            days.add(current);
            current = current.plusDays(1);
        }
        return days;
    }

    /**
     * finds the right sunday to start the week with.
     * (goes backwords until you find sunday of the chosen week)
     * @param current
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static LocalDate sundayFromDate(LocalDate current) {
        LocalDate oneWeekAgo = current.minusWeeks(1);

        while(current.isAfter(oneWeekAgo)){
                if(current.getDayOfWeek() == DayOfWeek.SUNDAY){
                    return current;
                }

                current = current.minusDays(1);
        }
        return null;
    }
}
