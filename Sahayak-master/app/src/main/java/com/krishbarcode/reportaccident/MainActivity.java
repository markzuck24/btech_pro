package com.krishbarcode.reportaccident;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    static String takevehno;
    String email;
    String pass;
    EditText ed1, ed2;
    ProgressDialog probar;
    FirebaseFirestore firestore;
    String vehno;
    int flag = 0;
    private FirebaseAuth firebaseauth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        firestore = FirebaseFirestore.getInstance();
        //Log.v("load",firebaseUser.getUid().toString()+"vehno");
        firebaseauth = FirebaseAuth.getInstance();

        if (firebaseauth.getCurrentUser() != null && firebaseUser.isEmailVerified()==true) {
            Log.v("load", "intent usermain");

            Log.v("try", "start of fetching data");


            Intent i = new Intent(this, Homeuser.class);
            i.putExtra("takevehno", takevehno);
            startActivity(i);


        }
//vehno = getIntent().getStringExtra("vehno");
        probar = new ProgressDialog(this);

        ed1 = (EditText) findViewById(R.id.email1);
        ed2 = (EditText) findViewById(R.id.pass1);

    }

    public void signup(View v) {


        Log.v("user", "creating user id");
        email = ed1.getText().toString().trim();
        pass = ed2.getText().toString().trim();
        if (verification()) {
            probar.setMessage("Registering user......");
            probar.show();
           // Toast.makeText(this, "" + email + pass, Toast.LENGTH_SHORT).show();

            firebaseauth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    probar.dismiss();
                    if (task.isSuccessful()) {


                        Log.v("user", "user id ban gaya");
                        Toast.makeText(MainActivity.this, "Creating Login ID...", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), Profile.class);
                        i.putExtra("EMAIL", email);
                        startActivity(i);

                    } else {
                        Toast.makeText(MainActivity.this, "Could not registered.....  Please try again  ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    public void signin(View v) {
        Log.v("user", "getting logged in with id and pass");
        email = ed1.getText().toString().trim();
        pass = ed2.getText().toString().trim();

        if (verification()) {

            probar.setMessage("Getting you logged in...");
            probar.show();
            firebaseauth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    probar.dismiss();
                    if (task.isSuccessful()) {
                        finish();
                        firebaseUser = firebaseauth.getCurrentUser();
                        if (firebaseUser.isEmailVerified() == false) {
                            Toast.makeText(MainActivity.this, "Email is not Verified", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, EmailVerification.class));

                        } else {

                            Log.v("user", "sign in successfully");
                            Intent i = new Intent(getApplicationContext(), Homeuser.class);
                            i.putExtra("EMAIL", email);
                            i.putExtra("vehno", vehno);
                            startActivity(i);
                            Toast.makeText(MainActivity.this, "Getting you Logged in....", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(MainActivity.this, "Not able to login ..Retry", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public boolean verification() {

        Log.v("user", "verification of id and pass");
        if (TextUtils.isEmpty(email)) {
            ed1.setError("Enter email");
            return false;
        }

        if (TextUtils.isEmpty(pass)) {
            ed2.setError("Enter pass");
            return false;
        }
        if (pass.length() < 6) {
            ed2.setError("Password should be at least 6 characters");

            return false;
        }
        return true;

    }


}
