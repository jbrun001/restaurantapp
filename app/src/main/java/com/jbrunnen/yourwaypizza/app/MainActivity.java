package com.jbrunnen.yourwaypizza.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.yourwaypizzaparlourapp.R;

import java.util.Objects;
/*
    this is the Start order use case. it is the first Activity that appears when the app is run
    This activity has two functions.
        create new order in the database (and get the databasekey) then pass this to ViewMenu
        check the password entered and if correct to to ChefOverview
*/
public class MainActivity extends AppCompatActivity {

    String globalOrderKey;
    Button buttonNewOrder, buttonChefOverview;
    EditText editTextPassword;
    ImageView imageBackground;

    // get a connection to the database
    OrderDatabaseAdapter orderDatabaseAction = new OrderDatabaseAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // connect the design objects to the code
        imageBackground = findViewById(R.id.imageBackground);
        buttonNewOrder = findViewById(R.id.buttonNewOrder);
        buttonChefOverview = findViewById(R.id.buttonChefOverview);

        // get the background image
        new GetImageFromURL(imageBackground).execute("https://images.unsplash.com/photo-1558138838-76294be30005?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1887&q=80");
        JBrunnenTesting.testDataLog("Main004,BillView003", "MainActivity", "", "after back in ViewMenu and exit in Bill");

        // listener for the NewOrder button
        View.OnClickListener buttonNewOrderClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 activity_view_menuStart();
            }
        };
        buttonNewOrder.setOnClickListener(buttonNewOrderClickListener);

        // listener for the Submit password button
        View.OnClickListener buttonChefOverviewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the password and check that is is correct
                editTextPassword = findViewById(R.id.editPassword);
                String password = String.valueOf(editTextPassword.getText());
                JBrunnenTesting.testDataLog("Chef001,Chef002", "MainActivity", "password tried: " + password, "submit password clicked");
                if (Objects.equals(password,"chop")) activity_view_ChefOverview();
            }
        };
        buttonChefOverview.setOnClickListener(buttonChefOverviewClickListener);

    }

    private void activity_view_menuStart() {
        // create a new order in the database and remember the key
        globalOrderKey = orderDatabaseAction.createNewOrderDatabase();
        // set the orderId to the globalOrderKey
        orderDatabaseAction.updateOrderId(globalOrderKey);
        JBrunnenTesting.testDataLog("Main005", "MainActivity", "new key for new record: " + globalOrderKey, "new order created check database");
        JBrunnenTesting.testDataLog("Main003", "MainActivity", "", "before open ViewMenu");
        Intent intent = new Intent(this, ViewMenu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("globalOrderKey", globalOrderKey);
        JBrunnenTesting.testDataLog("Main006", "MainActivity", globalOrderKey, "order database key");
        startActivity(intent);
        finish();
    }

    private void activity_view_ChefOverview() {
        JBrunnenTesting.testDataLog("Main00x", "MainActivity", "", "before open ChefOverview");
        Intent intent = new Intent(this, ChefOverview.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


}