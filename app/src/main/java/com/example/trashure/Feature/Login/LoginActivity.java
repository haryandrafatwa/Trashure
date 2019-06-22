package com.example.trashure.Feature.Login;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.trashure.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressDialog mDialog;
    Button btnMasuk,btnFacebook,btnGoogle;
    EditText etEmail,etPassword;
    CheckBox cbIngatSaya;
    TextView tvLupaPassword,tvDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        btnFacebook = (Button) findViewById(R.id.btn_facebook);
        btnMasuk = (Button) findViewById(R.id.btn_masuk);
        btnGoogle = (Button) findViewById(R.id.btn_google);
        etEmail = (EditText) findViewById(R.id.et_email_login);
        etPassword = (EditText) findViewById(R.id.et_pass_login);
        cbIngatSaya = (CheckBox) findViewById(R.id.cb_ingat_saya);
        tvDaftar = (TextView) findViewById(R.id.tv_daftar);
        tvLupaPassword = (TextView) findViewById(R.id.tv_lupa_password);

    }

}
