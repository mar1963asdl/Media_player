package com.example.user.music_player;

import android.content.Context;
import android.media.MediaPlayer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Timer;

public class MyPhoneStateListener extends PhoneStateListener{
    MediaPlayer mediaPlayer;
    int pausecurrentposition;
    boolean isCellPlay;
    Timer timer;
    ImageButton btn_play;
    MyPhoneStateListener myPhoneStateListener;
    @Override
    public void onCallStateChanged(int state, String incomingNumber)
    {
        switch(state){
            case TelephonyManager.CALL_STATE_RINGING:
                if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
                    pausecurrentposition=
                            mediaPlayer.getCurrentPosition();
                            mediaPlayer.pause();
                            isCellPlay = true;
                            btn_play.setImageResource(android.R.drawable.ic_media_play)
                            ;
                            timer.purge();
                    }
                    break;
            case TelephonyManager.CALL_STATE_IDLE:
                if (isCellPlay){
                    isCellPlay = false;
                }
                break;
        }
    }
}
    @Override
    protected void onDestroy(){
    if (timer!=null)
    {
        timer.cancel();
        timer.purge();
        timer = null;
    }
    if (mediaPlayer!=null)
    {
        mediaPlayer.release();
        mediaPlayer = null;
    }
    TelephonyManager tmgr = (TelephonyManager) getSystemService (Context.TELECOM_SERVICE);
    tmgr.listen(myPhoneStateListener, 0);
    super.onDestroy();
        Toast.showToast(PlayMusicActivity.this,"onDestroy");
    }
}