package com.ulens.ulensapp.NonUserThings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

public class SplashScreen extends AppCompatActivity {

    ConstraintLayout clSplash;
    final String tokenUrl = "http://ulensapp.com/Token";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean isRemember = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);  // Daha önceden kullanıcının kayıtlamış
        editor = sharedPreferences.edit();                                              // beni hatırla tercihini telefon hafızasından
                                                                                        // almak için kullanılmıştır.
        clSplash = findViewById(R.id.clSplash);
        boolean isConnected = checkConnectivityStatus(); // Cihaz internete bağlı mı sorgusu.
        if (isConnected){
            autoLogin();
        }
        if (!isRemember){                                // Beni hatırla değeri aktif değil ise, 1500milisaniye gecikmenin ardından, login ekranına geçiş sağlanır.
            goToLoginScreen(1500);
        }
    }

    private void loginRequest(final String email, final String password, final ProgressDialog pd) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tokenUrl, // Post metodu ile token alabileceğim servise HttpRequest gönderiyorum.
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {                                                          // Olası JSON exceptionları için try bloğu.
                            JSONObject json = new JSONObject(response);
                            if(json.getString("access_token")!=""){              // Eğer almış olduğum token null değil ise if bloğunda devam et.
                                String token = json.getString("access_token");   // Token'ı telefon hafızasına kaydediyorum.
                                editor.putString("token", token);
                                editor.commit();
                                Intent intent = new Intent(getApplicationContext(),HomePage.class);
                                intent.putExtra("token",token);
                                pd.dismiss();
                                startActivity(intent);
                                SplashScreen.this.finish();                             // Anasayfaya geçmenin ardından bulunduğum aktiviteyi öldürüyorum.
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("onErrorResponse",error.toString());

//                pd.dismiss();
//
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
//                                    alertDialog = builder.create();
//                            alertDialog.show();
//                        }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//
//                            break;
//
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
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        VolleyClass.getInstance(SplashScreen.this).addToRequestQueue(stringRequest);

    }

    private void goToLoginScreen(int delayTime) {  // SplashScreen'de beklemeyi yaptıran fonksiyon. İdeal süre 1500ms gibi gözüküyor.
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(getApplicationContext(),LoginActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, delayTime);
    }

    public void autoLogin(){
        ProgressDialog pd = new ProgressDialog(SplashScreen.this);
        pd.setCancelable(false);
        pd.setMessage("Lütfen Bekleyiniz...");

        isRemember = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("isRemember",false);
        String userName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("userName","username");
        String password = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("password","password");

        if (isRemember) {  // Eğer önceki oturumlarda beni hatırla seçeneği aktif edilip, başarılı login gerçekleşmişse değer "true gelip", giriş isteği göndermektedir.
            pd.show();     // İlerleyen sürümlerde kullanıcı bilgileri üzerinden değil, token üzerinden, geçerli veya geçerli değil gibi kontrole geçilecektir.
            loginRequest(userName,password,pd);
        }
    }

    public boolean checkConnectivityStatus() {
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED   // Cihaz mobil veri üzerinden yada wifi bağlantısı ile internete bağlanmış mı kontrolü.
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {
            return true;
        }
        else if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }
}
