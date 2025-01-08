package com.jbrunnen.yourwaypizza.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.example.yourwaypizzaparlourapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
/*
    This is the use case View Bill and Chef Update order this Activity is
    activated from the ViewMenu activity and also from the ChefOverView activity.
    This activity displays the bill for the databasekey that is passed as a parameter.
    It does this by creating a webpage to display the formatted information.
    This activity displays different buttons and has different behaviour if ViewMenu was the
    previous activity or if Chef Overview was the previous activity.  There is one Bill activity
    but it is used in two different situations to meet two different use cases
 */
public class Bill extends AppCompatActivity {
    String globalOrderKey;
    String previousActivity = "";


    TextView textViewBill;
    WebView webViewBill;
    Button buttonExit, buttonCollected, buttonReady, buttonPreparing;
    static String stringBill = "";
    static String htmlBill = "";
    OrderDatabaseAdapter orderDatabaseAction = new OrderDatabaseAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        // get the parameters from the last activity
        globalOrderKey = getIntent().getStringExtra("globalOrderKey");
        previousActivity = getIntent().getStringExtra("previousActivity");
        JBrunnenTesting.testDataLog("BillView001, BillChef001", "Bill", "globalorderkey: " + globalOrderKey + "previous activity: " + previousActivity, "after on create Bill");

        // connect the design objects with the code
        textViewBill = findViewById(R.id.textViewBill);
        webViewBill = findViewById(R.id.webViewBill);
        buttonExit = findViewById(R.id.buttonExit);
        buttonCollected = findViewById(R.id.buttonCollected);
        buttonReady = findViewById(R.id.buttonReady);
        buttonPreparing = findViewById(R.id.buttonPreparing);

        // only show the exit button if we came from the ViewMenu Activity
        // if we didn't we are comiong from the ChefOverview and we don't want to exit
        if (Objects.equals(previousActivity,"ViewMenu")) {
            buttonExit.setVisibility(View.VISIBLE);
            buttonCollected.setVisibility(View.GONE);
            buttonReady.setVisibility(View.GONE);
            buttonPreparing.setVisibility(View.GONE);
        } else {
            buttonExit.setVisibility(View.GONE);
            buttonCollected.setVisibility(View.VISIBLE);
            buttonReady.setVisibility(View.VISIBLE);
            buttonPreparing.setVisibility(View.VISIBLE);
        }
        // listener for buttonExit
        View.OnClickListener buttonExitClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JBrunnenTesting.testDataLog("Bill003", "Bill", "", "before back to main activity");
                activity_mainActivity();
            }
        };
        buttonExit.setOnClickListener(buttonExitClickListener);

        // listener for buttonCollected
        View.OnClickListener buttonCollectedClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JBrunnenTesting.testDataLog("BillChef005", "Bill", "", "collected button clicked");
                orderDatabaseAction.updateOrderStatus(globalOrderKey,"Completed");
                activity_chefOverview();
            }
        };
        buttonCollected.setOnClickListener(buttonCollectedClickListener);

        // listener for buttonReady
        View.OnClickListener buttonReadyClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JBrunnenTesting.testDataLog("BillChef004", "Bill", "", "ready button clicked");
                orderDatabaseAction.updateOrderStatus(globalOrderKey,"Ready");
                activity_chefOverview();
            }
        };
        buttonReady.setOnClickListener(buttonReadyClickListener);

        // listener for buttonPreparing
        View.OnClickListener buttonPreparingClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JBrunnenTesting.testDataLog("BillChef003", "Bill", "", "preparing button clicked");
                orderDatabaseAction.updateOrderStatus(globalOrderKey,"Preparing");
                activity_chefOverview();
            }
        };
        buttonPreparing.setOnClickListener(buttonPreparingClickListener);

        // output the current order
        // make the text view scrollable so if the order is very bit it can be viewed
        textViewBill.setMovementMethod(new ScrollingMovementMethod());
        // connect to the database
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderItemRef = rootRef.child("orders").child(globalOrderKey).child("orderItems");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // make the bill heading in HTML
                htmlBill ="<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "  <body>\n" +
                        "    <h2>Your Way Pizza Parlour</h2>\n" +
                        "    <h4>Bill for order number: " + globalOrderKey + " </h4></br>\n" +
                        "    <table width =\"100%\" border =\"0\">\n" +
                        "<!-- Header ends here-->\n";
                // loop through all orderItems for this database record - this is so one row
                // in the table can be created for each order item
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    double itemTotalCost = ds.child("itemTotalCost").getValue(Double.class);
                    String itemName = ds.child("itemName").getValue(String.class);
                    String itemSize = ds.child("itemSize").getValue(String.class);
                    String itemType = ds.child("itemType").getValue(String.class);
                    String itemToppingSummary = ds.child("itemToppingsSummary").getValue(String.class);

                    // add to the html for this orderItem row
                    htmlBill = htmlBill + "<!-- orderItemRow start here -->\n" +
                            "      <tr valign=\"top\">\n" +
                            "        <td align=\"left\">1x</td>\n        <td align=\"left\">";
                    // only Pizzas have sizes - sides and other items don't
                    if (Objects.equals(itemType, "Pizza")) {
                        // only show size for pizzas
                        htmlBill = htmlBill + itemSize + " ";
                    }
                    htmlBill = htmlBill + itemName;
                    if (!Objects.equals(itemToppingSummary, "")) {
                        // only show toppings if there are any
                        htmlBill = htmlBill  +"</br>\nWith extra " + itemToppingSummary;
                    }
                    htmlBill = htmlBill  + "        </td>\n" +
                            "        <td align=\"right\">£" + String.format("%.2f", doubleRound(itemTotalCost)) + "</td>\n" +
                            "      </tr>\n" +
                            "<!-- orderItemRow end here -->\n";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        // get the data - this is to write the end of the bill
        orderItemRef.addListenerForSingleValueEvent(eventListener);
        DatabaseReference orderRef = rootRef.child("orders").child(globalOrderKey);
        ValueEventListener orderEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double orderTotalCost = dataSnapshot.child("orderTotalItemCost").getValue(double.class);
                long orderStartDateTime = dataSnapshot.child("orderStartDateTime").getValue(long.class);
                String stringStartDateTime = getTimeDate(orderStartDateTime);
                JBrunnenTesting.testDataLog("OrderDatabase00X", "OrderDatabaseAdapter", "orderTotalCost: " + orderTotalCost, "getCurrentOrderCost");
                // Accuracy001 code change
                htmlBill = htmlBill + "<!-- orderSeparator start-->\n" +
                        "<tr valign=\"top\">\n" +
                        "        <td align=\"left\"></td>\n" +
                        "        <td align=\"left\"></td>\n" +
                        "        <td align=\"right\">========</td>\n" +
                        "      </tr>\n" +
                        "<!-- orderSeparator end-->\n" +
                        "<!-- ordertotalcosts start -->\n" +
                        "<tr valign=\"top\">\n" +
                        "        <td align=\"left\"></td>\n" +
                        "        <td align=\"right\">Order Cost</td>\n" +
                        "        <td align=\"right\">£" + String.format("%.2f", doubleRound(orderTotalCost)) + "</td>\n" +
                        "      </tr>\n" +
                        "<tr valign=\"top\">\n" +
                        "        <td align=\"left\"></td>\n" +
                        "        <td align=\"right\">VAT</td>\n" +
                        "        <td align=\"right\">£" + String.format("%.2f", doubleRound(doubleRound(orderTotalCost)*0.2)) + "</td>\n" +
                        "      </tr>\n" +
                        "<!-- orderSeparator start-->\n" +
                        "<tr valign=\"top\">\n" +
                        "        <td align=\"left\"></td>\n" +
                        "        <td align=\"left\"></td>\n" +
                        "        <td align=\"right\">========</td>\n" +
                        "      </tr>\n" +
                        "<!-- orderSeparator end-->\n" +
                        "\n" +
                        "<tr valign=\"top\">\n" +
                        "        <td align=\"left\"></td>\n" +
                        "        <td align=\"right\">Total Amount</td>\n" +
                        "        <td align=\"right\">£" + String.format("%.2f", doubleRound(orderTotalCost+doubleRound(orderTotalCost*0.2))) + "</td>\n" +
                        "      </tr>\n" +
                        "\n" +
                        "<tr valign=\"top\">\n" +
                        "        <td align=\"left\"></td>\n" +
                        "        <td align=\"left\"></td>\n" +
                        "        <td align=\"right\">========</td>\n" +
                        "      </tr>\n" +
                        "\n" +
                        "    </table>\n" +
                        "</br></br><h4>Order Date: " + stringStartDateTime + "" +
                        "<h4>" +
                        "  </body>\n" +
                        "</html>\n";
                orderDatabaseAction.updateOrderSummary(globalOrderKey,(doubleRound(orderTotalCost)+(doubleRound(orderTotalCost*0.2)) ), orderStartDateTime);
                String encodedHtml = Base64.encodeToString(htmlBill.getBytes(), Base64.NO_PADDING);
                webViewBill.loadData(encodedHtml, "text/html; charset=utf-8", "base64");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        orderRef.addListenerForSingleValueEvent(orderEventListener);
    }


    // on back go to Main activity or to Chef Overview
    public void onBackPressed() {
        if (Objects.equals(previousActivity,"ViewMenu")) {
            JBrunnenTesting.testDataLog("Bill004", "Bill", "", "before back to main activity");
            activity_mainActivity();

        } else {
            Intent intent = new Intent(this, ChefOverview.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            JBrunnenTesting.testDataLog("Bill00X", "Bill", "", "before back to ChefOverview");
            startActivity(intent);
            finish();
        }
    }

    private void activity_mainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        JBrunnenTesting.testDataLog("Bill00X", "Bill", "", "before click to main activity");
        startActivity(intent);
        finish();
    }

    private void activity_chefOverview() {
        Intent intent = new Intent(this, ChefOverview.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        JBrunnenTesting.testDataLog("Bill>Chef", "Bill", "", "before click to chefoverview");
        startActivity(intent);
        finish();
    }


    public double doubleRound(double numberToRound) {
        double roundedNumber = 0;
        numberToRound = numberToRound * 100;
        roundedNumber = Math.round(numberToRound);
        roundedNumber = roundedNumber / 100;
        return roundedNumber;
    }

    public static String getTimeDate(long timestamp){
        try{
            Date netDate = (new Date(timestamp));
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

}