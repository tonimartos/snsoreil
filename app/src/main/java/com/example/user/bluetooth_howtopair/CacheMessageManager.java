package com.example.user.bluetooth_howtopair;

import android.os.Handler;
import android.os.Message;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CacheMessageManager {
    public static final int SENDMESSAGE = 101;
    public static final int WAITTIME = 90;
    private ExecutorService executor;
    Handler handeHandler;
    private long lasttime;
    private CacheMessageListener listener;
    private ConcurrentLinkedQueue<Object> msgQueue;
    private boolean msgService;

    class CacheHandler extends Handler {
        CacheHandler() {
        }

        public void handleMessage(Message msg) {
            if (msg.what == CacheMessageManager.SENDMESSAGE) {
                CacheMessageManager.this.lasttime = System.currentTimeMillis();
                CacheMessageManager.this.listener.NextMessage(msg.obj);
            }
        }
    }

    public interface CacheMessageListener {
        void NextMessage(Object obj);
    }

    class MessageRunnable implements Runnable {
        private Handler handler;
        private boolean wait;

        public MessageRunnable(Handler handler, boolean wait) {
            this.handler = handler;
            this.wait = wait;
        }

        public void run() {
            CacheMessageManager.this.msgService = true;
            while (CacheMessageManager.this.msgQueue.peek() != null) {
                Object object = CacheMessageManager.this.msgQueue.poll();
                Message message = this.handler.obtainMessage();
                message.what = CacheMessageManager.SENDMESSAGE;
                message.obj = object;
                this.handler.sendMessage(message);
                try {
                    Thread.sleep(90);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            CacheMessageManager.this.msgService = false;
        }
    }

    public CacheMessageManager(CacheMessageListener listener) {
        this.msgQueue = new ConcurrentLinkedQueue();
        this.executor = Executors.newScheduledThreadPool(1);
        this.msgService = false;
        this.lasttime = System.currentTimeMillis();
        this.handeHandler = new CacheHandler();
        this.listener = listener;
    }

    public void readyMessage(Object object) {
        this.msgQueue.add(object);
        if (!this.msgService) {
            sendWaitingMessage(false);
        }
    }

    private void sendWaitingMessage(boolean wait) {
        if (!this.msgService && !this.executor.isTerminated()) {
            this.executor.execute(new MessageRunnable(this.handeHandler, wait));
        }
    }

    public void removeAllMessage() {
        this.msgQueue.clear();
    }
}

