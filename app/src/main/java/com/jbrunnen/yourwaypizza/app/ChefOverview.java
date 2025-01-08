package com.jbrunnen.yourwaypizza.app;

import static com.jbrunnen.yourwaypizza.app.JBrunnenTesting.isDeviceTablet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.yourwaypizzaparlourapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
/*
    This activity is the Chef View activity.
    This displays all orders that don;t have a completed or a unconfirmed status in a recyclerview
    Clicking on an item changes the activity to the Bill activity
 */
public class ChefOverview extends AppCompatActivity {

    OrderAdapter adapterNew;
    OrderDatabaseAdapter orderDatabase;
    Decoration decoration = new Decoration(10);
    RecyclerView recyclerViewNew;

    static ArrayList<OrderModel> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_overview);
        JBrunnenTesting.testDataLog("Main004", "ChefOverview", "", "on create in ChefOverview");

        // connect design objects with code
        recyclerViewNew = findViewById(R.id.recyclerviewnew);

        // set up the recycler view
        recyclerViewNew.setHasFixedSize(true);
        recyclerViewNew.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerViewNew.addItemDecoration(decoration);

        // connect the design component withe the adapter for orders
        adapterNew = new OrderAdapter(this);
        // connect the design with the data
        recyclerViewNew.setAdapter(adapterNew);


        //Usability01 change - change the layout of the recycler if this is a tablet
        if(isDeviceTablet()) {
            // change to two columns rather than one
            recyclerViewNew.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }

        // get a database connection
        orderDatabase = new OrderDatabaseAdapter();

        // load the data from the database
        loadDataNew();

        // this is the listener for clicking on a row
        adapterNew.setOnItemClickListener(new OrderAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                OrderModel currentOrder = orders.get(position);
                JBrunnenTesting.testDataLog("Chef00X", "ChefOverview", "order model:" + currentOrder.toString(), "testing to make sure the orderId is found and correct");
                activity_bill(currentOrder.getOrderId());
            }

            @Override
            public void onItemLongClick(int position, View v) {
                return;
            }
        });


    }

    // load the data from the online database
    private void loadDataNew () {
        orders.clear();
        // call the get data method and set up listners if the data changes or errors
        orderDatabase.getOrderData().addValueEventListener(new ValueEventListener() {
            @Override
            // using onDataChange here means that this happens when the data is changed
            // so if a new order is made (by someone else) the data will update
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // when the data comes back it starts with the key so we need to get the child records to get the data
                // there will be more than one order returned
      //        load the array each time, clear the array first
                orders.clear();
                // for each row - load up the arraylist
                for (DataSnapshot data : snapshot.getChildren()) {
                    // get the data from the database using the order model class
                    String orderId = data.child("orderId").getValue(String.class);
                    String orderStatus = data.child("orderStatus").getValue(String.class);
                    double orderTotalItemCost = data.child("orderTotalItemCost").getValue(double.class);
                    double orderVATCost = data.child("orderVATCost").getValue(double.class);
                    double orderOtherCost = data.child("orderOtherCost").getValue(double.class);
                    double orderTotal = data.child("orderTotal").getValue(double.class);
                    Long orderStartDateTime = data.child("orderStartDateTime").getValue(long.class);
                    Long orderCompleteDateTime = data.child("orderStartDateTime").getValue(long.class);
                    String orderSummary =  data.child("orderSummary").getValue(String.class);

                    OrderModel order = new OrderModel(orderId, orderStatus, orderTotalItemCost, orderVATCost, orderOtherCost, orderTotal, orderStartDateTime, orderCompleteDateTime, orderSummary);
                    JBrunnenTesting.testDataLog("ReadOrders001", "ChefOverView", "order about to be added:" + order, "testing order from database matches class defintion");
                     if (!Objects.equals(orderStatus, "unconfirmed")) {
                        if(!Objects.equals(orderStatus,"Completed")) {
                            orders.add(order);
                        }
                    }
                }

                // put the data in the array into the design object
                adapterNew.setItems(orders);
                // let the recycler know data has chanegd
                adapterNew.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        JBrunnenTesting.testDataLog("Chef006", "ChefOverview", "", "before back button pressed to go to main activity");
        startActivity(intent);
        finish();
    }

    private void activity_bill(String globalOrderKey) {
        Intent intent = new Intent(this, Bill.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("globalOrderKey", globalOrderKey);
        // send through the activity calling the bill - so we can change what back and the buttons do
        intent.putExtra("previousActivity", "ChefOverview");
        // change to order status from unconfirmed to "New"
        JBrunnenTesting.testDataLog("Chef>Bill", "ChefOverview", "globalOrderKey: " + globalOrderKey, "before click to bill");
        startActivity(intent);
        finish();
    }

}