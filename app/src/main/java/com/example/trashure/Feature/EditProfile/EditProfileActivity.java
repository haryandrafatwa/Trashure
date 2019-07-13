package com.example.trashure.Feature.EditProfile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trashure.Feature.Akun.AkunFragment;
import com.example.trashure.MainActivity;
import com.example.trashure.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView civProfileImage;
    private AkunFragment akunFragment;
    private EditText et_nama,et_phonenumber,et_email,et_bod;
    private Toolbar toolbar;
    private Button btnSimpan;
    private ImageButton btnBack;
    private int counterSimpan = 0;
    private String currentUID;
    private FirebaseAuth mAuth;
    private DatabaseReference userRefs;
    private ProgressDialog mDialog;
    private Calendar mCalendar;
    private TextView tv_chooseImage;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private int GALLERY = 1, CAMERA = 2;
    FirebaseStorage storage;
    StorageReference storageReference,loadPict;

    private void setToolbar() {
        setSupportActionBar(toolbar);
        setTitle("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_akun);
        initialize();
        setToolbar();
    }

    public void initialize(){
        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();
        loadPict = FirebaseStorage.getInstance().getReference().child("DisplayPictures").child("Users");
        civProfileImage = (CircleImageView) findViewById(R.id.civ_display_picture);
        et_nama = (EditText) findViewById(R.id.et_nama);
        et_phonenumber = (EditText) findViewById(R.id.et_hp);
        et_email = (EditText) findViewById(R.id.et_email);
        btnSimpan = (Button) findViewById(R.id.btn_simpan);
        tv_chooseImage = (TextView) findViewById(R.id.tv_choose_image);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        toolbar = (Toolbar) findViewById(R.id.toolbar_edit_akun);
        et_bod = (EditText) findViewById(R.id.et_tanggal);
        mDialog = new ProgressDialog(this);
        akunFragment = new AkunFragment();
        mCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR,year);
                mCalendar.set(Calendar.MONTH,month);
                mCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                datepickerPop();
            }
        };

        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(currentUID);
        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    String nama = dataSnapshot.child("nama").getValue().toString();
                    String phonenumber = dataSnapshot.child("phonenumber").getValue().toString();
                    String tgllahir = dataSnapshot.child("bod").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();

                    if (dataSnapshot.hasChild("displaypicture"))
                    {
                        Picasso.with(getApplicationContext()).load(dataSnapshot.child("displaypicture").getValue().toString()).into(civProfileImage);
                    } else {
                        Picasso.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/trashure-71595.appspot.com/o/DisplayPictures%2Fdummy%2FUserLogo.png?alt=media&token=0ca8fe79-4dac-46c7-8356-0df6ea65464b").into(civProfileImage);
                    }
                    Log.d("INI NAMA AWAL",et_nama.getText().toString());
                    et_nama.setText(nama);
                    et_bod.setText(tgllahir);
                    et_email.setText(email);
                    et_phonenumber.setText(phonenumber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tv_chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        et_bod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditProfileActivity.this,date,mCalendar.get(Calendar.YEAR),mCalendar.get(Calendar.MONTH),mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
                counterSimpan++;
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                civProfileImage.setImageBitmap(bitmap);
                uploadImage();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            Log.d("huohouhojuhou","DisplayPictures/Users/"+mAuth.getCurrentUser().getUid());
            storageReference = storage.getInstance().getReference().child("DisplayPictures").child("Users").child(mAuth.getCurrentUser().getUid()).child("DisplayPicture");
            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }else{
            Log.d("MKAOJOIDJOQIMNJE","File Path NULL");
        }
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    private void datepickerPop(){
        String myFormat = "dd - MMMM - yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_bod.setText(sdf.format(mCalendar.getTime()));
    }

    private void updateData(){
        final String email = et_email.getText().toString();
        final String nama = et_nama.getText().toString();
        final String telepohone = et_phonenumber.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(nama) || TextUtils.isEmpty(telepohone))
        {
            Toast.makeText(this, "Data harus diisi", Toast.LENGTH_SHORT).show();
        }else{
            mDialog.setTitle("Edit Akun");
            mDialog.setCancelable(true);
            mDialog.setMessage("Tunggu sebentar .. ");
            mDialog.show();

            mAuth.updateCurrentUser(mAuth.getCurrentUser()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        String currentUserID = mAuth.getCurrentUser().getUid();
                        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID);
                        HashMap userMap = new HashMap();
                        userMap.put("nama",nama);
                        userMap.put("phonenumber",telepohone);
                        userMap.put("email",email);
                        userMap.put("bod",et_bod.getText().toString());
                        loadPict.child(mAuth.getCurrentUser().getUid()).child("DisplayPicture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                userRefs.child("displaypicture").setValue(uri.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("DISPLAY PICTURE FAILED","OMG");
                            }
                        });
                        userRefs.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(EditProfileActivity.this, "Edit Berhasil", Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();
                                }
                                else
                                {
                                    Toast.makeText(EditProfileActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        //Display alert message when back button has been pressed
        if(counterSimpan>0){
            sentToMainActivity();
        }else{
            backButtonHandler();
        }
        return;
    }

    public void backButtonHandler() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                EditProfileActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Konfirmasi Kembali");
        // Setting Dialog Message
        alertDialog.setMessage("Apakah anda yakin ingin kembali?");
        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.dialog_icon);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Iya",
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent mainIntent = new Intent(EditProfileActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Tidak",
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private void sentToMainActivity()
    {
        Intent mainIntent = new Intent(EditProfileActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
