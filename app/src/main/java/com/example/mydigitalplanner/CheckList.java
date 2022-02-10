package com.example.mydigitalplanner;

import static com.example.mydigitalplanner.FBref.refDBUC;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mydigitalplanner.databinding.ActivityCheckListBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CheckList extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnCreateContextMenuListener {

    private ActivityCheckListBinding binding;

    ListView titles;

    ArrayList<String> title_Mission= new ArrayList<String>();
    ArrayList<Mission> missions= new ArrayList<Mission>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCheckListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        titles=(ListView) findViewById(R.id.titles);



        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(CheckList.this, CreateMission.class));
            }
        });


        titles.setOnItemClickListener(this);
        titles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        titles.setOnCreateContextMenuListener(this);


        refDBUC.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                title_Mission.clear();
                missions.clear();
                for(DataSnapshot data : dS.getChildren()) {
                    Mission mTmp = data.getValue(Mission.class);
                    title_Mission.add(mTmp.getTitle());
                    missions.add(mTmp);
                }
                ArrayAdapter <String> adp= new ArrayAdapter<String>(CheckList.this,
                        R.layout.support_simple_spinner_dropdown_item, title_Mission);
                titles.setAdapter(adp);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.add("check mission");
        menu.add("update mission");
        menu.add("cancel");

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String oper = item.getTitle().toString();
        if (oper.equals("check mission")) {

        }
        return true;

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }
}