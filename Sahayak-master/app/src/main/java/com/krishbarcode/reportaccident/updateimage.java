package com.krishbarcode.reportaccident;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class updateimage extends AppCompatActivity implements View.OnClickListener{


    ImageView imageView;
    Button but,next;
    private Uri filePath;
    private StorageReference storageReference;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseStorage storage;
    Bitmap bit;
    private final int RESULT_CROPadh = 400;

    String vehno;  FirebaseFirestore firestore;

    private static final int PICK_IMAGE_REQUEST = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateimage);
        but = (Button) findViewById(R.id.but);

        vehno = getIntent().getStringExtra("vehno");
        firestore = FirebaseFirestore.getInstance();
        imageView = (ImageView) findViewById(R.id.profileimage);
        next = (Button)findViewById(R.id.next);
        storage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(vehno);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        firestore = FirebaseFirestore.getInstance();

        Log.v("veh",vehno+"saveprofileimage");
        next.setOnClickListener(this);
        but.setOnClickListener(this);
        next.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

    }
    private void performCropahdar(String picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROPadh);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Log.v("filepath",filePath.toString());


            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath1 = cursor.getString(columnIndex);
            cursor.close();
            performCropahdar(picturePath1);

        }
        if (requestCode == RESULT_CROPadh ) {
            if(resultCode == Activity.RESULT_OK){
                //Toast.makeText(this, "request result", Toast.LENGTH_SHORT).show();
                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");
                // Set The Bitmap Data To ImageView
                bit = getCroppedBitmap(selectedBitmap);
                imageView.setImageBitmap(bit);
                //        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                //button1.setText("Adhar card Selected");

                // Toast.makeText(this, "uploadfile call hua", Toast.LENGTH_SHORT).show();
                // Toast.makeText(this, ""+file1, Toast.LENGTH_SHORT).show();
                uploadFile();
                // Toast.makeText(this, "upload file call o gaya", Toast.LENGTH_SHORT).show();;



            }
        }
    }
    @Override
    public void onClick(View view) {
        if (view == but) {
            but.setVisibility(View.GONE);
            showFileChooser();

        } else if (view == next) {
           onBackPressed();
        }
    }
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }



    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {

        if (filePath != null) {
            Toast.makeText(this, ""+filePath, Toast.LENGTH_SHORT).show();
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            storageReference = storage.getReference();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


            //getting the storage reference
            StorageReference sRef = storageReference.child(vehno +"/profile" + "." + getFileExtension(filePath));
            // String stoname = Constants.STORAGE_PATH_UPLOADS +firebaseUser.getUid();
            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog

                            progressDialog.dismiss();

                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            imageView.setVisibility(View.VISIBLE);
                            next.setVisibility(View.VISIBLE);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            @SuppressWarnings("VisibleForTests")double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploading " + ((int) progress) + "%...");
                        }
                    });
        } else {

        }
}

    public void next(View view) {
    }
}
