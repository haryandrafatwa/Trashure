package com.example.trashure.Feature.Register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trashure.Feature.Login.LoginActivity;
import com.example.trashure.MainActivity;
import com.example.trashure.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText etNama,etNoTelephone,etEmail,etPassword;
    FirebaseAuth mAuth;
    TextView tvMasuk;
    Button btnGoogle,btnFB,btnDaftar;
    DatabaseReference userRefs;
    ProgressDialog mDialog;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();


    }
    public void initialize()
    {
        etEmail = (EditText) findViewById(R.id.et_email_register);
        etNama = (EditText) findViewById(R.id.et_nama_register);
        etNoTelephone = (EditText) findViewById(R.id.et_nomorhp_register);
        etPassword = (EditText) findViewById(R.id.et_pass_register);
        btnDaftar = (Button) findViewById(R.id.btn_daftar);
        btnFB = (Button) findViewById(R.id.btn_facebook_register);
        btnGoogle = (Button) findViewById(R.id.btn_google_register);
        tvMasuk = (TextView) findViewById(R.id.tv_masuk);
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar_register);
        setToolbar();

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerWithEmailandPassword();
            }
        });

        tvMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerWithGoogle();
            }
        });

        btnFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerWithFB();
            }
        });

    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void registerWithFB()
    {

    }

    private void registerWithGoogle()
    {

    }

    private void registerWithEmailandPassword()
    {

        final String email = etEmail.getText().toString();
        final String nama = etNama.getText().toString();
        final String telepohone = etNoTelephone.getText().toString();
        String password = etPassword.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(nama) || TextUtils.isEmpty(telepohone) || TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Data harus diisi", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mDialog.setTitle("Daftar");
            mDialog.setCancelable(true);
            mDialog.setMessage("Tunggu sebentar .. ");
            mDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        String currentUserID = mAuth.getCurrentUser().getUid();
                        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID);
                        HashMap userMap = new HashMap();
                        userMap.put("nama",nama);
                        userMap.put("phonenumber",telepohone);
                        userMap.put("jumlahsampah",0);
                        userMap.put("saldo",0);
                        userMap.put("level","Noob");
                        userMap.put("email",email);
                        userMap.put("bod","none");

                        userRefs.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(RegisterActivity.this, "Daftar Berhasil", Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();
                                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(mainIntent);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(RegisterActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();
                                }
                            }
                        });

                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                }
            });
        }
    }


}
