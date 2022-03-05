package com.example.mydigitalplanner;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.mydigitalplanner.FBref.reAuth;
import static com.example.mydigitalplanner.FBref.refDB;
import static com.example.mydigitalplanner.FBref.refDBUC;
import static com.example.mydigitalplanner.FBref.storageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
//fix dialog with pictures.
// TextView for dates!!
// OnResume() that will save the information.
//לעשות בדיקה - אם אין מידע אין מה לשמור
//לעשות onClick לכל כפתור של דחיפות מטלה!
public class CreateMission extends AppCompatActivity  implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    EditText t, des;
    TextView start, end;
    Spinner catS;
    ListView links;
    RadioButton i0,i1,i2;
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
     * the user comes back to this activity from to different activities:
     * 1.Camera_or_Gallery- check = 0
     * 2.CheckList-> check= 1;
     */
    int check;
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
    /**
     * for a check in when the user goes between activities and he didn't press any importance "RadioButtons"
     */
    int importance=3;
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
        t=(EditText) findViewById(R.id.t);
        des=(EditText) findViewById(R.id.des);
        catS=(Spinner) findViewById(R.id.catS);
        links=(ListView) findViewById(R.id.links);

        i0=(RadioButton) findViewById(R.id.i0);
        i1=(RadioButton) findViewById(R.id.i1);
        i2= (RadioButton) findViewById(R.id.i2);

        start=(TextView) findViewById(R.id.dateEvent);
        end=(TextView) findViewById(R.id.EndEvent);

        iGroup=(RadioGroup) findViewById(R.id.iGroup);

        links.setOnItemSelectedListener(this);

        links.setChoiceMode(ListView.CHOICE_MODE_SINGLE);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences setting = getSharedPreferences("missionInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = setting.edit();

                editor.putString("title", t.getText().toString());
                editor.putString("start", start.getText().toString());
                editor.putString("end", end.getText().toString());

                editor.putInt("importance", importance);
                editor.putInt("category", category);
                editor.putString("description", des.getText().toString());
                editor.commit();

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

                if (c.isEmpty()){
                    c.add("category->");
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

    /**
     * the intent gets a check from Camera_or_gallery activity and from CheckList activity in order to know the
     * difference between them(from checkList activity it needs to brings and images Arraylist that was in the mission for a update.
     *
     * (90% of the code is the same except the images ArrayList part.)
     *
     * check=0 -> the user got back from Camera_or_Gallery Activity
     * check=1-> the user got back to update a mission from CheckList Activity
     */
    @Override
    protected void onResume() {
        super.onResume();

        Intent getI= getIntent();
        status=getI.getBooleanExtra("status",false);
        check= getI.getIntExtra("check", 3);
        if(status){
            if(!images.contains(getI.getStringExtra("way"))){
                images.add(getI.getStringExtra("way"));
            }

            SharedPreferences settings= getSharedPreferences("missionInfo", MODE_PRIVATE);
            t.setText(settings.getString("title", "a"));
            start.setText(settings.getString("start", "s"));
            end.setText(settings.getString("end","e"));
            switch (settings.getInt("importance", -1)){
                case 0: i0.setChecked(true);break;
                case 1: i1.setChecked(true); break;
                case 2: i2.setChecked(true); break;
                case 3: iGroup.clearCheck(); break;
            }
            int n = settings.getInt("category", -1);
            catS.setSelection(n,true);
            des.setText(settings.getString("description","fff"));

            if(check == 1){
                images= getI.getStringArrayListExtra("images");
            }
        }

        adpLinks= new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, images);
        links.setAdapter(adpLinks);
    }

    /**
     * lets te user pick date and time for the beginning of the mission
     * @param view
     */
    public void startM(View view) {
         showDateTimeDialog(start);
    }

    /**
     * lets the user pick the date and time for the ending of the mission.
     * @param view
     */
    public void endM(View view) {
        showDateTimeDialog(end);
    }


    /**
     * creates a date picker and a time picker at the same time.
     * saves more space and easier to collect the information that way.
     *
     * @param date_time_in
     */
    public void showDateTimeDialog(final TextView date_time_in) {
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
     *  collects the number of the category the user chose.
     * @param parent
     * @param v
     * @param pos
     * @param rowid
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int pos, long rowid)
    {
        if(!(category!=0)){
            category= pos;
        }

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
     * shows the image for the user in a dialog.
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AlertDialog.Builder adb= new AlertDialog.Builder(this);
        adb.setMessage("selected Image");

        adb.setCancelable(true);

        ImageView show= new ImageView(this);

        StorageReference dateRef = storageReference.child("gs://mydigitalplanner-79d94.appspot.com/"+images.get(i));

        final long ONE_MEGABYTE = 1024 * 1024;
        dateRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bMap = BitmapFactory.decodeByteArray(bytes, 0,bytes.length );
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

    public void i0(View view) {
        importance=0;
    }

    public void i1(View view) {
        importance=1;
    }

    public void i2(View view) {
        importance=2;
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



            Mission m= new Mission(title, importance, description, s, e, category);
            m.setimages(images);
            if(check == 1){
                SharedPreferences settings= getSharedPreferences("missionInfo", MODE_PRIVATE);
                if(title!=settings.getString("title", "a")){
                    refDBUC.child(settings.getString("title", "a")).removeValue();
                }
            }
            refDBUC.child(title).setValue(m);

            t.setText(" ");
            catS.setSelection(0);
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
            case R.id.ap:
                i= new Intent(this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.ui:
                i= new Intent(this, LogInOk.class);
                startActivity(i);
                break;
            case R.id.c:
                i= new Intent(this, com.example.mydigitalplanner.Calendar.class);
                startActivity(i);
                break;
            case R.id.cl:
                i= new Intent(this, CheckList.class);
                startActivity(i);
                break;
            case R.id.ft:
                i= new Intent(this, Focus_Timer.class);
                startActivity(i);
                break;
        }
        return true;
    }
}


