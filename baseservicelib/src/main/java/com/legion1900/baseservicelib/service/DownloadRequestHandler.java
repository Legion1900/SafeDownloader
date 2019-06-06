package com.legion1900.baseservicelib.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.File;

/*
 * Handler of incoming messages from clients
 * */
class DownloadRequestHandler extends Handler {

    private static final String TAG_ERROR_CONTROLLER = "Verifier";
    private static final String TAG_ERROR_RESPONDING = "DownloadRequestHandler";

    private Thread executor;

    private Thread controller;

    // Reference to parent service.
    private SafeDownloaderService service;

    private Messenger client;

    DownloadRequestHandler(SafeDownloaderService service) {
        this.service = service;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ServiceMessages.MSG_REGISTER_CLIENT:
                client = msg.replyTo;
                break;
            case ServiceMessages.MSG_DOWNLOAD:
                startDownloadNVerify(msg);
                // TODO add verification
            default:
                super.handleMessage(msg);
        }
    }

    private void startDownloadNVerify(Message msg) {
        Message response = Message.obtain(null, ClientMessages.MSG_DWNLD_STARTED);
        sendMessageToClient(response);

        if (msg.obj.getClass() == Bundle.class) {
            Bundle bundle = (Bundle)msg.obj;
            File pathOnDevice = getPathOnDevice(msg);
            String downloadFrom = bundle.getString(SafeDownloaderService.BUNDLE_KEY_DWNLD_FROM);
            String hash = bundle.getString(SafeDownloaderService.BUNDLE_KEY_HASH);
            executor = new Thread(new Downloader(pathOnDevice, downloadFrom));
            controller = new Thread(new Verifier(hash, pathOnDevice));
            executor.start();
            controller.start();
        }
        else {
            throw new IllegalArgumentException("File obj should be Bundle");
        }
    }

    private boolean verify(String hash, File file) {
        Message response = Message.obtain(null, ClientMessages.MSG_VERIFY);
        sendMessageToClient(response);

        return MD5.checkMD5(hash, file);
    }

    private void onVerificationTrue() {
        Message response = Message.obtain(null, ClientMessages.MSG_VERIFY_SUCCEDSUCCEED);
        sendMessageToClient(response);
    }

    private void onVerificationFalse(File fileToBeDeleted) {
        Message response = Message.obtain(null, ClientMessages.MSG_VERIFY_FAILED);
        sendMessageToClient(response);
        fileToBeDeleted.delete();
    }

    private File getPathOnDevice(Message msg) {
        Bundle bundle = (Bundle)msg.obj;
        String fileName = bundle.getString(SafeDownloaderService.BUNDLE_KEY_FILENAME);
        return new File(
                service.getFilesDir()
                        + "/"
                        + fileName);
    }

    private void sendMessageToClient(Message msg) {
        try {
            client.send(msg);
        }
        catch (RemoteException e) {
            Log.e(TAG_ERROR_RESPONDING, "Cannot respond to client", e);
        }
    }

    private class Downloader implements Runnable {

        File pathOnDevice;

        String downloadFrom;

        Downloader(File pathOnDevice, String downloadFrom) {
            this.pathOnDevice = pathOnDevice;
            this.downloadFrom = downloadFrom;
        }

        @Override
        public void run() {
            service.download(pathOnDevice, downloadFrom);
        }
    }

    private class Verifier implements Runnable {

        final String hash;
        final File file;

        Verifier(String hash, File file) {
            this.hash = hash;
            this.file = file;
        }

        @Override
        public void run() {
            try {
                executor.join();
                Message response = Message.obtain(null, ClientMessages.MSG_DWNLD_FINISHED);
                sendMessageToClient(response);

                boolean result = verify(hash, file);
                if (result)
                    onVerificationTrue();
                else
                    onVerificationFalse(file);
            }
            catch (InterruptedException e) {
                Log.e(TAG_ERROR_CONTROLLER, "Interrupter", e);
            }
        }
    }
}
