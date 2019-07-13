package com.example.trashure.Feature.Akun;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.trashure.Feature.Login.LoginActivity;
import com.example.trashure.MainActivity;
import com.example.trashure.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class AkunFragment extends Fragment {

    FirebaseAuth mAuth;
    DatabaseReference userRefs;
    String currentUserId;
    CircleImageView civProfileImage;
    Button btnKeluar,btnEdit;
    TextView tvLevel,tvSaldo,tvTotalSampah,tvNomorHp,tvEmail,tvTglLahir,tvNama;
    private static final String TAG = "Upload";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_akun, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        eventFragmentAkun();
    }

    private void eventFragmentAkun() {
        initialize();
    }

    private void initialize() {
        btnKeluar = (Button) getActivity().findViewById(R.id.btn_keluar);
        tvLevel = (TextView) getActivity().findViewById(R.id.tv_level_profile);
        tvSaldo = (TextView) getActivity().findViewById(R.id.tv_saldo_profile);
        tvTotalSampah = (TextView) getActivity().findViewById(R.id.tv_totalsampah_profile);
        tvTglLahir = (TextView) getActivity().findViewById(R.id.tv_tgl_lahir_profile);
        tvNama = (TextView) getActivity().findViewById(R.id.tv_profile_name);
        tvEmail = (TextView) getActivity().findViewById(R.id.tv_email_profile);
        tvNomorHp = (TextView) getActivity().findViewById(R.id.tv_nohandphone_profile);
        civProfileImage = (CircleImageView) getActivity().findViewById(R.id.civ_profile_image);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserId);

        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    String nama = dataSnapshot.child("nama").getValue().toString();
                    String level = dataSnapshot.child("level").getValue().toString();
                    String saldo = dataSnapshot.child("saldo").getValue().toString();
                    String totalsampah = dataSnapshot.child("jumlahsampah").getValue().toString();
                    String phonenumber = dataSnapshot.child("phonenumber").getValue().toString();
                    String tgllahir = dataSnapshot.child("bod").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();

                    if (dataSnapshot.hasChild("profileimage"))
                    {

                    }

                    tvNama.setText(nama);
                    tvLevel.setText(level);
                    tvSaldo.setText("Rp. "+saldo);
                    tvTglLahir.setText(tgllahir);
                    tvEmail.setText(email);
                    tvNomorHp.setText(phonenumber);
                    tvTotalSampah.setText(totalsampah + "Kg");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*mAuth.signOut();
                Intent loginIntent = new Intent(getContext(),LoginActivity.class);
                startActivity(loginIntent);
                getActivity().finish();*/
                alertsignout();
            }
        });

    }

    public void alertsignout()
    {
        AlertDialog.Builder alertDialog2 = new
                AlertDialog.Builder(
                getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle("Konfirmasi Keluar");

        // Setting Dialog Message
        alertDialog2.setMessage("Apakah anda yakin ingin keluar?");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("Iya",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        FirebaseAuth.getInstance().signOut();
                        Intent i = new Intent(getActivity(),
                                LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                });

        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        /*Toast.makeText(getContext(),
                                "You clicked on NO", Toast.LENGTH_SHORT)
                                .show();*/
                        dialog.cancel();
                    }
                });

        // Showing Alert Dialog
        alertDialog2.show();

    }

}
