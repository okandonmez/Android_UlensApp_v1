package com.ulens.ulensapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ulens.ulensapp.NonUserThings.LoginActivity;
import com.ulens.ulensapp.Reports.UnResulted.AllReportsNotSended;

public class SupportActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    EditText edtSuppot;
    Button btnSupport;
    Toolbar toolbar;

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.navigationview);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        TextView txtName = hView.findViewById(R.id.txtHeaderName);
        TextView txtBrand = hView.findViewById(R.id.txtBrandName);
        setHeader(txtName,txtBrand);

        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        edtSuppot = findViewById(R.id.edtSupport);
        btnSupport = findViewById(R.id.btnSupport);
        btnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                String[] to = {"support@ulensapp.com"};
                intent.putExtra(Intent.EXTRA_EMAIL,to);
                intent.putExtra(Intent.EXTRA_SUBJECT,"Support to UlensApp");
                intent.putExtra(Intent.EXTRA_TEXT,edtSuppot.getText());
                intent.setType("message/rfc822");
                Intent chooser = Intent.createChooser(intent, "Send Email");
                startActivity(chooser);
            }
        });
    }

    public void setHeader (TextView txtName, TextView txtBrand){
        String nameLastName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("nameLastName","username");
        String brandName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("brandName","şirket adı");

        if (nameLastName.compareTo("null") == 0){
            txtName.setText("Ad Soyad");
        }
        else{
            txtName.setText(nameLastName);
        }

        if (brandName.compareTo("null") == 0){
            txtBrand.setText("Şirket bilgisi bulunamadı");
            txtBrand.setTextSize(12);
        }
        else {
            txtBrand.setText(brandName);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.myReports){
            startActivity(new Intent(getApplicationContext(),AllReportsNotSended.class));
            SupportActivity.this.finish();
        }
//        else if (id == R.id.resultedReports){
//            startActivity(new Intent(getApplicationContext(), ResultedReportsConfirmed.class));
//            SupportActivity.this.finish();
//        }
        else if (id==R.id.support){

        }
//        else if (id==R.id.settings){
//
//        }
        else if (id == R.id.logout){
            logOut();
        }
        else if (id == R.id.unreportedInvoices){
            startActivity(new Intent(getApplicationContext(),HomePage.class));
            SupportActivity.this.finish();
        }


        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return  true;
    }

    @Override
    public void onBackPressed() {
        SupportActivity.this.finish();
        super.onBackPressed();
    }

    public void logOut(){

        builder = new AlertDialog.Builder(SupportActivity.this);

        builder.setTitle("Çıkış");
        builder.setMessage("Çıkmak istediğine emin misin ?");
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearPrefs();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                SupportActivity.this.finish();

            }
        }).setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void clearPrefs(){
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().clear().commit();

    }
}
