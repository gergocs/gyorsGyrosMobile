package com.main.gyorsgyros.mainActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.main.gyorsgyros.R;
import com.main.gyorsgyros.loginActivity.LoginActivity;
import com.main.gyorsgyros.services.DatabaseHandler;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Messenger mService = null;
    boolean bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void onRegister(View view) {
        if (!bound){
            return;
        }
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        EditText passwordAgain = findViewById(R.id.passwordAgain);
        Message msg = Message.obtain(null, DatabaseHandler.MSG_NEW_USER, 0, 0);
        if ((isEmpty(email) || isEmpty(password) || isEmpty(passwordAgain)) && passwordAgain.getText().toString().equals(password.getText().toString())){
            return;
        }
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add(email.getText().toString());
        tmp.add(password.getText().toString());
        try {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("user", tmp);
            msg.setData(bundle);
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        finish();
    }

    public void onLogin(View view) {
        Intent register = new Intent(this, LoginActivity.class);
        startActivity(register);
        finish();
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}