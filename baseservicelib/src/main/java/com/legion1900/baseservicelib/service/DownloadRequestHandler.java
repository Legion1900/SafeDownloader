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

    private static final String TAG_ERROR_CONTROLLER = "ExecutorController";
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
                startDownloadRoutine(msg);
                // TODO add verification
                break;
            default:
                super.handleMessage(msg);
        }
    }

    private void startDownloadRoutine(Message msg) {
        Message response = Message.obtain(null, ClientMessages.MSG_DWNLD_STARTED);
        sendMessageToClient(response);

        if (msg.obj.getClass() == Bundle.class) {
            Bundle bundle = (Bundle)msg.obj;
            String fileName = bundle.getString(SafeDownloaderService.BUNDLE_KEY_FILENAME);
            File pathOnDevice = new File(
                    service.getFilesDir()
                            + "/"
                            + fileName);
            String downloadFrom = bundle.getString(SafeDownloaderService.BUNDLE_KEY_DWNLD_FROM);
            executor = new Thread(new Executor(pathOnDevice, downloadFrom));
            controller = new Thread(new ExecutorController());
            executor.start();
            controller.start();
        }
        else {
            throw new IllegalArgumentException("File obj should be Bundle");
        }
    }

    private void sendMessageToClient(Message msg) {
        try {
            client.send(msg);
        }
        catch (RemoteException e) {
            Log.e(TAG_ERROR_RESPONDING, "Cannot respond to client", e);
        }
    }

    private class Executor implements Runnable {

        File pathOnDevice;

        String downloadFrom;

        Executor(File pathOnDevice, String downloadFrom) {
            this.pathOnDevice = pathOnDevice;
            this.downloadFrom = downloadFrom;
        }

        @Override
        public void run() {
            service.download(pathOnDevice, downloadFrom);
        }
    }

    private class ExecutorController implements Runnable {
        @Override
        public void run() {
            try {
                executor.join();
                Message msg = Message.obtain(null, ClientMessages.MSG_DWNLD_FINISHED);
                sendMessageToClient(msg);
            }
            catch (InterruptedException e) {
                Log.e(TAG_ERROR_CONTROLLER, "Interrupter", e);
            }
        }
    }
}
