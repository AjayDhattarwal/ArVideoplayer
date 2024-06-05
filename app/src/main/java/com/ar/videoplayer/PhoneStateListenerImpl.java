package com.ar.videoplayer;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.google.android.exoplayer2.SimpleExoPlayer;

public class PhoneStateListenerImpl extends PhoneStateListener {
    private final SimpleExoPlayer player;

    public PhoneStateListenerImpl(SimpleExoPlayer player) {
        this.player = player;
    }

    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (player.getPlayWhenReady()) {
                    player.setPlayWhenReady(false);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                if (!player.getPlayWhenReady()) {
                    player.setPlayWhenReady(true);
                }
                break;
            default:
                break;
        }
    }
}

