package com.ulens.ulensapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ulens.ulensapp.Reports.ReportUnSubmittedInvoices;
import com.ulens.ulensapp.SupportClasses.VolleyClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceChoose extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String getInvoices = "http://ulensapp.com/api/Bill/GetInvoices?reportId=-1";
    String addToReport = "http://ulensapp.com/Api/Bill/AddInvoicesToReport";

    public class Item {
        boolean checked;
        Drawable ItemDrawable;
        String ItemString;
        Item(Drawable drawable, String t, boolean b){
            ItemDrawable = drawable;
            ItemString = t;
            checked = b;
        }

        public boolean isChecked(){
            return checked;
        }
    }

    static class ViewHolder {
        CheckBox checkBox;
        ImageView icon;
        TextView text;
    }

    public class ItemsListAdapter extends BaseAdapter {

        private Context context;
        private List<Item> list;

        ItemsListAdapter(Context c, List<Item> l) {
            context = c;
            list = l;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public boolean isChecked(int position) {
            return list.get(position).checked;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = convertView;

            // reuse views
            ViewHolder viewHolder = new ViewHolder();
            if (rowView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                rowView = inflater.inflate(R.layout.listitemrow_checkbox, null);

                viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.rowCheckBox);
                viewHolder.icon = (ImageView) rowView.findViewById(R.id.rowImageView);
                viewHolder.text = (TextView) rowView.findViewById(R.id.rowTextView);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }

            viewHolder.icon.setImageDrawable(list.get(position).ItemDrawable);
            viewHolder.checkBox.setChecked(list.get(position).checked);

            final String itemStr = list.get(position).ItemString;
            viewHolder.text.setText(itemStr);

            viewHolder.checkBox.setTag(position);

            /*
            viewHolder.checkBox.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    list.get(position).checked = b;

                    Toast.makeText(getApplicationContext(),
                            itemStr + "onCheckedChanged\nchecked: " + b,
                            Toast.LENGTH_LONG).show();
                }
            });
            */

            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean newState = !list.get(position).isChecked();
                    list.get(position).checked = newState;
                    //Toast.makeText(getApplicationContext(),
                        //    itemStr + "setOnClickListener\nchecked: " + newState,
                          //  Toast.LENGTH_LONG).show();
                }
            });

            viewHolder.checkBox.setChecked(isChecked(position));

            return rowView;
        }
    }


    List<Item> items = new ArrayList<Item>();
    ListView listView;
    ItemsListAdapter myItemsListAdapter;

    Context context = this;

    Button btnLookup;


    String token;
    String id;
    String name;
    String TotalAmount;

    TextView txtNoInvoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_choose);

        id = getIntent().getStringExtra("Id");
        name = getIntent().getStringExtra("Name");
        TotalAmount = getIntent().getStringExtra("TotalAmount");
        txtNoInvoice = findViewById(R.id.txtNoInvoiceChs);



        listView = (ListView)findViewById(R.id.lvChoose);

        btnLookup = findViewById(R.id.btnLookup);

        //initItems();

        getInvoices(context);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                Toast.makeText(InvoiceChoose.this,
//                        ((Item)(parent.getItemAtPosition(position))).ItemString,
//                        Toast.LENGTH_LONG).show();
            }});



        sharedPreferences= getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        token=sharedPreferences.getString("token","");



    }

    private void initItems(){

        TypedArray arrayDrawable = getResources().obtainTypedArray(R.array.resicon);
        TypedArray arrayText = getResources().obtainTypedArray(R.array.restext);

        for(int i=0; i<arrayDrawable.length(); i++){
            Drawable d = arrayDrawable.getDrawable(i);
            String s = arrayText.getString(i);
            boolean b = false;
            Item item = new Item(d, s, b);
            items.add(item);
        }

        arrayDrawable.recycle();
        arrayText.recycle();
    }


    public void getInvoices(final Context context){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getInvoices,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("responseInvoices",response);
                        try {
                            final JSONArray jsonArray = new JSONArray(response);

                            if (jsonArray.length()==0){
                                Log.e("jsonArray","boş");
                                txtNoInvoice.setVisibility(View.VISIBLE);
                            }

                            for (int i = 0; i < jsonArray.length(); i++){
                                Drawable d = getResources().getDrawable(R.drawable.ulogomini);
                                String s = jsonArray.getJSONObject(i).getString("Name");
                                boolean b = false;
                                Item item = new Item(d, s, b);
                                items.add(item);
                            }

                            myItemsListAdapter = new ItemsListAdapter(context, items);
                            listView.setAdapter(myItemsListAdapter);


                            btnLookup.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String str = "Check items:\n";

                                    for (int i=0; i<items.size(); i++){
                                        if (items.get(i).isChecked()){
                                            str += i + "\n";

                                            try {

                                                addToReport(jsonArray.getJSONObject(i).getString("Id"));


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                        }
                                    }

                                        Intent intent = new Intent(getApplicationContext(),ReportUnSubmittedInvoices.class);
                                        intent.putExtra("Id",id);
                                        intent.putExtra("Name",name);
                                        intent.putExtra("TotalAmount",TotalAmount);
                                        startActivity(intent);
                                        InvoiceChoose.this.finish();



                /*
                int cnt = myItemsListAdapter.getCount();
                for (int i=0; i<cnt; i++){
                    if(myItemsListAdapter.isChecked(i)){
                        str += i + "\n";
                    }
                }
                */

//                                   // Toast.makeText(InvoiceChoose.this,
//                                            str,
//                                            Toast.LENGTH_LONG).show();

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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","Bearer"+" "+token);

                return params;
            }
        };

        VolleyClass.getInstance(InvoiceChoose.this).addToRequestQueue(stringRequest);


    }

    public void addToReport(final String invoiceId){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, addToReport,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("reposne add",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);




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
                params.put("reportId",id);
                params.put("InvoiceIdList",invoiceId);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","Bearer"+" "+token);

                return params;
            }
        };

        VolleyClass.getInstance(InvoiceChoose.this).addToRequestQueue(stringRequest);




    }



}
