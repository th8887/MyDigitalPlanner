package com.example.mydigitalplanner;

import static com.example.mydigitalplanner.FBref.reAuth;
import static com.example.mydigitalplanner.FBref.storageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera_or_Gallery extends AppCompatActivity {

    TextView title;
    /**
     * Button that switches between open camera and open gallery
     */
    Button btnS;

    ImageView showPic;
    /**
     * @param- s= status.
     */
    boolean s;
    /**
     * for the type of the OnActivityResult
     * 1=gallery
     * 2=camera
     */
    int i,count=0;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;

    Uri photoUri;
    final int CAMERA_REQUEST = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_or_gallery);

        title=(TextView) findViewById(R.id.title);
        btnS=(Button) findViewById(R.id.btnS);
        showPic=(ImageView) findViewById(R.id.showPic);

        s=true;


        switchToGallery();
    }

    private void switchToGallery() {
        SpannableString ss = new SpannableString("From Gallery");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                btnS.setText("Camera");
                s = false;
                SwitchToCamera();
            }
        };
        ss.setSpan(span, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(ss);
        title.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void SwitchToCamera() {
        SpannableString ss = new SpannableString("From Camera");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                btnS.setText("Gallery");
                s = true;
                switchToGallery();
            }
        };
        ss.setSpan(span, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(ss);
        title.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * true- gallery
     * false- camera
     * @param view
     */
    public void openA(View view) {
        if(s){
            i=1;
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(
                            intent,
                            "Select Image from here..."),
                    PICK_IMAGE_REQUEST);

        }
        else{
            i=2;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (true) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, CAMERA_REQUEST);
                }
            }

        }
    }

    /**
     * creating the image file and returning it.
     * @return
     * @throws IOException
     */
        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            photoUri = Uri.fromFile(image);
            return image;
        }


    @Override
    protected void onActivityResult(int requestCode, int resultcode, Intent data) {
        super.onActivityResult(requestCode, resultcode, data);
        switch (i){
            case 1:if (requestCode == PICK_IMAGE_REQUEST
                    && resultcode == RESULT_OK
                    && data != null
                    && data.getData() != null) {
                filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    getContentResolver(),
                                    filePath);
                    showPic.setImageBitmap(bitmap);
                } catch (IOException e) {
                    // Log the exception
                    e.printStackTrace();
                }
            }
            break;

            case 2:
                if (resultcode == RESULT_OK && requestCode == CAMERA_REQUEST) {
                    showPic.setImageURI(photoUri);
                }
            break;
        }
    }

    /**
     * uploading an imagefrom :
     * 1- gallery
     * 2- camera
     * @param view
     */

    public void upload(View view) {
        switch (i){
            case 1:
                if (filePath != null) {
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    StorageReference ref = storageReference.child("images/users/" +reAuth.getCurrentUser().getUid()+"/image-"+count);
                    count++;
                    ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(
                                UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(Camera_or_Gallery.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Camera_or_Gallery.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(
                                UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int)progress + "%");
                        }
                    });
                }

                break;
            case 2:
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                UploadTask uploadTask = storageReference.child("images/users/" + reAuth.getCurrentUser().getUid()+"/image-"+count).putFile(photoUri);
                //StorageReference ref = mStorageRef.child("images/users/" + auth.getCurrentUser().getUid()+"-"+Gallery.count);
                count++;
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(Camera_or_Gallery.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Camera_or_Gallery.this, "Failed to upload.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int)progress + "%");
                    }
                });

                break;
        }
    }
}