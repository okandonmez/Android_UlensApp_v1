package com.ulens.ulensapp.EditInvoices;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.ulens.ulensapp.R;
import com.ulens.ulensapp.SupportClasses.VolleyClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReportedInvoiceDetails extends AppCompatActivity {

    final String getInvoicePhotoUrl = "http://www.ulensapp.com/api/Bill/GetInvoicePhoto?invoiceId=";
    final String getDetailUrl = "http://www.ulensapp.com/Api/Bill/GetInvoice?invoiceId=";
    final String getReports = "http://ulensapp.com/api/Bill/GetReports?isVisible=false";



    ImageView imgHeader;
    Button btnBack;
    EditText edtMerchantName;
    Spinner spCategory;
    EditText edtAmount;
    EditText edtKdvRatio;
    EditText edtKdvAmount;
    Spinner spReports;
    EditText edtDescription;
    Button btnUpdate;
    Context context = this;
    String reportId;

    ArrayList<String> reportNames = new ArrayList<>();

    boolean isEnabled = false;
    int k = 0;

    String token;
    String invoiceId;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reported_invoice_details);
        setParams();
        setViews();
        setBackButton();


        getInvoicePhoto();
        getInvoiceDetails();
        reportName();

    }

    private void setEnabled(boolean isEnabled) {
        Log.e("isEnabled",String.valueOf(isEnabled));
        if (isEnabled){
            Log.e("içerde","içerde");
            edtMerchantName.setEnabled(false);
            spCategory.setEnabled(false);
            edtAmount.setEnabled(false);
            edtKdvRatio.setEnabled(false);
            edtKdvAmount.setEnabled(false);
            spReports.setEnabled(false);
            edtDescription.setEnabled(false);
            edtDescription.setEnabled(false);
            btnUpdate.setVisibility(View.INVISIBLE);

        }
    }

    public void reportName(){

        spReports = findViewById(R.id.spReportNames);
        spReports.setEnabled(false);

        final JSONArray[] jsonArray = new JSONArray[1];
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getReports,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("Respornse",response);
                            jsonArray[0] = new JSONArray(response);
                            reportNames.clear();
                            reportNames.add("Rapora Ekleyin");

                            for (int i = 0; i < jsonArray[0].length() ; i++ ){
                                Log.e("name", i + " " + jsonArray[0].getJSONObject(i).getString("Name"));


                                if (jsonArray[0].getJSONObject(i).get("IsVisible").toString().equals("false")  && !jsonArray[0].getJSONObject(i).getString("IsConfirmed").equals("false")){
                                    Log.e("deneme", jsonArray[0].getJSONObject(i).get("IsVisible").toString());
                                    reportNames.add(jsonArray[0].getJSONObject(i).getString("Name"));



                                    if (reportId.equals(jsonArray[0].getJSONObject(i).getString("Id"))){
                                        Log.e("k deneme",String.valueOf(i));
                                        k = i;
                                    }

                                }


                            }




                            ArrayAdapter<String> adapter;
                            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, reportNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spReports.setAdapter(adapter);
                            Log.e("k", String.valueOf(k));
                            k++;
                            spReports.setSelection(k);




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

        VolleyClass.getInstance(ReportedInvoiceDetails.this).addToRequestQueue(stringRequest);


    }

    private void getInvoiceDetails() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getDetailUrl + invoiceId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String merchantName;
                        String amount;
                        String category;
                        String KDVAmount;
                        String KDVRatio;
                        String description;
                        try {
                            JSONObject json = new JSONObject(response);

                            merchantName = json.getString("Name");
                            edtMerchantName.setText(merchantName);
                          //  edtMerchantName.setEnabled(true);

                            amount = json.getString("Amount");
                            edtAmount.setText(amount);
                        //    edtAmount.setEnabled(true);
                            edtAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

                            KDVRatio = json.getString("KDVRatio");
                            edtKdvRatio.setText("%"+KDVRatio);
                         //   edtKdvRatio.setEnabled(true);
                            edtKdvRatio.setInputType(InputType.TYPE_CLASS_NUMBER);

                            KDVAmount = json.getString("KDVAmount");
                            edtKdvAmount.setText(KDVAmount);
                      //      edtKdvAmount.setEnabled(true);
                            edtKdvAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

                            description = json.getString("Description");
                            if(description.compareTo("null") != 0)
                                edtDescription.setText(description);

                     //       edtDescription.setEnabled(true);
                            setEnabled(isEnabled);






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

        VolleyClass.getInstance(ReportedInvoiceDetails.this).addToRequestQueue(stringRequest);
    }

    private void setParams() {
        sharedPreferences= getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        reportId = getIntent().getStringExtra("reportId");

        token=sharedPreferences.getString("token","");
        invoiceId = getIntent().getStringExtra("invoiceId");

        isEnabled = getIntent().getBooleanExtra("isSubmitted",false);

    }


    public void setBackButton(){
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog);
                } else {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("Geri Dön")
                        .setMessage("Yapılan değişiklikler kaydedilmeyecektir ?")
                        .setPositiveButton("Geri Dön", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                            }
                        })
                        .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    public void setViews(){
        imgHeader = findViewById(R.id.imgHeaderRID);
        btnBack = findViewById(R.id.btnBackRID);
        edtMerchantName = findViewById(R.id.edtNameRID);
        spCategory = findViewById(R.id.spCategoryRID);
        edtAmount = findViewById(R.id.edtAmountRID);
        edtKdvRatio = findViewById(R.id.edtKDVRatioRID);
        edtKdvAmount = findViewById(R.id.edtKDVAmountRID);
        spReports = findViewById(R.id.spReportNames);
        edtDescription = findViewById(R.id.edtDescriptionRID);
        btnUpdate = findViewById(R.id.btnUpdateRID);

        if (isEnabled){
            Log.e("içerde","içerde");
            edtMerchantName.setEnabled(false);
            spCategory.setEnabled(false);
            edtAmount.setEnabled(false);
            edtKdvRatio.setEnabled(false);
            edtKdvAmount.setEnabled(false);
            spReports.setEnabled(false);
            edtDescription.setEnabled(false);
            edtDescription.setEnabled(false);
            btnUpdate.setVisibility(View.INVISIBLE);

        }
    }

    public void getInvoicePhoto(){
        GlideUrl glideUrl = new GlideUrl(getInvoicePhotoUrl+invoiceId, new LazyHeaders.Builder()
                .addHeader("Authorization","Bearer"+" "+token)
                .build());

        Glide.with(this).load(glideUrl).into(imgHeader);

    }




}
