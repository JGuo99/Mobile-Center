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
    private IMediaService funCenterService;
    boolean mBound = false;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button playButton = (Button) findViewById(R.id.play);
        final Button pauseButton = (Button) findViewById(R.id.pause);
        final Button stopButton = (Button) findViewById(R.id.stop);
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);

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
                        int imgIndex;
                        if (!(iHolder.equals(""))) {
                            imgIndex = Integer.parseInt(iHolder);
                            if ((imgIndex) < 1 || (imgIndex ) > 3) {
                                Toast.makeText(MainActivity.this, "Please enter number from 1 to 3", Toast.LENGTH_SHORT).show();
                            } else {
                                Bitmap b = funCenterService.imgIndex(imgIndex - 1); //-1 to match normal 1 - 3 count method
                                imageView.setImageBitmap(b);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Please enter a number!", Toast.LENGTH_SHORT).show();
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
                        int index;
                        String mHolder = mInput.getText().toString();
                        if (!(mHolder.equals(""))) {
                            index = Integer.parseInt(mHolder);
                            if ((index) < 1 || (index) > 3) {
                                Toast.makeText(MainActivity.this, "Please enter number from 1 to 3", Toast.LENGTH_SHORT).show();
                            } else {
                                //-1 to match normal 1 - 3 count method
                                if (isPlaying != false) {
                                    funCenterService.stop();
                                    isPlaying = false;
                                }
                                funCenterService.musicIndex(index - 1); //-1 to match normal 1 - 3 count method
                                pauseButton.setEnabled(true);
                                stopButton.setEnabled(true);
                                isPlaying = true;
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Please enter a number!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.i("Client: ", "Service was not bound!");
                    }
                } catch (RemoteException e) {
                    Log.e("Client: ", e.toString());
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mBound) {
                        funCenterService.play();
                        pauseButton.setEnabled(true);
                        playButton.setEnabled(false);
                    } else {
                        Log.i("Client: ", "Service was not bound!");
                    }
                } catch (RemoteException e) {
                    Log.e("Client: ", e.toString());
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mBound) {
                        funCenterService.pause();
                        pauseButton.setEnabled(false);
                        playButton.setEnabled(true);
                    } else {
                        Log.i("Client: ", "Service was not bound!");
                    }
                } catch (RemoteException e) {
                    Log.e("Client: ", e.toString());
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mBound) {
                        funCenterService.stop();
                        pauseButton.setEnabled(false);
                        playButton.setEnabled(false);
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
//            startService(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mBound) {
//            unbindService(this.connection);
//        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            try {
                funCenterService.endService();
                unbindService(this.connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}