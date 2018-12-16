package com.ulens.ulensapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.ulens.ulensapp.Reports.ReportUnSubmittedInvoices;
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

public class UnsubmittedReports extends AppCompatActivity {

    final String getUnSubmittedReportsUrl = "http://ulensapp.com/api/Bill/GetReports?isVisible=false";
    final String postReportUrl = "http://ulensapp.com/Api/Bill/PostReport";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private android.support.v7.widget.Toolbar toolbar;

    ListView lv;

    Context context = this;
    String token;

    TextView txtNoReport;

    Button btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsubmitted_reports);

        toolbar = findViewById(R.id.toolbarUnsubmitted);
        setSupportActionBar(toolbar);

        btnBack = findViewById(R.id.btnBackUR);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),HomePage.class));
                UnsubmittedReports.this.finish();
            }
        });

        txtNoReport = findViewById(R.id.txtNoReports);

        lv=findViewById(R.id.lvReports);


        sharedPreferences= getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        token=sharedPreferences.getString("token","");
        getReports(context);


    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(getApplicationContext(),HomePage.class));
        UnsubmittedReports.this.finish();
    }

    public void getReports(final Context context){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getUnSubmittedReportsUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

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

                                        startActivity(intent);

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

        VolleyClass.getInstance(UnsubmittedReports.this).addToRequestQueue(stringRequest);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.addreports, menu);

        int position = 0;
        MenuItem item = menu.getItem(position);
        SpannableString s = new SpannableString("Rapor Ekle");
        s.setSpan(new ForegroundColorSpan(Color.BLACK),0,s.length(),0);
        item.setTitle(s);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int  id = item.getItemId();
        if (id == R.id.addReport){
            //   Toast.makeText(getApplicationContext(),"asldkjasd",Toast.LENGTH_SHORT).show();

            postReport();


        }


        return super.onOptionsItemSelected(item);
    }


    public void postReport(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText editText = new EditText(getApplicationContext());
        editText.setText("Yeni Rapor");
        alert.setMessage("Oluşturacağınız gruplar, fişleri gruplandırmanıza olanak sağlar.");
        alert.setTitle("Yeni Rapor Ekle");

        editText.requestFocus();
        final InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);



        alert.setView(editText);


        alert.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                imm.toggleSoftInputFromWindow(editText.getWindowToken(), 0,0);
                postReport(editText.getText().toString());


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

                                getReports(context);
                                Toast.makeText(getApplicationContext(),"Rapor Ekleme başarılı, Gönderilmemiş Raporlar menüsünden faturalarınızı ekleyebilirsiniz.",Toast.LENGTH_LONG).show();
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


        VolleyClass.getInstance(UnsubmittedReports.this).addToRequestQueue(stringRequest);






    }

}
