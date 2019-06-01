package com.legion1900.safedownload;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderBuilder;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private static final String PATH_TO_FILE = "/home/Apps/MitDTargetAppServer/test.apk";
    private static final File PATH_ON_DEVICE;

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

    public void onButtonDownloadClick(View parent) {
        askPermission();

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

        Thread thred = new Thread(
                new DbxDownloadHelper(PATH_ON_DEVICE, PATH_TO_FILE, id, this)
        );
        thred.start();
    }

    private void askPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    42);
        }
    }

    class DbxDownloadHelper implements Runnable {

        private /*static*/ String TAG = "DbxDownloadHelper";

        private DbxClientV2 mDbxClient;
        private DbxDownloader mDownloader;

        private Activity mBoundActivity;

        private File mPathOnDevice;
        private String mPathOnDbx;

        private String mId;

        public DbxDownloadHelper(File pathOnDevice, String pathOnDbx, String id, Activity boundActivity) {
            mPathOnDevice = pathOnDevice;
            mPathOnDbx = pathOnDbx;
            mBoundActivity = boundActivity;
            mId = id;
        }

        @Override
        public void run() {
            initializeDbxApi(mId);

//        FileOutputStream fout = null;
//        try {
//            fout = new FileOutputStream(mPathOnDevice);
//            mDownloader.download(fout);
//        }
//        catch (FileNotFoundException e) {
//            Log.e(TAG, "Cannot create FileOutputStream", e);
//        }
//        catch (DbxException e) {
//            Log.e(TAG, "Download failure", e);
//        }
//        catch (IOException e) {
//            Log.e(TAG, "Download failure", e);
//        }

            ListFolderResult result = null;
            try {
                result = mDbxClient.files().listFolderBuilder("").start();
                String tmp = "";
                for (Metadata meta : result.getEntries()) {
                    tmp += meta;
                }

                String path = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        + "/"
                        + "test.txt";

                File file = new File(path);
                file.createNewFile();
                FileOutputStream fout = new FileOutputStream(file);
                OutputStreamWriter writer = new OutputStreamWriter(fout);
                writer.append(tmp);

                writer.close();
                fout.flush();
                fout.close();
            }
            catch (ListFolderErrorException e) {
                Log.e("TAG", "shit", e);
            }
            catch (DbxException e) {
                Log.e("TAG", "shit", e);
            }
            catch (IOException e) {
                Log.e("TAG", "shit", e);
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
}