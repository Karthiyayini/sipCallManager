package com.example.kthiagarajan.linknycui;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.BreakIterator;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppBaseActivity {

    TextView edtPhoneNo;
    static Context context = null;
    static Intent intent = null;
    private final int REQUEST_USE_SIP = 1;
    public static String mCallStatus = null;

    public String sipAddress = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        edtPhoneNo = (TextView) findViewById(R.id.txtPhoneNumber);
        context = getApplicationContext();
        intent = getIntent();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                edtPhoneNo.setTransformationMethod(new HiddenPassTransformationMethod());
            }
        }, 2000);

        edtPhoneNo.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    private Timer timer = new Timer();
                    private final long DELAY = 3000; // milliseconds

                    @Override
                    public void afterTextChanged(final Editable s) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        // TODO: do what you need here (refresh list)
                                        // you will probably need to use runOnUiThread(Runnable action) for some specific actions
                                    }
                                },
                                DELAY
                        );
                    }
                }
        );

        initializeManager();
    }

    private void requestPermissions() {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.USE_SIP);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_SIP}, REQUEST_USE_SIP);
        } else {
        }
    }

    private class HiddenPassTransformationMethod extends PasswordTransformationMethod {

        private char DOT = '\u2022';

        @Override
        public CharSequence getTransformation(final CharSequence charSequence, final View view) {
            return new PassCharSequence(charSequence);
        }

        @Override
        public void onFocusChanged(final View view, final CharSequence charSequence, final boolean b, final int i,
                                   final Rect rect) {
            //nothing to do here
        }

        private class PassCharSequence implements CharSequence {

            private final CharSequence charSequence;

            public PassCharSequence(final CharSequence charSequence) {
                this.charSequence = charSequence;
            }

            @Override
            public char charAt(final int index) {
                return DOT;
            }

            @Override
            public int length() {
                return charSequence.length();
            }

            @Override
            public CharSequence subSequence(final int start, final int end) {
                return new PassCharSequence(charSequence.subSequence(start, end));
            }
        }
    }


    public void buttonClickEvent(View v) {
        String phoneNo = edtPhoneNo.getText().toString();
        try {

            switch (v.getId()) {
                case R.id.btnAterisk:
                    phoneNo += "*";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnHash:
                    phoneNo += "#";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnZero:
                    phoneNo += "0";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnOne:
                    phoneNo += "1";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnTwo:
                    phoneNo += "2";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnThree:
                    phoneNo += "3";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnFour:
                    phoneNo += "4";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnFive:
                    phoneNo += "5";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnSix:
                    phoneNo += "6";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnSeven:
                    phoneNo += "7";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnEight:
                    phoneNo += "8";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnNine:
                    phoneNo += "9";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnBackspace:
                    if (phoneNo != null && phoneNo.length() > 0) {
                        phoneNo = phoneNo.substring(0, phoneNo.length() - 1);
                    }
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnMakeCall:
                    if (phoneNo != null && phoneNo.length() > 0)
                    {
                        sipAddress = phoneNo.toString();
                        Intent myIntent = new Intent(context, InCallViewActivity.class);
                        Log.d("Text","Phone number"+sipAddress);
                        MySipManager.getInstance().setSipAddress(sipAddress);
                        MySipManager.getInstance().setIsMakeCall(true);
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myIntent);
                    }
                    else
                    {
                        showToast(getResources().getString(R.string.ErrValidPhoneNo));
                    }

                    break;
                case R.id.ivHidePassword:
                    edtPhoneNo.setTransformationMethod(null);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            edtPhoneNo.setTransformationMethod(new MainActivity.HiddenPassTransformationMethod());
                        }
                    }, 5000);
                    break;
            }

        } catch (Exception ex) {
            Log.d("MainActivity", "Failed to  Open local profile.", ex);

        }
    }

}







