package com.example.kthiagarajan.linknycui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends Activity {

    Button mBtnLogin;
    TextInputEditText mEtUserId,mEtPassword,mEtDomain;
    String mUserId=null, mPassword, mDomain;

    private final int REQUEST_USE_SIP=1;
    private final int REQUEST_MICROPHONE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestPermissions();
        init();

    }

    private void init() {

        Fabric.with(this, new Crashlytics());
        logUser();

        mBtnLogin = (Button) findViewById(R.id.btnLogin);
        mEtUserId = (TextInputEditText) findViewById(R.id.etUserId);
        mEtPassword = (TextInputEditText) findViewById(R.id.etPassword);
        mEtDomain = (TextInputEditText) findViewById(R.id.etDomain);
        Boolean isLoggedIn = checkPrefSettings();
        if (isLoggedIn)
        {
            Intent myIntent = new Intent(this, MainActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);
        }
    }

    private void logUser() {
        Crashlytics.setUserIdentifier("12345");
        Crashlytics.setUserEmail("user@fabric.io");
        Crashlytics.setUserName("Test User");
    }


    private Boolean checkPrefSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String username = prefs.getString("namePref", "");
        String domain = prefs.getString("domainPref", "");
        String password = prefs.getString("passPref", "");
        if (username.length() == 0 || domain.length() == 0 || password.length() == 0) {
            return false;
        }
        else
        {
            return true;
        }
    }

    public void loginInitiated(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                {
                mUserId = mEtUserId.getText().toString();
                mPassword = mEtPassword.getText().toString();
                mDomain = mEtDomain.getText().toString();
                if (mUserId.length() != 0 || mPassword.length() != 0 || mDomain.length() != 0) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("namePref", mUserId.toString());
                    editor.putString("domainPref", mDomain.toString());
                    editor.putString("passPref", mPassword.toString());
                    editor.apply();
                    editor.commit();
                    Intent myIntent = new Intent(this, MainActivity.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                }
            }
                break;
            default:
                showToast(getResources().getString(R.string.defaultErrorMessage));
                break;
        }

    }

    private void requestPermissions() {

        String[] permissionList = new String[2];

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.USE_SIP)) != PackageManager.PERMISSION_GRANTED) {
            permissionList[0] = Manifest.permission.USE_SIP;
        }

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) != PackageManager.PERMISSION_GRANTED) {
            permissionList[1] = Manifest.permission.RECORD_AUDIO;
        }

        if (permissionList[0]!=null || permissionList[1]!=null) {
            ActivityCompat.requestPermissions(this, permissionList, REQUEST_USE_SIP);
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this,message.toString() ,Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.setBackgroundColor(Color.TRANSPARENT);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        text.setTextColor(Color.WHITE);
        toast.setGravity(Gravity.BOTTOM,0,140);
        text.setTextSize(Integer.valueOf("15"));
        toast.show();
    }
}
