package com.example.mydigitalplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    /**
     * tv_title- for the title of the activity.
     * tvin- TextView for sign in or log in in the bottom.
     */
    TextView tv_title,tvin;
    EditText name, phone,email,password;
    /**
     *ls- log in or register.
     */
    Button ls;

    FirebaseAuth fAuth;

    boolean register,stayConnect;
    String sName, sPhone, sPassword, sEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_title=(TextView) findViewById(R.id.tv_title);
        phone=(EditText) findViewById(R.id.phone);
        name=(EditText) findViewById(R.id.name);
        ls=(Button) findViewById(R.id.ls);
        tvin=(TextView) findViewById(R.id.tvin);
        email=(EditText) findViewById(R.id.email);
        password=(EditText) findViewById(R.id.password);

        fAuth= FirebaseAuth.getInstance();

        logOption();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences settings= getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        Boolean isChecked= settings.getBoolean("stayConnect", false);
        Intent si= new Intent (MainActivity.this, userInfo.class);
        if (fAuth.getCurrentUser()!= null&& isChecked){
            stayConnect=true;
            startActivity(si);
        }
    }

    /**
     * On activity Pause- If logged in & asked to be remembered- kill activity.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (stayConnect) finish();
    }

    public void regOption() {
        SpannableString ss = new SpannableString("Don't have an account?  Register here.");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                tv_title.setText("Register");
                name.setVisibility(View.VISIBLE);
                phone.setVisibility(View.VISIBLE);
                ls.setText("Register");
                register= false;
                logOption();
            }
        };
        ss.setSpan(span, 24,38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvin.setText(ss);
        tvin.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void logOption() {
        SpannableString ss = new SpannableString("Already have an account? login here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                tv_title.setText("Register");
                name.setVisibility(View.INVISIBLE);
                phone.setVisibility(View.INVISIBLE);
                ls.setText("Log In");
                register= true;
                regOption();
            }
        };
        ss.setSpan(span, 24,38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvin.setText(ss);
        tvin.setMovementMethod(LinkMovementMethod.getInstance());
    }


    public void enter(View view) {
        sName=name.getText().toString();
        sEmail=email.getText().toString();
        sPassword=password.getText().toString();
        sPhone=phone.getText().toString();

        final ProgressDialog pd= ProgressDialog.show(this, "Register", "Registering...",true);
        
    }
}