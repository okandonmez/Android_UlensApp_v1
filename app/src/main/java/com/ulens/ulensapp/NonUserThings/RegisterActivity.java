package com.ulens.ulensapp.NonUserThings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ulens.ulensapp.R;
import com.ulens.ulensapp.SupportClasses.VolleyClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    TextView txtGoLogin;

    EditText edtName,edtUsername,edtPass;
    String registerURL = "http://www.ulensapp.com/Api/Auth/Register";

    Button btnRegister;

    ProgressDialog pd;
    RelativeLayout rlRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rlRegister = findViewById(R.id.rlRegister);

        edtName = findViewById(R.id.edtName);
        edtUsername = findViewById(R.id.edtEmailRegister);
        edtPass = findViewById(R.id.edtPassReg);

        txtGoLogin = findViewById(R.id.txtGoLogin);
        txtGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        setTextBox(1);
        setTextBox(2);
        setTextBox(3);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setCancelable(false);
                pd.setMessage("Lütfen Bekleyiniz..");

               if (edtName.getText().toString().equals(""))
                   Toast.makeText(getApplicationContext(),"Gerekli Alanları Doldurunuz",Toast.LENGTH_SHORT).show();
//               else if (edtPass.getText().toString().equals(""))
//                    Toast.makeText(getApplicationContext(),"Gerekli Alanları Doldurunuz",Toast.LENGTH_SHORT).show();
               else if (edtUsername.getText().toString().equals(""))
                   Toast.makeText(getApplicationContext(),"Gerekli Alanları Doldurunuz",Toast.LENGTH_SHORT).show();
               else if (edtName.getText().toString().length()<4){
                   edtName.setError("Adınızı ve Soyadınızı Giriniz");
               }
               else if (!isValidEmail(edtUsername.getText().toString())){
                   edtUsername.setError("Email adresinizi tam giriniz.");
               }
//               else if(edtPass.getText().toString().length()<6){
//                   edtPass.setError("Şifre en az 6 karakter olmalıdır.");
//               }

               else{
                   pd.show();
                   register();
               }

            }
        });

    }

    public void setTextBox(int view){   // textboxların arkaplanını beyaz yapıp köşeleri yumuşatmak

        ImageView imageView;

        if (view==1) {
            imageView = findViewById(R.id.imgText2);
        }
        else if(view==2) {
            imageView = findViewById(R.id.imgPass2);
        }
        else {
            imageView=findViewById(R.id.imgName);
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.whitepattern);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCornerRadius(20);
        imageView.setImageDrawable(roundedBitmapDrawable);

    }


    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void register() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, registerURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("Kayıt Response",response);

                            if (jsonObject.getString("HasError").equals("false"))
                                Toast.makeText(getApplicationContext(),"Kayıt Başarılı. Eposta adresini aktif ediniz",Toast.LENGTH_SHORT).show();
                            if (jsonObject.getString("HasError").equals("true")){
                                Snackbar snackbar = Snackbar.make(rlRegister,"Eposta adresi sistemde kayıtlı veya geçerli değil.",Snackbar.LENGTH_LONG);
                                snackbar.show();
                                edtUsername.setError("");
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
                params.put("Email",edtUsername.getText().toString());
                params.put("UserName",edtUsername.getText().toString());
//                params.put("Password",edtPass.getText().toString());
//                params.put("ConfirmPassword",edtPass.getText().toString());
                params.put("Name",edtName.getText().toString());
                params.put("Lastname","Donmez");

                return params;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        VolleyClass.getInstance(RegisterActivity.this).addToRequestQueue(stringRequest);


    }


    public void checkConnectivityStatus() {
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {


        }
        else if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {

            Snackbar snackbar = Snackbar.make(rlRegister,"Internete Bağlanılamadı",Snackbar.LENGTH_INDEFINITE);
            snackbar.show();


            btnRegister.setEnabled(false);

        }
    }



}
