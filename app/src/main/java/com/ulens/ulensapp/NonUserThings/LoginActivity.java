package com.ulens.ulensapp.NonUserThings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.ulens.ulensapp.HomePage;
import com.ulens.ulensapp.R;
import com.ulens.ulensapp.SupportClasses.VolleyClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements TextWatcher, CompoundButton.OnCheckedChangeListener {

    TextView txtRegister,txtPassForget;
    Button btnLogin;

    EditText edtEmail;
    EditText edtPass;

    final String tokenUrl = "http://ulensapp.com/Token";
    AlertDialog.Builder builder;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String PREF_NAME = "prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASS = "password";

    RelativeLayout rlLogin;

    CheckBox cxRemember;


    ProgressDialog pd;


    SharedPreferences sharedPref;
    SharedPreferences.Editor editor2;

    TextView txtAdmin;

    Context context = this;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        txtAdmin = findViewById(R.id.txtAdmin);
        txtAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Administration.class));
            }
        });




        edtEmail = findViewById(R.id.edtEmailRegister);
        edtPass = findViewById(R.id.edtPass);

        rlLogin = findViewById(R.id.rlLogin);


        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        sharedPref= getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor2=sharedPref.edit();



        cxRemember = findViewById(R.id.cx_remember);
        cxRemember.setEnabled(false);

        edtPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!edtEmail.getText().equals("")){
                        cxRemember.setEnabled(true);
                    }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if(sharedPreferences.getBoolean(KEY_REMEMBER, false)){
            cxRemember.setChecked(true);
        }
        else
            cxRemember.setChecked(false);

        edtEmail.setText(sharedPreferences.getString(KEY_USERNAME,""));
        edtPass.setText(sharedPreferences.getString(KEY_PASS,""));

        edtEmail.addTextChangedListener(this);
        edtPass.addTextChangedListener(this);
        cxRemember.setOnCheckedChangeListener(this);




        txtRegister = findViewById(R.id.txtRegister);
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });

        txtPassForget = findViewById(R.id.txtPassForget);
        txtPassForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ForgetPassword.class));
            }
        });


        setTextBox(1);
        setTextBox(2);


        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString();
                String password = edtPass.getText().toString();


                pd = new ProgressDialog(LoginActivity.this);
                pd.setCancelable(false);
                pd.setMessage("Lütfen Bekleyiniz..");



                if (email.equals("") && password.equals(""))
                    Toast.makeText(getApplicationContext(),"Gerekli alanları doldurunuz",Toast.LENGTH_SHORT).show();

                else if(email.equals("")){
                    Toast.makeText(getApplicationContext(),"Email alanı boş bırakılamaz",Toast.LENGTH_LONG).show();
                }
                else if(password.equals("")){
                    Toast.makeText(getApplicationContext(),"Şifre alanı boş bırakılamaz",Toast.LENGTH_LONG).show();
                }
                else{
                    pd.show();
                    loginRequest(email,password,pd);
                }


            }
        });


        boolean isConnected = checkConnectivityStatus();

        if (isConnected){
            AutoLogin();
        }



    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog);
        } else {
            builder = new android.app.AlertDialog.Builder(context);
        }
        builder.setTitle("Çıkış Yap")
                .setMessage("Uygulamadan ayrılmak istiyor musunuz ?")
                .setPositiveButton("Geri Dön", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Çıkış Yap", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(DialogInterface dialog, int which) {
                        finishAndRemoveTask();
                    }
                })
                .setIcon(R.drawable.iconerror)
                .show();


    //    super.onBackPressed();
    }

    private void loginRequest(final String email, final String password, final ProgressDialog pd) {

        builder = new AlertDialog.Builder(LoginActivity.this);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, tokenUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                       // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

                        String rspService="";
                        try {
                            JSONObject json = new JSONObject(response);


                            if(json.getString("access_token")!=""){

                                if(cxRemember.isChecked()){
                                    editor.putString(KEY_USERNAME, edtEmail.getText().toString().trim());
                                    editor.putString(KEY_PASS, edtPass.getText().toString().trim());
                                    editor.putBoolean(KEY_REMEMBER, true);
                                    editor.apply();


                                    saveUserInfos(edtEmail.getText().toString(),edtPass.getText().toString());



                                }





                                String token = json.getString("access_token");
                                Intent intent = new Intent(getApplicationContext(),HomePage.class);

                                editor.putString("token", token);
                                editor.commit();


                                intent.putExtra("token",token);
                                pd.dismiss();
                                startActivity(intent);
                                LoginActivity.this.finish();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }






                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse",error.toString());
                pd.dismiss();

                int statusCode = error.networkResponse.statusCode;

                if (statusCode == 400){
                    builder.setTitle("Hatalı Bilgi");
                            builder.setMessage("Hatali kullanıcı adı veya şifre.");
                            builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                }
                else if (statusCode == 500) {
                    builder.setTitle("Server Hatası");
                    builder.setMessage("Serverdan cevap alınamadı. En kısa sürede problem Ulens ekibi tarafından giderilecektir.");
                    builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else if (statusCode == 401){

                }
                else if (statusCode == 403){

                }
                else if (statusCode == 404){

                }

//                JSONObject jsonObject = null;
//                NetworkResponse networkResponse = error.networkResponse;
//
//
//                NetworkResponse response = error.networkResponse;
//                String jsonError = new String(networkResponse.data);
//
//                try {
//                    jsonObject = new JSONObject(jsonError);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//                if(response != null && response.data != null){
//                    switch(response.statusCode){
//                        case 500:
//                            Log.e("500Hatası","switch içindeyim");
//
//                            builder.setTitle("Server Hatası");
//                            builder.setMessage("Serverdan cevap alınamadı. En kısa sürede problem Ulens ekibi tarafından giderilecektir.");
//                            builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//
//                            AlertDialog alertDialog = builder.create();
//                            alertDialog.show();
//
//                            break;
//
//                        case 400:
//
//                            // Toast.makeText(getApplicationContext(),"bilgiler yanlis",Toast.LENGTH_LONG).show();7
//                            try {
//                                if (jsonObject.getString("error").equals("invalid_grant")) {
//                                    editor.putBoolean(KEY_REMEMBER, false);
//                                    editor.remove(KEY_PASS);//editor.putString(KEY_PASS,"");
//                                    editor.remove(KEY_USERNAME);//editor.putString(KEY_USERNAME, "");
//                                    editor.apply();
//
//
//                                    builder.setTitle("Hatali Giriş");
//                                    builder.setMessage("Kullanıcı adı veya Şifre Hatalı.");
//                                    builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
//                                        }
//                                    });
//
//
//                                    alertDialog = builder.create();
//                                    alertDialog.show();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            try {
//
//                            if(jsonObject.getString("error").equals("not_confirmed")){
//
//                            builder.setTitle("Hatali Giriş");
//                            builder.setMessage("Eposta adresi onaylı değil.");
//                            builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//
//                            alertDialog = builder.create();
//                            alertDialog.show();
//                        }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            break;
//                    }
//                }
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("grant_type","password");
                params.put("password",password);
                params.put("username",email);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 15000;
            }
            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        VolleyClass.getInstance(LoginActivity.this).addToRequestQueue(stringRequest);

    }

    public void setTextBox(int view){   // textboxların arkaplanını beyaz yapıp köşeleri yumuşatmak

        ImageView imageView;

        if (view==1) {
            imageView = (ImageView) findViewById(R.id.imgText);
        }
        else {
            imageView = (ImageView) findViewById(R.id.imgPass);
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.whitepattern);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCornerRadius(20);
        imageView.setImageDrawable(roundedBitmapDrawable);

    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (edtEmail.getText().toString().equals("") && edtPass.getText().toString().equals("")){

            Snackbar snackbar = Snackbar.make(rlLogin,"Email ve Şifre girdikten sonra \"Beni Hatırla\" özelliiği kullanılabilir.",Snackbar.LENGTH_LONG);
            snackbar.show();

            cxRemember.setChecked(false);

            return;
        }


        managePrefs();
    }

    private void managePrefs(){
        if(cxRemember.isChecked()){
            editor.putString(KEY_USERNAME, edtEmail.getText().toString().trim());
            editor.putString(KEY_PASS, edtPass.getText().toString().trim());
            editor.putBoolean(KEY_REMEMBER, true);
            editor.apply();
        }else{
            editor.putBoolean(KEY_REMEMBER, false);
            editor.remove(KEY_PASS);//editor.putString(KEY_PASS,"");
            editor.remove(KEY_USERNAME);//editor.putString(KEY_USERNAME, "");
            editor.apply();
        }
    }


    public boolean checkConnectivityStatus() {
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {

            btnLogin.setEnabled(true);
            cxRemember.setEnabled(true);
            txtPassForget.setEnabled(true);

            return true;

        }
        else if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {

            Snackbar snackbar = Snackbar.make(rlLogin,"Internete Bağlanılamadı",Snackbar.LENGTH_INDEFINITE);


            snackbar.setAction("Yeniden Dene", new MyRetryListener());
            snackbar.show();

           btnLogin.setEnabled(false);
           cxRemember.setEnabled(false);
           txtPassForget.setEnabled(false);

           return false;

        }

        return false;
    }

    public void saveUserInfos(String userName, String password){

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("userName", userName).apply();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("password",password).apply();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("isRemember", true).apply();
        Log.e("saveUserInfos","kaydedildi");
    }

    public void AutoLogin(){

        ProgressDialog pd;
        pd = new ProgressDialog(LoginActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Lütfen Bekleyiniz..");

        boolean isRemember = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("isRemember",false);
        String userName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("userName","username");
        String password = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("password","password");
        Log.e("AutoLogin",userName + " " + password + " " + String.valueOf(isRemember));

        if (isRemember) {
            Log.e("isRemember","içerdeyim");

            pd.show();
            loginRequest(userName,password,pd);
        }


    }



    public class MyRetryListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            checkConnectivityStatus();
        }
    }



}


