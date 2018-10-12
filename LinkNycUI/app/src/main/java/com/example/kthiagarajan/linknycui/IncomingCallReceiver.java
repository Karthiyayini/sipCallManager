package com.example.kthiagarajan.linknycui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;

public class IncomingCallReceiver extends BroadcastReceiver {

//    public void onReceive(Context context, Intent intent) {
//        SipAudioCall incomingCall = null;
//        try {
//            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
//                @Override
//                public void onRinging(SipAudioCall call, SipProfile caller) {
//                    try {
//                        call.answerCall(30);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            MainActivity wtActivity = (MainActivity) context;
//            incomingCall = wtActivity.manager.takeAudioCall(intent, listener);
//            incomingCall.answerCall(30);
//            incomingCall.startAudio();
//            incomingCall.setSpeakerMode(true);
//            if(incomingCall.isMuted()) {
//                incomingCall.toggleMute();
//            }
//            wtActivity.call = incomingCall;
//            wtActivity.updateStatus(incomingCall);
//        } catch (Exception e) {
//            if (incomingCall != null) {
//                incomingCall.close();
//            }
//        }
//    }


    @Override
    public void onReceive(Context context, Intent intent) {

    final AppBaseActivity baseActivity = (AppBaseActivity) context;
    SipAudioCall incomingCall = null;

    SipAudioCall.Listener listener = new SipAudioCall.Listener(){
        @Override
        public void onRinging(SipAudioCall call, SipProfile caller) {
            try {
                call.answerCall(30);
            } catch (SipException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCalling(SipAudioCall call) {
            super.onCalling(call);
            baseActivity.updateStatus("Calling " + call.getPeerProfile().getUserName());
        }

        @Override
        public void onCallEnded(SipAudioCall call) {
            super.onCallEnded(call);
//            mainActivity.updateStatus(MainActivity.STATE_CONNECTED);
        }
    };

        try {
        incomingCall = baseActivity.manager.takeAudioCall(intent, listener);
    } catch (SipException e) {
        e.printStackTrace();
        return;
    }

    final SipAudioCall call = incomingCall;
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("New Call")
                .setMessage("From " + incomingCall.getPeerProfile().getUserName())
            .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            try {
                call.answerCall(30);
                call.startAudio();
                call.setSpeakerMode(true);
                if (call.isMuted()) call.toggleMute();
            } catch (SipException e) {
                e.printStackTrace();
            }
        }
    })
            .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            try {
                call.endCall();
                call.close();
            } catch (SipException e) {
                e.printStackTrace();
            }
        }
    });
        builder.create().show();
}
}
