package com.example.mydigitalplanner;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.mydigitalplanner.FBref.reAuth;
import static com.example.mydigitalplanner.FBref.refDB;
import static com.example.mydigitalplanner.FBref.refDBM;
import static com.example.mydigitalplanner.FBref.refDBUC;
import static com.example.mydigitalplanner.FBref.storageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
//fix dialog with pictures.
public class CreateMission extends AppCompatActivity  implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    EditText t, des, start, end;
    Spinner catS;
    ListView links;
    RadioButton i0, i1, i2;
    RadioGroup iGroup;
    /**
     * a plus to add links for pictures.
     */
    FloatingActionButton fab;
    /**
     * Taking the information from firebase into the object.
     */
    User user;
    /**
     * takes the users id from firebase auth.
     */
    String uid,n;
    /**
     * Strings, boolean and arrayList for the update user.
     */
    String name, phone, uID, email;
    ArrayList<String> c;
    boolean a;
    /**
     * params for a new Mission.
     * @param- oD- opendate
     * @paran- dD- dueDate
     * @param- oT- openTime
     * @param- dT- dueTime
     * @param- importance:
     *          0-very important
     *          1-less important
     *          2-not important
     */
    int category;
    int importance;
    String title, description, s, e;
    ArrayList<String> images= new ArrayList<String>();
    /**
     * if the user went to another activity to upload a picture- true
     * else- false
     */
    boolean status= false;

    /**
     * for the time picker.
     */
    int hour, min;
    /**
     * Adapter for the spinner.
     */
    ArrayAdapter<String> adp;
    /**
     * Adapter for listview- for the links of the images.
     */
    ArrayAdapter<String> adpLinks;

    public static DatePickerDialog.OnDateSetListener mDataSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mission);
        fab = findViewById(R.id.fab);
        t=(EditText) findViewById(R.id.title);
        des=(EditText) findViewById(R.id.des);
        catS=(Spinner) findViewById(R.id.catS);
        links=(ListView) findViewById(R.id.links);
        start=(EditText) findViewById(R.id.start);
        end=(EditText) findViewById(R.id.end);

        i0=(RadioButton) findViewById(R.id.i0);
        i1=(RadioButton) findViewById(R.id.i1);
        i2=(RadioButton) findViewById(R.id.i2);
        iGroup=(RadioGroup) findViewById(R.id.iGroup);

        links.setOnItemSelectedListener(this);

        links.setChoiceMode(ListView.CHOICE_MODE_SINGLE);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateMission.this, Camera_or_Gallery.class));

            }
        });


        catS.setOnItemSelectedListener(this);

        FirebaseUser fbuser = reAuth.getCurrentUser();
        uid = fbuser.getUid();
        Query q = refDB.orderByChild("uID").equalTo(uid);
        q.addListenerForSingleValueEvent(VEL);

        images.add("images->");
    }

    ValueEventListener VEL = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dS) {
            if (dS.exists()) {
                for(DataSnapshot data : dS.getChildren()) {
                    user = data.getValue(User.class);

                    c= user.getCategory();
                    name=user.getName();
                    phone= user.getPhone();
                    uID= user.getuID();
                    email= user.getEmail();
                    a= user.getActive();
                }
                adp = new ArrayAdapter<String>(CreateMission.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        c);
                catS.setAdapter(adp);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        Intent getI= getIntent();
        status=getI.getBooleanExtra("status",false);
        if(status){
            images.add(getI.getStringExtra("way"));
        }

        adpLinks= new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, images);
        links.setAdapter(adpLinks);
    }

    public void startM(View view) {
         showDateTimeDialog(start);
    }

    public void endM(View view) {
        showDateTimeDialog(end);
    }


    /**
     * creates a date picker and a time picker at the same time.
     * saves more space and easier to collect the information that way.
     *
     * @param date_time_in
     */
    public void showDateTimeDialog(final EditText date_time_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd HH:mm");

                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(CreateMission.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(CreateMission.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    /**
     * if the adbLinks is selected- it will show a dialog that will show the image that the user chose.
     * if adb is selected- if will collect the number of the category the user chose.
     * @param parent
     * @param v
     * @param pos
     * @param rowid
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int pos, long rowid)
    {
        category= pos+1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    /**
     * the user can add a category to the mission.
     * @param view
     */
    public void showCustomDialog(View view) {
        final Dialog dialog = new Dialog(CreateMission.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.create_category);

        final EditText nameC= dialog.findViewById(R.id.nameC);
        Button add= dialog.findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                n= nameC.getText().toString();
                c.add(n);

                ArrayAdapter<String> adp2= new ArrayAdapter<String>(CreateMission.this
                        ,R.layout.support_simple_spinner_dropdown_item
                        ,c);
                catS.setAdapter(adp2);

                User newU= new User(name,email,phone,uID,a);
                newU.setCategory(c);
                refDB.child(uID).setValue(newU);


                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Saves the mission into Firebase Database according to users ID and
     * completed/ uncompleted missions.
     * @param view
     */
    public void saveMission(View view)
    {
        if(t.getText().equals(null)){
            Toast.makeText(CreateMission.this, "You must have a title", Toast.LENGTH_SHORT).show();
        }
        else{
            title= t.getText().toString();
            description= des.getText().toString();
            s= start.getText().toString();
            e= end.getText().toString();

            if(i0.isChecked()){
                importance=0;
            }
            if(i1.isChecked()){
                importance=1;
            }
            if (i2.isChecked()){
                importance=2;
            }

            Mission m= new Mission(title, importance, description, s, e, category, images);
            refDBUC.child(title).setValue(m);

            t.setText(" ");

            iGroup.clearCheck();
            des.setText(" ");
            start.setText(" ");
            end.setText(" ");
            images.clear();
            images.add("images");

            Toast.makeText(this, "Your mission has been uploaded😊", Toast.LENGTH_SHORT).show();
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
        Intent i;
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
                Toast.makeText(this, "You're already here!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.page4:
                i= new Intent(this, com.example.mydigitalplanner.Calendar.class);
                startActivity(i);
                break;
            case R.id.page5:
                i= new Intent(this, CheckList.class);
                startActivity(i);
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AlertDialog.Builder adb= new AlertDialog.Builder(this);
        adb.setMessage("selected Image");

        adb.setCancelable(true);

        ImageView show= new ImageView(this);

        StorageReference dateRef = storageReference.child(images.get(i));

        final long ONE_MEGABYTE = 1024 * 1024;
        dateRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bMap = BitmapFactory.decodeByteArray(bytes, 0,1 );
                show.setImageBitmap(bMap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        AlertDialog ad= adb.create();
        ad.show();
    }
}


