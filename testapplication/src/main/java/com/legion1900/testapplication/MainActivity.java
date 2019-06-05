package com.legion1900.testapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.legion1900.safeservicelib.Downloadable;
import com.legion1900.safeservicelib.Messages;
import com.legion1900.testapplication.serviceutils.DbxDownloadable;

public class MainActivity extends AppCompatActivity {

    private static final String PATH_ON_DBX = "/test.apk";

    private Messenger mService = null;

    private boolean mIsBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mIsBound = false;
        }
    };

    public void download(View view) {
        Message message = Message.obtain(null, Messages.MSG_START_DWNLD, 0, 0);
        message.obj = new DbxDownloadable(
                new Downloadable.ArgsContainer("test.apk", ""),
                getId(),
                PATH_ON_DBX
        );
        try {
            mService.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                        "com.legion1900.service",
                        "com.legion1900.service.SafeDownloaderService"
                ));
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    private String getId() {
        String id = "";
        try {
            id = getString(getApplicationInfo().labelRes)
                    + "/"
                    + getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.e("MainActivity", "Error while building ID", e);
        }
        return id;
    }
}
