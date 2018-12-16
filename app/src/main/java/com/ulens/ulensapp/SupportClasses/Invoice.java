package com.ulens.ulensapp.SupportClasses;

import android.graphics.Bitmap;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.model.GlideUrl;

public class Invoice {
    public int icon;
    public String title;
    public String amount="0";
    public String category;
    public RequestBuilder<Bitmap> bMapInvoice;
    public GlideUrl glideUrl;
    public String date;
    public Invoice(){
        super();
    }

    public Invoice(int icon, String title, String amount, String category, RequestBuilder<Bitmap> bMapInvoice,GlideUrl glideUrl,String date) {
        super();
        this.icon = icon;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.bMapInvoice = bMapInvoice;
        this.glideUrl = glideUrl;
        this.date = date;
    }
}