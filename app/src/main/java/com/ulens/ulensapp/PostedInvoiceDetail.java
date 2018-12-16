package com.ulens.ulensapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.ulens.ulensapp.SupportClasses.VolleyClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostedInvoiceDetail extends AppCompatActivity {

    String token, Id;

    String name, date, category;
    String getDetailUrl = "http://www.ulensapp.com/Api/Bill/GetInvoice?invoiceId=";
    final String getInvoicePhotoUrl = "http://www.ulensapp.com/api/Bill/GetInvoicePhoto?invoiceId=";
    final String getReports = "http://ulensapp.com/api/Bill/GetReports";
    final String addInvoicesToReport= "http://ulensapp.com/Api/Bill/AddInvoicesToReport";

    ImageView imgPostedBill;

    Switch swBillablePosted;
    TextView txtPostedName,txtPostedDate,txtPostedCategory,txtPostedAmount,txtPostedTaxAmo,txtPaymentPosted;
    EditText edtDescriptionPosted;

    String []categories = {"Uçak Bileti","Eğlence","Etkinlik","Sabit Kıymet","Hediye","Yemek","Çeşitli","Ofis İhtiyaçları","Posta","Servisler","İletişim","Ulaşım"};

    ProgressBar pb;

    Button btnBack;

    Spinner spReports;

    Button btnAddToReport;

    int InvoiceId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posted_invoice_detail);

        spReports = findViewById(R.id.spReports);

        token = getIntent().getStringExtra("token");
        Id = getIntent().getStringExtra("Id");

        btnAddToReport = findViewById(R.id.btnAddToReport);

        txtPostedName = findViewById(R.id.txtNamePosted);
        txtPostedDate = findViewById(R.id.txtDate);
        txtPostedCategory = findViewById(R.id.txtCategoryPosted);
        txtPostedAmount = findViewById(R.id.txtAmountPosted);
        txtPostedTaxAmo = findViewById(R.id.txtTaxAmoPosted);
        txtPaymentPosted = findViewById(R.id.txtPaymentPosted);

        pb = findViewById(R.id.pb);

        swBillablePosted = findViewById(R.id.swBillablePosted);

        imgPostedBill = findViewById(R.id.imgPostedBill);

        edtDescriptionPosted = findViewById(R.id.edtDescriptionPosted);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        getInvoiceDetail(token);
        getInvoicePhoto(token,getInvoicePhotoUrl,Id);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pb.setVisibility(View.INVISIBLE);
            }
        },3000);

        reportName();

    }


    public void getInvoiceDetail(final String token){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getDetailUrl + Id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);

                            InvoiceId = json.getInt("Id");

                            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                            txtPostedName.setText(json.getString("Name"));

                            String[] parts = json.getString("Date").split("T");
                            txtPostedDate.setText(parts[0]);

                            txtPostedCategory.setText(categories[json.getInt("Category")]);

                            txtPostedAmount.setText("₺"+json.getString("Amount"));

                            txtPostedTaxAmo.setText(json.getString("KDVAmount"));






                            swBillablePosted.setEnabled(false);
                            if (json.get("Billable").equals("true")){
                                swBillablePosted.setChecked(true);
                            }

                            if (json.getString("Payment").equals("2"))
                             txtPaymentPosted.setText("Nakit");
                            else
                                txtPaymentPosted.setText("Kredi Kartı");

                            edtDescriptionPosted.setEnabled(false);
                            edtDescriptionPosted.setText(json.getString("Description"));








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
                params.put("Authorization","Bearer "+token);

                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };
        VolleyClass.getInstance(PostedInvoiceDetail.this).addToRequestQueue(stringRequest);


    }


    public void getInvoicePhoto (String token, String URL, String invoiceId){


        GlideUrl glideUrl = new GlideUrl(getInvoicePhotoUrl+invoiceId, new LazyHeaders.Builder()
                .addHeader("Authorization","Bearer"+" "+token)
                .build());

        Glide.with(this).load(glideUrl).into(imgPostedBill);



    }


    public void reportName(){

        final JSONArray[] jsonArray = new JSONArray[1];
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getReports,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("Respornse",response);
                            jsonArray[0] = new JSONArray(response);

                            ArrayList<String> reportNames = new ArrayList<>();
                            reportNames.add("Rapora Ekleyin");

                            for (int i = 0; i < jsonArray[0].length() ; i++ ){
                                Log.e("name", jsonArray[0].getJSONObject(i).getString("Name"));

                                if (jsonArray[0].getJSONObject(i).get("IsVisible").toString().equals("false")){
                                    Log.e("deneme", jsonArray[0].getJSONObject(i).get("IsVisible").toString());
                                    reportNames.add(jsonArray[0].getJSONObject(i).getString("Name"));
                                }


                            }


                            ArrayAdapter<String> adapter;
                            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, reportNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spReports.setAdapter(adapter);

                            btnAddToReport.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(spReports.getSelectedItemPosition() == 0 )
                                        onBackPressed();
                                    else
                                    {
                                        int pos = spReports.getSelectedItemPosition();
                                        pos--;
                                        try {
                                            int reportId = jsonArray[0].getJSONObject(pos).getInt("Id");
                                            Log.e("reportId",String.valueOf(reportId)+" "+String.valueOf(InvoiceId));

                                            addInvoicesToReport(reportId,InvoiceId);




                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","Bearer "+token);

                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };

        VolleyClass.getInstance(PostedInvoiceDetail.this).addToRequestQueue(stringRequest);








    }


    public void addInvoicesToReport(final int reportId, final int invoiceId){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, addInvoicesToReport,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.get("HasError").toString().equals("false")){
                                Toast.makeText(getApplicationContext(),"Fatura başarıyla rapora eklendi.",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(),HomePage.class));
                                PostedInvoiceDetail.this.finish();
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("reportId",String.valueOf(reportId));
                params.put("InvoiceIdList",String.valueOf(invoiceId));

                return params;

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","Bearer "+token);

                return params;
            }
        };

        VolleyClass.getInstance(PostedInvoiceDetail.this).addToRequestQueue(stringRequest);


    }








}
