package com.example.funcenter;

import com.example.common.IMediaService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class MainActivity extends Service {
    private static String CHANNEL_ID = "Music player style";
    private static final int NOTIFICATION_ID = 1;
    private Notification notification;
    private MediaPlayer m;
    private boolean isPlaying = false;


    ArrayList<Integer> images = new ArrayList<>(
            Arrays.asList(R.drawable.cute, R.drawable.panda, R.drawable.pandroid)
    );

    ArrayList<Integer> songs = new ArrayList<>(
            Arrays.asList(R.raw.adventure, R.raw.epic, R.raw.evolution)
    );

    private int startID;

    // Binder given to clients
    //    private final IBinder binder = new LocalBinder();
    private final IMediaService.Stub binder = new IMediaService.Stub() {
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            //Does Nothing
        }

        public Bitmap imgIndex(int index) {
            Bitmap b = null;
            b = BitmapFactory.decodeResource(getResources(), images.get(index));
            return b;
        }

        public void musicIndex(int index) {
            m = MediaPlayer.create(MainActivity.this, songs.get(index));
            Dj(m);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

//    public void onCreate() {
//        super.onCreate();
//
//        this.createNotificationChannel();
//    }

    public int Dj(MediaPlayer m) {
        this.createNotificationChannel();

        final Intent notificationIntent = new Intent(getApplicationContext(), IMediaService.class);

        final PendingIntent pendingIntent = PendingIntent.getActivity(this,0, notificationIntent, 0);

        notification =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_media_play)
                        .setOngoing(true).setContentTitle("Music Playing")
                        .setContentText("Click to Access Music Player")
                        .setTicker("Music is playing!")
                        .setFullScreenIntent(pendingIntent, false)
                        .build();

        if (null != m) {
            m.setLooping(false);
            // Stop Service when music has finished playing
            m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    // stop Service if it was started with this ID
                    // Otherwise let other start commands proceed
                    stopSelf(startID);
                }
            });
        }

        startForeground(NOTIFICATION_ID, notification);

        if (m.isPlaying()) {
            m.seekTo(0);
//            m.stop();
        } else {
            m.start();
        }
        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music player notification";
            String description = "The channel for music player notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        if (null != m) {
            m.stop();
            m.release();
        }
    }
}


