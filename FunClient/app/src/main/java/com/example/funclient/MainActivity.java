package com.example.funclient;

import com.example.common.IMediaService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import javax.crypto.KeyGenerator;

public class MainActivity extends Activity {
//Test Client
    private IMediaService funCenterService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Request Image
        final EditText iInput = (EditText) findViewById(R.id.imgInput);
        final ImageView imageView = (ImageView) findViewById(R.id.imageId);

        final Button iRequestButton = (Button) findViewById(R.id.send_request_img);
        iRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mBound) {
                        String iHolder = iInput.getText().toString();
                        int imgIndex = Integer.parseInt(iHolder);
                        if ((imgIndex + 1) < 1 || (imgIndex + 1) > 4) {
                            Toast.makeText(MainActivity.this, "Please enter number from 1 to 3", Toast.LENGTH_SHORT).show();
                        } else {
                            Bitmap b = funCenterService.imgIndex(imgIndex - 1); //-1 to match normal 1 - 3 count method
                            imageView.setImageBitmap(b);
                        }
                    } else {
                        Log.i("Client: ", "Service was not bound!");
                    }
                } catch (RemoteException e) {
                    Log.e("Client: ", e.toString());
                }
            }
        });

//        Request Song
        final EditText mInput = (EditText) findViewById(R.id.musicInput);

        final Button mRequestButton = (Button) findViewById(R.id.send_request_song);
        mRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mBound) {
                        String mHolder = mInput.getText().toString();
                        int musicIndex = Integer.parseInt(mHolder);
                        if ((musicIndex) < 0 || (musicIndex) > 3) {
                            Toast.makeText(MainActivity.this, "Please enter number from 1 to 3", Toast.LENGTH_SHORT).show();
                        } else {
                            funCenterService.musicIndex(musicIndex - 1); //-1 to match normal 1 - 3 count method
                        }
                    } else {
                        Log.i("Client: ", "Service was not bound!");
                    }
                } catch (RemoteException e) {
                    Log.e("Client: ", e.toString());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBound) {
            // Bind to Service
            Intent intent = new Intent(IMediaService.class.getName());
            ResolveInfo info = getPackageManager().resolveService(intent, 0);
            intent.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
            bindService(intent, this.connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(this.connection);
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            funCenterService = IMediaService.Stub.asInterface(service);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.i("Client: ", "Service has disconnected");
            funCenterService = null;
            mBound = false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}