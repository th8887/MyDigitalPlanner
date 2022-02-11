package com.example.mydigitalplanner;

import static com.example.mydigitalplanner.FBref.refDBC;
import static com.example.mydigitalplanner.FBref.refDBUC;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

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

    Mission m;

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

        if (v.getId() == R.id.titles){
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            String s= (String) lv.getItemAtPosition(acmi.position);

            int i=0;
            while((i<title_Mission.size()) && (!s.equals(title_Mission.get(i)))){
                i++;
            }

            m= missions.get(i);
        }

        menu.add("check mission");
        menu.add("update mission");
        menu.add("cancel");

    }

    /**
     * check mission- deletes the mission from uncompleted root, updates the activity
     * to true(the mission is done), and adds the mission into completed root in Firebase.
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String oper = item.getTitle().toString();
        if (oper.equals("check mission")) {
            refDBUC.child(m.getTitle()).removeValue();
            m.setActive(true);
            refDBC.child(m.getTitle()).setValue(m);


             int i=0;
             while(i<missions.size()&& !m.equals(missions.get(i))){
                 i++;
             }
             missions.remove(i);
             title_Mission.remove(i);
            ArrayAdapter <String> adp= new ArrayAdapter<String>(CheckList.this,
                    R.layout.support_simple_spinner_dropdown_item, title_Mission);
            titles.setAdapter(adp);
            Toast.makeText(this, "Works!😁", Toast.LENGTH_SHORT).show();

        }

        if(oper.equals("update mission")){
            Intent si= new Intent(CheckList.this, CreateMission.class);

            si.putExtra("status", true);
            si.putExtra("check", 1);
            si.putExtra("images", m.getImages());


            SharedPreferences setting = getSharedPreferences("missionInfo", MODE_PRIVATE);
            SharedPreferences.Editor editor = setting.edit();

            editor.putString("title",m.getTitle() );
            editor.putString("start",  m.getOpenDate());
            editor.putString("end", m.getDueDate());
            editor.putInt("importance",  m.getImportance());
            editor.putInt("category", m.getCategory());
            editor.putString("description", m.getDescription());
            editor.commit();

            startActivity(si);
            finish();
        }

        if(oper.equals("cancel")){
            closeContextMenu();
        }
        return true;

    }

    @Override
    public void onItemClick(AdapterView<?> par, View v, int pos, long l) {
        m= missions.get(pos);
    }
}