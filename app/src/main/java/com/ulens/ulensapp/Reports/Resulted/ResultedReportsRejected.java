package com.ulens.ulensapp.Reports.Resulted;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ulens.ulensapp.HomePage;
import com.ulens.ulensapp.NonUserThings.LoginActivity;
import com.ulens.ulensapp.R;
import com.ulens.ulensapp.Reports.UnResulted.AllReportsNotSended;
import com.ulens.ulensapp.SupportActivity;
import com.ulens.ulensapp.SupportClasses.VolleyClass;
import com.ulens.ulensapp.SupportClasses.Weather;
import com.ulens.ulensapp.SupportClasses.WeatherAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class ResultedReportsRejected extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    final String getSubmittedReportsUrl = "http://ulensapp.com/api/Bill/GetReports?isVisible=false&isConfirmed=false";

    String postReportUrl="http://ulensapp.com/Api/Bill/PostReport";

    ListView lv;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String token;

    Button btnNewReport;

    Context context = this;
    TextView txtNoReport;

    Toolbar toolbar;

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resulted_reports_rejected);

        ////////////////////////////////////////////////

        toolbar = findViewById(R.id.toolbar15);
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


        //////////////////////////////////


        sharedPreferences= getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        txtNoReport = findViewById(R.id.txtNoReports);

        token=sharedPreferences.getString("token","");

        lv=findViewById(R.id.lvReports);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==0){
                    startActivity(new Intent(getApplicationContext(), ResultedReportsConfirmed.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    overridePendingTransition(0,0);
                    ResultedReportsRejected.this.finish();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setDefaultTab(1,tabLayout);
        getReports(context);

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


    public void getReports(final Context context){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getSubmittedReportsUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

                        Weather weather_data[];


                        try {
                            final JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length()>0){
                                lv.setVisibility(View.VISIBLE);

                                weather_data = new Weather[jsonArray.length()];
                                for (int  i = 0 ; i < jsonArray.length() ; i++){
                                    String name = jsonArray.getJSONObject(i).getString("Name");
                                    String totalAmount = "₺"+jsonArray.getJSONObject(i).getString("TotalAmount");

                                    weather_data[i] = new Weather(R.drawable.iconreport,name,totalAmount,"2018-02-02");
                                }
                                WeatherAdapter adapter = new WeatherAdapter(context, R.layout.listview_item_row2, weather_data);
                                lv.setAdapter(adapter);



                            }
                            else{
                                txtNoReport.setVisibility(View.VISIBLE);
                            }

                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                                    try {
//                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                        Intent intent = new Intent(getApplicationContext(),ReportSubmittedInvoices.class);
//                                        intent.putExtra("Id",jsonObject.getString("Id"));
//                                        intent.putExtra("Name",jsonObject.getString("Name"));
//                                        intent.putExtra("TotalAmount",jsonObject.getString("TotalAmount"));
//
//                                        //startActivity(intent);
//                                        //ResultedReportsRejected.this.finish();
//
//
//
//
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }

                                    String rejectingReason = "Göndermiş olduğunuz rapor şu nedenden dolayı reddedilmiştir : \n";
                                    try {
                                        rejectingReason = rejectingReason + jsonArray.getJSONObject(i).getString("Comment");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    android.app.AlertDialog.Builder builder;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        builder = new android.app.AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog);
                                    } else {
                                        builder = new android.app.AlertDialog.Builder(context);
                                    }
                                    builder.setTitle("Rapor Reddedilme Nedeni")
                                            .setMessage(rejectingReason)
                                            .setPositiveButton("Geri Dön", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .setIcon(R.drawable.iconerror)
                                            .show();

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","Bearer"+" "+token);

                return params;
            }
        };

        VolleyClass.getInstance(ResultedReportsRejected.this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),HomePage.class));
        ResultedReportsRejected.this.finish();
        super.onBackPressed();
    }

    public void setDefaultTab(int position, TabLayout tabLayout){
        TabLayout.Tab defaultTab = tabLayout.getTabAt(position);
        defaultTab.select();
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
            startActivity(new Intent(getApplicationContext(), AllReportsNotSended.class));
            ResultedReportsRejected.this.finish();        }

//        else if (id == R.id.resultedReports){
//            startActivity(new Intent(getApplicationContext(), ResultedReportsConfirmed.class));
//            ResultedReportsRejected.this.finish();
//        }
        else if (id==R.id.support){
            startActivity(new Intent(getApplicationContext(),SupportActivity.class));
            ResultedReportsRejected.this.finish();
        }
//        else if (id==R.id.settings){
//
//        }
        else if (id == R.id.logout){
            logOut();
        }
        else if (id == R.id.unreportedInvoices){
            startActivity(new Intent(getApplicationContext(),HomePage.class));
            ResultedReportsRejected.this.finish();
        }


        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return  true;
    }

    public void logOut(){

        builder = new AlertDialog.Builder(ResultedReportsRejected.this);

        builder.setTitle("Çıkış");
        builder.setMessage("Çıkmak istediğine emin misin ?");
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearPrefs();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                ResultedReportsRejected.this.finish();

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
