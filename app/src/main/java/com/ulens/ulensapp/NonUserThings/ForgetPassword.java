package com.ulens.ulensapp.NonUserThings;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ulens.ulensapp.R;
import com.ulens.ulensapp.SupportClasses.VolleyClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgetPassword extends AppCompatActivity {

    TextView txtGoLogin;
    String forgetPassUrl="http://www.ulensapp.com/Api/Auth/ForgotPassword";

    EditText edtMail;
    Button btnForgetPass;

    RelativeLayout rlForgetPass;

    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        setTextBox();

        rlForgetPass = findViewById(R.id.rlForgetPass);


        edtMail = findViewById(R.id.edtEmailForget);
        btnForgetPass = findViewById(R.id.btnForgetPass);

        txtGoLogin = findViewById(R.id.txtGoLoginForget);
        txtGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        btnForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtMail.getText().toString().equals("")){
                    Snackbar snackbar = Snackbar.make(rlForgetPass,"Email Adresi Boş Bırakılamaz.",Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else{
                    sifreGonder();
                }


            }
        });






    }

    private void sifreGonder() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, forgetPassUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        builder = new AlertDialog.Builder(ForgetPassword.this);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("HasError").equals("true")){

                                builder.setTitle("Hatali Email");
                                builder.setMessage("Email Adresi bulunamadi.");
                                builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();



                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                       // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Email",edtMail.getText().toString());

                return params;
            }
        };

        VolleyClass.getInstance(ForgetPassword.this).addToRequestQueue(stringRequest);


    }



    public void setTextBox(){   // textboxların arkaplanını beyaz yapıp köşeleri yumuşatmak

        ImageView imageView;

        imageView = findViewById(R.id.imgEmailForget);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.whitepattern);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCornerRadius(20);
        imageView.setImageDrawable(roundedBitmapDrawable);

    }

}
