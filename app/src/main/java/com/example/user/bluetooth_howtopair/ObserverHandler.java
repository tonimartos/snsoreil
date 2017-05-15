package com.example.user.bluetooth_howtopair;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

public class ObserverHandler {
    public static final int ONNotifyStatus = 0;
    private Context context;
    Handler handler;
    private ObserverListener listener;
    private HomeObserver observer;
    private Uri uri;

    class OBVHandler extends Handler {
        OBVHandler() {
        }

        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                ObserverHandler.this.listener.onDataChange(ObserverHandler.this.uri);
            }
        }
    }

    class HomeObserver extends ContentObserver {
        private Handler handler;

        public HomeObserver(Handler handler) {
            super(handler);
            this.handler = handler;
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            this.handler.obtainMessage(0).sendToTarget();
        }

        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }
    }

    public interface ObserverListener {
        Context getContext();

        void onDataChange(Uri uri);
    }

    public ObserverHandler(ObserverListener listener) {
        this.handler = new OBVHandler();
        this.listener = listener;
        this.context = listener.getContext();
    }

    public void registerObserver(Uri uri, boolean notifyForDescendents) {
        this.uri = uri;
        this.observer = new HomeObserver(this.handler);
        this.context.getContentResolver().registerContentObserver(uri, true, this.observer);
    }

    public void UnregisterObserver() {
        this.context.getContentResolver().unregisterContentObserver(this.observer);
    }
}

