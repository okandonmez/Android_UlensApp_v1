package com.ulens.ulensapp.Reports;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import com.ulens.ulensapp.EditInvoices.ReportedInvoiceDetails;
import com.ulens.ulensapp.HomePage;
import com.ulens.ulensapp.InvoiceChoose;
import com.ulens.ulensapp.R;
import com.ulens.ulensapp.Reports.UnResulted.AllReportsNotSended;
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

public class ReportUnSubmittedInvoices extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String getInvoicesUrl = "http://ulensapp.com/api/Bill/GetInvoices?reportId=";
    String sendReportUrl = "http://ulensapp.com/api/Bill/SendReport?reportId=";
    String getReportDetailUrl = "http://ulensapp.com/api/Bill/GetReport?reportId=";

    String token;
    String id;
    String name;
    String totalAmount;

    TextView txtTotalAmount;
    TextView txtNoInvoice;

    Toolbar toolbar;

    ListView lvInvoices;
    Context context = this;

    Button btnSendReport;
    Button btnInvoiceToReport;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_invoices);

        getIntentFields();

        lvInvoices = findViewById(R.id.lvInvoicesReport);

        btnBack = findViewById(R.id.btnBackUSI);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AllReportsNotSended.class));
                ReportUnSubmittedInvoices.this.finish();
            }
        });

        btnSendReport = findViewById(R.id.btnSendReport);
        btnInvoiceToReport = findViewById(R.id.btnInvoiceToReport);

        toolbar = findViewById(R.id.toolbarReportInvoices);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setTitle(name);

        txtTotalAmount = findViewById(R.id.txtTotalAmount);
       // txtTotalAmount.setText("Toplam Tutar : " + totalAmount+"₺");
        txtNoInvoice = findViewById(R.id.txtNoInvoice);


        getInvoices();
        getReportDetail();


        btnSendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReportAlert();
            }
        });

        btnInvoiceToReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),InvoiceChoose.class);
                intent.putExtra("Id",id);
                intent.putExtra("Name",name);
                intent.putExtra("TotalAmount",totalAmount);
                startActivity(intent);
                ReportUnSubmittedInvoices.this.finish();


            }
        });


        if (totalAmount.compareTo("0.0")==0){
            btnSendReport.setVisibility(View.INVISIBLE);
        }
        else{
            btnSendReport.setVisibility(View.VISIBLE);
        }


    }

    public void getReportDetail(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getReportDetailUrl+id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("getReportDetailRESPONSE",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            totalAmount = jsonObject.getString("TotalAmount");
                            if (totalAmount.compareTo("0.0")!=0)
                                btnSendReport.setVisibility(View.VISIBLE);
                            else
                                btnSendReport.setVisibility(View.INVISIBLE);

                            Log.e("reporDetail",jsonObject.getString("TotalAmount"));

                            txtTotalAmount.setText("Toplam Tutar : " + jsonObject.getString("TotalAmount")+"₺");
                            name = jsonObject.getString("Name");
                            getSupportActionBar().setTitle(name);

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

        VolleyClass.getInstance(ReportUnSubmittedInvoices.this).addToRequestQueue(stringRequest);

    }

    public void sendReportAlert(){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("Raporu Onaya Gönder")
                .setMessage("Rapor gönderilecektir. Onaylıyor musunuz ?")
                .setPositiveButton("Gönder", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendReport();
                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.drawable.iconerror)
                .show();
    }

    public void sendReport(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, sendReportUrl + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("responseSendReport",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("HasError").compareTo("false")==0){
                                startActivity(new Intent(getApplicationContext(),HomePage.class));
                                Toast.makeText(getApplicationContext(),"Rapor Gönderme Başarılı",Toast.LENGTH_LONG).show();
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

        VolleyClass.getInstance(ReportUnSubmittedInvoices.this).addToRequestQueue(stringRequest);


    }





    public void getIntentFields(){

        id = getIntent().getStringExtra("Id");
        name = getIntent().getStringExtra("Name");
        totalAmount = getIntent().getStringExtra("TotalAmount");


        sharedPreferences= getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        token=sharedPreferences.getString("token","");
       // Toast.makeText(getApplicationContext(),id + " " + token,Toast.LENGTH_SHORT).show();

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
                                       Intent intent = new Intent(getApplicationContext(),ReportedInvoiceDetails.class);
                                        try {
                                            intent.putExtra("invoiceId",jsonArray.getJSONObject(i).getString("Id")) ;
                                            intent.putExtra("reportId",id);
                                            Log.e("REPORTID",id);
                                            startActivity(intent);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });


                            }
                            else
                            {
                                txtNoInvoice.setVisibility(View.VISIBLE);
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

        VolleyClass.getInstance(ReportUnSubmittedInvoices.this).addToRequestQueue(stringRequest);


    }

}
