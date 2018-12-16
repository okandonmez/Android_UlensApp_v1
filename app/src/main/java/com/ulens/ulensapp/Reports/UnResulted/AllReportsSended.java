package com.ulens.ulensapp.Reports.UnResulted;

import android.annotation.SuppressLint;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ulens.ulensapp.HomePage;
import com.ulens.ulensapp.NonUserThings.LoginActivity;
import com.ulens.ulensapp.R;
import com.ulens.ulensapp.Reports.ReportSubmittedInvoices;
import com.ulens.ulensapp.Reports.ReportUnSubmittedInvoices;
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

public class AllReportsSended extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    final String getSubmittedReportsUrl = "http://ulensapp.com/api/Bill/GetReports";
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
        setContentView(R.layout.activity_all_reports_sended);

        ////////////////////////////////////////////////

        toolbar = findViewById(R.id.toolbar4);
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

        btnNewReport = findViewById(R.id.btnNewReport);
        btnNewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postReport();
            }
        });

        sharedPreferences= getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        txtNoReport = findViewById(R.id.txtNoInvoice4);

        token=sharedPreferences.getString("token","");

        lv=findViewById(R.id.lvSubmitted);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==0){
                    startActivity(new Intent(getApplicationContext(), AllReportsNotSended.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    overridePendingTransition(0,0);
                    AllReportsSended.this.finish();
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

    @SuppressLint("RestrictedApi")
    public void postReport(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText editText = new EditText(getApplicationContext());
        editText.setText("Yeni Rapor");
        alert.setMessage("Oluşturacağınız raporlar, fişleri gruplandırmanıza olanak sağlar.");
        alert.setTitle("Yeni Rapor Ekle");

        editText.requestFocus();
        final InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        alert.setView(editText,70,0,80,0);


        alert.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                imm.toggleSoftInputFromWindow(editText.getWindowToken(), 0,0);
                if (editText.getText().toString().length()==0)
                    Toast.makeText(getApplicationContext(),"Rapor adı giriniz.",Toast.LENGTH_SHORT).show();
                else
                {
                    postReport(editText.getText().toString());
                }

            }
        }).setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
            }
        });

        alert.show();
        editText.selectAll();
    }


    public void postReport(final String reportName){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, postReportUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("HasError").equals("false"))
                            {
                                //Toast.makeText(getApplicationContext(),"Rapor Ekleme başarılı, Gönderilmemiş Raporlar menüsünden faturalarınızı ekleyebilirsiniz.",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(),ReportUnSubmittedInvoices.class);
                                intent.putExtra("Id",jsonObject.getString("returnId"));
                                intent.putExtra("Name","deneme");
                                intent.putExtra("TotalAmount","0");
                                startActivity(intent);


                            }
                            else{

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","Bearer"+" "+token);

                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Name",reportName);
                params.put("Description","");

                return params;
            }
        };


        VolleyClass.getInstance(AllReportsSended.this).addToRequestQueue(stringRequest);

    }

    public void getReports(final Context context){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getSubmittedReportsUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        // Log.e("reports",response);

                        Weather weather_data[];


                        try {
                            final JSONArray jsonArray = new JSONArray(response);
                            List<String> array= new ArrayList<>();

                            if (jsonArray.length()>0){

                                lv.setVisibility(View.VISIBLE);

                                int k = 0;
                                for (int  i = 0 ; i < jsonArray.length() ; i++){
                                   // array.add("Fatura Id :" + jsonArray.getJSONObject(i).getString("Id"));
                                    String name = jsonArray.getJSONObject(i).getString("Name");
                                    String totalAmount = "₺"+jsonArray.getJSONObject(i).getString("TotalAmount");

                                    if (jsonArray.getJSONObject(i).getString("IsConfirmed").contains("null") &&
                                            jsonArray.getJSONObject(i).getString("IsVisible").contains("false")){
                                        Log.e("reports",String.valueOf(jsonArray.getJSONObject(i)));
                                        continue;
                                    }

                                    k++;

                                }
                                weather_data = new Weather[k];
                                Log.e("K",String.valueOf(k));
                                k = 0;
                                for (int  i = 0 ; i < jsonArray.length() ; i++){
                                    // array.add("Fatura Id :" + jsonArray.getJSONObject(i).getString("Id"));
                                    String name = jsonArray.getJSONObject(i).getString("Name");
                                    String totalAmount = "₺"+jsonArray.getJSONObject(i).getString("TotalAmount");
                                    String reportDate = jsonArray.getJSONObject(i).getString("CreatedDate");
                                    String[] seperated = reportDate.split("T");
                                    reportDate = seperated[0];

                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Log.e("içerde",String.valueOf(jsonObject+"\n"));

                                    if (jsonObject.getString("IsConfirmed").contains("null") &&
                                            jsonObject.getString("IsVisible").contains("true")){
                                            weather_data[k] = new Weather(R.drawable.iconreport,name,totalAmount,reportDate);
                                            k++;
                                    }

                                    else if (jsonObject.getString("IsVisible").contains("true") &&
                                            jsonObject.getString("IsConfirmed").contains("true")){
                                        weather_data[k] = new Weather(R.drawable.iconconfirmedreports,name,totalAmount,reportDate);
                                        k++;
                                    }
                                    else if (jsonObject.getString("IsVisible").contains("false") &&
                                            jsonObject.getString("IsConfirmed").contains("false")){
                                        weather_data[k] = new Weather(R.drawable.iconrejectedreports,name,totalAmount,reportDate);
                                        k++;
                                    }
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
                                        Intent intent = new Intent(getApplicationContext(),ReportSubmittedInvoices.class);
                                        intent.putExtra("Id",jsonObject.getString("Id"));
                                        intent.putExtra("Name",jsonObject.getString("Name"));
                                        intent.putExtra("TotalAmount",jsonObject.getString("TotalAmount"));

                                        Log.e("Deneme",jsonObject.getString("Id"));


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

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

        VolleyClass.getInstance(AllReportsSended.this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),HomePage.class));
        AllReportsSended.this.finish();
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

        }

//        else if (id == R.id.resultedReports){
//            startActivity(new Intent(getApplicationContext(), ResultedReportsConfirmed.class));
//            AllReportsSended.this.finish();
//        }

        else if (id==R.id.support){
            startActivity(new Intent(getApplicationContext(),SupportActivity.class));
            AllReportsSended.this.finish();
        }
//        else if (id==R.id.settings){
//
//        }
        else if (id == R.id.logout){
            logOut();
        }
        else if (id == R.id.unreportedInvoices){
            startActivity(new Intent(getApplicationContext(),HomePage.class));
            AllReportsSended.this.finish();
        }


        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return  true;
    }

    public void logOut(){

        builder = new AlertDialog.Builder(AllReportsSended.this);

        builder.setTitle("Çıkış");
        builder.setMessage("Çıkmak istediğine emin misin ?");
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearPrefs();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                AllReportsSended.this.finish();

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
