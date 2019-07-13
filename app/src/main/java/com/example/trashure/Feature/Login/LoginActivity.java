package com.example.trashure.Feature.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trashure.Feature.Register.RegisterActivity;
import com.example.trashure.MainActivity;
import com.example.trashure.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity{

    private Boolean userStat;
    private LoginButton btnFacebook;
    ImageView btnFacebookView;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    ProgressDialog mDialog;
    private DatabaseReference userRefs;
    private GoogleSignInClient mGoogleSignInClient;
    Button btnMasuk,btnGoogle;
    EditText etEmail,etPassword;
    CheckBox cbIngatSaya;
    TextView tvLupaPassword,tvDaftar;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static final int RC_SIGN_IN = 927;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        mCallbackManager = CallbackManager.Factory.create();
        btnFacebook.setReadPermissions("email","public_profile");
        btnFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Facebook:onSuccess: "+loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Login Batal", Toast.LENGTH_SHORT).show();
                sendUserToLoginActivity();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,error.toString());
                sendUserToLoginActivity();
            }
        });
    }

    private void initialize() {

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        btnFacebook = (LoginButton) findViewById(R.id.login_button);
        btnMasuk = (Button) findViewById(R.id.btn_masuk);
        btnGoogle = (Button) findViewById(R.id.btn_google);
        etEmail = (EditText) findViewById(R.id.et_email_login);
        etPassword = (EditText) findViewById(R.id.et_pass_login);
        cbIngatSaya = (CheckBox) findViewById(R.id.cb_ingat_saya);
        tvDaftar = (TextView) findViewById(R.id.tv_daftar);
        tvLupaPassword = (TextView) findViewById(R.id.tv_lupa_password);
        btnFacebookView = (ImageView) findViewById(R.id.btn_facebook);

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
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginWithGoogle();
            }
        });
        btnFacebookView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFacebook.performClick();
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
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Toast.makeText(LoginActivity.this, "Tunggu sebentar ..", Toast.LENGTH_SHORT).show();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Toast.makeText(LoginActivity.this, "Tunggu sebentar ..", Toast.LENGTH_SHORT).show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            userRefs = FirebaseDatabase.getInstance().getReference().child("User");
                            userRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    userStat = new Boolean(true);
                                    for (DataSnapshot dsp:dataSnapshot.getChildren()){
                                        if (user.getUid().equalsIgnoreCase(dsp.getKey())){
                                            userStat = true;
                                            Log.d("CHECKING","ADA");
                                            break;
                                        }else{
                                            userStat = false;
                                            Log.d("CHECKING","GADA");
                                        }
                                    }
                                    if (userStat){
                                        sentToMainActivity();
                                    }else{
                                        HashMap userMap = new HashMap();
                                        userMap.put("nama",user.getDisplayName());
                                        String phoneNumber = user.getPhoneNumber();
                                        if(phoneNumber == null){
                                            userMap.put("phonenumber","-");
                                        }else{
                                            userMap.put("phonenumber",user.getPhoneNumber());
                                        }
                                        userMap.put("jumlahsampah",0);
                                        userMap.put("saldo",0);
                                        userMap.put("level","-");
                                        userMap.put("email",user.getEmail());
                                        userMap.put("bod","-");
                                        userMap.put("displaypicture",user.getPhotoUrl().toString());
                                        userRefs.child(user.getUid()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                                                    mDialog.dismiss();
                                                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(mainIntent);
                                                    finish();
                                                }
                                                else
                                                {
                                                    Toast.makeText(LoginActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    mDialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            sendUserToLoginActivity();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        Toast.makeText(this, "Tunggu sebentar ..", Toast.LENGTH_SHORT).show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            userRefs = FirebaseDatabase.getInstance().getReference().child("User");
                            userRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    userStat = new Boolean(true);
                                    for (DataSnapshot dsp:dataSnapshot.getChildren()){
                                        if (user.getUid().equalsIgnoreCase(dsp.getKey())){
                                            userStat = true;
                                            Log.d("CHECKING","ADA");
                                            break;
                                        }else{
                                            userStat = false;
                                            Log.d("CHECKING","GADA");
                                        }
                                    }
                                    if (userStat){
                                        sentToMainActivity();
                                    }else{
                                        HashMap userMap = new HashMap();
                                        userMap.put("nama",user.getDisplayName());
                                        String phoneNumber = user.getPhoneNumber();
                                        if(phoneNumber == null){
                                            userMap.put("phonenumber","-");
                                        }else{
                                            userMap.put("phonenumber",user.getPhoneNumber());
                                        }
                                        userMap.put("jumlahsampah",0);
                                        userMap.put("saldo",0);
                                        userMap.put("level","-");
                                        userMap.put("email",user.getEmail());
                                        userMap.put("bod","-");
                                        userMap.put("displaypicture",user.getPhotoUrl().toString());
                                        userRefs.child(user.getUid()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                                                    mDialog.dismiss();
                                                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(mainIntent);
                                                    finish();
                                                }
                                                else
                                                {
                                                    Toast.makeText(LoginActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    mDialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            sendUserToLoginActivity();
                        }
                    }
                });
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
                Toast.makeText(this, "Login batal", Toast.LENGTH_SHORT).show();
                sendUserToLoginActivity();
            }
        }
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            sentToMainActivity();
        } else{
            mGoogleSignInClient.revokeAccess();
            LoginManager.getInstance().logOut();
        }
    }


}
