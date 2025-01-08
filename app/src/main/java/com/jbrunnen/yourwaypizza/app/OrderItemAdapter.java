package com.jbrunnen.yourwaypizza.app;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
    This Adapter class connects the datamodel for ordersItems with the recycler design component
    it connects each row to the data.  It extends recycler view which manages the other functions
    but through interfaces I can use this code to alter the behaviour of the recycler - like
    what it does when an item is clicked
 */

public class OrderItemAdapter {
// URL for the database https://console.firebase.google.com/u/0/project/yourwaypizza-44f90/database/yourwaypizza-44f90-default-rtdb/data

    private DatabaseReference databaseReference;

    public void orderAdapter(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://yourwaypizza-44f90-default-rtdb.europe-west1.firebasedatabase.app");
        databaseReference = database.getReference("orders");
    }

    public Task<Void> add(String globalOrderKey, OrderItemModel orderItem) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://yourwaypizza-44f90-default-rtdb.europe-west1.firebasedatabase.app");
        databaseReference = database.getReference("orders/" + globalOrderKey + "/orderItems");
        JBrunnenTesting.testDataLog("Customise011", "orderAdapter", "tostring for the passed orderModel: "+ orderItem.toString(), "");
        return databaseReference.push().setValue(orderItem);
    }

}
