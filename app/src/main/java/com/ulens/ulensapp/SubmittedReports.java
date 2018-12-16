package com.ulens.ulensapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.ulens.ulensapp.Reports.ReportSubmittedInvoices;
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

public class SubmittedReports extends AppCompatActivity {
    final String getSubmittedReportsUrl = "http://ulensapp.com/api/Bill/GetReports?isVisible=true";

    ListView lv;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String token;

    Button btnBack;



    Context context = this;
    TextView noReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitted_reports);

        btnBack = findViewById(R.id.btnBackSR);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),HomePage.class));
                SubmittedReports.this.finish();
            }
        });

        sharedPreferences= getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        noReport = findViewById(R.id.txtNoInvoice4);

        token=sharedPreferences.getString("token","");

        lv=findViewById(R.id.lvSubmitted);

        getReports(context);




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
                            List<String> array= new ArrayList<>();

                            if (jsonArray.length()>0){
                                lv.setVisibility(View.VISIBLE);
                                weather_data = new Weather[jsonArray.length()];
                                for (int  i = 0 ; i < jsonArray.length() ; i++){
                                    array.add("Fatura Id :" + jsonArray.getJSONObject(i).getString("Id"));
                                    weather_data[i] = new Weather(R.drawable.iconreport,jsonArray.getJSONObject(i).getString("Name"),"₺"+jsonArray.getJSONObject(i).getString("TotalAmount"),"2018-02-02");

                                }


                                WeatherAdapter adapter = new WeatherAdapter(context, R.layout.listview_item_row, weather_data);



                                lv.setAdapter(adapter);

                            }
                            else{
                                noReport.setVisibility(View.VISIBLE);
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

                                        startActivity(intent);
                                        SubmittedReports.this.finish();




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

        VolleyClass.getInstance(SubmittedReports.this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SubmittedReports.this.finish();
    }
}
