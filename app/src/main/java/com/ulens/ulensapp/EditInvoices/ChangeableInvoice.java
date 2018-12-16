package com.ulens.ulensapp.EditInvoices;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.ulens.ulensapp.HomePage;
import com.ulens.ulensapp.R;
import com.ulens.ulensapp.SupportClasses.VolleyClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChangeableInvoice extends AppCompatActivity {

    final String getInvoicePhotoUrl = "http://www.ulensapp.com/api/Bill/GetInvoicePhoto?invoiceId=";
    final String getDetailUrl = "http://www.ulensapp.com/Api/Bill/GetInvoice?invoiceId=";
    final String getReports = "http://ulensapp.com/api/Bill/GetReports?isVisible=false";
    final String addInvoicesToReport= "http://ulensapp.com/Api/Bill/AddInvoicesToReport";
    final String editInvoice = "http://ulensapp.com/Api/Bill/EditInvoice";

    String []categories = {"Uçak Bileti","Eğlence","Etkinlik","Sabit Kıymet","Hediye","Yemek","Çeşitli","Ofis İhtiyaçları","Posta","Servisler","İletişim","Ulaşım"};

    ImageView imgHeader;
    TextView txtDate;
    Spinner spCategory;
    EditText edtMerchantName;
    EditText edtAmount;
    EditText edtKdvRatio;
    EditText edtKdvAmount;
    EditText edtCurrancy;
    Button btnBack;
    Spinner spReports;
    Button btnUpdate;
    EditText edtDescription;

    static final int DILOG_ID=0;

    int year = 2018 ,month = 0,day = 1;



    String token;
    String invoiceId;

    int reportId;
    int k = 0;
    boolean isSubmitted = false;

    ArrayList<String> reportNames = new ArrayList<>();
    Context context = this;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeable_invoice);

        txtDate = findViewById(R.id.txtDate);
        btnUpdate = findViewById(R.id.btnUpdateRp);

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        token=sharedPreferences.getString("token","");

        getIntents();
        setBackButton();
        getInvoicePhoto();
        getInvoiceDetail();
 //       isEnabled(isSubmitted);
        reportName();

        showDialogOnClick();




    }

    public void showDialogOnClick(){
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DILOG_ID);
            }
        });

    }

    private DatePickerDialog.OnDateSetListener dpickerlistener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            year = i;
            month = ++i1;
            day = i2;

            txtDate.setText(year+"-"+month+"-"+day);
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == DILOG_ID)
            return new DatePickerDialog(this,dpickerlistener,year,month,day);
        return null;

    }

    private void isEnabled(boolean isSubmitted) {
        if (isSubmitted){
            edtMerchantName.setEnabled(false);
            spCategory.setEnabled(false);
            edtAmount.setEnabled(false);
            edtKdvAmount.setEnabled(false);
            edtKdvRatio.setEnabled(false);
            spReports.setEnabled(false);
            spCategory.setEnabled(false);
            edtDescription.setEnabled(false);
            btnUpdate.setEnabled(false);

            String reportName = getIntent().getStringExtra("reportName");
            reportNames.add(reportName);
            spCategory.setSelection(reportNames.size());

        }

    }

    public void getInvoiceDetail(){

        edtMerchantName = findViewById(R.id.edtNameRp);
        edtMerchantName.setEnabled(false);

        edtAmount = findViewById(R.id.edtAmountRID);
        edtAmount.setEnabled(false);

        edtKdvRatio = findViewById(R.id.edtKDVRatioRID);
        edtKdvRatio.setEnabled(false);

        Selection.setSelection(edtKdvRatio.getText(), edtKdvRatio.getText().length());
        edtKdvRatio.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("%")){
                    edtKdvRatio.setText("%");
                    Selection.setSelection(edtKdvRatio.getText(), edtKdvRatio.getText().length());

                }

            }
        });

        edtKdvAmount = findViewById(R.id.edtKDVAmountRID);
        edtKdvAmount.setEnabled(false);

        edtCurrancy = findViewById(R.id.edtCurrancyRcp);
        edtCurrancy.setText("TL");
        edtCurrancy.setEnabled(false);

        edtDescription = findViewById(R.id.edtDescriptionRID);
        edtDescription.setEnabled(false);

        spCategory = findViewById(R.id.spCategoryRID);
        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item,  getApplicationContext().getResources().getStringArray(R.array.spinnerCategory));
        spinnerArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view getApplicationContext().getResources().getStringArray(R.array.spinnerCategory)
        spCategory.setAdapter(spinnerArrayAdapter2);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getDetailUrl + invoiceId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String merchantName;
                        String amount;
                        String category;
                        String payment;
                        String KDVAmount;
                        String KDVRatio;
                        String description;
                        String date;
                        try {
                            JSONObject json = new JSONObject(response);

                            merchantName = json.getString("Name");
                            edtMerchantName.setText(merchantName);
                            edtMerchantName.setEnabled(true);


                            date = json.getString("Date");
                            String[] parts = date.split("T");
                            date = parts[0];
                            txtDate.setText(date);
                            txtDate.setTextColor(Color.WHITE);



                            amount = json.getString("Amount");
                            edtAmount.setText(amount);
                            edtAmount.setEnabled(true);
                            edtAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

                            KDVRatio = json.getString("KDVRatio");
                            edtKdvRatio.setText("%"+KDVRatio);
                            edtKdvRatio.setEnabled(true);
                            edtKdvRatio.setInputType(InputType.TYPE_CLASS_NUMBER);

                            KDVAmount = json.getString("KDVAmount");
                            edtKdvAmount.setText(KDVAmount);
                            edtKdvAmount.setEnabled(true);
                            edtKdvAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

                            description = json.getString("Description");
                            if(description.compareTo("null") != 0)
                                edtDescription.setText(description);

                            edtDescription.setEnabled(true);

                            category = json.getString("Category");
                            spCategory.setSelection(Integer.parseInt(category));

                            reportId = json.getInt("ReportId");
                           // reportName();

                            isEnabled(isSubmitted);


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
        VolleyClass.getInstance(ChangeableInvoice.this).addToRequestQueue(stringRequest);
    }

    public void reportName(){
        spReports = findViewById(R.id.spReportNames);

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

                                if (jsonArray[0].getJSONObject(i).get("IsVisible").toString().equals("false") && !jsonArray[0].getJSONObject(i).getString("IsConfirmed").equals("false") ){
                                    Log.e("deneme", jsonArray[0].getJSONObject(i).get("IsVisible").toString());

                                    reportNames.add(jsonArray[0].getJSONObject(i).getString("Name"));

                                    if (reportId == jsonArray[0].getJSONObject(i).getInt("Id")){
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
                            //spReports.setSelection(k);

                            btnUpdate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    updateInvoice();

                                   if (spReports.getSelectedItemPosition() != 0){
                                       int pos = spReports.getSelectedItemPosition();
                                       pos--;
                                       int reportId = 0;

                                       try {
                                            reportId = jsonArray[0].getJSONObject(pos).getInt("Id");
                                            Log.e("reportId",String.valueOf(reportId));
                                            Log.e("invoiceId",invoiceId);

                                            addInvoicesToReport(reportId,Integer.parseInt(invoiceId));

                                       } catch (JSONException e) {
                                           e.printStackTrace();
                                       }

                                   }


                                }
                            }



                            );



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

        VolleyClass.getInstance(ChangeableInvoice.this).addToRequestQueue(stringRequest);


    }



    public void addInvoicesToReport(final int reportId, final int invoiceId){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, addInvoicesToReport,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("HasError")){
                                startActivity(new Intent(getApplicationContext(),HomePage.class));
                                ChangeableInvoice.this.finish();
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
                params.put("Authorization","Bearer "+token);

                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("reportId",String.valueOf(reportId));
                params.put("InvoiceIdList",String.valueOf(invoiceId));

                return params;
            }
        };

        VolleyClass.getInstance(ChangeableInvoice.this).addToRequestQueue(stringRequest);



    }


    public void updateInvoice (){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, editInvoice,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("updateInvoice",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("HasError").contains("false")){
                                Toast.makeText(getApplicationContext(),"Fatura Güncellendi",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),HomePage.class));
                                ChangeableInvoice.this.finish();
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
                params.put("Authorization","Bearer "+token);

                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Id",invoiceId);
                params.put("Name",edtMerchantName.getText().toString());
                params.put("Amount",edtAmount.getText().toString());
                params.put("KDVRatio",edtKdvRatio.getText().toString().substring(1));
                params.put("KDVAmount",edtKdvAmount.getText().toString());
                params.put("Description",edtDescription.getText().toString());
                params.put("Category",String.valueOf(spCategory.getSelectedItemPosition()));
                params.put("Date",txtDate.getText().toString());

                return params;

            }
        };

        VolleyClass.getInstance(ChangeableInvoice.this).addToRequestQueue(stringRequest);

    }




    public void getIntents() {
        //token = getIntent().getStringExtra("token");
        isSubmitted = getIntent().getBooleanExtra("isSubmitted",false);
        invoiceId = getIntent().getStringExtra("Id");
    }

    public void setBackButton(){
        btnBack = findViewById(R.id.btnBackBillDetail);

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
                        .setIcon(R.drawable.iconerror)
                        .show();

            }
        });
    }


    public void getInvoicePhoto(){

        imgHeader = findViewById(R.id.imgHeaderRID);
        //getInvoicePhotoUrl+invoiceId
        final GlideUrl glideUrl = new GlideUrl(getInvoicePhotoUrl+invoiceId, new LazyHeaders.Builder()
                .addHeader("Authorization","Bearer"+" "+token)
                .build());

        Log.e("getInvoicePhotoUrl",getInvoicePhotoUrl+invoiceId);

        Glide.with(this).load(glideUrl).into(imgHeader);

        imgHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ChangeableInvoice.this);
                LayoutInflater inflater = ChangeableInvoice.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialogimage, null);
                ImageView imageView = dialogView.findViewById(R.id.my_image);
                Glide.with(ChangeableInvoice.this).load(glideUrl).into(imageView);




                builder.setView(dialogView)
                        .setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                Dialog dialog = builder.create();
                dialog.show();

                dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

            }
        });

    }




}
