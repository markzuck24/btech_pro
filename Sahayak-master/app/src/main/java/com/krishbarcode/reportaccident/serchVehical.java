package com.krishbarcode.reportaccident;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.Calendar;

public class serchVehical extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    String vehno;
    TextView name,con1,con2,con3;
    String conn1,conn2,conn3;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    ImageView imageView;
    StorageReference proimageref, storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch_vehical);
        vehno = getIntent().getStringExtra("vehno");

        name = (TextView)findViewById(R.id.name);
        con1 = (TextView)findViewById(R.id.con1);
        con2 = (TextView)findViewById(R.id.con2);
        con3 = (TextView)findViewById(R.id.con3);
        imageView = (ImageView)findViewById(R.id.i1);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        String dwnldurl = vehno + "/profile.jpg";
        Log.v("dwnldurl", dwnldurl);
        proimageref = storageReference.child(dwnldurl);

        Glide.with(this).using(new FirebaseImageLoader()).load(proimageref).into(imageView);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching data....");
        progressDialog.show();
        progressDialog.setCancelable(true);

        firestore.collection(vehno+"Profile")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("data", "=>" + document.getData() + document.get("name") + document.get("veh") + document.get("email"));
                                name.setText("Name :    "+document.get("name").toString());
                                progressDialog.dismiss();
                            }

                        } else {
                            Log.e("data", "error occured user main" + task.getException());
                        }

                    }
                });
        firestore.collection(vehno+"contacts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("data", "=>" + document.getData() + document.get("contact1") + document.get("contact2") + document.get("contact3"));
                                con1.setText("1. "+document.get("contact1"));
                                Log.v("tag",conn1 +"  "+conn2+"  "+conn3);

                                conn1 = ""+document.get("contact1");
                                Log.v("tag",conn1 +"  "+conn2+"  "+conn3);

                                con2.setText("2. "+document.get("contact2"));
                                conn2 = document.get("contact2").toString().trim();
                                con3.setText("3. "+document.get("contact3"));
                                Log.v("tag",conn1 +"  "+conn2+"  "+conn3);

                                conn3 = ""+document.get("contact3");

                            }

                        } else {
                            Log.e("data", "error occured user main" + task.getException());
                        }

                    }
                });




    }

    public void notify(View v) {
        int i = conn1.length();
        conn1 = conn1.substring(i - 10, i);
        int j = conn2.length();
        conn2 = conn2.substring(j - 10, j);
        int k = conn3.length();
        conn3 = conn3.substring(k - 10, k);

        Log.v("tag",conn1 +"  "+conn2+"  "+conn3);

        int permissinCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS);

        if (permissinCheck == PackageManager.PERMISSION_GRANTED) {

            MyMessage(conn1, "https://goo.gl/idSgox");
            MyMessage(conn2, "https://goo.gl/idSgox");
            MyMessage(conn3, "https://goo.gl/idSgox");

        } else {
            //TODO
            ActivityCompat.requestPermissions(this, new String[]
                            {
                                    android.Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);

        }

    }
    private void MyMessage(String phone, String smstext) {
        SmsManager smsManager = SmsManager.getDefault();
        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        smsManager.sendTextMessage(phone, null, "The vehical no "+vehno+" has met with an accident on "+mydate+"\nLocation : https://goo.gl/idSgox", null, null);
        Toast.makeText(this, "Message sent successfully to " + phone, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String dtandtime = DateFormat.getDateTimeInstance().toString().trim();
                    String msg="The "+vehno+" has met with an accident on "+dtandtime+".\n"+"Location : https://goo.gl/idSgox";
                    //Name of Method for Calling Message
                    MyMessage(conn1, msg);
                    MyMessage(conn2, msg);
                    MyMessage(conn3, msg);
                } else {
                    Toast.makeText(this, "You dont have required permission to make the Action", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

}
