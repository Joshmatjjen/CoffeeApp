package com.example.jasky.coffeeapp;


import android.annotation.SuppressLint;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.*;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    int quantity;
    private Context context;
    Button btnCustomAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.space_in_top_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        context = this;
        btnCustomAlertDialog = (Button) findViewById(R.id.activity_main_btn_dialog);
        btnCustomAlertDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                EditText text = (EditText) findViewById(R.id.customer_name);
                String name = text.getText().toString();
                if (name.matches("")) {
                    Toast.makeText(context, "Name required", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    final Dialog dialog = new Dialog(context);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setContentView(R.layout.orderbox);
//                EditText text = (EditText) findViewById(R.id.customer_name);
//                String name = text.getText().toString();

                    TextView toptext = (TextView) dialog.findViewById(R.id.topview);
                    toptext.setText("\b\bHi\b " + name);

                    final CheckBox checking = (CheckBox) dialog.findViewById(R.id.end_summary);

                    Button btnContinue = (Button) dialog.findViewById(R.id.custom_dialog_btn_continue);
                    Button btnDone = (Button) dialog.findViewById(R.id.custom_dialog_btn_done);

                    btnDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (checking.isChecked()) {
                                dialog.dismiss();
                                submitOrder(v);
                            } else {
                                Toast.makeText(MainActivity.this, "Please Check the box", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    btnContinue.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                }
            }
        });
    }

    /**
     * Calculates the price of the order.
     *
     *@param addWhippedCream is whether or not the customer want Whipping Cream in his coffee
     *@param addChocolate is whether or not the customer want Chocolate in his coffee
     *@return total price
     */
    private int calculatePrice(boolean addWhippedCream, boolean addChocolate, boolean addSugar) {
        int pricepercup = 15;
        if(addWhippedCream){
            pricepercup += 35;
        }
        if(addChocolate){
            pricepercup += 20;
        }
        if(addSugar){
            pricepercup += 10;
        }
        int price = quantity * pricepercup;
        return price;
    }
    @SuppressLint("StringFormatInvalid")
    private String createOrderSummary(int price, String addcustomerName, boolean addwhippingcream, boolean addchocolate, boolean addsugar){
        @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) String orderSummary = getString(R.string.order_summary_name,addcustomerName);
        if(addwhippingcream==true) {
            orderSummary += "\n" + getString(R.string.order_summary_whipped_cream);
        }else{
            //do nothing
        }
        if (addchocolate==true) {
            orderSummary += "\n" + getString(R.string.order_summary_chocolate);
        }else {
            //do nothing
        }
        if (addsugar==true) {
            orderSummary += "\n" + getString(R.string.order_summary_sugar);
        }else {
            //do nothing
        }
        orderSummary += "\n"+ getString(R.string.order_summary_quantity,quantity);
        orderSummary += "\n"+ getString(R.string.order_summary_price,NumberFormat.getCurrencyInstance(new Locale("en","NG")).format(price));
        orderSummary += "\n\n"+ getString(R.string.thank_you);
        return orderSummary;
    }
    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {

        /*Edit Text Input*/

        EditText text = (EditText) findViewById(R.id.customer_name);
        String customerName = text.getText().toString();
        /*Whipping_Cream*/
        CheckBox whippingCream = (CheckBox) findViewById(R.id.whipping_cream);
        boolean hasWhippingCream = whippingCream.isChecked();

        /*Chocolate*/
        CheckBox chocolate = (CheckBox) findViewById(R.id.chocolate_checkbox);
        boolean hasChocolate = chocolate.isChecked();

        /*Sugar*/
        CheckBox sugar = (CheckBox) findViewById(R.id.sugar_checkbox);
        boolean hasSugar = sugar.isChecked();

        int price = calculatePrice(hasWhippingCream,hasChocolate,hasSugar);
        String priceMessage = createOrderSummary(price,customerName,hasWhippingCream,hasChocolate,hasSugar);
        composeEmail(customerName,priceMessage);
//        displayMessage(priceMessage);
    }

    public void composeEmail(String username, String pricemessage) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:joshmatparrot@gmail.com")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "JCoffee Order For "+ username);
        intent.putExtra(Intent.EXTRA_TEXT, pricemessage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void increment(View view){
        quantity = quantity + 1;
        if(quantity > 100){
            Toast.makeText(this, "You cannot have more than 100 coffee", Toast.LENGTH_SHORT).show();
            quantity = 100;
        }
        displayPrice(quantity *15);
        displayQuantity(quantity);
    }
    public void decrement(View view){
        quantity = quantity - 1;
        if(quantity < 1){
            Toast.makeText(this, "You cannot have less than 1 coffee", Toast.LENGTH_SHORT).show();
            quantity = 1;
        }
        displayPrice(quantity * 15);
        displayQuantity(quantity);
    }
    /**
     * This method displays the given quantity value on the screen.
     */
    private void displayQuantity(int number) {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        quantityTextView.setText("" + number);
    }
    /**
     * This method displays the given price on the screen.
     */

    private void displayPrice(int number) {
        TextView pricetextView = (TextView) findViewById(R.id.order_price);
        pricetextView.setText(NumberFormat.getCurrencyInstance(new Locale("en","NG")).format(number));
    }
    /**
     * This method displays a message when the order button is clicked on the screen.
     */

}
