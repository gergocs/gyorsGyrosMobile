package com.main.gyorsgyros.cartActivity;

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
import com.main.gyorsgyros.services.DatabaseHandler;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private final Cart localDataSet;
    private final Messenger mService;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final Button button;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            button = view.findViewById(R.id.removeFromCart);
        }

        public TextView getName() {
            return name;
        }

        public Button getButton() {
            return button;
        }
    }

    public CartAdapter(Cart cart, Messenger mService) {
        this.localDataSet = cart;
        this.mService = mService;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart, viewGroup, false);
        return new CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.getName().setText(localDataSet.getItems().get(position));
        viewHolder.getButton().setOnClickListener(view -> {
            localDataSet.getItems().remove(viewHolder.getAdapterPosition());
            Message msg = Message.obtain(null, DatabaseHandler.MSG_REMOVE_CART, 0, 0);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("cart", localDataSet.getItems());
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
        return localDataSet.getItems().size();
    }
}
