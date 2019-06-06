package com.legion1900.safedownload;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.legion1900.baseservicelib.service.SafeDownloaderService;
import com.legion1900.baseservicelib.service.ServiceMessages;
import com.legion1900.safedownload.service.DbxDownloadService;

public class MainActivity extends AppCompatActivity {

    private static final String PATH_TO_FILE = "/test.apk";

    private Messenger mService = null;

    private boolean bound;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, DbxDownloadService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(mConnection);
            bound = false;
        }
    }

    public void onButtonDownloadClick(View parent) {
        if (!bound) return;
        Message msg = Message.obtain(null, ServiceMessages.MSG_DOWNLOAD, 0, 0);
        Bundle args = new Bundle();
        args.putString(SafeDownloaderService.BUNDLE_KEY_DWNLD_FROM, PATH_TO_FILE);
        args.putString(SafeDownloaderService.BUNDLE_KEY_FILENAME, "test.apk");
        msg.obj = args;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
