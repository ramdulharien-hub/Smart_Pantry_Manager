package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private ArrayList<PantryItem> pantryItems;

    public interface OnPantryItemListener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private OnPantryItemListener listener;

    public PantryAdapter(ArrayList<PantryItem> pantryItems,
                         OnPantryItemListener listener) {

        this.pantryItems = pantryItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position) {

        PantryItem item = pantryItems.get(position);

        holder.txtIngredientName.setText(item.getName());

        holder.txtIngredientQuantity.setText(
                item.getQuantity() + " " + item.getUnit()
        );

        if (item.getExpiryDate() == null ||
                item.getExpiryDate().isEmpty()) {

            holder.txtIngredientExpiry.setText(
                    "Expiry: Not specified"
            );

        } else {

            holder.txtIngredientExpiry.setText(
                    "Expiry: " + item.getExpiryDate()
            );
        }

        holder.btnEdit.setOnClickListener(v ->
                listener.onEdit(item)
        );

        holder.btnDelete.setOnClickListener(v ->
                listener.onDelete(item)
        );
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    public void updateList(ArrayList<PantryItem> newList) {

        pantryItems = newList;

        notifyDataSetChanged();
    }

    public static class PantryViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtIngredientName;
        TextView txtIngredientQuantity;
        TextView txtIngredientExpiry;

        Button btnEdit;
        Button btnDelete;

        public PantryViewHolder(@NonNull View itemView) {

            super(itemView);

            txtIngredientName =
                    itemView.findViewById(R.id.txtIngredientName);

            txtIngredientQuantity =
                    itemView.findViewById(R.id.txtIngredientQuantity);

            txtIngredientExpiry =
                    itemView.findViewById(R.id.txtIngredientExpiry);

            btnEdit =
                    itemView.findViewById(R.id.btnEdit);

            btnDelete =
                    itemView.findViewById(R.id.btnDelete);
        }
    }
}
