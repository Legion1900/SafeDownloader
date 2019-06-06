package com.legion1900.baseservicelib.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;

import java.io.File;

public abstract class SafeDownloaderService extends Service {

    public static final String BUNDLE_KEY_FILENAME = "fileName";
    public static final String BUNDLE_KEY_CKSM = "checksumAlg";
    public static final String BUNDLE_KEY_DWNLD_FROM = "downloadFrom";

    private Messenger mMessenger;

    abstract protected void download(File pathOnDevice, String downloadFrom);

    @Override
    public IBinder onBind(Intent intent) {
        mMessenger = new Messenger(new DownloadRequestHandler(this));
        return mMessenger.getBinder();
    }
}
