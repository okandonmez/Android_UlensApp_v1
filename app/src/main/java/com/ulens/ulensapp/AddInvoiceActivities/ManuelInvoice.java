package com.ulens.ulensapp.AddInvoiceActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ulens.ulensapp.HomePage;
import com.ulens.ulensapp.R;
import com.ulens.ulensapp.SupportClasses.VolleyClass;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ManuelInvoice extends AppCompatActivity {

    final String POST_INVOICE_URL ="http://www.ulensapp.com/Api/Bill/PostInvoice";
    final String ADD_INVOICE_URL = "http://ulensapp.com/Api/Bill/PostInvoicePhoto";

    Toolbar toolbar;
    Spinner spCategory, spKDVRatio, spReports, spPayment;
    EditText edtTotalAmount, edtKDVAmount, edtMerchantName, edtDate, edtFişNo, edtDescription;
    Button btnSend;

    String token;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manuel_invoice);

        sharedPreferences= getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        token=sharedPreferences.getString("token","");

        toolbar = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spCategory = findViewById(R.id.spCategory);
        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(this, R.layout.spinneritemblacktext,  getApplicationContext().getResources().getStringArray(R.array.spinnerCategory));
        spinnerArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view getApplicationContext().getResources().getStringArray(R.array.spinnerCategory)
        spCategory.setAdapter(spinnerArrayAdapter2);
        spCategory.setSelection(5);

        spKDVRatio = findViewById(R.id.spKDVRatio);
        ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(this, R.layout.spinneritemblacktext,  getApplicationContext().getResources().getStringArray(R.array.KDVRatio));
        spinnerArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view getApplicationContext().getResources().getStringArray(R.array.spinnerCategory)
        spKDVRatio.setAdapter(spinnerArrayAdapter3);
        spKDVRatio.setSelection(1);

        spPayment = findViewById(R.id.spPayment);
        ArrayAdapter<String> spinnerArrayAdapter4 = new ArrayAdapter<String>(this, R.layout.spinneritemblacktext,  getApplicationContext().getResources().getStringArray(R.array.paymentmethod));
        spinnerArrayAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view getApplicationContext().getResources().getStringArray(R.array.spinnerCategory)
        spPayment.setAdapter(spinnerArrayAdapter4);

        edtTotalAmount = findViewById(R.id.edtTotalAmount);
        edtKDVAmount = findViewById(R.id.edtKDVAmount);
        edtMerchantName = findViewById(R.id.edtMerchantName);
        edtDate = findViewById(R.id.edtDate);
        edtFişNo = findViewById(R.id.edtInvoiceNo);
        edtDescription = findViewById(R.id.edtDescription);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtTotalAmount.getText().toString().length() == 0 ||
                        edtKDVAmount.getText().toString().length() == 0 ||
                        edtMerchantName.getText().toString().length() == 0 ||
                        edtDate.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(),"Boş Alanları Doldurunuz.",Toast.LENGTH_LONG).show();
                }else {
                    postReport();
                }

            }
        });


    }

    public void postReport (){
        int KDVRatio = spKDVRatio.getSelectedItemPosition();
        String strKDVRatio = null;

        if (KDVRatio == 0)
            strKDVRatio = "1";
        else if (KDVRatio == 1)
            strKDVRatio = "8";
        else
            strKDVRatio = "18";

        final String finalStrKDVRatio = strKDVRatio;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_INVOICE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("HasError").contains("false")){
                                startActivity(new Intent(getApplicationContext(),HomePage.class));
                                ManuelInvoice.this.finish();
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Name", edtMerchantName.getText().toString());
                params.put("Amount", edtTotalAmount.getText().toString());
                params.put("KDVRatio", finalStrKDVRatio);
                params.put("KDVAmount",edtKDVAmount.getText().toString());
                params.put("Billable","1");
                params.put("Date","2017-12-25 11:34:21");
                params.put("Description", edtDescription.getText().toString());
                params.put("Category", String.valueOf(spCategory.getSelectedItemPosition()));
                params.put("Payment", String.valueOf(spPayment.getSelectedItemPosition()));

                return params;
            }
        };

        VolleyClass.getInstance(ManuelInvoice.this).addToRequestQueue(stringRequest);


    }
    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(getApplicationContext(), HomePage.class));
        ManuelInvoice.this.finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }
}
