package com.jbrunnen.yourwaypizza.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourwaypizzaparlourapp.R;

import java.util.ArrayList;

/*
    This Adapter class connects the datamodel for menu items with the recycler desgin component
    it connects each row to the data.  It extends recycler view which manages the other functions
    but through interfaces I can use this code to alter the behaviour of the recycler - like
    what it does when an item is clicked
 */

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ViewHolder> {
    private static ClickListener clickListener;
    ArrayList<MenuItemModel> data = new ArrayList<>();

    public MenuItemAdapter(ArrayList<MenuItemModel> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MenuItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemAdapter.ViewHolder holder, int position) {
        holder.name.setText(data.get(position).name);
        double menuItemCost = data.get(position).cost;
        String costSummary = "";
        if (data.get(position).type == "Pizza") {
            costSummary = "from ";
        }
        // make sure the double is formatted correctly
        costSummary = costSummary + "£" + String.format("%.2f", menuItemCost);
        holder.summary.setText(costSummary);
        new GetImageFromURL(holder.picture).execute(data.get(position).picture);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView name, summary;
        ImageView picture;
        RelativeLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.order_name);
            summary = itemView.findViewById(R.id.order_summary);
            picture = itemView.findViewById(R.id.item_picture);
            layout = itemView.findViewById(R.id.layout);
            // on click event for one item in the list and it returns the name
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            name = itemView.findViewById(R.id.order_name);
        }
        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            return false;
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        MenuItemAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }


}