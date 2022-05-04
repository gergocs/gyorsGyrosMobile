package com.main.gyorsgyros.userActivity;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.main.gyorsgyros.R;
import com.main.gyorsgyros.models.Order;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final ArrayList<Order> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView items;
        private final TextView price;
        private final TextView date;
        public ViewHolder(View view) {
            super(view);
            this.items = view.findViewById(R.id.orderItems);
            this.price = view.findViewById(R.id.price);
            this.date = view.findViewById(R.id.orderDate);
        }

        public TextView getItems() {
            return items;
        }

        public TextView getPrice() {
            return price;
        }

        public TextView getDate() {
            return date;
        }
    }

    public UserAdapter(ArrayList<Order> order) {
        this.localDataSet = order;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order, viewGroup, false);
        return new UserAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.getPrice().setText(Integer.toString(localDataSet.get(position).getPrice()));
        viewHolder.getDate().setText(localDataSet.get(position).getDate());
        viewHolder.getItems().setText(localDataSet.get(position).getItems());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
