package com.main.gyorsgyros.loginActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.main.gyorsgyros.R;
import com.main.gyorsgyros.mainActivity.MainActivity;
import com.main.gyorsgyros.services.DatabaseHandler;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private Messenger mService = null;
    boolean bound;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button login = findViewById(R.id.login);
        Button register = findViewById(R.id.register);
        Intent intent = getIntent();
        if(intent.hasExtra("fail")){
            final Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
            login.startAnimation(animShake);
            register.startAnimation(animShake);
        } else {
            email.setAlpha(0f);
            password.setAlpha(0f);
            email.setTranslationY(50);
            password.setTranslationY(50);

            email.animate().alpha(1f).translationYBy(-50).setDuration(1500);
            password.animate().alpha(1f).translationYBy(-50).setDuration(1500);
            login.animate().rotation(360).setDuration(1500);
            register.animate().rotation(360).setDuration(1500);
        }
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

    public void onRegister(View view) {
        Intent register = new Intent(this, MainActivity.class);
        startActivity(register);
        finish();
    }

    public void onLogin(View view) {
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Message msg = Message.obtain(null, DatabaseHandler.MSG_SIGN_IN, 0, 0);
        if (isEmpty(email) || isEmpty(password)){
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
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 1000);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}