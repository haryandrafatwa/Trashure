package com.example.trashure.Feature.Register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trashure.Feature.Login.LoginActivity;
import com.example.trashure.MainActivity;
import com.example.trashure.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Boolean userStat;
    private StorageReference dummyDispPict;
    private CallbackManager mCallbackManager;
    LoginButton btnFacebook;
    ImageView btnFacebookView;
    EditText etNama,etNoTelephone,etEmail,etPassword;
    FirebaseAuth mAuth;
    TextView tvMasuk;
    Button btnGoogle,btnDaftar;
    DatabaseReference userRefs;
    ProgressDialog mDialog;
    Toolbar toolbar;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 394;
    private static final String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();
        dummyDispPict = FirebaseStorage.getInstance().getReference("DisplayPictures/dummy").child("UserLogo.png");
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
                Log.d(TAG,"Facebook:onSuccess: "+loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(RegisterActivity.this, "Daftar Batal", Toast.LENGTH_SHORT).show();
                toRegister();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"error: "+error.toString());
                toRegister();
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
                                                    Toast.makeText(RegisterActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
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
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(RegisterActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            toRegister();
                        }
                    }
                });
    }

    public void initialize()
    {
        etEmail = (EditText) findViewById(R.id.et_email_register);
        etNama = (EditText) findViewById(R.id.et_nama_register);
        etNoTelephone = (EditText) findViewById(R.id.et_nomorhp_register);
        etPassword = (EditText) findViewById(R.id.et_pass_register);
        btnDaftar = (Button) findViewById(R.id.btn_daftar);
        btnFacebook = (LoginButton) findViewById(R.id.register_button);
        btnFacebookView = (ImageView) findViewById(R.id.btn_facebook_register);
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

        btnFacebookView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFacebook.performClick();
            }
        });

    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void registerWithGoogle()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Toast.makeText(RegisterActivity.this, "Tunggu sebentar ..", Toast.LENGTH_LONG).show();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    private void sentToMainActivity(){
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void toRegister(){
        Intent toRegister = new Intent(RegisterActivity.this,RegisterActivity.class);
        startActivity(toRegister);
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
                Log.w(TAG, "Google sign up failed", e);
                // ...
                Toast.makeText(this, "Daftar batal", Toast.LENGTH_SHORT).show();
                toRegister();
            }
        }

        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Toast.makeText(RegisterActivity.this, "Tunggu sebentar ..", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(RegisterActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
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
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(RegisterActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            toRegister();
                        }
                    }
                });
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
                        userMap.put("level","-");
                        userMap.put("email",email);
                        userMap.put("bod","-");
                        dummyDispPict.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                                    Toast.makeText(RegisterActivity.this, "Daftar Berhasil", Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();
                                    sentToMainActivity();
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
                        Toast.makeText(RegisterActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                }
            });
        }
    }



}
