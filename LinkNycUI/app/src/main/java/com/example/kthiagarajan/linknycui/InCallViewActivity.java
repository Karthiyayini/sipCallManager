package com.example.kthiagarajan.linknycui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class InCallViewActivity extends AppBaseActivity {

    TextView mEdtPhoneNo;
    ProgressBar mVolumeBar;
    int mProgressValue;
    Button mBtnEndCall,mBtnHideKeypad,mKeyOne,mKeyTwo,
            mKeyThree,mKeyFour,mKeyFive,mKeySix,mKeySeven,mKeyEight,mKeyNine,
            mKeyZero,mKeyAterisk,mKeyHash;
    ImageView mIvBackSpace,mIvShowNumber;
    int nCallStatus=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_call_view);
            init();
    }

    private void init() {
        mVolumeBar = (ProgressBar) findViewById(R.id.pbVolumeBar);
        nCallStatus = MySipManager.getInstance().getCallStatus();
        if (nCallStatus == 0)
        {
            initiateCall();
        }
    }

    private class HiddenPassTransformationMethod implements TransformationMethod {

        private char DOT = '\u2022';

        @Override
        public CharSequence getTransformation(final CharSequence charSequence, final View view) {
            return new InCallViewActivity.HiddenPassTransformationMethod.PassCharSequence(charSequence);
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
                return new InCallViewActivity.HiddenPassTransformationMethod.PassCharSequence(charSequence.subSequence(start, end));
            }
        }
    }

    public void initiateCallEnd()
    {
        MySipManager.getInstance().setIsMakeCall(false);
        endIniatedCall();
        Intent myIntend = new Intent(this,MainActivity.class);
        myIntend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myIntend);
    }

    public void inCallClickEvent(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivKeypad:
                    final Dialog dialog = new Dialog(v.getContext(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    dialog.setContentView(R.layout.dialog_keypad);
                    initValues(dialog);
                    setClickListener(dialog);

                    mBtnEndCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            initiateCallEnd();
                        }
                    });
                    mBtnHideKeypad.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    break;

                case R.id.ivMinusVolume:
                    mProgressValue = mVolumeBar.getProgress();
                    if (mProgressValue<=0)
                    {
                        break;
                    }
                    else {
                        decreaseVolume();
                        mVolumeBar.setProgress(mProgressValue - 1);
                        break;
                    }

                case R.id.ivPlusVolume:
                    mProgressValue = mVolumeBar.getProgress();
                    if (mProgressValue >= 10)
                    {
                        break;
                    }
                    else
                    {
                        increaseVolume();
                        mVolumeBar.setProgress(mProgressValue+1);
                        break;
                    }

                case R.id.ivMuteCall:
                    muteCall();
                    break;

                case R.id.ivEndCall:
                    initiateCallEnd();
                    break;
            }
        } catch (Exception ex) {
        }
    }




    public void initValues(Dialog dialog)
    {
        mEdtPhoneNo = (TextView) dialog.findViewById(R.id.txtPhoneNumber);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEdtPhoneNo.setTransformationMethod(new InCallViewActivity.HiddenPassTransformationMethod());
            }
        }, 2000);
        mBtnEndCall = (Button) dialog.findViewById(R.id.btnEndCall);
        mBtnHideKeypad = (Button) dialog.findViewById(R.id.btnHideKeypad);
        mIvBackSpace = (ImageView) dialog.findViewById(R.id.ivBackspace);
        mIvShowNumber = (ImageView)dialog.findViewById(R.id.ivShowNumber);
        mKeyOne = (Button) dialog.findViewById(R.id.btnOne);
        mKeyTwo = (Button) dialog.findViewById(R.id.btnTwo);
        mKeyThree = (Button) dialog.findViewById(R.id.btnThree);
        mKeyFour = (Button) dialog.findViewById(R.id.btnFour);
        mKeyFive = (Button) dialog.findViewById(R.id.btnFive);
        mKeySix = (Button) dialog.findViewById(R.id.btnSix);
        mKeySeven = (Button) dialog.findViewById(R.id.btnSeven);
        mKeyEight = (Button) dialog.findViewById(R.id.btnEight);
        mKeyNine = (Button) dialog.findViewById(R.id.btnNine);
        mKeyZero = (Button) dialog.findViewById(R.id.btnZero);
        mKeyAterisk = (Button) dialog.findViewById(R.id.btnAterisk);
        mKeyHash = (Button) dialog.findViewById(R.id.btnHash);
    }

    public void setClickListener (Dialog dialog)
    {
        mKeyOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction("mKeyOne");
            }
        });

        mKeyTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction("mKeyTwo");
            }
        });
        mKeyThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction("mKeyThree");
            }
        });
        mKeyFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction("mKeyFour");
            }
        });
        mKeyFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction("mKeyFive");
            }
        });
        mKeySix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction("mKeySix");
            }
        });
        mKeySeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction("mKeySeven");
            }
        });
        mKeyEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction("mKeyEight");
            }
        });
        mKeyNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction("mKeyNine");
            }
        });
        mKeyZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction("mKeyZero");
            }
        });
        mKeyHash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction("mKeyHash");
            }
        });
        mKeyAterisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction("mKeyAterisk");
            }
        });
        mKeyZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction("mKeyZero");
            }
        });
        mIvBackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction("mIvBackSpace");
            }
        });
        mIvShowNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFunction("mIvShowNumber");
            }
        });
    }

    private View.OnClickListener callFunction(String i) {
        String mPhoneNo = mEdtPhoneNo.getText().toString();
        try {
            switch (i) {
                case "mKeyAterisk":
                    mPhoneNo += "*";
                    mEdtPhoneNo.setText(mPhoneNo);
                    break;
                case "mKeyHash":
                    mPhoneNo += "#";
                    mEdtPhoneNo.setText(mPhoneNo);
                    break;
                case "mKeyZero":
                    mPhoneNo += "0";
                    mEdtPhoneNo.setText(mPhoneNo);
                    break;
                case "mKeyOne":
                    mPhoneNo += "1";
                    mEdtPhoneNo.setText(mPhoneNo);
                    break;
                case "mKeyTwo":
                    mPhoneNo += "2";
                    mEdtPhoneNo.setText(mPhoneNo);
                    break;
                case "mKeyThree":
                    mPhoneNo += "3";
                    mEdtPhoneNo.setText(mPhoneNo);
                    break;
                case "mKeyFour":
                    mPhoneNo += "4";
                    mEdtPhoneNo.setText(mPhoneNo);
                    break;
                case "mKeyFive":
                    mPhoneNo += "5";
                    mEdtPhoneNo.setText(mPhoneNo);
                    break;
                case "mKeySix":
                    mPhoneNo += "6";
                    mEdtPhoneNo.setText(mPhoneNo);
                    break;
                case "mKeySeven":
                    mPhoneNo += "7";
                    mEdtPhoneNo.setText(mPhoneNo);
                    break;
                case "mKeyEight":
                    mPhoneNo += "8";
                    mEdtPhoneNo.setText(mPhoneNo);
                    break;
                case "mKeyNine":
                    mPhoneNo += "9";
                    mEdtPhoneNo.setText(mPhoneNo);
                    break;
                case "mIvBackSpace":
                    if (mPhoneNo != null && mPhoneNo.length() > 0) {
                        mPhoneNo = mPhoneNo.substring(0, mPhoneNo.length() - 1);
                    }
                    mEdtPhoneNo.setText(mPhoneNo);
                    break;
                case "mIvShowNumber":
                    mEdtPhoneNo.setTransformationMethod(null);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEdtPhoneNo.setTransformationMethod(new HiddenPassTransformationMethod());
                        }
                    }, 5000);
                    break;
            }
        } catch (Exception ex) {

        }

        return null;

    }
}
