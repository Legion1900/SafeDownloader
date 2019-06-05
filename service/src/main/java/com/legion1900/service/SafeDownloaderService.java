package com.legion1900.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.legion1900.safeservicelib.Downloadable;
import com.legion1900.safeservicelib.Messages;

import java.io.File;
import java.io.IOException;

public class SafeDownloaderService extends Service {
    private static final String ERROR_TAG_EXECUTOR = "Executor error";

    /*
    * Handler of incoming messages from clients
    * */
    private static class DownloadRequestHandler extends Handler {
        private Context serviceContext;

        private Thread executor;

        DownloadRequestHandler(Context context) {
            serviceContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
//            serviceContext.getFilesDir();
            switch (msg.what) {
                case Messages.MSG_START_DWNLD:
                {
                    startDownload(msg);
                } break;
                    default:
                        super.handleMessage(msg);
            }
        }

        private void startDownload(Message msg) {
            if (msg.obj.getClass().getSuperclass() == Downloadable.class) {
                Downloadable downloadable = (Downloadable) msg.obj;
                String tmp = serviceContext.getFilesDir()
                        + "/" + downloadable.args.fileName;
                File pathOnDevice = new File(tmp);
                createFile(pathOnDevice);
                executor = new Thread(new Executor(downloadable, pathOnDevice));
            }
        }

        private void createFile(File pathOnDevice) {
            try {
                pathOnDevice.createNewFile();
            }
            catch (IOException e) {
                Log.e(ERROR_TAG_EXECUTOR, "Cannot create file", e);
            }
        }
    }

    private static class Executor implements Runnable {
        private Downloadable downloadable;

        private File pathOnDevice;

        Executor(Downloadable downloadable, File pathOnDevice) {
            this.downloadable = downloadable;
            this.pathOnDevice = pathOnDevice;
        }

        @Override
        public void run() {
            downloadable.download(pathOnDevice);
        }
    }

    private Messenger mMessenger;

    @Override
    public IBinder onBind(Intent intent) {
        mMessenger = new Messenger(new DownloadRequestHandler(this));
        return mMessenger.getBinder();
    }
}
