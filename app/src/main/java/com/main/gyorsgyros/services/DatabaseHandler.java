package com.main.gyorsgyros.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.main.gyorsgyros.cartActivity.CartActivity;
import com.main.gyorsgyros.homePageActivity.HomePageActivity;
import com.main.gyorsgyros.loginActivity.LoginActivity;
import com.main.gyorsgyros.models.Cart;
import com.main.gyorsgyros.models.Food;
import com.main.gyorsgyros.models.Order;

import java.text.SimpleDateFormat;
import java.util.*;

import static android.content.ContentValues.TAG;

public class DatabaseHandler extends Service {
    public static final int MSG_NEW_USER = 1;
    public static final int MSG_SIGN_IN = 2;
    public static final int MSG_GET_FOODS = 3;
    public static final int MSG_ADD_CART = 4;
    public static final int MSG_GET_CART = 5;
    public static final int MSG_REMOVE_CART = 6;
    public static final int MSG_GET_ORDERS = 7;
    public static final int MSG_UPDATE_ADDRESS = 8;
    public static final int MSG_GET_ADDRESS = 9;
    public static final int MSG_GET_PRICE = 10;
    public static final int MSG_CREATE_ORDER = 11;
    public static final int MSG_SING_OUT = 12;

    private static Gson gson;
    int startMode;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    public DatabaseHandler() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    @SuppressLint("HandlerLeak")
    private final class ServiceHandler extends Handler {
        private final Context applicationContext;

        ServiceHandler(Context context) {
            applicationContext = context.getApplicationContext();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NEW_USER: {
                    ArrayList<String> data = msg.getData().getStringArrayList("user");
                    mAuth.createUserWithEmailAndPassword(data.get(0), data.get(1))
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Map<String, Object> address = new HashMap<>();
                                    address.put("address", "");
                                    address.put("uid", getUserID());
                                    db.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).set(address).addOnSuccessListener(unused -> {
                                        Intent intent = new Intent(applicationContext, LoginActivity.class);
                                        startActivity(intent);
                                    });
                                }
                            });
                    break;
                }
                case MSG_SIGN_IN: {
                    ArrayList<String> data = msg.getData().getStringArrayList("user");
                    mAuth.signInWithEmailAndPassword(data.get(0), data.get(1))
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(applicationContext, HomePageActivity.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(applicationContext, LoginActivity.class);
                                    intent.putExtra("fail", true);
                                    startActivity(intent);
                                }
                            });
                }
                case MSG_GET_FOODS: {
                    db.collection("food")
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    List<Food> foods = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        foods.add(new Food(Objects.requireNonNull(document.getData().get("name")).toString(), (Long) document.getData().get("price"), Objects.requireNonNull(document.getData().get("image")).toString(), new ArrayList<>()));
                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putString("key", "foods");
                                    bundle.putString("data", getGsonParser().toJson(foods));
                                    DatabaseHandler.this.sendMessage(bundle, "food");
                                }
                            });
                    break;
                }
                case MSG_ADD_CART: {
                    ArrayList<String> data = msg.getData().getStringArrayList("cart");
                    Map<String, Object> cart = new HashMap<>();
                    cart.put("cart", String.join(", ", data.get(data.size() - 1)));
                    cart.put("uid", getUserID());
                    db.collection("cart").document(Objects.requireNonNull(getUserID()))
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult().get("cart") != null) {
                                    cart.put("cart", cart.get("cart") + ", " + Objects.requireNonNull(task.getResult().get("cart")));
                                    db.collection("cart").document(Objects.requireNonNull(getUserID()))
                                            .update(cart);
                                } else {
                                    db.collection("cart").document(Objects.requireNonNull(getUserID()))
                                            .set(cart);
                                }
                            });
                    break;
                }
                case MSG_GET_CART: {
                    db.collection("cart")
                            .whereEqualTo("uid", Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Cart cart = null;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String[] str = {};
                                        if (!Objects.requireNonNull(document.getData().get("cart")).toString().equals("")) {
                                            str = Objects.requireNonNull(document.getData().get("cart")).toString().split(",");
                                        }

                                        cart = new Cart(new ArrayList<>(Arrays.asList(str)), mAuth.getCurrentUser().getUid());
                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putString("key", "cart");
                                    bundle.putString("data", getGsonParser().toJson(cart == null ? new ArrayList<String>() : cart.getItems()));
                                    DatabaseHandler.this.sendMessage(bundle, "cart");
                                }
                            });
                    break;
                }
                case MSG_REMOVE_CART: {
                    ArrayList<String> data = msg.getData().getStringArrayList("cart");
                    Map<String, Object> cart = new HashMap<>();
                    cart.put("cart", String.join(", ", data));
                    cart.put("uid", getUserID());
                    db.collection("cart").document(Objects.requireNonNull(getUserID()))
                            .set(cart)
                            .addOnSuccessListener(aVoid -> {
                                Intent intent = new Intent(applicationContext, CartActivity.class);
                                startActivity(intent);
                            });
                    break;
                }
                case MSG_GET_ORDERS: {
                    db.collection("order")
                            .whereEqualTo("uid", Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                            .orderBy("date", Query.Direction.DESCENDING)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    ArrayList<Order> orders = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        orders.add(new Order(Objects.requireNonNull(document.getData().get("address")).toString(),
                                                newFormat.format(Long.parseLong(Objects.requireNonNull(document.getData().get("date")).toString())), Objects.requireNonNull(document.getData().get("items")).toString(),
                                                Objects.requireNonNull(document.getData().get("payment")).toString(),
                                                Objects.requireNonNull(document.getData().get("phone")).toString(),
                                                Integer.parseInt(Objects.requireNonNull(document.getData().get("price")).toString()),
                                                Objects.requireNonNull(document.getData().get("uid")).toString()));
                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putString("key", "order");
                                    bundle.putString("data", getGsonParser().toJson(orders));
                                    DatabaseHandler.this.sendMessage(bundle, "order");
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("key", "order");
                                    bundle.putString("data", getGsonParser().toJson(new ArrayList<>()));
                                    DatabaseHandler.this.sendMessage(bundle, "order");
                                }
                            });
                    break;
                }
                case MSG_UPDATE_ADDRESS: {
                    String data = msg.getData().getString("address");
                    Map<String, Object> address = new HashMap<>();
                    address.put("address", data);
                    address.put("uid", getUserID());
                    db.collection("users").document(Objects.requireNonNull(getUserID()))
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult().get("address") != null) {
                                    db.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).update(address).addOnSuccessListener(unused -> {
                                        Intent intent = new Intent(applicationContext, HomePageActivity.class);
                                        startActivity(intent);
                                    });
                                } else {
                                    db.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).set(address).addOnSuccessListener(unused -> {
                                        Intent intent = new Intent(applicationContext, HomePageActivity.class);
                                        startActivity(intent);
                                    });
                                }
                            });
                    break;
                }
                case MSG_GET_ADDRESS: {
                    db.collection("users").whereEqualTo("uid", Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.exists()) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("key", "checkout");
                                            bundle.putString("data", Objects.requireNonNull(document.getData().get("address")).toString());
                                            DatabaseHandler.this.sendMessage(bundle, "checkout");
                                        }
                                    }
                                }
                            });
                    break;
                }
                case MSG_GET_PRICE: {
                    db.collection("food").whereIn("name", msg.getData().getStringArrayList("cart"))
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    long price = 0;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //noinspection ConstantConditions
                                        price += (Long) document.getData().get("price");
                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putString("key", "price");
                                    bundle.putLong("data", price);
                                    DatabaseHandler.this.sendMessage(bundle, "price");
                                }
                            });
                    break;
                }
                case MSG_CREATE_ORDER: {
                    ArrayList<String> tmp = msg.getData().getStringArrayList("order");
                    Map<String, Object> order = new HashMap<>();
                    order.put("address", tmp.get(0));
                    order.put("date", Long.parseLong(tmp.get(1)));
                    order.put("items", tmp.get(2));
                    order.put("payment", tmp.get(3));
                    order.put("phone", tmp.get(4));
                    order.put("price", Long.parseLong(tmp.get(5)));
                    order.put("uid", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                    db.collection("order").document()
                            .set(order)
                            .addOnSuccessListener(aVoid -> db.collection("cart").document(mAuth.getCurrentUser().getUid())
                                    .delete()
                                    .addOnSuccessListener(aVoid1 -> {
                                        Intent intent = new Intent(applicationContext, HomePageActivity.class);
                                        intent.putExtra("order", true);
                                        intent.putExtra("items", tmp.get(2));
                                        startActivity(intent);
                                    }));
                    break;
                }
                case MSG_SING_OUT: {
                    mAuth.signOut();
                    break;
                }
                default:
                    super.handleMessage(msg);
            }
            stopSelf(msg.arg1);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
        return startMode;
    }

    Messenger mMessenger;

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        mMessenger = new Messenger(new ServiceHandler(this));
        return mMessenger.getBinder();
    }

    private void sendMessage(Bundle bundle, String name) {
        Intent intent = new Intent(name);
        intent.putExtra("data", bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public static Gson getGsonParser() {
        if (null == gson) {
            GsonBuilder builder = new GsonBuilder();
            gson = builder.create();
        }
        return gson;
    }

    private String getUserID() {
        return Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    }
}