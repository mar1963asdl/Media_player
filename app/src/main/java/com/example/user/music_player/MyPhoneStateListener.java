package com.example.user.music_player;

import android.media.MediaPlayer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.ImageButton;

public class MyPhoneStateListener extends PhoneStateListener{
    MediaPlayer mediaPlayer;
    int pausecurrentposition;
    boolean isCellPlay;
    ImageButton btn_play;
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

        }
    }
}
