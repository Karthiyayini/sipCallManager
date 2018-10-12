package com.example.kthiagarajan.linknycui;

import android.net.sip.SipAudioCall;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;

public class MySipManager {
    private static MySipManager ourInstance = null;
    private static SipManager manager= null;
    public static SipProfile me = null;
    public static SipAudioCall call = null;
    public static int callStatus = 0;
    public static String sipAddress = null;
    public static Boolean isMakeCall = false;

    public static MySipManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new MySipManager();
        }
        return(ourInstance);
    }

    private MySipManager() {}

    public void setSipManager(SipManager manager) {
        this.manager = manager;
    }
    public SipManager getSipManager() {
        return manager;
    }

    public void setSipProfile(SipProfile me) {
        this.me = me;
    }

    public SipProfile getSipProfile() {
        return me;
    }

    public void setSipAudioCall(SipAudioCall call) {
        this.call = call;   }

    public SipAudioCall getSipAudioCall() {
        return call;
    }

    public void setCallStatus(int callStatus) {
        this.callStatus = callStatus;
    }

    public int getCallStatus() {
        return callStatus;
    }

    public void setSipAddress(String sipAddress) {
        this.sipAddress = sipAddress;   }

    public String getSipAddress() {
        return sipAddress;
    }


    public void setIsMakeCall(Boolean isMakeCall) {
        this.isMakeCall = isMakeCall;   }

    public Boolean getIsMakeCall() {
        return isMakeCall;
    }
}
