package com.moko.contacttracker.activity;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.moko.contacttracker.AppConstants;
import com.moko.contacttracker.R;
import com.moko.contacttracker.adapter.ExportDataListAdapter;
import com.moko.contacttracker.dialog.AlertMessageDialog;
import com.moko.contacttracker.entity.ExportData;
import com.moko.contacttracker.utils.ToastUtils;
import com.moko.contacttracker.utils.Utils;
import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.OrderTaskAssembler;
import com.moko.support.entity.ConfigKeyEnum;
import com.moko.support.entity.OrderType;
import com.moko.support.event.ConnectStatusEvent;
import com.moko.support.event.OrderTaskResponseEvent;
import com.moko.support.log.LogModule;
import com.moko.support.task.OrderTaskResponse;
import com.moko.support.utils.MokoUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExportDataActivity extends BaseActivity {

    @BindView(R.id.iv_sync)
    ImageView ivSync;
    @BindView(R.id.tv_export)
    TextView tvExport;
    @BindView(R.id.tv_sync)
    TextView tvSync;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.rv_export_data)
    RecyclerView rvExportData;

    private boolean mReceiverTag = false;
    private StringBuilder storeString = new StringBuilder();
    private ArrayList<ExportData> exportDatas;
    private boolean mIsShown;
    private boolean isSync;
    private ExportDataListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_data);
        ButterKnife.bind(this);

        exportDatas = new ArrayList<>();
        adapter = new ExportDataListAdapter();
        adapter.openLoadAnimation();
        adapter.replaceData(exportDatas);
        rvExportData.setLayoutManager(new LinearLayoutManager(this));
        rvExportData.setAdapter(adapter);
        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        if (!MokoSupport.getInstance().isBluetoothOpen()) {
            // 蓝牙未打开，开启蓝牙
            MokoSupport.getInstance().enableBluetooth();
        } else {
            MokoSupport.getInstance().enableStoreDataNotify();
            Animation animation = AnimationUtils.loadAnimation(ExportDataActivity.this, R.anim.rotate_refresh);
            ivSync.startAnimation(animation);
            tvSync.setText("Stop");
            isSync = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_CONN_STATUS_DISCONNECTED.equals(action)) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_CURRENT_DATA.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderType orderType = response.orderType;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderType) {
                    case STORE_DATA_NOTIFY:
                        if (!mIsShown) {
                            mIsShown = true;
                            tvExport.setEnabled(true);
                        }

                        if (value.length >= 13) {
                            byte[] timeBytes = Arrays.copyOfRange(value, 0, 6);
                            byte[] macBytes = Arrays.copyOfRange(value, 6, 12);
                            byte[] rawDataBytes = Arrays.copyOfRange(value, 12, value.length);
                            int year = timeBytes[0] & 0xff;
                            int month = timeBytes[1] & 0xff;
                            int day = timeBytes[2] & 0xff;
                            int hour = timeBytes[3] & 0xff;
                            int minute = timeBytes[4] & 0xff;
                            int second = timeBytes[5] & 0xff;

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.YEAR, 2000 + year);
                            calendar.set(Calendar.MONTH, month - 1);
                            calendar.set(Calendar.DAY_OF_MONTH, day);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, second);

                            int rssi = value[12];
                            String rssiStr = String.format("%ddBm", rssi);

                            String rawData = "";
                            if (rawDataBytes.length > 0) {
                                rawData = MokoUtils.bytesToHexString(rawDataBytes);
                            }

                            StringBuffer stringBuffer = new StringBuffer();
                            for (int i = macBytes.length - 1, l = 0; i >= l; i--) {
                                stringBuffer.append(MokoUtils.byte2HexString(macBytes[i]));
                                if (i > l)
                                    stringBuffer.append(":");
                            }
                            String mac = stringBuffer.toString();
                            ExportData exportData = new ExportData();

                            String time = Utils.calendar2strDate(calendar, AppConstants.PATTERN_YYYY_MM_DD_HH_MM_SS);
                            exportData.time = time;
                            exportData.rssi = rssi;
                            exportData.mac = mac;
                            exportData.rawData = rawData;
                            exportDatas.add(exportData);
                            adapter.replaceData(exportDatas);

                            storeString.append(String.format("Time:%s", time));
                            storeString.append("\n");
                            storeString.append(String.format("Mac Address:%s", mac));
                            storeString.append("\n");
                            storeString.append(String.format("RSSI:%s", rssiStr));
                            storeString.append("\n");
                            storeString.append(String.format("Raw Data:%s", rawData));
                            storeString.append("\n");
                            storeString.append("\n");
                        }
                        break;
                }
            }
            if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
            }
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                dismissSyncProgressDialog();
            }
            if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderType orderType = response.orderType;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderType) {
                    case WRITE_CONFIG:
                        if (value.length >= 2) {
                            int key = value[1] & 0xff;
                            ConfigKeyEnum configKeyEnum = ConfigKeyEnum.fromConfigKey(key);
                            if (configKeyEnum == null) {
                                return;
                            }
                            int length = value[3] & 0xFF;
                            switch (configKeyEnum) {
                                case DELETE_STORE_DATA:
                                    if (length == 0) {
                                        storeString = new StringBuilder();
                                        LogModule.writeTrackedFile("");
                                        mIsShown = false;
                                        exportDatas.clear();
                                        adapter.replaceData(exportDatas);
                                        tvExport.setEnabled(false);
                                        ToastUtils.showToast(ExportDataActivity.this, "Empty success!");
                                    } else {
                                        ToastUtils.showToast(ExportDataActivity.this, "Failed");
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }
        });
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            dismissSyncProgressDialog();
                            finish();
                            break;
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {
            mReceiverTag = false;
            unregisterReceiver(mReceiver);
        }
        EventBus.getDefault().unregister(this);
    }

    private ProgressDialog syncingDialog;

    public void showSyncingProgressDialog() {
        syncingDialog = new ProgressDialog(this);
        syncingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        syncingDialog.setCanceledOnTouchOutside(false);
        syncingDialog.setCancelable(false);
        syncingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        syncingDialog.setMessage("Syncing...");
        if (!isFinishing() && syncingDialog != null && !syncingDialog.isShowing()) {
            syncingDialog.show();
        }
    }

    public void dismissSyncProgressDialog() {
        if (!isFinishing() && syncingDialog != null && syncingDialog.isShowing()) {
            syncingDialog.dismiss();
        }
    }

    @OnClick({R.id.tv_back, R.id.tv_empty, R.id.ll_sync, R.id.tv_export})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                back();
                break;
            case R.id.tv_empty:
                AlertMessageDialog dialog = new AlertMessageDialog();
                dialog.setTitle("Warning!");
                dialog.setMessage("Are you sure to empty the saved tracked datas?");
                dialog.setOnAlertConfirmListener(() -> {
                    showSyncingProgressDialog();
                    MokoSupport.getInstance().sendOrder(OrderTaskAssembler.deleteTrackedData());
                });
                dialog.show(getSupportFragmentManager());
                break;
            case R.id.ll_sync:
                if (!isSync) {
                    isSync = true;
                    tvEmpty.setEnabled(false);
//                    showSyncingProgressDialog();
//                    MokoSupport.getInstance().sendOrder(OrderTaskAssembler.openTrackedNotify());
                    MokoSupport.getInstance().enableStoreDataNotify();
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
                    ivSync.startAnimation(animation);
                    tvSync.setText("Stop");
                } else {
//                    showSyncingProgressDialog();
//                    MokoSupport.getInstance().sendOrder(OrderTaskAssembler.closeTrackedNotify());
                    MokoSupport.getInstance().disableStoreDataNotify();
                    isSync = false;
                    tvEmpty.setEnabled(true);
                    ivSync.clearAnimation();
                    tvSync.setText("Sync");
                }
                break;
            case R.id.tv_export:
                if (mIsShown) {
                    showSyncingProgressDialog();
                    LogModule.writeTrackedFile("");
                    tvExport.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissSyncProgressDialog();
                            String log = storeString.toString();
                            if (!TextUtils.isEmpty(log)) {
                                LogModule.writeTrackedFile(log);
                                File file = LogModule.getTrackedFile();
                                // 发送邮件
                                String address = "Development@mokotechnology.com";
                                String title = "Tracked Log";
                                String content = title;
                                Utils.sendEmail(ExportDataActivity.this, address, content, title, "Choose Email Client", file);
                            }
                        }
                    }, 500);
                }
                break;
        }
    }

    private void back() {
        // 关闭通知
//        MokoSupport.getInstance().sendOrder(OrderTaskAssembler.closeTrackedNotify());
        MokoSupport.getInstance().disableStoreDataNotify();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
