package com.example.user.bluetooth_howtopair.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.view.View;

import com.example.user.bluetooth_howtopair.DevicesProvider.DevicesColumns;
import com.example.user.bluetooth_howtopair.handlers.NotificationExtend;
import com.example.user.bluetooth_howtopair.handlers.ObserverHandler;
import com.example.user.bluetooth_howtopair.handlers.ObserverHandler.ObserverListener;
import com.example.user.bluetooth_howtopair.R;
import com.example.user.bluetooth_howtopair.utils.ServiceConstants;

import java.util.Locale;

public abstract class OtherBaseActivity extends BaseActivity implements ObserverListener {
    private AlertDialog NoDevicedialog;
    protected NotificationExtend mNotification;
    private ObserverHandler observerHandler;
    private boolean offobserver;
    BroadcastReceiver settingreceiver;

    /* renamed from: com.changhewulian.ble.taiya.activity.OtherBaseActivity.1 */
    class C00861 extends BroadcastReceiver {
        C00861() {
        }

        public void onReceive(Context context, Intent intent) {
            String SYSTEM_DIALOG_REASON_KEY = "reason";
            String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
            String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
            String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
            String action = intent.getAction();
            if (action.equals(ServiceConstants.SETTING_CHANGE)) {
                OtherBaseActivity.this.onSettingChange();
            } else if (action.equals(ServiceConstants.NOCONNECTION)) {
                OtherBaseActivity.this.showNoDialog();
            } else if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(action)) {
                String reason = intent.getStringExtra("reason");
                if (reason == null) {
                    return;
                }
                if (reason.equals("homekey")) {
                    OtherBaseActivity.this.mNotification = new NotificationExtend(OtherBaseActivity.this);
                    OtherBaseActivity.this.mNotification.showNotification();
                    OtherBaseActivity.this.moveTaskToBack(true);
                    return;
                }
                reason.equals("recentapps");
            }
        }
    }

    /* renamed from: com.changhewulian.ble.taiya.activity.OtherBaseActivity.2 */
    class C00872 implements DialogInterface.OnClickListener {
        C00872() {
        }

        public void onClick(DialogInterface dialog, int which) {
            OtherBaseActivity.this.NoDevicedialog.dismiss();
            OtherBaseActivity.this.NoDevicedialog = null;
        }
    }

    /* renamed from: com.changhewulian.ble.taiya.activity.OtherBaseActivity.3 */
    class C00883 implements DialogInterface.OnCancelListener {
        C00883() {
        }

        public void onCancel(DialogInterface dialog) {
            OtherBaseActivity.this.NoDevicedialog.dismiss();
            OtherBaseActivity.this.NoDevicedialog = null;
        }
    }

    protected abstract void onSettingChange();

    public OtherBaseActivity() {
        this.settingreceiver = new C00861();
        this.offobserver = false;
    }

    protected void sendBroadcastNotify(String action) {
        sendBroadcast(new Intent(action));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguage();
        this.observerHandler = new ObserverHandler(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceConstants.SETTING_CHANGE);
        filter.addAction(ServiceConstants.NOCONNECTION);
        filter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        registerReceiver(this.settingreceiver, filter);
    }

    protected void OffObserver() {
        this.offobserver = true;
    }

    protected void changeLanguage() {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int language = getLanguage();

        switch (language) {
            case MainActivity.OPENSET /*0*/:
                config.locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case CursorAdapter.FLAG_AUTO_REQUERY /*1*/:
                config.locale = Locale.TRADITIONAL_CHINESE;
                break;
            case CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER /*2*/:
                config.locale = Locale.ENGLISH;
                break;
            default:
                config.locale = Locale.ENGLISH;
                break;
        }
        resources.updateConfiguration(config, dm);
    }

    public int getLanguage() {
        String st = Locale.getDefault().getCountry();
        if (st.equals("CN")) {
            return 0;
        }
        if (st.equals("TW") || st.equals("HK")) {
            return 1;
        }
        return 2;
    }

    protected void showNoDialog() {
        if (this.NoDevicedialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog9);
            builder.setTitle(R.string.dialog1);
            builder.setPositiveButton(R.string.dialog2, new C00872());
            builder.setOnCancelListener(new C00883());
            this.NoDevicedialog = builder.create();
            this.NoDevicedialog.show();
        }
    }

    protected void onStart() {
        super.onStart();
        if (!this.offobserver) {
            this.observerHandler.registerObserver(DevicesColumns.CONTENT_URI, false);
        }
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onResume() {
        super.onResume();
        if (this.mNotification != null) {
            this.mNotification.cancelNotification();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (!this.offobserver) {
            unregisterReceiver(this.settingreceiver);
            this.observerHandler.UnregisterObserver();
        }
    }

    public void viewexitClick(View view) {
        finish();
    }
}

