package com.example.trashure.Feature.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trashure.Feature.Register.RegisterActivity;
import com.example.trashure.MainActivity;
import com.example.trashure.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressDialog mDialog;
    Button btnMasuk,btnFacebook,btnGoogle;
    EditText etEmail,etPassword;
    CheckBox cbIngatSaya;
    TextView tvLupaPassword,tvDaftar;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

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

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!etEmail.getText().toString().matches(emailPattern))
                {
                    etEmail.setTextColor(getResources().getColor(R.color.red));
                }
                else{
                    etEmail.setTextColor(getResources().getColor(R.color.colorInputText));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        tvDaftar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sentToRegisterActivity();
            }
        });
        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithEmailandPassword();
            }
        });
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginWithFacebook();
            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginWithGoogle();
            }
        });

    }



    private void sendUserToLoginActivity()
    {
        Intent homeIntent = new Intent(LoginActivity.this,LoginActivity.class);
        startActivity(homeIntent);
        finish();
    }

    private void LoginWithGoogle()
    {

    }

    private void LoginWithFacebook()
    {
    }

    private void loginWithEmailandPassword()
    {

        String email = etEmail.getText().toString();
        String pass = etPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pass))
        {
            Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mDialog.setMessage("tunggu sebentar...");
            mDialog.setCancelable(false);
            mDialog.setTitle("Login");
            mDialog.show();

            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        sentToMainActivity();
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                }
            });
        }
    }

    private void sentToRegisterActivity()
    {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }

    private void sentToMainActivity()
    {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            sentToMainActivity();
        }
    }


}
