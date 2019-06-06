package com.legion1900.safedownload.service;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.legion1900.baseservicelib.service.ClientMessages;

public class ResponseHandler extends Handler {

    private static final String ON_DOWNLOAD_STARTED = "Downloading";
    private static final String ON_DOWNLOAD_FINISHED = "Downloading is finished";
    private static final String ON_VERIFY = "Verifying";
    private static final String ON_VERIFY_FAILED = "Verification failed";
    private static final String ON_VERIFY_SUCCEED = "File is OK";

    private final Activity context;

    public ResponseHandler(Activity context) {
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ClientMessages.MSG_DWNLD_STARTED:
                displayLongToast(ON_DOWNLOAD_STARTED);
                break;
            case ClientMessages.MSG_DWNLD_FINISHED:
                displayLongToast(ON_DOWNLOAD_FINISHED);
                break;
            case ClientMessages.MSG_VERIFY:
                displayLongToast(ON_VERIFY);
                break;
            case ClientMessages.MSG_VERIFY_FAILED:
                displayLongToast(ON_VERIFY_FAILED);
                break;
            case ClientMessages.MSG_VERIFY_SUCCEDSUCCEED:
                displayLongToast(ON_VERIFY_SUCCEED);
                break;
            default: super.handleMessage(msg);
        }
    }

    private void displayLongToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
