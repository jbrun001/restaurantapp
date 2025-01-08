package com.jbrunnen.yourwaypizza.app;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourwaypizzaparlourapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
    This Adapter class connects the datamodel for toppings with the recycler design component
    it connects each row to the data.  It extends recycler view which manages the other functions
    but through interfaces I can use this code to alter the behaviour of the recycler - like
    what it does when an item is clicked

    In this adapter there is a checkedToppings array list - this keeps track of what toppings the
    user has checked (and will remove them if the user unchecks them).  Checked toppings is not saved
    because only a summary of the toppings is needed for the order, this arraylist is also used
    when calculating the extra topping cost for the order
 */

public class ToppingAdapter extends RecyclerView.Adapter<ToppingAdapter.ViewHolder> {
    private static ClickListener clickListener;
    ArrayList<ToppingModel> toppings = new ArrayList<>();

    ArrayList<ToppingModel> checkedToppings=new ArrayList<>();

    public ToppingAdapter(ArrayList<ToppingModel> toppings) {
      this.toppings = toppings;
    }

    @NonNull
    @Override
    public ToppingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_toppings, null));
    }

    // connect the data with the design object row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ToppingModel topping = toppings.get(position);
        holder.toppingDescription.setText(topping.getDescription());
        holder.toppingCost.setText("+ £" + String.format("%.2f", topping.getCost()));
        holder.toppingCheckBox.setChecked(topping.getCheckbox());
    }

    @Override
    public int getItemCount() {
        return toppings.size();
    }

    // this gets a string with all the checked toppings in
    // this is what is saved in the orderItem and displayed in the bill activity
    public String getCheckedToppingsSummary() {
        String toppingsSummary = "";
        // if there are any toppings checked
        if(checkedToppings.size() > 0) {
            // loop though all the toppings
            for (int i = 0; i <= checkedToppings.size()-1; i++) {
                ToppingModel loopTopping = checkedToppings.get(i);
                if ( i > 0) {
                    // if we are not on the first topping add a comma after the last topping in the string
                    toppingsSummary = toppingsSummary + ", ";
                }
                toppingsSummary = toppingsSummary + loopTopping.getDescription();
            }

        }
        return toppingsSummary;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView toppingDescription, toppingCost;
        CheckBox toppingCheckBox;
        RelativeLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            toppingDescription= itemView.findViewById(R.id.toppingDescription);
            toppingCost = itemView.findViewById(R.id.toppingCost);
            toppingCheckBox = itemView.findViewById(R.id.toppingCheckBox);
            layout = itemView.findViewById(R.id.layout);

            // on click event for the item in the list
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        // when a user clicks the checkbox is checked or unchecked and the
        // currentToppings has the checked topping added or the unchecked topping removed
        // in the testing this code was running after the code that calculated the cost
        // and so the total displayed was not correct.  Putting a delay in the code in Customise
        // gave this code enough time to finish
        @Override
        public void onClick(View v) {
           clickListener.onItemClick(getAdapterPosition(), v);
           toppingCheckBox.performClick();
           ToppingModel currentTopping = toppings.get(getAdapterPosition());
           Log.d(TAG, "toppingAdapter onItemClick checkbox value: " + toppingCheckBox.isChecked());
           if (toppingCheckBox.isChecked()) {
               checkedToppings.add(currentTopping);
               JBrunnenTesting.testDataLog("Customise008", "toppingAdapter", "topping Checked: " + currentTopping.toString(), "Topping checked");
               Log.d(TAG, "toppingAdapter onItemClick added: " + currentTopping);
           } else if (!toppingCheckBox.isChecked()) {
               checkedToppings.remove(currentTopping);
               JBrunnenTesting.testDataLog("Customise009", "toppingAdapter", "topping UnChecked: " + currentTopping.toString(), "Topping unchecked");
           }
        }

        @Override
        public boolean onLongClick( View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            return false;
        }
    }

    // calculate the extra toppings cost by looping through all the checked toppings
    public double gettotalToppingCost() {
        double customiseToppingCost = 0.0;
        for (int i = 0; i <= checkedToppings.size()-1; i++) {
            ToppingModel loopTopping = checkedToppings.get(i);
            customiseToppingCost = customiseToppingCost + loopTopping.getCost();
            JBrunnenTesting.testDataLog("Customise008 009", "toppingAdapter", "all checkedToppings: " + loopTopping, "checkedToppings array contents");
        }
        JBrunnenTesting.testDataLog("Customise003 005abc, 008, 009", "toppingAdapter", "total customised cost: " + customiseToppingCost, "total matches options chosen");
        return customiseToppingCost;
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        ToppingAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    // this sorts the toppings by the order field in the data model
    public ArrayList<ToppingModel> sortByOrder(ArrayList<ToppingModel> data) {
        Collections.sort(data, new Comparator<ToppingModel>() {
            @Override
            public int compare(ToppingModel o1, ToppingModel o2) {
                if(o1.order>o2.order)
                    return 1;
                else if(o1.order<o2.order)
                    return -1;
                else return 0;
            }
        });
        return data;
    }
}