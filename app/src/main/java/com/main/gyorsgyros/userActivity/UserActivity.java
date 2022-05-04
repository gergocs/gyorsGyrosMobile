package com.main.gyorsgyros.userActivity;

import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.main.gyorsgyros.R;
import com.main.gyorsgyros.cartActivity.CartActivity;
import com.main.gyorsgyros.homePageActivity.HomePageActivity;
import com.main.gyorsgyros.loginActivity.LoginActivity;
import com.main.gyorsgyros.models.Order;
import com.main.gyorsgyros.services.DatabaseHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private Messenger mService = null;
    boolean bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        RecyclerView recyclerView = findViewById(R.id.order);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        UserAdapter adapter = new UserAdapter(new ArrayList<>());
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
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("order"));
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
            List<Order> order = Arrays.asList(DatabaseHandler.getGsonParser().fromJson(bundle.getString("data"), Order[].class));
            RecyclerView recyclerView = findViewById(R.id.order);
            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
            recyclerView.setLayoutManager(layoutManager);
            UserAdapter adapter = new UserAdapter(new ArrayList<>(order));
            recyclerView.setAdapter(adapter);
            if (order.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    };

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            try{
                mService.send(Message.obtain(null, DatabaseHandler.MSG_GET_ORDERS, 0, 0));
            }catch (Exception ignored){

            }
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            startActivity(intent);
            finish();
        }
    }

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

    public void onChangeAddress(View view) {
        EditText city = findViewById(R.id.city);
        EditText street = findViewById(R.id.street);
        EditText houseNumber = findViewById(R.id.houseNumber);
        Message msg = Message.obtain(null, DatabaseHandler.MSG_UPDATE_ADDRESS, 0, 0);
        if (isEmpty(city) || isEmpty(street) || isEmpty(houseNumber)){
            return;
        }
        try {
            Bundle bundle = new Bundle();
            bundle.putString("address", city.getText().toString() + ", " + street.getText().toString() + ", " + houseNumber.getText().toString());
            msg.setData(bundle);
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}