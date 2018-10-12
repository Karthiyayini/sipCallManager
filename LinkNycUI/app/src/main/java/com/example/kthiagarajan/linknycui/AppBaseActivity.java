package com.example.kthiagarajan.linknycui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;

public class AppBaseActivity extends Activity {

    TextView edtPhoneNo;
    static Context baseContext = null;
    static Intent baseIntent = null;
    private final int REQUEST_USE_SIP=1;
    public static String mCallStatus = null;

    public static String sipAddress = null;
    public static SipManager manager = null;
    public static SipProfile me = null;
    public static SipAudioCall call = null;
    public static IncomingCallReceiver callReceiver;
    public static int CALL_NONE = 0;
    public static int CALL_CALLING = 1;
    public static int CALL_ESTABLISHED = 2;
    public static int CALL_ENDED = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate", "onCreate: "+MySipManager.getInstance().getIsMakeCall());
//        if (!MySipManager.getInstance().getIsMakeCall()) {

            IntentFilter filter = new IntentFilter();
            filter.addAction("android.Sip.INCOMING_CALL");
            callReceiver = new IncomingCallReceiver();
            this.registerReceiver(callReceiver, filter);
//        }
    }

    public void initializeManager() {
        if(manager == null) {
            manager = SipManager.newInstance(this);
        }
        initializeLocalProfile();
    }

    public void initializeLocalProfile() {
        if (manager == null) {
            return;
        }
        if (me != null) {
            closeLocalProfile();
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String username = prefs.getString("namePref", "");
        String domain = prefs.getString("domainPref", "");
        String password = prefs.getString("passPref", "");
        if (username.length() == 0 || domain.length() == 0 || password.length() == 0) {
            showToast(getResources().getString(R.string.ErrPrefSettings));
            return;
        }
        try {

            SipProfile.Builder builder = new SipProfile.Builder(username, domain);
            builder.setPassword(password);
            me = builder.build();
            Intent i = new Intent();
            i.setAction("android.Sip.INCOMING_CALL");
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, Intent.FILL_IN_DATA);
            manager.open(me, pi, null);
            // This listener must be added AFTER manager.open is called,
            // Otherwise the methods aren't guaranteed to fire.
            manager.setRegistrationListener(me.getUriString(), new SipRegistrationListener() {
                public void onRegistering(String localProfileUri) {
                    Log.d("MainActivity","Registering");
                    updateStatus("Registering with SIP Server");
                    MySipManager.getInstance().setSipManager(manager);
                    MySipManager.getInstance().setSipProfile(me);
                    MySipManager.getInstance().setSipAudioCall(call);
                    MySipManager.getInstance().setCallStatus(CALL_NONE);
                }
                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    Log.d("MainActivity","Ready");
                    updateStatus("Ready");
                }
                public void onRegistrationFailed(String localProfileUri, int errorCode,
                                                 String errorMessage) {
                    Log.d("MainActivity","Registration failed.");
                    updateStatus("Registration failed.  Please check settings.");
                }
            });
        } catch (ParseException pe) {
            updateStatus("Connection Error.");
        } catch (SipException se) {
            updateStatus("Connection error.");
        }
    }


    public void closeLocalProfile() {
        if (manager == null) {
            return;
        }
        try {
            if (me != null) {
                manager.close(me.getUriString());
            }
        } catch (Exception ee) {
            Log.d("MainActivity/onDestroy", "Failed to close local profile.", ee);
        }
    }

    public void updateStatus(final String status) {
        if (status == "Ready")
        {
            this.runOnUiThread(new Runnable() {
                public void run() {
                    showToast(getResources().getString(R.string.MsgSipReady));
//                            Intent myIntent = new Intent(context, InCallViewActivity.class);
//                            myIntent.putExtra("makeACallTo",sipAddress);
//                            MySipManager.getInstance().setSipManager(manager);
//                            MySipManager.getInstance().setSipProfile(me);
//                            MySipManager.getInstance().setSipAudioCall(call);
//                            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(myIntent);
                }
            });

        }
        else if (status == "Registering with SIP Server")
        {
            this.runOnUiThread(new Runnable() {
                public void run() {
                    showToast(getResources().getString(R.string.MsgSipRegistering));
                }
            });
        }
        else
        {
            this.runOnUiThread(new Runnable() {
                public void run() {
                    showToast(getResources().getString(R.string.MsgSipFailed));
                }
            });
//                    destroyOnCallEnd();
//                    Intent myIntend = new Intent(this,LoginActivity.class);
//                    myIntend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(myIntend);
        }
    }

    public void initiateCall() {
        sipAddress = MySipManager.getInstance().getSipAddress();
        showToast(sipAddress);

        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                // Much of the client's interaction with the SIP Stack will
                // happen via listeners.  Even making an outgoing call, don't
                // forget to set up a listener to set things up once the call is established.

                @Override
                public void onCalling(SipAudioCall call) {
                    if (!call.isMuted())
                        call.toggleMute();
                    MySipManager.getInstance().setCallStatus(CALL_CALLING);
                    updateStatus(call.getPeerProfile().getUserName());
                }

                @Override
                public void onCallEstablished(SipAudioCall call) {
                    Log.d(this.toString(),"CallEstablished");
                    MySipManager.getInstance().setCallStatus(CALL_ESTABLISHED);
                    call.startAudio();
                    call.setSpeakerMode(true);
                    call.toggleMute();
                    updateStatus(call);
                }

                @Override
                public void onCallEnded(SipAudioCall call) {
                    MySipManager.getInstance().setCallStatus(CALL_ENDED);
                    Log.d(this.toString(),"CallEnded");
                    showToast("Call Ended.");
                }
            };

            SipProfile sipTarget = (new SipProfile.Builder(sipAddress, "srv.myvtel.com")).build();
            call = manager.makeAudioCall(me.getUriString(), sipTarget.getUriString(), listener, 30);
        }
        catch (Exception e) {
            Log.i("InitiateCall", "Error when trying to close manager.", e);
            if (me != null) {
                try {
                    manager.close(me.getUriString());
                } catch (Exception ee) {
                    Log.i("InitiateCall",
                            "Error when trying to close manager.", ee);
                    ee.printStackTrace();
                }
            }
            if (call != null) {
                call.close();
            }
        }
    }

    public void updateStatus(SipAudioCall call) {
        String useName = call.getPeerProfile().getDisplayName();
        if(useName == null) {
            useName = call.getPeerProfile().getUserName();
        }
        Log.d("MainActivity", "updateStatus: "+useName);
        updateStatus(useName + "@" + call.getPeerProfile().getSipDomain());
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
//        destroyOnCallEnd();
    }

    public void destroyOnCallEnd()
    {
        if (call != null) {
            call.close();
        }
        closeLocalProfile();
        if (callReceiver != null) {
            this.unregisterReceiver(callReceiver);
        }
    }

    public void endIniatedCall() {
        if(call != null) {
            try {
                call.endCall();
            } catch (SipException se) {
                Log.d("onOptionsItemSelected",
                        "Error ending call.", se);
            }
            call.close();
        }
        destroyOnCallEnd();
    }

    public void showToast(String message) {
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


    public void muteCall() {

        if (call.isMuted())
        {
            showToast("Call UnMuted.");
        }
        else
        {
            showToast("Call Muted.");
        }
        call.toggleMute();
    }

    public void increaseVolume()
    {
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
    }

    public void decreaseVolume()
    {
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
    }


}