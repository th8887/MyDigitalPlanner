package com.example.mydigitalplanner;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mydigitalplanner.databinding.ActivityCheckListBinding;

public class CheckList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ActivityCheckListBinding binding;

    ListView titles;

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

        CustomAdapter customadp = new CustomAdapter(getApplicationContext());
        titles.setAdapter(customadp);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}