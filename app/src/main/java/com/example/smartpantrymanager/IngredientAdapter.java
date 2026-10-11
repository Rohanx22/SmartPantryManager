package com.example.smartpantrymanager;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Custom RecyclerView.Adapter for displaying Pantry Ingredient items and handling
 * in-list deletion when the Delete Switch is enabled.
 * Modeled directly on ContactAdapter (Listings 6.4, 6.7, and 6.14) in the MAD700 Learner Guide.
 */
public class IngredientAdapter extends RecyclerView.Adapter {

    private ArrayList<Ingredient> ingredientData;
    private View.OnClickListener mOnItemClickListener;
    private boolean isDeleting;
    private Context parentContext;
    private boolean highlightExpiringSoon;

    public class IngredientViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewIngredientName;
        public TextView textViewQuantityUnit;
        public TextView textViewCategoryExpiry;
        public Button deleteButton;
        public ConstraintLayout cardContainer;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewIngredientName = itemView.findViewById(R.id.textIngredientName);
            textViewQuantityUnit = itemView.findViewById(R.id.textQuantityUnit);
            textViewCategoryExpiry = itemView.findViewById(R.id.textCategoryExpiry);
            deleteButton = itemView.findViewById(R.id.buttonDeleteIngredient);
            cardContainer = itemView.findViewById(R.id.cardIngredientItem);

            // Set tag to ViewHolder so Activity can identify which item was clicked (Listing 6.4)
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }

        public TextView getIngredientNameTextView() {
            return textViewIngredientName;
        }

        public TextView getQuantityUnitTextView() {
            return textViewQuantityUnit;
        }

        public TextView getCategoryExpiryTextView() {
            return textViewCategoryExpiry;
        }

        public Button getDeleteButton() {
            return deleteButton;
        }

        public ConstraintLayout getCardContainer() {
            return cardContainer;
        }
    }

    public IngredientAdapter(ArrayList<Ingredient> arrayList, Context context, boolean highlightExpiring) {
        ingredientData = arrayList;
        parentContext = context;
        highlightExpiringSoon = highlightExpiring;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ingredient, parent, false);
        return new IngredientViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        IngredientViewHolder ivh = (IngredientViewHolder) holder;
        Ingredient current = ingredientData.get(position);

        ivh.getIngredientNameTextView().setText(current.getIngredientName());
        ivh.getQuantityUnitTextView().setText(current.getFormattedQuantity());

        String formattedDate = DateFormat.format("dd/MM/yyyy", current.getExpiryDate()).toString();
        String metaText = "Category: " + current.getCategory() + "  |  Expires: " + formattedDate;

        // Check if expiring within 3 days when expiry alert preference is enabled
        long diffMillis = current.getExpiryDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        long daysLeft = diffMillis / (1000 * 60 * 60 * 24);
        if (highlightExpiringSoon && daysLeft <= 3) {
            metaText += " (Expiring Soon!)";
            ivh.getCategoryExpiryTextView().setTextColor(Color.parseColor("#D32F2F"));
            ivh.getCardContainer().setBackgroundColor(Color.parseColor("#FFF3E0"));
        } else {
            ivh.getCategoryExpiryTextView().setTextColor(Color.parseColor("#616161"));
            ivh.getCardContainer().setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        ivh.getCategoryExpiryTextView().setText(metaText);

        if (isDeleting) {
            ivh.getDeleteButton().setVisibility(View.VISIBLE);
            ivh.getDeleteButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteItem(holder.getAdapterPosition());
                }
            });
        } else {
            ivh.getDeleteButton().setVisibility(View.INVISIBLE);
        }
    }

    public void setDelete(boolean b) {
        isDeleting = b;
    }

    private void deleteItem(int position) {
        if (position < 0 || position >= ingredientData.size()) return;
        Ingredient item = ingredientData.get(position);
        PantryDataSource ds = new PantryDataSource(parentContext);
        try {
            ds.open();
            boolean didDelete = ds.deleteIngredient(item.getIngredientID());
            ds.close();
            if (didDelete) {
                ingredientData.remove(position);
                notifyDataSetChanged();
                Toast.makeText(parentContext, "Deleted " + item.getIngredientName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(parentContext, "Delete Failed!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(parentContext, "Delete Failed!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return ingredientData.size();
    }
}
