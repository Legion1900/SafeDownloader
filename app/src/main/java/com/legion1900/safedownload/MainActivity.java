package com.legion1900.safedownload;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.legion1900.safedownload.service.SafeDownloaderService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String PATH_TO_FILE = "/test.apk";
    private static final File PATH_ON_DEVICE;

    private Messenger mService = null;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            Log.d("inConnection", mService.toString());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    static {
        String tmp = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/"
                + "test.apk";
        PATH_ON_DEVICE = new File(tmp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, SafeDownloaderService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    public void onButtonDownloadClick(View parent) {
//        askPermission();
//
//        String id = "";
//        try {
//            id = getString(getApplicationInfo().labelRes)
//                    + "/"
//                    + getPackageManager()
//                    .getPackageInfo(getPackageName(), 0);
//        }
//        catch (PackageManager.NameNotFoundException e) {
//            Log.e("MainActivity", "Error while building ID", e);
//        }
//
//        Thread thred = new Thread(
//                new DbxDownloadHelper(PATH_ON_DEVICE, PATH_TO_FILE, id, this)
//        );
//        thred.start();

        Message msg = Message.obtain(null, SafeDownloaderService.MSG_HELLO_WORLD, 0, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void askPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    42);
        }
    }
}

class DbxDownloadHelper implements Runnable {

    private static String TAG = "DbxDownloadHelper";

    private DbxDownloader mDownloader;

    private File mPathOnDevice;
    private String mPathOnDbx;

    private String mId;

    DbxDownloadHelper(File pathOnDevice, String pathOnDbx, String id, Activity boundActivity) {
        mPathOnDevice = pathOnDevice;
        mPathOnDbx = pathOnDbx;
        mId = id;
    }

    @Override
    public void run() {
        initializeDbxApi(mId);

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
        DbxClientV2 mDbxClient = new DbxClientV2(config, BuildConfig.MyAccessToken);

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