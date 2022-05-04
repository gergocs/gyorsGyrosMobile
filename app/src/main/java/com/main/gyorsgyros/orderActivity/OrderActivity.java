package com.main.gyorsgyros.orderActivity;

import android.annotation.SuppressLint;
import android.content.*;
import android.os.*;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.main.gyorsgyros.R;
import com.main.gyorsgyros.homePageActivity.HomePageActivity;
import com.main.gyorsgyros.services.DatabaseHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private List<String> cart;
    private EditText city;
    private EditText street;
    private EditText house;
    private String payment;
    private long price;

    private Messenger mService = null;
    private boolean bound;

    private final BroadcastReceiver messageReceiverCard = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra("data");
            cart = Arrays.asList(DatabaseHandler.getGsonParser().fromJson(bundle.getString("data"), String[].class));
            bundle.putStringArrayList("cart", new ArrayList<>(cart));
            Message msg = Message.obtain(null, DatabaseHandler.MSG_GET_PRICE, 0, 0);
            try{
                msg.setData(bundle);
                mService.send(msg);
            }catch (Exception ignored){

            }
        }
    };

    private final BroadcastReceiver messageReceiverOrder = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra("data");
            String[] tmp = bundle.getString("data").split(", ");
            if (tmp.length == 3){
                city.setHint(tmp[0]);
                city.setText(tmp[0]);
                street.setHint(tmp[1]);
                street.setText(tmp[1]);
                house.setHint(tmp[2]);
                house.setText(tmp[2]);
            }
            Message msg = Message.obtain(null, DatabaseHandler.MSG_GET_CART, 0, 0);

            try{
                mService.send(msg);
            }catch (Exception ignored){

            }
        }
    };

    private final BroadcastReceiver messageReceiverPrice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra("data");
            price = bundle.getLong("data");
        }
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        payment = "card";

        cart = new ArrayList<>();
        city = findViewById(R.id.checkOutCity);
        street = findViewById(R.id.checkOutStreet);
        house = findViewById(R.id.checkOutHouseNumber);
        RadioGroup rg = findViewById(R.id.checkoutPaymentMethode);

        rg.setOnCheckedChangeListener((group, checkedId) -> {
            switch(checkedId){
                case R.id.card:
                    payment = "card";
                    break;
                case R.id.cash:
                    payment = "cash";
                    break;
            }
        });
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
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiverCard, new IntentFilter("cart"));
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiverOrder, new IntentFilter("checkout"));
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiverPrice, new IntentFilter("price"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiverCard);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiverOrder);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiverPrice);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onCheckOut(View view) {
        Date date = new Date();
        Message msg = Message.obtain(null, DatabaseHandler.MSG_CREATE_ORDER, 0, 0);
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add(city.getText().toString() + " " + street.getText().toString() + " " + house.getText().toString());
        tmp.add(Long.toString(date.getTime()));
        tmp.add(String.join(", ", cart));
        tmp.add(payment);
        tmp.add(((EditText)findViewById(R.id.checkOutPhone)).getText().toString());
        tmp.add(Long.toString(price));
        try {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("order", tmp);
            msg.setData(bundle);
            mService.send(msg);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 1000);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        finish();
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
}