package com.jbrunnen.yourwaypizza.app;

import static com.jbrunnen.yourwaypizza.app.JBrunnenTesting.isDeviceTablet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
// import android.widget.Button;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.yourwaypizzaparlourapp.R;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.Objects;

public class Customise extends AppCompatActivity {
    // get the global variable for the last item clicked
    int localcurrentItemId = ViewMenu.globalcurrentItemId;
    String globalOrderKey;

    MenuItemModel mmenuItemModel = ViewMenu.dmenuItems.get(localcurrentItemId);
    TextView textItemId, textItemName, textItemHasToppings, textItemSummary;
    ImageView imageItemPicture;
    Button buttonCustomiseSummary;
    RadioGroup radioSize;
    RadioButton radioSmall, radioMedium, radioLarge;

    // set up recycler for toppings
    RecyclerView tRecyclerView;
    // create an arraylist for toppings because we are loading them from the program
    static ArrayList<ToppingModel> topping = new ArrayList<>();

    /* override the back button so it goes back to the ViewMenu but destroys the ViewMenu to save memory  */
    @Override
    public void onBackPressed() {
        activateViewScreen();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the order key from the parameters sent by ViewMenu
        globalOrderKey = getIntent().getStringExtra("globalOrderKey");

        // initialise the database
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_customise);
        JBrunnenTesting.testDataLog("View003", "Customise", "localCurrentId:" + localcurrentItemId, "after click to customise");
        JBrunnenTesting.testDataLog("Cust001", "Customise", "localCurrentId:" + localcurrentItemId, "global menu position received ok?");

        // connect the design objects with the code
        textItemName = findViewById(R.id.textItemName);
        textItemSummary = findViewById(R.id.textItemSummary);
        imageItemPicture = findViewById(R.id.imageItemPicture);
        buttonCustomiseSummary = findViewById(R.id.buttonCustomiseSummary);

        radioSize = findViewById(R.id.radioSize);
        radioSmall = findViewById(R.id.radioSmall);
        radioMedium = findViewById(R.id.radioMedium);
        radioLarge = findViewById(R.id.radioLarge);

        textItemName.setText(mmenuItemModel.getName());
        textItemSummary.setText(mmenuItemModel.getSummary());
        tRecyclerView = findViewById(R.id.recyclerviewToppings);

        // get the image for the current menu item
        new GetImageFromURL(imageItemPicture).execute(mmenuItemModel.getPicture());

        // check if the item is a side, if the item is a side make the size menu and
        // the list GONE (which means invisible but also not taking up any layout space
        if (mmenuItemModel.getType().equals("Side")) {
            JBrunnenTesting.testDataLog("Cust002,Cust006", "Customise", "", "no size and topping options for this item");
            radioSize.setVisibility(View.GONE);
            tRecyclerView.setVisibility(View.GONE);
        }

        // add topping choices - only add this data if the array is empty
        if (topping.isEmpty()) {
            topping.add(new ToppingModel(1, "Cheese", 0.85, 1, false));
            topping.add(new ToppingModel(2, "Mushrooms", 0.85, 2, false));
            topping.add(new ToppingModel(3, "Meatballs", 0.85, 3, false));
            topping.add(new ToppingModel(4, "Sweetcorn", 0.85, 4, false));
            topping.add(new ToppingModel(5, "Black olives", 0.95, 5, false));
            topping.add(new ToppingModel(6, "Pepperoni", 0.95, 6, false));
            topping.add(new ToppingModel(7, "Ham", 0.95, 6, false));
            topping.add(new ToppingModel(8, "Green peppers", 0.95, 6, false));
            topping.add(new ToppingModel(9, "Pineapple", 0.95, 6, false));
            topping.add(new ToppingModel(10, "Ground beef", 0.95, 6, false));
// erroneous data - commented out test data
            //        topping.add(new ToppingModel(13, "", 0.95, 6, false));
// extreme data - commented out test data
            //        topping.add(new ToppingModel(10, "Extreme very long topping name which is really really long", 0.95, 6, false));
            //        topping.add(new ToppingModel(11, "Extreme price 0", 0.0, 6, false));
            //        topping.add(new ToppingModel(12, "Extreme price 99999.99", 99999.99, 6, false));
        }

        // configure the recycler and connect the data with the design component adapter
        Decoration decoration = new Decoration(10);
        ToppingAdapter adapter = new ToppingAdapter(topping);
        tRecyclerView.setAdapter(adapter);
        tRecyclerView.addItemDecoration(decoration);
        tRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // if the screen is big enough display the toppings in two columns
        if(isDeviceTablet()) {
            tRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }

        // make sure that the button at the bottom shows the cost for the item when going into the screen
        buttonCustomiseSummary.setText("current cost £" + String.format("%.2f", (doubleRound(adapter.gettotalToppingCost() + getMenuItemSizeCost(localcurrentItemId)))) + "\nADD TO ORDER?");

        // listner for the Size radio buttons, if any change then update the text of the button at the bottom
        radioSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // if the button is checked update the cost
                buttonCustomiseSummary.setText("current cost £" + String.format("%.2f", (doubleRound(adapter.gettotalToppingCost() + getMenuItemSizeCost(localcurrentItemId)))) + "\nADD TO ORDER?");

            }
        });

        // listener for click on a topping - this should check (or uncheck) the box
        // add the topping to a list of checked topping or remove it
        // recalculate the cost displayed in the button at the bottom
        adapter.setOnItemClickListener(new ToppingAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                ToppingModel currentTopping = topping.get(position);

                // this fixes the issue found in functional testing
                // short pause to fix the problem with the data not being there because this code happens first
                new CountDownTimer(500, 50) {
                    @Override
                    public void onTick(long arg0) {

                    }
                    @Override
                    public void onFinish() {
                        // update the button at the bottom of the screen with the new cost
                        JBrunnenTesting.testDataLog("Customise008", "Customise", "toppingClicked: " + currentTopping.toString(), "toppingClicked");
                        double customiseToppingCost = 0.0;
                        buttonCustomiseSummary.setText("current cost £" + String.format("%.2f", (doubleRound(adapter.gettotalToppingCost() + getMenuItemSizeCost(localcurrentItemId)))) + "\nADD TO ORDER?");
                        JBrunnenTesting.testDataLog("Customise003 005abc, 008", "Customise", "total customised cost: " + customiseToppingCost, "total matches options chosen");

                    }
                }.start();
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }

        });

        OrderItemAdapter orderData = new OrderItemAdapter();

        // listener for the Add to order button
        // this adds the orderItem to the current order in the database if it is clicked
        View.OnClickListener buttonCustomiseClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create a model of the order
                OrderItemModel orderLine = new OrderItemModel(globalOrderKey, mmenuItemModel.getName(), doubleRound((adapter.gettotalToppingCost() + getMenuItemSizeCost(localcurrentItemId))), getMenuItemSize(), 1, adapter.getCheckedToppingsSummary(), mmenuItemModel.getType());
                orderData.add(globalOrderKey,orderLine).addOnSuccessListener(suc -> {
                    JBrunnenTesting.testDataLog("Customise011", "Customise", "Database update success", "");
                }).addOnFailureListener(er -> {
                    JBrunnenTesting.testDataLog("Customise011", "Customise", "Database update failure", "");
                });

                // move back to ViewMenu and close this activity
                activateViewScreen();
            }
        };

        buttonCustomiseSummary.setOnClickListener(buttonCustomiseClickListener);

    }


    //  gets the cost for the menu item passes menu item and the size you want the cost for
    //  for now pizzas are the same cost SML and sides are the same cost
    public double getMenuItemSizeCost(int menuItemId) {
        double menuItemCost = mmenuItemModel.getCost();
        // if there are no changes then the cost is the orginal cost
        double menuItemSizeCost = menuItemCost;
        String selectedSizeText = getMenuItemSize();
        String menuItemType = mmenuItemModel.getType();
        if (Objects.equals(menuItemType, "Pizza")) {
            if (Objects.equals(selectedSizeText, "Small")) {
                // if it is small then no change to the base price
                menuItemSizeCost = doubleRound(menuItemCost)  ;
            } else if (Objects.equals(selectedSizeText, "Medium")) {
                // if it is medium then base price multiplied by 1.2
                menuItemSizeCost = doubleRound((menuItemCost * 1.4));
            } else if (Objects.equals(selectedSizeText, "Large")) {
                menuItemSizeCost = doubleRound((menuItemCost * 1.8));
            }
        }
        JBrunnenTesting.testDataLog("Customise004a 005abc", "Customise", "sized based cost: " + menuItemSizeCost, "");
        return menuItemSizeCost;
    }

    public String getMenuItemSize() {
        // work out the menubutton size from the radio buttons
        RadioButton radioHolder;
        int selectedSize = radioSize.getCheckedRadioButtonId();
        radioHolder = findViewById(selectedSize);
        String selectedSizeText = (String) radioHolder.getText();
        return selectedSizeText;
    }

    public void activateViewScreen() {
        Intent intent = new Intent(this, ViewMenu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("globalOrderKey", globalOrderKey);
        JBrunnenTesting.testDataLog("ViewMenu004", "Customise", "", "before back button pressed to go to ViewMenu");
        JBrunnenTesting.testDataLog("Customise012","Customise","","before orderbutton pressed to go to ViewMenu");
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


}



