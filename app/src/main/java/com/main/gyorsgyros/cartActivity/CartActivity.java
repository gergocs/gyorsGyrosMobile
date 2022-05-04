package com.main.gyorsgyros.cartActivity;

import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.main.gyorsgyros.R;
import com.main.gyorsgyros.homePageActivity.HomePageActivity;
import com.main.gyorsgyros.loginActivity.LoginActivity;
import com.main.gyorsgyros.models.Cart;
import com.main.gyorsgyros.orderActivity.OrderActivity;
import com.main.gyorsgyros.services.DatabaseHandler;
import com.main.gyorsgyros.userActivity.UserActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private List<String> cart;

    private Messenger mService = null;
    boolean bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cart = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.cart);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        CartAdapter adapter = new CartAdapter(new Cart(new ArrayList<>(cart)), mService);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, DatabaseHandler.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(mConnection);
            bound = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("cart"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onPause();
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            bound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            bound = false;
        }
    };

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra("data");
            cart = Arrays.asList(DatabaseHandler.getGsonParser().fromJson(bundle.getString("data"), String[].class));
            RecyclerView recyclerView = findViewById(R.id.cart);
            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
            recyclerView.setLayoutManager(layoutManager);
            CartAdapter adapter = new CartAdapter(new Cart(new ArrayList<>(cart)), mService);
            recyclerView.setAdapter(adapter);
            TextView emptyView = findViewById(R.id.empty_view);
            Button button = findViewById(R.id.payButton);
            if (cart.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        }
    };

    public void onHome(View view){
        try{
            mService.send(Message.obtain(null, DatabaseHandler.MSG_GET_FOODS, 0, 0));
        }catch (Exception ignored){

        }
        Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    public void onCart(View view){
        try{
            mService.send(Message.obtain(null, DatabaseHandler.MSG_GET_CART, 0, 0));
        }catch (Exception ignored){

        }
        Intent intent = new Intent(getApplicationContext(), CartActivity.class);
        startActivity(intent);
        finish();
    }

    public void onUser(View view){
        try{
            mService.send(Message.obtain(null, DatabaseHandler.MSG_GET_ORDERS, 0, 0));
        }catch (Exception ignored){

        }
        Intent intent = new Intent(getApplicationContext(), UserActivity.class);
        startActivity(intent);
        finish();
    }

    public void onExit(View view){
        try{
            mService.send(Message.obtain(null, DatabaseHandler.MSG_SING_OUT, 0, 0));
        }catch (Exception ignored){

        }
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onPay(View view) {
        try{
            mService.send(Message.obtain(null, DatabaseHandler.MSG_GET_ADDRESS, 0, 0));
        }catch (Exception ignored){

        }
        Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
        startActivity(intent);
        finish();
    }
}