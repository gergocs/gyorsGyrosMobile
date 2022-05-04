package com.main.gyorsgyros.homePageActivity;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.main.gyorsgyros.R;
import com.main.gyorsgyros.models.Cart;
import com.main.gyorsgyros.models.Food;
import com.main.gyorsgyros.services.DatabaseHandler;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private final List<Food> localDataSet;
    private final Cart cart = new Cart();
    private final Messenger mService;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView ingredients;
        private final Button button;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            ingredients = view.findViewById(R.id.ingredients);
            button = view.findViewById(R.id.addToCart);
        }

        public TextView getName() {
            return name;
        }

        public TextView getIngredients() {
            return ingredients;
        }

        public Button getButton() {
            return button;
        }
    }

    public FoodAdapter(List<Food> foods, Messenger mService) {
        this.localDataSet = foods;
        this.mService = mService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.food, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getName().setText(localDataSet.get(position).getName());
        viewHolder.getIngredients().setText("A,B,C,D");
        viewHolder.getButton().setOnClickListener(view -> {
            Message msg = Message.obtain(null, DatabaseHandler.MSG_ADD_CART, 0, 0);
            cart.addItem(localDataSet.get(viewHolder.getAdapterPosition()).getName());
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("cart", cart.getItems());
            msg.setData(bundle);
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
