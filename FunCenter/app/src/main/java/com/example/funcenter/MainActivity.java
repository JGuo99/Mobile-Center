package com.example.funcenter;

import com.example.common.IMediaService;

import android.app.Service;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class MainActivity extends Service {
//    Test Service
    // Random number generator
    private final Random mGenerator = new Random();

    ArrayList<Integer> images = new ArrayList<>(
            Arrays.asList(R.drawable.cute, R.drawable.panda, R.drawable.pandroid)
    );
    // Binder given to clients
    //    private final IBinder binder = new LocalBinder();
    private final IMediaService.Stub binder = new IMediaService.Stub() {
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            //Does Nothing
        }

    //        Testing
        public int getPid() {
            return getRandomNumber();
        }

        public Bitmap imgIndex(int index) {
            Bitmap b = null;
            b = BitmapFactory.decodeResource(getResources(), images.get(index));
            return b;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /** method for clients */
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }
//    End Test
}


