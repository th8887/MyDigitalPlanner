package com.example.mydigitalplanner;

import static com.example.mydigitalplanner.FBref.reAuth;
import static com.example.mydigitalplanner.FBref.refDB;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    /**
     * tv_title- for the title of the activity.
     * tvin- TextView for sign in or log in in the bottom.
     */
    TextView tv_title, tvin;
    /**
     * short names for editTexts:
     * n=name
     * p=phone
     * pa=password
     * e=email
     */
    EditText n, p, e, pa;
    /**
     * ls- log in or register.
     */
    Button ls;

    CheckBox cBstayconnect;

    boolean register, stayConnect;
    String name, phone, password, email, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_title = (TextView) findViewById(R.id.tv_title);
        p = (EditText) findViewById(R.id.phone);
        n = (EditText) findViewById(R.id.name);
        ls = (Button) findViewById(R.id.ls);
        tvin = (TextView) findViewById(R.id.tvin);
        e = (EditText) findViewById(R.id.email);
        pa = (EditText) findViewById(R.id.password);
        cBstayconnect = (CheckBox) findViewById(R.id.cBstayconnect);

        stayConnect = false;
        register = true;

        regOption();
    }

    /**
     * On activity start- checking if user already logged in.
     */
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        Boolean isChecked = settings.getBoolean("stayConnect", false);
        Intent si = new Intent(MainActivity.this, userInfo.class);
        if (reAuth.getCurrentUser() != null && isChecked) {
            stayConnect = true;
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

    /**
     * Switches from log in option to registration option if the user is new in the app.
     */
    public void regOption() {
        SpannableString ss = new SpannableString("Don't have an account?  Register here.");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                tv_title.setText("Register");
                n.setVisibility(View.VISIBLE);
                p.setVisibility(View.VISIBLE);
                ls.setText("Register");
                register = false;
                logOption();
            }
        };
        ss.setSpan(span, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvin.setText(ss);
        tvin.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Switches back to log option from registration in case the user wants to log in.
     */
    private void logOption() {
        SpannableString ss = new SpannableString("Already have an account? login here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                tv_title.setText("Login");
                n.setVisibility(View.INVISIBLE);
                p.setVisibility(View.INVISIBLE);
                ls.setText("Log In");
                register = true;
                regOption();
            }
        };
        ss.setSpan(span, 26, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvin.setText(ss);
        tvin.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void logorreg(View view) {
        if (register) {
            email=e.getText().toString();
            password=pa.getText().toString();

            final ProgressDialog pd=ProgressDialog.show(this,"Login","Connecting...",true);
            reAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                                SharedPreferences.Editor editor=settings.edit();
                                editor.putBoolean("stayConnect",cBstayconnect.isChecked());
                                editor.commit();
                                Log.d("MainActivity", "signinUserWithEmail:success");
                                Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                Intent si = new Intent(MainActivity.this,LogInOk.class);
                                si.putExtra("newuser",false);
                                startActivity(si);
                            } else {
                                Log.d("MainActivity", "signinUserWithEmail:fail");
                                Toast.makeText(MainActivity.this, "e-mail or password are wrong!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            name=n.getText().toString();
            phone=p.getText().toString();
            email=e.getText().toString();
            password=pa.getText().toString();

            final ProgressDialog pd=ProgressDialog.show(this,"Register","Registering...",true);
            reAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                                SharedPreferences.Editor editor=settings.edit();
                                editor.putBoolean("stayConnect",cBstayconnect.isChecked());
                                editor.commit();
                                Log.d("MainActivity", "createUserWithEmail:success");
                                FirebaseUser user = reAuth.getCurrentUser();
                                uid = user.getUid();
                                User u=new User(name,email, phone,uid,true);
                                refDB.child(uid).setValue(u);
                                Toast.makeText(MainActivity.this, "Successful registration", Toast.LENGTH_SHORT).show();
                                Intent si = new Intent(MainActivity.this,LogInOk.class);
                                si.putExtra("newuser",true);
                                startActivity(si);
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                    Toast.makeText(MainActivity.this, "User with e-mail already exist!", Toast.LENGTH_SHORT).show();
                                else {
                                    Log.w("MainActivity", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "User create failed.",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }
    }
}
