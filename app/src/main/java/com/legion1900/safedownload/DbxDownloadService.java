package com.legion1900.safedownload;

import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.legion1900.safedownload.service.SafeDownloaderService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DbxDownloadService extends SafeDownloaderService {

    private static final String TAG = "DbxDownloadService";

    private DbxDownloader mDownloader;

    private String pathOnDbx;

    @Override
    protected void download(File pathOnDevice, String downloadFrom) {
        initializeDbxApi(downloadFrom);

        // Create new file.
        try {
            pathOnDevice.createNewFile();
        }
        catch (IOException e) {
            Log.e(TAG, "Cannot create file on device", e);
        }

        // Download logic itself.
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(pathOnDevice);
            mDownloader.download(fout);
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "Cannot create FileOutputStream", e);
        }
        catch (DbxException e) {
            Log.e(TAG, "Download failure", e);
        }
        catch (IOException e) {
            Log.e(TAG, "Download failure", e);
        }
    }

    @Override
    protected void onDownloadFinished() {
        File file = new File(getFilesDir() + "/test.apk");
        Toast.makeText(this, file.length() + "", Toast.LENGTH_LONG).show();
    }

    private void initializeDbxApi(String pathOnDbx) {
        String id = getApplicationContext().getPackageName();
        DbxRequestConfig config = DbxRequestConfig.newBuilder(id).build();
        DbxClientV2 client = new DbxClientV2(config, BuildConfig.MyAccessToken);
        try {
            mDownloader = client.files().download(pathOnDbx);
        }
        catch (DbxException e) {
            Log.e(TAG, "API initialization error", e);
        }
    }
}
