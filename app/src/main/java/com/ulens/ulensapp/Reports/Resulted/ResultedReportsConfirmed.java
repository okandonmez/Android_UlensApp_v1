package com.ulens.ulensapp.Reports.Resulted;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.ulens.ulensapp.HomePage;
import com.ulens.ulensapp.NonUserThings.LoginActivity;
import com.ulens.ulensapp.R;
import com.ulens.ulensapp.Reports.ReportUnSubmittedInvoices;
import com.ulens.ulensapp.Reports.UnResulted.AllReportsNotSended;
import com.ulens.ulensapp.SupportActivity;
import com.ulens.ulensapp.SupportClasses.VolleyClass;
import com.ulens.ulensapp.SupportClasses.Weather;
import com.ulens.ulensapp.SupportClasses.WeatherAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultedReportsConfirmed extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final String confirmedReportsUrl = "http://ulensapp.com/api/Bill/GetReports?isVisible=true&isConfirmed=true";

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;

    AlertDialog.Builder builder;

    SwipeMenuListView lv;
    String token;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    TextView txtNoReport;

    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resulted_reports_confirmed);

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        token=sharedPreferences.getString("token","");

        txtNoReport = findViewById(R.id.txtNoReports);

        lv=findViewById(R.id.lvReports);





        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar15);
        setSupportActionBar(toolbar);

        ///////////////////////////////////////////////////////////////////////


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

        ///////////////////////////////////////////////////////////////////////


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1){
                    startActivity(new Intent(getApplicationContext(), ResultedReportsRejected.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    overridePendingTransition(0,0);
                    ResultedReportsConfirmed.this.finish();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        getReports(context);


    }


    public void getReports(final Context context){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, confirmedReportsUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response confirmed ", response);
                        Weather weather_data[];


                        try {
                            final JSONArray jsonArray = new JSONArray(response);
                            List<String> array= new ArrayList<>();

                            if (jsonArray.length()>0){
                                lv.setVisibility(View.VISIBLE);



                                weather_data = new Weather[jsonArray.length()];


                                for (int  i = 0 ; i < jsonArray.length() ; i++){
                                    // array.add("Fatura Id :" + jsonArray.getJSONObject(i).getString("Id"));
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

                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        Intent intent = new Intent(getApplicationContext(),ReportUnSubmittedInvoices.class);
                                        intent.putExtra("Id",jsonObject.getString("Id"));
                                        intent.putExtra("Name",jsonObject.getString("Name"));
                                        intent.putExtra("TotalAmount",jsonObject.getString("TotalAmount"));

                                      //  startActivity(intent);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                            lv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = (JSONObject) jsonArray.get(index);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    switch (index) {
                                        case 0:
                                            Log.e("deneme","sil basıldı");
//                                            try {
//                                                //deleteReportAlert(jsonObject.getString("Id"));
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
                                            break;
                                        case 1:
                                            // delete
                                            break;
                                    }
                                    // false : close the menu; true : not close the menu
                                    return false;
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

        VolleyClass.getInstance(ResultedReportsConfirmed.this).addToRequestQueue(stringRequest);
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
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),HomePage.class));
        ResultedReportsConfirmed.this.finish();
        super.onBackPressed();
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
            ResultedReportsConfirmed.this.finish();
        }
//        else if (id == R.id.resultedReports){
//
//        }
        else if (id==R.id.support){
            startActivity(new Intent(getApplicationContext(),SupportActivity.class));
            ResultedReportsConfirmed.this.finish();
        }
//        else if (id==R.id.settings){
//
//        }
        else if (id == R.id.logout){
            logOut();
        }
        else if (id == R.id.unreportedInvoices){
            startActivity(new Intent(getApplicationContext(),HomePage.class));
            ResultedReportsConfirmed.this.finish();
        }


        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return  true;
    }

    public void logOut(){

        builder = new AlertDialog.Builder(ResultedReportsConfirmed.this);

        builder.setTitle("Çıkış");
        builder.setMessage("Çıkmak istediğine emin misin ?");
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearPrefs();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                ResultedReportsConfirmed.this.finish();

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


