package com.legion1900.testapplication.serviceutils;

import android.util.Log;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.legion1900.service_lib.Downloadable;
import com.legion1900.testapplication.BuildConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DbxDownloadable extends Downloadable {

    private static final String TAG = "DbxDownloadHelper";

    public DbxDownloadable(ArgsContainer container) {
        super(container);
    }

    @Override
    public void download(File whereTo) {

    }

    private class DbxDownloadHelper implements Runnable{

        DbxDownloader downloader;
        String id;

        File pathOnDevice;
        String pathOnDbx;

        DbxDownloadHelper(String id, String pathOnDbx, File pathOnDevice) {
            this.id = id;
            this.pathOnDbx = pathOnDbx;
            this.pathOnDevice = pathOnDevice;
        }

        @Override
        public void run() {
            initializeDbxDownloader();
            boolean fileExists = createFile();
            if (fileExists) {
                FileOutputStream fout;
                try {
                    fout = new FileOutputStream(pathOnDevice);
                    downloader.download(fout);
                }
                catch (FileNotFoundException e) {
                    Log.e(TAG, "Cannot create FileOutputStream, target file not found", e);
                }
                catch (IOException e) {
                    Log.e(TAG, "Cannot write to file", e);
                }
                catch (DbxException e) {
                    Log.e(TAG, "Download error", e);
                }
            }
        }

        /*
        * This method should not be called in constructor!
        * Call it only from run to avoid NetworkOnMainThreadException
        * */
        private void initializeDbxDownloader() {
            DbxRequestConfig config = DbxRequestConfig.newBuilder(id).build();
            DbxClientV2 client = new DbxClientV2(config, BuildConfig.MyAccessToken);

            try {
                downloader = client.files().download(pathOnDbx);
            }
            catch (DownloadErrorException e) {
                Log.e(TAG, "Download error", e);
            }
            catch (DbxException e) {
                Log.e(TAG, "DropBox exception?", e);
            }
        }

        private boolean createFile() {
            boolean ans = false;
            try {
                ans = pathOnDevice.createNewFile();
            }
            catch (IOException e) {
                Log.e(TAG, "Cannot create file", e);
            }
            return ans;
        }
    }
}
