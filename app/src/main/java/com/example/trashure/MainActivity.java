package com.example.trashure;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.trashure.Feature.Akun.AkunFragment;
import com.example.trashure.Feature.Beranda.BerandaFragment;
import com.example.trashure.Feature.Harga.HargaFragment;
import com.example.trashure.Feature.Penukaran.PenukaranFragment;
import com.example.trashure.Feature.Scan.ScanFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavBar);
        final AkunFragment akunFragment = new AkunFragment();
        final PenukaranFragment penukaranFragment = new PenukaranFragment();
        final ScanFragment scanFragment = new ScanFragment();
        final BerandaFragment berandaFragment = new BerandaFragment();
        final HargaFragment hargaFragment = new HargaFragment();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {

                int id = menuItem.getItemId();
                if (id == R.id.menuAkun) {
                    setFragment(akunFragment);
                    return true;
                } else if (id == R.id.menuBeranda) {
                    setFragment(berandaFragment);
                    return true;
                } else if (id == R.id.menuHarga) {
                    setFragment(hargaFragment);
                    return true;
                } else if (id == R.id.menuPenukaran) {
                    setFragment(penukaranFragment);
                    return true;
                } else if (id == R.id.menuScan) {
                    setFragment(scanFragment);
                    return true;
                }
                return  false;
            }

        });
        bottomNavigationView.setSelectedItemId(R.id.menuBeranda);

    }
    public void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment);
        fragmentTransaction.commit();
    }
}
