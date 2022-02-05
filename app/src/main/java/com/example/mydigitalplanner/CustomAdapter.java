package com.example.mydigitalplanner;

import static com.example.mydigitalplanner.FBref.refDBM;
import static com.example.mydigitalplanner.FBref.refDBUC;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<String> titles;
    ArrayList<Mission> m;

    public CustomAdapter(Context context) {
        this.context = context;
        inflater =(LayoutInflater.from(context)) ;

        refDBUC.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                titles.clear();
                m.clear();
                for(DataSnapshot data : dS.getChildren()) {
                    Mission mTmp = data.getValue(Mission.class);
                    titles.add(mTmp.getTitle());
                    m.add(mTmp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.checklist_cell, null);
        TextView mission_title = (TextView) view.findViewById(R.id.mission_title);
        CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
        mission_title.setText(titles.get(i));

        if (cb.isChecked()){
            Mission m1= m.get(i);
            m1.setActive(false);
        }

        return view;
    }

}
