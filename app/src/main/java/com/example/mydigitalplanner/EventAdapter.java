package com.example.mydigitalplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * An adapter for the event list that will be shown in each day
 */
public class EventAdapter extends ArrayAdapter<Event> {
    public EventAdapter(@NonNull Context context, List<Event> events) {
        super(context, 0, events);
    }

    /**
     * inflates the evet cell for each event that was created
     * @param position
     * @param cv
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View cv, @NonNull ViewGroup parent) {
        Event e = getItem(position);

        if(cv == null){
            cv = LayoutInflater.from(getContext())
                    .inflate(R.layout.event_cell, parent, false);
        }

        TextView eventCell = (TextView) cv.findViewById(R.id.eventCell);
        String eventS = e.getTitle();
        eventCell.setText(eventS);

        return cv;
    }
}
