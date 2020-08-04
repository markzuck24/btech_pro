package com.krishbarcode.reportaccident;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Profile extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs";
    EditText fname;
    EditText lname;
    EditText address;
    EditText veh_no;
    EditText con_no;
    EditText adharno;
    String name;
    Button save, verify;
    FirebaseFirestore firestore;
    String frstname, lstname, ae, veh, con, adhar;
    String globalvehno;
    String ema;
    SharedPreferences sharedpreferences;
    private String USERID = "";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firestore = FirebaseFirestore.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        USERID = firebaseUser.getUid();


        firebaseAuth = FirebaseAuth.getInstance();
        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        address = (EditText) findViewById(R.id.age);

        veh_no = (EditText) findViewById(R.id.veh_no);

        con_no = (EditText) findViewById(R.id.contact_no);
        adharno = (EditText) findViewById(R.id.adhar_no);
        save = (Button) findViewById(R.id.save);

        ema = firebaseUser.getEmail();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("user", "user ka save button");
                frstname = fname.getText().toString().trim();
                lstname = lname.getText().toString().trim();
                name = frstname + " " + lstname;
                ae = address.getText().toString().trim();
                veh = veh_no.getText().toString().trim();
                Log.v("veh", veh);
                con = con_no.getText().toString().trim();
                adhar = adharno.getText().toString().trim();
                if (validateForm()) {


                    Log.v("tag", "\n" + name + "\n" + ae + "\n" + veh + "\n" + con + "\n" + adhar + "\n");


                    Map<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("address", ae);
                    user.put("veh", veh);
                    user.put("con", con);
                    user.put("adhar", adhar);
                    user.put("email", ema);
                    user.put("userid", USERID);

                    Toast.makeText(Profile.this, "start", Toast.LENGTH_SHORT).show();
                    firestore.collection(veh+"Profile").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            Log.v("tag", "Document is added with id - " + documentReference.getId());


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.v("tag", "error occured profile" + e);
                        }
                    });

                    firestore.collection(firebaseUser.getUid()+"Profile").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            Log.v("tag", "Document is added with id - " + documentReference.getId());


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.v("tag", "error occured profile" + e);
                        }
                    });
                   // savevehno();

                    Intent i = new Intent(Profile.this,AddContacts.class);
                    i.putExtra("vehno", veh);
                    Log.v("veh", veh);
                    startActivity(i);

                }


            }
        });


    }

    private void savevehno() {
        Map<String, Object> veh1 = new HashMap<>();
        veh1.put("vehno", veh);
        veh1.put("userid", firebaseUser.getUid().toString());
        Toast.makeText(this, "" + veh_no + firebaseUser.getUid(), Toast.LENGTH_SHORT).show();
        firestore.collection(firebaseUser.getUid() + "vehno").add(veh1).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Log.v("veh", "vehno stored");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("veh", "error occured profile" + e);

            }
        });

    }




    public boolean validateForm() {
        boolean alldone = true;
        String frstname = fname.getText().toString().trim();
        String lstname = lname.getText().toString().trim();
        String ae = address.getText().toString().trim();

        if (TextUtils.isEmpty(frstname)) {
            fname.setError("Enter your first name");
            return false;
        } else {
            alldone = true;
            fname.setError(null);
        }
        if (TextUtils.isEmpty(lstname)) {
            lname.setError("Enter your last name");
            return false;
        }
        if (TextUtils.isEmpty(ae)) {
            address.setError("Enter your Address");
            return false;
        } else {
            alldone = true;
            address.setError(null);
        }

        if(con.length()!=10)
        {
            con_no.setError("Invalid Contact Number");
            android.widget.Toast.makeText(this, "Invalid Contact Number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(adhar.length()!=12)
        {
            adharno.setError("Invalid Aadhaar Number");
            android.widget.Toast.makeText(this, "Invalid Aadhaar Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(Pattern.matches("^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$", veh))
        {
            alldone = true;
            veh_no.setError(null);
        }
        else
        {
            veh_no.setError("Invalid Vehicle Number");
            android.widget.Toast.makeText(this, "Invalid Vehicle Number", Toast.LENGTH_SHORT).show();
            return false;
        }




        return alldone;
    }



}
