package com.jbrunnen.yourwaypizza.app;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
/* this class manages the connection, create read and update for the online database
*/

public class OrderDatabaseAdapter {


    private final DatabaseReference orderRef;

    public OrderDatabaseAdapter() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        orderRef = database.getReference("orders");
    }

    public String createNewOrderDatabase(){
        // this creates a new blank order for the user to enter data into and returns the database key
        String databaseKey = "not set";
        OrderModel newOrder = new OrderModel("blank","unconfirmed" , 0, 0,0,0,0L,0L, "");
        // database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://yourwaypizza-44f90-default-rtdb.europe-west1.firebasedatabase.app");
        JBrunnenTesting.testDataLog("Main005", "ordersModel", "databaseKey: "+databaseKey, "");
        DatabaseReference ref = database.getReference("orders");
        //      DatabaseReference OrdersRef = ref.child("Orders");
        DatabaseReference newOrderRef = ref.push();
        newOrderRef.setValue(newOrder);
        databaseKey = newOrderRef.getKey();
        JBrunnenTesting.testDataLog("Main005", "ordersModel", "databaseKey: "+databaseKey, "");

        return databaseKey;
    }

    public void updateOrderId(String globalOrderKey) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderRef = rootRef.child("orders").child(globalOrderKey);
        Map<String, Object> orderUpdates = new HashMap<>();
        orderUpdates.put("orderId", globalOrderKey);
        orderRef.updateChildren(orderUpdates);
    }


    public void updateOrderCompleteDateTime(String globalOrderKey) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderRef = rootRef.child("orders").child(globalOrderKey);
        Map<String, Object> orderUpdates = new HashMap<>();
        orderUpdates.put("orderCompleteDateTime", ServerValue.TIMESTAMP);
        orderRef.updateChildren(orderUpdates);
    }


    public void updateOrderStartDateTime(String globalOrderKey) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderRef = rootRef.child("orders").child(globalOrderKey);
        Map<String, Object> orderUpdates = new HashMap<>();
        orderUpdates.put("orderStartDateTime", ServerValue.TIMESTAMP);
        orderRef.updateChildren(orderUpdates);
    }

    public void updateOrderSummary(String globalOrderKey, double orderTotal, long orderStartDateTime) {
        String formattedDate = "";
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderRef = rootRef.child("orders").child(globalOrderKey);
        Date netDate = (new Date(orderStartDateTime));
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MMM HH:mm:ss", Locale.getDefault());
        formattedDate = sfd.format(netDate);
        Map<String, Object> orderUpdates = new HashMap<>();
        orderUpdates.put("orderSummary", "£" + String.format("%.2f", doubleRound(orderTotal)) + " - " + formattedDate);
        orderRef.updateChildren(orderUpdates);
    }


    public void updateOrderVATCost(String globalOrderKey, double orderVATCost) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderRef = rootRef.child("orders").child(globalOrderKey);
        Map<String, Object> orderUpdates = new HashMap<>();
        orderUpdates.put("orderVATCost", doubleRound(orderVATCost));
        orderRef.updateChildren(orderUpdates);
    }

    public void updateOrderTotalCost(String globalOrderKey, double orderTotalCost) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderRef = rootRef.child("orders").child(globalOrderKey);
        Map<String, Object> orderUpdates = new HashMap<>();
        orderUpdates.put("orderTotal", doubleRound(orderTotalCost));
        orderRef.updateChildren(orderUpdates);
    }


    public void updateOrderTotalItemCost(String globalOrderKey, double orderTotalItemCost) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderRef = rootRef.child("orders").child(globalOrderKey);
        Map<String, Object> orderUpdates = new HashMap<>();
        orderUpdates.put("orderTotalItemCost", doubleRound(orderTotalItemCost));
        orderRef.updateChildren(orderUpdates);
    }

    public void updateOrderStatus(String globalOrderKey, String orderStatus) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderRef = rootRef.child("orders").child(globalOrderKey);
        Map<String, Object> orderUpdates = new HashMap<>();
        orderUpdates.put("orderStatus", orderStatus);
        orderRef.updateChildren(orderUpdates);
        // update the start and end date for the order when the order changes status
        if (Objects.equals(orderStatus, "New")) {
            updateOrderStartDateTime(globalOrderKey);
        }
        if (Objects.equals(orderStatus, "Completed")) {
            updateOrderCompleteDateTime(globalOrderKey);
        }
    }
    // this function rounds the double to two decimal places
    public double doubleRound(double numberToRound) {
        double roundedNumber = 0;
        numberToRound = numberToRound * 100;
        roundedNumber = Math.round(numberToRound);
        roundedNumber = roundedNumber / 100;
        return roundedNumber;
    }

    // this adds a new order to the database
    public Task<Void> add(OrderModel order) {
        return orderRef.push().setValue(order);
    }

    // this allows update of a child data i.e. {orderStatus: "New"
    public Task<Void> update(String globalOrderKey, HashMap<String ,Object> hashMap) {
        return orderRef.child(globalOrderKey).updateChildren(hashMap);
    }

    // this is queries the firebase database and gets records back
    // this method uses polymorphism overloading with no parameters it will get all data
    public Query getOrderData() {
        return orderRef.orderByKey();
    }
    // with one parameter it will get one record for the database key that is passed
    public Query getOrderData(String globalDatabaseKey) {
        return orderRef.orderByKey().equalTo(globalDatabaseKey);
    }

}
