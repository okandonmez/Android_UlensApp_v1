package com.ulens.ulensapp.SupportClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.ulens.ulensapp.R;

public class InvoiceAdapter extends ArrayAdapter<Invoice> {

    Context context;
    int layoutResourceId;
    Invoice data[] = null;

    public InvoiceAdapter (Context context, int layoutResourceId, Invoice[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        InvoiceHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new InvoiceAdapter.InvoiceHolder();
            holder.imgIcon = row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtAmount = row.findViewById(R.id.txtAmountRow);
            holder.txtCategory = row.findViewById(R.id.txtCategory);
            holder.txtDate = row.findViewById(R.id.txtDate);



            row.setTag(holder);
        }
        else
        {
            holder = (InvoiceHolder)row.getTag();
        }

        final Invoice invoice = data[position];

        final InvoiceHolder finalHolder = holder;
        holder.imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.dialogimage, null);
                ImageView imageView = dialogView.findViewById(R.id.my_image);
                invoice.bMapInvoice.load(invoice.glideUrl).into(imageView);



                builder.setView(dialogView)
                        .setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                Dialog dialog = builder.create();
                dialog.show();

                dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            }
        });

        holder.txtTitle.setText(invoice.title);
        invoice.bMapInvoice.load(invoice.glideUrl).into(holder.imgIcon);
        holder.txtAmount.setText(invoice.amount);
        holder.txtCategory.setText(invoice.category);
        holder.txtDate.setText(invoice.date);



        return row;
    }

    static class InvoiceHolder
    {
        CircularImageView imgIcon;
        TextView txtTitle;
        TextView txtAmount;
        TextView txtCategory;
        TextView txtDate;
    }
}