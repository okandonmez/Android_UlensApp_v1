package com.ulens.ulensapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;
import com.ulens.ulensapp.AddInvoiceActivities.CameraActivity;
import com.ulens.ulensapp.AddInvoiceActivities.ManuelInvoice;
import com.ulens.ulensapp.EditInvoices.ChangeableInvoice;
import com.ulens.ulensapp.NonUserThings.LoginActivity;
import com.ulens.ulensapp.Reports.ReportUnSubmittedInvoices;
import com.ulens.ulensapp.Reports.UnResulted.AllReportsNotSended;
import com.ulens.ulensapp.SupportClasses.Invoice;
import com.ulens.ulensapp.SupportClasses.InvoiceAdapter;
import com.ulens.ulensapp.SupportClasses.VolleyClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.ulens.ulensapp.R.drawable.noinvoice;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String getInvoiceUrl="http://www.ulensapp.com/api/Bill/GetInvoices?reportId=-1";
    String postReportUrl="http://ulensapp.com/Api/Bill/PostReport";
    final String getInvoicePhotoUrl = "http://www.ulensapp.com/api/Bill/GetInvoicePhoto?invoiceId=";
    final String getUSerInfosUrl = "http://ulensapp.com/api/Auth/GetUserInfo";
    final String deleteInvoiceUrl = "http://ulensapp.com/Api/Bill/DeleteInvoice?invoiceId=";



    SwipeMenuListView list;
    AlertDialog.Builder builder;

    ImageView imgNoInvoice;
    ImageView imgBlackPattern;



//    String arrayName[]={"Kamera",
//                         "ALbum",
//                         "Manuel"};


    CoordinatorLayout coordLayout;
    CircleMenu circleMenuCamera;

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Button btnNewReport;

    Context context;

    private android.support.v7.widget.Toolbar toolbar;

    String token;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        toolbar = findViewById(R.id.toolbarHomePage);
        setSupportActionBar(toolbar);
        context = this;


        btnNewReport = findViewById(R.id.btnNewReport);
        btnNewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           //     postReport();
//                SheetMenu.with(getApplicationContext())
//                        .setTitle("Tara")
//                        .setMenu(R.menu.sheetmenu)
//                        .setAutoCancel(false)
//                        .setClick(new MenuItem.OnMenuItemClickListener() {
//                            @Override
//                            public boolean onMenuItemClick(MenuItem item) {
//                                return false;
//                            }
//                        }).show();


            }
        });




        sharedPreferences= getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        token=sharedPreferences.getString("token","");
       // Toast.makeText(getApplicationContext(),token,Toast.LENGTH_SHORT).show();


       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        NavigationView navigationView = findViewById(R.id.navigationview);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        TextView nav_user = hView.findViewById(R.id.txtHeaderName);
        TextView nav_brand = hView.findViewById(R.id.txtBrandName);
        saveUserInfos(nav_user,nav_brand);


        coordLayout = findViewById(R.id.coordLayout);
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2988a8")));

        checkAndRequestPermissions();


        final CircleMenu circleMenu = findViewById(R.id.circleMenu);
        circleMenu.setMainMenu(getResources().getColor(R.color.UlensSecondColor),R.drawable.ic_add,R.drawable.ic_remove)
                .addSubMenu(getResources().getColor(R.color.UlensSecondColor),R.drawable.iconcamera)
                .addSubMenu(getResources().getColor(R.color.UlensSecondColor),R.drawable.iconmanuel)
                .addSubMenu(getResources().getColor(R.color.UlensSecondColor),R.drawable.iconalbum)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(final int i) {
                       // Toast.makeText(getApplicationContext(),"You selected "+i,Toast.LENGTH_SHORT).show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                if (i==0){
                                    circleMenu.setVisibility(View.INVISIBLE);
                                    circleMenuCamera.setVisibility(View.VISIBLE);
                                    circleMenuCamera.openMenu();
                                }
                                else if(i==1){
                                    imgBlackPattern.setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(getApplicationContext(),ManuelInvoice.class);
                                    intent.putExtra("token",token);
                                    startActivity(intent);

                                }
                                else if (i==2){
                                    imgBlackPattern.setVisibility(View.INVISIBLE);
                                }

                            }
                        }, 1000);
                    }
                });


        circleMenu.setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {
            @Override
            public void onMenuOpened() {

            }

            @Override
            public void onMenuClosed() {
              //  Toast.makeText(getApplicationContext(),"Kapattın",Toast.LENGTH_SHORT).show();
                circleMenu.setVisibility(View.INVISIBLE);
                if (circleMenuCamera.getVisibility()!=View.VISIBLE)
                    imgBlackPattern.setVisibility(View.INVISIBLE);

            }
        });

       imgBlackPattern = findViewById(R.id.imgBlackPattern);

        circleMenuCamera = findViewById(R.id.circleMenuCamera);
       circleMenuCamera.setMainMenu(getResources().getColor(R.color.UlensSecondColor),R.drawable.iconcamera,R.drawable.iconcamera)
               .addSubMenu(getResources().getColor(R.color.UlensSecondColor),R.drawable.iconbill)
               .addSubMenu(getResources().getColor(R.color.UlensSecondColor),R.drawable.iconmeal)
               .addSubMenu(getResources().getColor(R.color.UlensSecondColor),R.drawable.iconcar)
               .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                   @Override
                   public void onMenuSelected(int i) {
                       Handler handler = new Handler();
                       handler.postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               circleMenuCamera.setVisibility(View.INVISIBLE);
                               Intent intent = new Intent(getApplicationContext(),CameraActivity.class);
                               intent.putExtra("token",token);
                               startActivity(intent);
                           }
                       }, 1000);
                   }
               });

       circleMenuCamera.setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {
           @Override
           public void onMenuOpened() {
               imgBlackPattern.setVisibility(View.VISIBLE);
           }

           @Override
           public void onMenuClosed() {
               imgBlackPattern.setVisibility(View.INVISIBLE);
               circleMenuCamera.setVisibility(View.INVISIBLE);
           }
       });

        setStatusBarColor(R.color.UlensStatusBarColor);

        //token = getIntent().getStringExtra("token");
        list =  findViewById(R.id.listView);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xed, 0x5e, 0x68)));
                // set item width
                deleteItem.setWidth(250);
                // set a icon
                deleteItem.setIcon(R.drawable.icondelete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

// set creator
        list.setMenuCreator(creator);


        View header = (View)getLayoutInflater().inflate(R.layout.listview_header_row, null);
       // list.addHeaderView(header);



        Context context = this;
        getInvoices(token,context);


        imgNoInvoice = findViewById(R.id.imgNoInvoice);


        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),CameraActivity.class);
                intent.putExtra("token",token);
                startActivity(intent);




//                if(circleMenu.getVisibility()==View.VISIBLE){
//                    circleMenu.closeMenu();
//                    circleMenu.setVisibility(View.INVISIBLE);
//                    imgBlackPattern.setVisibility(View.INVISIBLE);
//
//
//                }
//
//                else if (circleMenu.getVisibility()==View.INVISIBLE && circleMenuCamera.getVisibility()!=View.VISIBLE){
//                    imgBlackPattern.setVisibility(View.VISIBLE);
//                    circleMenu.setVisibility(View.VISIBLE);
//                    circleMenu.openMenu();
//
//                }


               // circleMenu.setVisibility(View.VISIBLE);
                // circleMenu.openMenu();
            }
        });
    }



    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.overflowmenu,menu);

//        if(menu instanceof MenuBuilder){
//            MenuBuilder m = (MenuBuilder) menu;
//            //noinspection RestrictedApi
//            m.setOptionalIconsVisible(true);
//        }


        MenuItem item = menu.getItem(0);
        SpannableString s = new SpannableString("Fiş Ekle");
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
        item.setTitle(s);

        item = menu.getItem(1);
        s = new SpannableString("Manuel Giriş");
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
        item.setTitle(s);

//        item = menu.getItem(2);
//         s = new SpannableString("Mesafe");
//        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
//        item.setTitle(s);

        item = menu.getItem(2);
        s = new SpannableString("Rapor Oluştur");
        s.setSpan(new ForegroundColorSpan(Color.BLACK),0,s.length(),0);
        item.setTitle(s);

        return true;

    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){
                    Log.e("MenuHomepage", "onMenuOpened", e);
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            return true;

        }
        switch (item.getItemId()){
            case R.id.newReport:{
                postReport();
                break;
            }
//            case R.id.distance:{
//                startActivity(new Intent(getApplicationContext(), DistanceActivity.class));
//                HomePage.this.finish();
//                break;
//            }
            case R.id.manuelAdd:{
                startActivity(new Intent(getApplicationContext(),ManuelInvoice.class));
                HomePage.this.finish();
                break;
            }
            case R.id.load :{
                Intent intent = new Intent(getApplicationContext(),CameraActivity.class);
                intent.putExtra("token",token);
                startActivity(intent);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    public void postReport(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText editText = new EditText(getApplicationContext());
        editText.setText("Yeni Rapor");
        alert.setMessage("Oluşturacağınız raporlar, fişleri gruplandırmanıza olanak sağlar.");
        alert.setTitle("Yeni Rapor Ekle");

        editText.requestFocus();
        final InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        alert.setView(editText,70,0,80,0);



        alert.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                imm.toggleSoftInputFromWindow(editText.getWindowToken(), 0,0);
                if (editText.getText().toString().length()==0)
                    Toast.makeText(getApplicationContext(),"Rapor adı giriniz.",Toast.LENGTH_SHORT).show();
                else
                {
                    postReport(editText.getText().toString());
                }

            }
        }).setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
            }
        });

        alert.show();
        editText.selectAll();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                                //Toast.makeText(getApplicationContext(),"Rapor Ekleme başarılı, Gönderilmemiş Raporlar menüsünden faturalarınızı ekleyebilirsiniz.",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(),ReportUnSubmittedInvoices.class);
                                intent.putExtra("Id",jsonObject.getString("returnId"));
                                intent.putExtra("Name","deneme");
                                intent.putExtra("TotalAmount","0");
                                startActivity(intent);


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


        VolleyClass.getInstance(HomePage.this).addToRequestQueue(stringRequest);

    }

    public void setCircleMenus(){

    }






    private  boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    private void getInvoices(final String token, final Context context) {

        builder = new AlertDialog.Builder(HomePage.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getInvoiceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Invoice weather_data[];

                        try {
                            final JSONArray jsonArray = new JSONArray(response);
                            List<String> array= new ArrayList<>();

                            if (jsonArray.length()>0){
                                list.setVisibility(View.VISIBLE);
                                weather_data = new Invoice[jsonArray.length()];

                                String category;
                                String date;
                                RequestBuilder<Bitmap> bMapInvoice;
                                GlideUrl glideUrl;
                                String[] dateParts;

                                for (int  i = 0 ; i < jsonArray.length() ; i++){

                                    bMapInvoice = getInvoiceBitmap(jsonArray.getJSONObject(i).getString("Id"));
                                    glideUrl = getInvoiceGlideUrl(jsonArray.getJSONObject(i).getString("Id"));
                                    date = jsonArray.getJSONObject(i).getString("Date");
                                    dateParts = date.split("T");

                                    array.add("Fatura Id :" + jsonArray.getJSONObject(i).getString("Id"));
                                    weather_data[i] = new Invoice(R.drawable.ulogomini,
                                            jsonArray.getJSONObject(i).getString("Name"),"₺"+jsonArray.getJSONObject(i).getString("Amount"),"Yemek",
                                            bMapInvoice,
                                            glideUrl,
                                            dateParts[0]);

                                }


                                InvoiceAdapter adapter = new InvoiceAdapter(context, R.layout.listview_item_row, weather_data);



                                list.setAdapter(adapter);

                            }
                            else{
                               // Toast.makeText(getApplicationContext(),"Hiç eklenmiş fatura bulunmamakta, Fatura ekleyiniz",Toast.LENGTH_LONG).show();
                                imgNoInvoice.setImageDrawable(getDrawable(noinvoice));

                            }

                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    try {
                                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                        Intent intent = new Intent(getApplicationContext(),ChangeableInvoice.class);
                                        intent.putExtra("token",token);
                                        intent.putExtra("Id",jsonObject.getString("Id"));
                                        startActivity(intent);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = (JSONObject) jsonArray.get(index);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    switch (index) {
                                        case 0:
                                            try {
                                                deleteInvoiceAlert(jsonObject.getString("Id"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case 1:
                                            // delete
                                            break;
                                    }
                                    // false : close the menu; true : not close the menu
                                    return false;
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Toast.makeText(getApplicationContext(),"uzun bastın",Toast.LENGTH_SHORT).show();

                                return true;
                            }
                        });


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
        };

        VolleyClass.getInstance(HomePage.this).addToRequestQueue(stringRequest);


    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setStatusBarColor(int color){
        Window window = HomePage.this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(HomePage.this,color));

    }

    public void deleteInvoiceAlert(final String invoiceID){
        android.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.app.AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog);
        } else {
            builder = new android.app.AlertDialog.Builder(context);
        }
        builder.setTitle("Fişi Sil ? ")
                .setMessage("Seçmiş olduğunuz fişi silmek istediğinizden emin misiniz ?")
                .setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteInvoice(invoiceID);
                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.drawable.iconerror)
                .show();


    }

    @Override
    public void onBackPressed() {
        builder.setTitle("Çıkış");
        builder.setMessage("Çıkmak istediğine emin misin ?");
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearPrefs();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                HomePage.this.finish();

            }
        }).setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.myReports){
            startActivity(new Intent(getApplicationContext(),AllReportsNotSended.class));
            HomePage.this.finish();
        }
//        else if (id == R.id.resultedReports){
//            startActivity(new Intent(getApplicationContext(), ResultedReportsConfirmed.class));
//            HomePage.this.finish();
//        }
        else if (id==R.id.support){
            startActivity(new Intent(getApplicationContext(),SupportActivity.class));
        }
//        else if (id==R.id.settings){
//
//        }
        else if (id == R.id.logout){
            onBackPressed();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return  true;
    }

    public void clearPrefs(){
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().clear().commit();

    }

    public RequestBuilder<Bitmap> getInvoiceBitmap(String invoiceId) throws ExecutionException, InterruptedException {

        GlideUrl glideUrl = new GlideUrl(getInvoicePhotoUrl+invoiceId, new LazyHeaders.Builder()
                .addHeader("Authorization","Bearer"+" "+token)
                .build());


        RequestBuilder<Bitmap> theBitmap = Glide.with(this)
                .asBitmap()
                .load(glideUrl);



        return theBitmap;
    }

    public GlideUrl getInvoiceGlideUrl(String invoiceId){
        GlideUrl glideUrl = new GlideUrl(getInvoicePhotoUrl+invoiceId, new LazyHeaders.Builder()
                .addHeader("Authorization","Bearer"+" "+token)
                .build());

        return glideUrl;
    }

    public void saveUserInfos(final TextView txtName, final TextView txtBrandName){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getUSerInfosUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String nameLastName = jsonObject.getString("Name")+" "+jsonObject.getString("Lastname");
                            String brandName = jsonObject.getString("Brand");

                            if(nameLastName != null && !nameLastName.trim().isEmpty()){
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("nameLastName", nameLastName).apply();

                            }
                            if (brandName != null && !brandName.trim().isEmpty()){
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("brandName", brandName).apply();
                            }


                            if (nameLastName.compareTo("null") == 0){
                                txtName.setText("Ad Soyad");
                            }
                            else{
                                txtName.setText(nameLastName);
                            }

                            if (brandName.compareTo("null") == 0){
                                txtBrandName.setText("Şirket bilgisi bulunamadı");
                                txtBrandName.setTextSize(12);
                            }
                            else {
                                txtBrandName.setText(brandName);
                            }

                            Log.e("userInfos",nameLastName+" "+brandName);

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
        };

        VolleyClass.getInstance(HomePage.this).addToRequestQueue(stringRequest);
    }


    public void deleteInvoice (String invoiceID){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, deleteInvoiceUrl + invoiceID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        overridePendingTransition(0,0);
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
        };

        VolleyClass.getInstance(HomePage.this).addToRequestQueue(stringRequest);


    }



}
