package com.example.mydigitalplanner;

import static com.example.mydigitalplanner.CalendarUtils.selectedDate;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final ArrayList<LocalDate> days;
    private final OnItemClickListener mOnItenClickListener;

    /**
     * An adapter for the custom calendar
     * @param daysOfMonth
     * @param mOnItenClickListener
     */
    public CalendarAdapter(ArrayList<LocalDate> daysOfMonth, OnItemClickListener mOnItenClickListener) {
        this.days = daysOfMonth;
        this.mOnItenClickListener = mOnItenClickListener;
    }

    /**
     * creates the cells for each view- for the monthly view and for the weekly view
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        View v = i.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        if(days.size()>15){//for a month
            lp.height = (int) (parent.getHeight()*0.1666666);
        }
        else//for a week
            lp.height = (int) (parent.getHeight());

        return new CalendarViewHolder(v, days, mOnItenClickListener);
    }

    /**
     * @des puts the dates(the number of each date) in its cells.
     * @param holder
     * @param position
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        LocalDate date = days.get(position);
        if (date == null)//for the month- the blank spaces
            holder.dayOfMonth.setText("");
        else {//for the month and the week: the right dates in the month and the
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if (date.isEqual(selectedDate)){
                holder.colorCell.setBackgroundColor(Color.LTGRAY);
            }
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public interface OnItemClickListener{
        void onItemClick(int pos, LocalDate date);
    }
}
