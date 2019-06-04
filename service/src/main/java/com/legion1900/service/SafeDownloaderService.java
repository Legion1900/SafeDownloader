package com.legion1900.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class SafeDownloaderService extends Service {

    /*
    * Handler of incoming messages from clients
    * */
    private static class DownloadRequestHandler extends Handler {
        private Context applicationContext;

        DownloadRequestHandler(Context context) {
            applicationContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Messages.MSG_HELLO_WORLD:
                    Toast
                            .makeText(applicationContext, "Hello World!", Toast.LENGTH_LONG)
                            .show();
                    break;
                    default:
                        super.handleMessage(msg);
            }
        }
    }

    private Messenger mMessenger;

    @Override
    public IBinder onBind(Intent intent) {
        mMessenger = new Messenger(new DownloadRequestHandler(this));
        return mMessenger.getBinder();
    }
}
