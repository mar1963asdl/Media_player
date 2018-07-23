package com.example.user.music_player;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    String localStorageAudioPath;
    ImageButton btn_play, btn_pause;
    //            , pause, stop;
    MediaPlayer mediaPlayer;
    TextView music_star;
    TextView music_end;
    int pausecurrentposition;
    SeekBar btn_seek;
    MyPhoneStateListener myPhoneStateListener;
    boolean isSeekBarChanging;
    boolean isFitstInit = true;
    Timer timer;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_play = findViewById(R.id.btn_play);
//        btn_pause= findViewById(R.id.btn_pause);
//        stop = findViewById(R.id.btn_stop);
        btn_seek = findViewById(R.id.btn_seek);
        music_star = findViewById(R.id.music_star);
        music_end = findViewById(R.id.music_end);


//        btn_pause.setOnClickListener(this);
//        stop.setOnClickListener(this);
        btn_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int duration2 = mediaPlayer.getDuration() / 1000;
                int position = mediaPlayer.getCurrentPosition();
                music_star.setText(calculateTime(position / 1000));
                music_end.setText(calculateTime(duration2));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = false;
                mediaPlayer.seekTo(seekBar.getProgress());

                music_star.setText(calculateTime(mediaPlayer.getCurrentPosition() / 1000));
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            boolean flag = true;

            @Override
            public void onClick(View view) {
                //  isPlayOrPause();
                if (flag) {
                    btn_play.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
//                    btn_play.setImageResource(R.drawable.pause);
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    String uriString = "android.resource://" + getPackageName() + "/" + R.raw.music;
                    try {
                        mediaPlayer.setDataSource(MainActivity.this, Uri.parse(uriString));
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });

                    flag = false;
                } else {
                    btn_play.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
//                    btn_play.setImageResource(R.drawable.play);
                    flag = true;
                }
            }
        });

        myPhoneStateListener = new MyPhoneStateListener();
        TelephonyManager phoneyMana = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneyMana.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }


    public void initDatas() {
//    coverImagePath = getIntent().getStringExtra("coverImagePath");
//    localStoragePath = getIntent().getStringExtra("localStoragePath");
        localStorageAudioPath = getIntent().getStringExtra("localStorageAudioPath");

        initMediaPlayer();
    }

    private void initMediaPlayer() {
        File file = new File(localStorageAudioPath);

        if (file.exists()) {
            if (mediaPlayer == null) {
                Log.i("Novate", "meidaPlayer 為空");

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(file.getAbsolutePath());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int duration2 = mediaPlayer.getDuration() / 1000;
                int position = mediaPlayer.getCurrentPosition();
                music_star.setText(calculateTime(position / 1000));
                music_end.setText(calculateTime(duration2));
            }
        }
    }

    public void isPlayOrPause() {
//        Log.i("Novate","localStorageAudioPath-->" + localStorageAudioPath);
//        File file = new File (localStorageAudioPath);
//        if (file.exists()){
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.music);
        if (mediaPlayer != null && isFitstInit == true) {
            isFitstInit = false;
            Log.i("Novate", "mediaPlayer!=null&&isFitstInit==true");
            mediaPlayer.start();
            btn_play.setImageResource(R.drawable.play);
            int duration = mediaPlayer.getDuration();
            btn_seek.setMax(duration);

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!isSeekBarChanging) {
                        btn_seek.setProgress(mediaPlayer.getCurrentPosition());
                    }
                }
            }, 0, 50);
        } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            Log.i("Novate", "音樂文件正在播放，則暂停並改變按鈕樣式");
            mediaPlayer.pause();
            btn_play.setImageResource(R.drawable.play);
        } else if (mediaPlayer != null && (!mediaPlayer.isPlaying())) {
            Log.i("Novate", "如果為暂停狀態則繼續播放，同時改變按鈕樣式");
            if (pausecurrentposition > 0) {
                mediaPlayer.seekTo(pausecurrentposition);
                pausecurrentposition = 0;
            }
            mediaPlayer.start();
            btn_play.setImageResource(R.drawable.pause);
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.seekTo(0);
                btn_play.setImageResource(R.drawable.play);
            }
        });
    }
//              else{
//            Toast.makeText(this, "你要播放的音樂不存在", Toast.LENGTH_SHORT).show();
//            }
//

    public String calculateTime(int time) {
        int minute;
        int second;
        String Minute;
        if (time >= 60) {
            minute = time / 60;
            second = time % 60;

            if (minute >= 0 && minute < 10) {
                if (second >= 0 && second < 10) {
                    return "0" + minute + ":" + "0" + second;
                } else {
                    return "00:" + second;
                }
            }
            return null;
        }
        return null;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        this.localStorageAudioPath =
                savedInstanceState.getString("filename");
        this.pausecurrentposition = savedInstanceState.getInt("position");
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("Novate", "回到當前音樂文件onRestoreInstanceState()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("filename", localStorageAudioPath);
        outState.putInt("position", pausecurrentposition);
        super.onSaveInstanceState(outState);
        Log.i("Novate", "保存音樂onSaveInstanceState()");
    }

    @Override
    protected void onPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            pausecurrentposition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            timer.purge();
            btn_play.setImageResource(R.drawable.play);
        }
        super.onPause();
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        @SuppressLint("ServiceCast") TelephonyManager tmgr = (TelephonyManager) getSystemService(Context.TELECOM_SERVICE);
        tmgr.listen(myPhoneStateListener, 0);
        super.onDestroy();
        Toast.makeText(MainActivity.this, "onDestroy", Toast.LENGTH_LONG).show();
    }
}


//        switch (view.getId()) {
//
//            case R.id.btn_play:
//                if (mediaPlayer==null) {
//                    mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.music);
//                    mediaPlayer.start();
//                }
//                else if (!mediaPlayer.isPlaying()){
//                    mediaPlayer.seekTo(pausecurrentposition);
//                    mediaPlayer.start();
//                }
//                break;

//            case R.id.btn_pause:
//                if(mediaPlayer!=null){
//                    mediaPlayer.pause();
//                    pausecurrentposition=mediaPlayer.getCurrentPosition();
//                }
//                break;
//
//            case R.id.btn_stop:
//                if(mediaPlayer!=null) {
//                    mediaPlayer.stop();
//                    mediaPlayer = null;
//                }
//                break;




