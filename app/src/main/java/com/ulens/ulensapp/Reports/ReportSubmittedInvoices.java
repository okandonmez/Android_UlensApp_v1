package com.ulens.ulensapp.Reports;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ulens.ulensapp.R;
import com.ulens.ulensapp.EditInvoices.ReportedSendedInvoiceDetails;
import com.ulens.ulensapp.Reports.UnResulted.AllReportsSended;
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

public class ReportSubmittedInvoices extends AppCompatActivity {

    String name;
    String totalAmount;
    String id;
    String token;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Context context = this;

    String getInvoicesUrl = "http://ulensapp.com/api/Bill/GetInvoices?reportId=";
    String getReportComment = "http://ulensapp.com/api/Bill/GetReport?reportId=";


    ListView lvInvoices;


    Toolbar toolbar;

    TextView txtTotalAmount;
    Button btnBack;

    Button btnNotif;
    String reason;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitted_invoices);

        getIntents();
        btnNotif = findViewById(R.id.btnNotification);

        btnBack = findViewById(R.id.btnBackSI);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AllReportsSended.class));
                ReportSubmittedInvoices.this.finish();
            }
        });

        lvInvoices = findViewById(R.id.lvInvoices);
        toolbar = findViewById(R.id.toolbarSubmittedReportsInvoices);

        txtTotalAmount = findViewById(R.id.txtTotalSbmitted);
        setSupportActionBar(toolbar);

        getInvoices();
        getReportReason();

        getSupportActionBar().setTitle(name);
        txtTotalAmount.setText("Toplam Tutar: "+totalAmount+"₺");

        btnNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showReason();
            }
        });

    }

    private void showReason() {
        Toast.makeText(getApplicationContext(),reason,Toast.LENGTH_LONG).show();
    }

    public void getIntents(){

        id = getIntent().getStringExtra("Id");
        name = getIntent().getStringExtra("Name");
        Log.e("NAME",name);
        totalAmount = getIntent().getStringExtra("TotalAmount");


        sharedPreferences= getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        token=sharedPreferences.getString("token","");


    }


    public void getInvoices(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getInvoicesUrl + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response",response);

                        Weather weather_data[];

                        try {
                            final JSONArray jsonArray = new JSONArray(response);
                            List<String> array= new ArrayList<>();

                            if (jsonArray.length()>0){
                                lvInvoices.setVisibility(View.VISIBLE);
                                weather_data = new Weather[jsonArray.length()];
                                for (int  i = 0 ; i < jsonArray.length() ; i++){
                                    array.add("Fatura Id :" + jsonArray.getJSONObject(i).getString("Id"));
                                    weather_data[i] = new Weather(R.drawable.iconreport,jsonArray.getJSONObject(i).getString("Name"),"₺"+jsonArray.getJSONObject(i).getString("Amount"),"2018-01-01");

                                }


                                WeatherAdapter adapter = new WeatherAdapter(context, R.layout.listview_item_row2, weather_data);

                                lvInvoices.setAdapter(adapter);

                                lvInvoices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        Intent intent = new Intent(getApplicationContext(),ReportedSendedInvoiceDetails.class);
                                        try {
                                            intent.putExtra("invoiceId",jsonArray.getJSONObject(i).getString("Id")) ;
                                            intent.putExtra("isSubmitted",true);
                                            intent.putExtra("reportId",id);
                                            intent.putExtra("reportName", name);
                                            startActivity(intent);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            }







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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","Bearer"+" "+token);

                return params;
            }
        };

        VolleyClass.getInstance(ReportSubmittedInvoices.this).addToRequestQueue(stringRequest);
    }

    public void getReportReason(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getReportComment + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("Deneme",response);
                            JSONObject jsonObject = new JSONObject(response);
                            reason = jsonObject.getString("Comment");
                            if (reason.length() != 0)
                                btnNotif.setVisibility(View.VISIBLE);

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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","Bearer"+" "+token);

                return params;
            }
        };

        VolleyClass.getInstance(ReportSubmittedInvoices.this).addToRequestQueue(stringRequest);
    }



}
