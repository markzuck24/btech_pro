package com.krishbarcode.reportaccident;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddContacts extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs";
    private static final int CONTACT1 = 1;
    private static final int CONTACT2 = 2;
    private static final int CONTACT3 = 3;
    TextView contactNumber1, ContactName1;
    TextView contactNumber2, ContactName2;
    TextView contactNumber3, ContactName3;
    Button button1, button2, button3;
    boolean flag1 = true, flag2 = true, flag3 = true;
    String con1 = "", con2 = "", con3 = "", name1, name2, name3, number1, number2, number3, n1, n2, n3;
    FirebaseFirestore firestore;
    String vehno;
    private String USERID = "";
    private FirebaseUser firebaseUser;

    Button b1,b2;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        b1 = (Button)findViewById(R.id.submit);

        vehno = getIntent().getStringExtra("vehno");
        // Toast.makeText(this, "aa gaya"+vehno, Toast.LENGTH_SHORT).show();
        firestore = FirebaseFirestore.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        USERID = firebaseUser.getUid();

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        ContactName1 = (TextView) findViewById(R.id.contactname1);
        ContactName2 = (TextView) findViewById(R.id.contactname2);
        ContactName3 = (TextView) findViewById(R.id.contactname3);

        contactNumber1 = (TextView) findViewById(R.id.contactnumber1);
        contactNumber2 = (TextView) findViewById(R.id.contactnumber2);
        contactNumber3 = (TextView) findViewById(R.id.contactnumber3);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT1 && flag1) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                Cursor cursor = managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();


                name1 = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                number1 = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                n1 = number(number1);
                button1.setVisibility(View.GONE);
                ContactName1.setText(name1);
               count++;
                contactNumber1.setText(n1);
                flag1 = false;

            }
        } else if (requestCode == CONTACT2 && flag2) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                Cursor cursor = managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();


                name2 = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                number2 = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                n2 = number(number2);
                button2.setVisibility(View.GONE);
                ContactName2.setText(name2);
                count++;
                contactNumber2.setText(n2);
                flag2 = false;
            }
        } else if (requestCode == CONTACT3 && flag3) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                Cursor cursor = managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();


                name3 = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                number3 = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                n3 = number(number3);

                button3.setVisibility(View.GONE);
                ContactName3.setText(name3);
                count++;
                contactNumber3.setText(n3);
                flag3 = false;

            }
        }
    }

    public void done(View view) {
        if(count==3){
        con1 = name1 + n1;
        con2 = name2 + n2;
        con3 = name3 + n3;
        Map<String, Object> contact = new HashMap<>();
        contact.put("contact1", con1);
        contact.put("contact2", con2);
        contact.put("contact3", con3);

        firestore.collection(vehno + "contacts").add(contact).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(AddContacts.this, "Contacts uploaded", Toast.LENGTH_SHORT).show();

                Log.v("tag", "Document is added with id - " + documentReference.getId());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("tag", "error occured profile" + e);
            }
        });
        Intent i = new Intent(this, SetProfileImage.class);
        i.putExtra("vehno", vehno);
        Log.v("veh", vehno);
        startActivity(i);


    }
    else {
            Toast.makeText(this, "Select all three Contacts", Toast.LENGTH_SHORT).show();
        }
    }

    public void button1(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        startActivityForResult(intent, 1);
    }

    public void button2(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        startActivityForResult(intent, 2);
    }

    public void button3(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        startActivityForResult(intent, 3);
    }

    public String number(String n) {
        String n1;
        int i;
        char nn[] = n.toCharArray();
        char n2[] = new char[20];
        int j = 9;
        for (i = nn.length - 1; i > 0 && j >= 0; i--) {
            if (nn[i] != ' ') {
                n2[j--] = nn[i];
            }

        }
        n1 = String.valueOf(n2);

        return n1;
    }

    public void done1(View view) {
        con1 = name1 + n1;
        con2 = name2 + n2;
        con3 = name3 + n3;
        Map<String, Object> contact = new HashMap<>();
        contact.put("contact1", con1);
        contact.put("contact2", con2);
        contact.put("contact3", con3);

        firestore.collection(vehno + "contacts").add(contact).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(AddContacts.this, "Contacts uploaded", Toast.LENGTH_SHORT).show();

                Log.v("tag", "Document is added with id - " + documentReference.getId());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("tag", "error occured profile" + e);
            }
        });

    }
}
