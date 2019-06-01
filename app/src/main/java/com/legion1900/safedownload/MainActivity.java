package com.legion1900.safedownload;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String pathToFile = "/Apps/MitD Target App Server/test.apk";

    private Button mButtonDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        String id = "";
//        try {
//            id = getString(getApplicationInfo().labelRes)
//                    + "/"
//                    + getPackageManager()
//                    .getPackageInfo(getPackageName(), 0);
//        }
//        catch (PackageManager.NameNotFoundException e) {
//            Log.e(TAG, "Error while initializing DbxClient", e);
//        }

        mButtonDownload = findViewById(R.id.button_download);
    }
}

class DbxDownloadHelper implements Runnable {

    private static String TAG = "DbxDownloadHelper";

    private DbxClientV2 mDbxClient;
    private DbxDownloader mDownloader;
    // mId - id for DbxDownloader initialization

    private File mPathOnDevice;
    private String mPathOnDbx;

    public DbxDownloadHelper(File pathOnDevice, String pathOnDbx, String id) {
        mPathOnDevice = pathOnDevice;
        mPathOnDbx = pathOnDbx;

        initializeDbxApi(id);
    }

    @Override
    public void run() {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(mPathOnDevice);
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

    private void initializeDbxApi(String id) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder(id).build();
        mDbxClient = new DbxClientV2(config, BuildConfig.MyAccessToken);

        try {
            mDownloader = mDbxClient.files().download(mPathOnDbx);
        }
        catch (DbxException e) {
            Log.e(TAG, "API initialization error", e);
        }

        try {
            mPathOnDevice.createNewFile();
        }
        catch (IOException e) {
            Log.e(TAG, "Cannot create file on device", e);
        }
    }
}