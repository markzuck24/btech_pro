package com.krishbarcode.reportaccident;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Homeuser extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String MyPREFERENCES = "MyPrefs";
    TextView name, navname, veh, navemail, navvehno;
    ImageView proimage, navimage;
    StorageReference proimageref, storageReference;
    FirebaseStorage storage;
    FirebaseUser firebaseUser;
    View header;
    DatabaseReference mDatabase;
    FirebaseFirestore firestore;
    String vehno1;
    boolean flag = false;
    private FirebaseAuth firebaseAuth;
    Button b;
    EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeuser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        b = (Button)findViewById(R.id.next);
        edit = (EditText)findViewById(R.id.vehno);
        navimage = (ImageView) header.findViewById(R.id.navproimage);
        navname = (TextView) header.findViewById(R.id.navname);
        navvehno = (TextView) header.findViewById(R.id.navvehno);
        navemail = (TextView) header.findViewById(R.id.navemail);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = storage.getReference();

        firestore = FirebaseFirestore.getInstance();
        //Log.v("load",vehno1+"prodata");
        firestore.collection(firebaseUser.getUid() + "Profile")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {

                                Log.v("load", "adding data to component");
                                Log.d("data", "=>" + document.getData() + document.get("name") + document.get("veh") + document.get("email"));

                                navname.setText(document.get("name").toString());
                                navvehno.setText(document.get("veh").toString());
                                vehno1 = document.get("veh").toString();
                                flag = true;

                                navemail.setText(document.get("email").toString());
                            }

                        } else {

                            Log.v("load", "error");
                            Log.e("data", "error occured user main" + task.getException());
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.v("load", "data not found");
                // Toast.makeText(UserMainActivity.this, "document not found", Toast.LENGTH_SHORT).show();
            }
        });

        if (flag) {
            setimage();
            // Toast.makeText(this, ""+vehno1, Toast.LENGTH_SHORT).show();

            // Glide.with(this).using(new FirebaseImageLoader()).load(proimageref).into(navimage);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


    }


    void setimage()
    {String dwnldurl = "uploads/" + vehno1 + ".jpg";
        Log.v("dwnldurl", dwnldurl);
        proimageref = storageReference.child(dwnldurl);}

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
//            int pid = android.os.Process.myPid();
//            android.os.Process.killProcess(pid);
           //finish();
            //System.exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homeuser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Viewprofile) {
            String dwnldurl = "uploads/" + vehno1 + ".jpg";
            Log.v("dwnldurl", dwnldurl);
            proimageref = storageReference.child(dwnldurl);
          //  Toast.makeText(this, "" + vehno1, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, ViewProfile.class);
            i.putExtra("vehno", vehno1);
            startActivity(i);

            // Handle the camera action
        } else if (id == R.id.logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, MainActivity.class));


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.addcon) {
            String dwnldurl = "uploads/" + vehno1 + ".jpg";
            Log.v("dwnldurl", dwnldurl);
            proimageref = storageReference.child(dwnldurl);
            setimage();
          //  Toast.makeText(this, "" + vehno1, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, updatecontact.class);
            i.putExtra("vehno", vehno1);
            startActivity(i);

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public  void  search (View view)
    {
        String dwnldurl = "uploads/" + vehno1 + ".jpg";
        Log.v("dwnldurl", dwnldurl);
        proimageref = storageReference.child(dwnldurl);
        String as = edit.getText().toString().trim();
        edit.setText("");
     //   Toast.makeText(this, "" + vehno1, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, serchVehical.class);
        i.putExtra("vehno", as);
        startActivity(i);

    }

}
