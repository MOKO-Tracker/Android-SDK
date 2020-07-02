package com.moko.contacttracker.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.moko.contacttracker.AppConstants;
import com.moko.contacttracker.R;
import com.moko.contacttracker.adapter.BeaconListAdapter;
import com.moko.contacttracker.dialog.AlertMessageDialog;
import com.moko.contacttracker.dialog.LoadingDialog;
import com.moko.contacttracker.dialog.LoadingMessageDialog;
import com.moko.contacttracker.dialog.PasswordDialog;
import com.moko.contacttracker.dialog.ScanFilterDialog;
import com.moko.contacttracker.entity.BeaconInfo;
import com.moko.contacttracker.service.MokoService;
import com.moko.contacttracker.utils.BeaconInfoParseableImpl;
import com.moko.contacttracker.utils.SPUtiles;
import com.moko.contacttracker.utils.ToastUtils;
import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.callback.MokoScanDeviceCallback;
import com.moko.support.entity.DeviceInfo;
import com.moko.support.entity.MokoCharacteristic;
import com.moko.support.entity.OrderType;
import com.moko.support.event.ConnectStatusEvent;
import com.moko.support.handler.MokoCharacteristicHandler;
import com.moko.support.log.LogModule;
import com.moko.support.task.OrderTask;
import com.moko.support.task.OrderTaskResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Date 2020/4/18
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.contacttracker.activity.MainActivity
 */
public class MainActivity extends BaseActivity implements MokoScanDeviceCallback, BaseQuickAdapter.OnItemChildClickListener {
    @Bind(R.id.iv_refresh)
    ImageView ivRefresh;
    @Bind(R.id.rv_devices)
    RecyclerView rvDevices;
    @Bind(R.id.tv_device_num)
    TextView tvDeviceNum;
    @Bind(R.id.rl_edit_filter)
    RelativeLayout rl_edit_filter;
    @Bind(R.id.rl_filter)
    RelativeLayout rl_filter;
    @Bind(R.id.tv_filter)
    TextView tv_filter;
    private MokoService mMokoService;
    private boolean mReceiverTag = false;
    private HashMap<String, BeaconInfo> beaconInfoHashMap;
    private ArrayList<BeaconInfo> beaconInfos;
    private BeaconListAdapter adapter;
    private Animation animation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Intent intent = new Intent(this, MokoService.class);
        startService(intent);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        EventBus.getDefault().register(this);
        mSavedPassword = SPUtiles.getStringValue(this, AppConstants.SP_KEY_SAVED_PASSWORD, "");
        beaconInfoHashMap = new HashMap<>();
        beaconInfos = new ArrayList<>();
        adapter = new BeaconListAdapter();
        adapter.replaceData(beaconInfos);
        adapter.setOnItemChildClickListener(this);
        adapter.openLoadAnimation();
        rvDevices.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.shape_recycleview_divider));
        rvDevices.addItemDecoration(itemDecoration);
        rvDevices.setAdapter(adapter);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMokoService = ((MokoService.LocalBinder) service).getService();
            IntentFilter filter = new IntentFilter();
            filter.addAction(MokoConstants.ACTION_ORDER_RESULT);
            filter.addAction(MokoConstants.ACTION_ORDER_TIMEOUT);
            filter.addAction(MokoConstants.ACTION_ORDER_FINISH);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.setPriority(100);
            registerReceiver(mReceiver, filter);
            mReceiverTag = true;
            if (!MokoSupport.getInstance().isBluetoothOpen()) {
                MokoSupport.getInstance().enableBluetooth();
            } else {
                if (animation == null) {
                    startScan();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private void startScan() {
        if (!MokoSupport.getInstance().isBluetoothOpen()) {
            MokoSupport.getInstance().enableBluetooth();
            return;
        }
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
        findViewById(R.id.iv_refresh).startAnimation(animation);
        beaconInfoParseable = new BeaconInfoParseableImpl();
        MokoSupport.getInstance().startScanDevice(this);
        mMokoService.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MokoSupport.getInstance().stopScanDevice();
            }
        }, 1000 * 60);
    }


    private BeaconInfoParseableImpl beaconInfoParseable;
    public String filterName;
    public int filterRssi = -100;

    @Override
    public void onStartScan() {
        beaconInfoHashMap.clear();
        new Thread(() -> {
            while (animation != null) {
                runOnUiThread(() -> {
                    adapter.replaceData(beaconInfos);
                    tvDeviceNum.setText(String.format("DEVICE(%d)", beaconInfos.size()));
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateDevices();
            }
        }).start();
    }

    @Override
    public void onScanDevice(DeviceInfo deviceInfo) {
        BeaconInfo beaconInfo = beaconInfoParseable.parseDeviceInfo(deviceInfo);
        if (beaconInfo == null)
            return;
        beaconInfoHashMap.put(beaconInfo.mac, beaconInfo);
    }

    @Override
    public void onStopScan() {
        findViewById(R.id.iv_refresh).clearAnimation();
        animation = null;
    }

    private void updateDevices() {
        beaconInfos.clear();
        if (!TextUtils.isEmpty(filterName) || filterRssi != -100) {
            ArrayList<BeaconInfo> beaconInfosFilter = new ArrayList<>(beaconInfoHashMap.values());
            Iterator<BeaconInfo> iterator = beaconInfosFilter.iterator();
            while (iterator.hasNext()) {
                BeaconInfo beaconInfo = iterator.next();
                if (beaconInfo.rssi > filterRssi) {
                    if (TextUtils.isEmpty(filterName)) {
                        continue;
                    } else {
                        if (TextUtils.isEmpty(beaconInfo.name) && TextUtils.isEmpty(beaconInfo.mac)) {
                            iterator.remove();
                        } else if (TextUtils.isEmpty(beaconInfo.name) && beaconInfo.mac.toLowerCase().replaceAll(":", "").contains(filterName.toLowerCase())) {
                            continue;
                        } else if (TextUtils.isEmpty(beaconInfo.mac) && beaconInfo.name.toLowerCase().contains(filterName.toLowerCase())) {
                            continue;
                        } else if (!TextUtils.isEmpty(beaconInfo.name) && !TextUtils.isEmpty(beaconInfo.mac) && (beaconInfo.name.toLowerCase().contains(filterName.toLowerCase()) || beaconInfo.mac.toLowerCase().replaceAll(":", "").contains(filterName.toLowerCase()))) {
                            continue;
                        } else {
                            iterator.remove();
                        }
                    }
                } else {
                    iterator.remove();
                }
            }
            beaconInfos.addAll(beaconInfosFilter);
        } else {
            beaconInfos.addAll(beaconInfoHashMap.values());
        }
        Collections.sort(beaconInfos, (lhs, rhs) -> {
            if (lhs.rssi > rhs.rssi) {
                return -1;
            } else if (lhs.rssi < rhs.rssi) {
                return 1;
            }
            return 0;
        });
    }

    @OnClick({R.id.iv_refresh, R.id.iv_about, R.id.rl_edit_filter, R.id.rl_filter, R.id.iv_filter_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_refresh:
                if (isWindowLocked())
                    return;
                if (!MokoSupport.getInstance().isBluetoothOpen()) {
                    MokoSupport.getInstance().enableBluetooth();
                    return;
                }
                if (animation == null) {
                    startScan();
                } else {
                    mMokoService.mHandler.removeMessages(0);
                    MokoSupport.getInstance().stopScanDevice();
                }
                break;
            case R.id.iv_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.rl_edit_filter:
            case R.id.rl_filter:
                if (animation != null) {
                    mMokoService.mHandler.removeMessages(0);
                    MokoSupport.getInstance().stopScanDevice();
                }
                ScanFilterDialog scanFilterDialog = new ScanFilterDialog(this);
                scanFilterDialog.setFilterName(filterName);
                scanFilterDialog.setFilterRssi(filterRssi);
                scanFilterDialog.setOnScanFilterListener(new ScanFilterDialog.OnScanFilterListener() {
                    @Override
                    public void onDone(String filterName, int filterRssi) {
                        MainActivity.this.filterName = filterName;
                        MainActivity.this.filterRssi = filterRssi;
                        if (!TextUtils.isEmpty(filterName) || filterRssi != -100) {
                            rl_filter.setVisibility(View.VISIBLE);
                            rl_edit_filter.setVisibility(View.GONE);
                            StringBuilder stringBuilder = new StringBuilder();
                            if (!TextUtils.isEmpty(filterName)) {
                                stringBuilder.append(filterName);
                                stringBuilder.append(";");
                            }
                            if (filterRssi != -100) {
                                stringBuilder.append(String.format("%sdBm", filterRssi + ""));
                                stringBuilder.append(";");
                            }
                            tv_filter.setText(stringBuilder.toString());
                        } else {
                            rl_filter.setVisibility(View.GONE);
                            rl_edit_filter.setVisibility(View.VISIBLE);
                        }
                        if (isWindowLocked())
                            return;
                        if (animation == null) {
                            startScan();
                        }
                    }
                });
                scanFilterDialog.setOnDismissListener(dialog -> {
                    if (isWindowLocked())
                        return;
                    if (animation == null) {
                        startScan();
                    }
                });
                scanFilterDialog.show();
                break;
            case R.id.iv_filter_delete:
                if (animation != null) {
                    mMokoService.mHandler.removeMessages(0);
                    MokoSupport.getInstance().stopScanDevice();
                }
                rl_filter.setVisibility(View.GONE);
                rl_edit_filter.setVisibility(View.VISIBLE);
                filterName = "";
                filterRssi = -100;
                if (isWindowLocked())
                    return;
                if (animation == null) {
                    startScan();
                }
                break;
        }
    }

    private String mPassword;
    private String mSavedPassword;

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (!MokoSupport.getInstance().isBluetoothOpen()) {
            MokoSupport.getInstance().enableBluetooth();
            return;
        }
        BeaconInfo beaconInfo = (BeaconInfo) adapter.getItem(position);
        if (beaconInfo != null && beaconInfo.connectable == 1 && !isFinishing()) {
            if (animation != null) {
                mMokoService.mHandler.removeMessages(0);
                MokoSupport.getInstance().stopScanDevice();
            }
            // show password
            final PasswordDialog dialog = new PasswordDialog(MainActivity.this);
            dialog.setData(mSavedPassword);
            dialog.setOnPasswordClicked(new PasswordDialog.PasswordClickListener() {
                @Override
                public void onEnsureClicked(String password) {
                    if (!MokoSupport.getInstance().isBluetoothOpen()) {
                        MokoSupport.getInstance().enableBluetooth();
                        return;
                    }
                    LogModule.i(password);
                    mPassword = password;
                    if (animation != null) {
                        mMokoService.mHandler.removeMessages(0);
                        MokoSupport.getInstance().stopScanDevice();
                    }
                    showLoadingProgressDialog();
                    ivRefresh.postDelayed(() -> mMokoService.connectBluetoothDevice(beaconInfo.mac), 500);
                }

                @Override
                public void onDismiss() {

                }
            });
            dialog.show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(() -> dialog.showKeyboard());
                }
            }, 200);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
                }
                if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                }
                if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                    OrderTaskResponse response = (OrderTaskResponse) intent.getSerializableExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TASK);
                    OrderType orderType = response.orderType;
                    int responseType = response.responseType;
                    byte[] value = response.responseValue;
                    switch (orderType) {
                        case DEVICE_TYPE:
                            if (value.length < 1)
                                return;
                            int type = (value[0] & 0xFF);
                            if (type < 4 || type > 7) {
                                deviceTypeErrorAlert();
                                return;
                            }
                            dismissLoadingProgressDialog();
                            Intent i = new Intent(MainActivity.this, DeviceInfoActivity.class);
                            startActivityForResult(i, AppConstants.REQUEST_CODE_DEVICE_INFO);
//                            rvDevices.postDelayed(() -> MokoSupport.getInstance().disConnectBle(), 3000);
                            break;
                        case PASSWORD:
                            dismissLoadingMessageDialog();
                            if (value.length < 1)
                                return;
                            showLoadingProgressDialog();
                            if (0 == (value[0] & 0xFF)) {
                                mSavedPassword = mPassword;
                                SPUtiles.setStringValue(MainActivity.this, AppConstants.SP_KEY_SAVED_PASSWORD, mSavedPassword);
                                LogModule.i("Success");
                                OrderTask orderTask = mMokoService.getDeviceType();
                                MokoSupport.getInstance().sendOrder(orderTask);
                            }
                            if (1 == (value[0] & 0xFF)) {
                                ToastUtils.showToast(MainActivity.this, "Password Error");
                                MokoSupport.getInstance().disConnectBle();
                            }
                    }
                }
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            if (animation != null) {
                                mMokoService.mHandler.removeMessages(0);
                                MokoSupport.getInstance().stopScanDevice();
                                onStopScan();
                            }
                            break;
                        case BluetoothAdapter.STATE_ON:
                            if (animation == null) {
                                startScan();
                            }
                            break;
                    }
                }
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        String action = event.getAction();
        if (MokoConstants.ACTION_CONN_STATUS_DISCONNECTED.equals(action)) {
            mPassword = "";
            dismissLoadingProgressDialog();
            dismissLoadingMessageDialog();
            ToastUtils.showToast(MainActivity.this, "Disconnected");
            if (animation == null) {
                startScan();
            }
        }
        if (MokoConstants.ACTION_DISCOVER_SUCCESS.equals(action)) {
            dismissLoadingProgressDialog();
            HashMap<OrderType, MokoCharacteristic> map = MokoCharacteristicHandler.getInstance().mokoCharacteristicMap;
            if (map.containsKey(OrderType.DEVICE_TYPE)) {
                showLoadingMessageDialog();
                mMokoService.mHandler.postDelayed(() -> {
                    // open password notify and set passwrord
                    List<OrderTask> orderTasks = new ArrayList<>();
                    orderTasks.add(mMokoService.openPasswordNotify());
                    orderTasks.add(mMokoService.setPassword(mPassword));
                    MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
                }, 500);
                return;
            }
            deviceTypeErrorAlert();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_DEVICE_INFO) {
            if (resultCode == RESULT_OK) {
                if (animation == null) {
                    startScan();
                }
            }
        }
    }

    private void deviceTypeErrorAlert() {
        MokoSupport.getInstance().disConnectBle();
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setMessage("Oops! Something went wrong. Please check the device version or contact MOKO.");
        dialog.show(getSupportFragmentManager());
    }

    private LoadingDialog mLoadingDialog;

    private void showLoadingProgressDialog() {
        mLoadingDialog = new LoadingDialog();
        mLoadingDialog.show(getSupportFragmentManager());

    }

    private void dismissLoadingProgressDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog.dismissAllowingStateLoss();
    }

    private LoadingMessageDialog mLoadingMessageDialog;

    private void showLoadingMessageDialog() {
        mLoadingMessageDialog = new LoadingMessageDialog();
        mLoadingMessageDialog.setMessage("Verifying..");
        mLoadingMessageDialog.show(getSupportFragmentManager());

    }

    private void dismissLoadingMessageDialog() {
        if (mLoadingMessageDialog != null)
            mLoadingMessageDialog.dismissAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        AlertMessageDialog dialog = new AlertMessageDialog();
        dialog.setMessage(R.string.main_exit_tips);
        dialog.setOnAlertConfirmListener(() -> MainActivity.this.finish());
        dialog.show(getSupportFragmentManager());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {
            mReceiverTag = false;
            unregisterReceiver(mReceiver);
        }
        unbindService(mServiceConnection);
        stopService(new Intent(this, MokoService.class));
        EventBus.getDefault().unregister(this);
    }
}
