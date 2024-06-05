package com.ar.videoplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;

public class MediaControlBroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            Log.d("play","play ");
            KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                int keyCode = keyEvent.getKeyCode();
                Log.d("play","Pause");
                if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                    Log.d("play","play Pause");
                }
            }
        }
    }
}
