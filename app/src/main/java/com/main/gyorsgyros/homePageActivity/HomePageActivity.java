package com.main.gyorsgyros.homePageActivity;

import android.app.NotificationManager;
import android.content.*;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.main.gyorsgyros.R;
import com.main.gyorsgyros.cartActivity.CartActivity;
import com.main.gyorsgyros.databinding.ActivityHomePageBinding;
import com.main.gyorsgyros.loginActivity.LoginActivity;
import com.main.gyorsgyros.models.Food;
import com.main.gyorsgyros.services.DatabaseHandler;
import com.main.gyorsgyros.userActivity.UserActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    List<Food> foods;

    private Messenger mService = null;
    boolean bound;

    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            bound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            bound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        aniFade.setStartOffset(1000);

        if(getIntent().getBooleanExtra("order", false)){
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.drawable.ic_baseline_done_24);
            mBuilder.setContentTitle("Your order is Done");
            mBuilder.setContentText(getIntent().getStringExtra("items"));
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }

        com.main.gyorsgyros.databinding.ActivityHomePageBinding binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        foods = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.items);
        recyclerView.startAnimation(aniFade);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        FoodAdapter adapter = new FoodAdapter(foods, mService);
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
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("food"));
    }

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra("data");
            foods = Arrays.asList(DatabaseHandler.getGsonParser().fromJson(bundle.getString("data"), Food[].class));
            RecyclerView recyclerView = findViewById(R.id.items);
            GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
            recyclerView.setLayoutManager(layoutManager);
            FoodAdapter adapter = new FoodAdapter(foods, mService);
            recyclerView.setAdapter(adapter);
        }
    };

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onPause();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            try{
                mService.send(Message.obtain(null, DatabaseHandler.MSG_GET_FOODS, 0, 0));
            }catch (Exception ignored){

            }
            Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
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
}