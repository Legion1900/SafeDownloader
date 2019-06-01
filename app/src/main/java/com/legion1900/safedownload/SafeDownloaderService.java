package com.legion1900.safedownload;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class SafeDownloaderService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder() {

        };
    }
}
