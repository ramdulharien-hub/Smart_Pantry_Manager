package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private ArrayList<Recipe> recipes;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private OnRecipeClickListener listener;

    public RecipeAdapter(
            ArrayList<Recipe> recipes,
            OnRecipeClickListener listener) {

        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecipeViewHolder holder,
            int position) {

        Recipe recipe = recipes.get(position);

        holder.txtRecipeName.setText(
                recipe.getName()
        );

        holder.itemView.setOnClickListener(v ->
                listener.onRecipeClick(recipe)
        );
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class RecipeViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtRecipeName;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            txtRecipeName =
                    itemView.findViewById(
                            R.id.txtRecipeName
                    );
        }
    }
}
