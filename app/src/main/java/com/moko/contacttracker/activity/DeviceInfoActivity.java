package com.moko.contacttracker.activity;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.moko.contacttracker.R;
import com.moko.contacttracker.dialog.AlertMessageDialog;
import com.moko.contacttracker.dialog.LoadingMessageDialog;
import com.moko.contacttracker.fragment.AdvFragment;
import com.moko.contacttracker.service.DfuService;
import com.moko.contacttracker.service.MokoService;
import com.moko.contacttracker.utils.ToastUtils;
import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.entity.ConfigKeyEnum;
import com.moko.support.entity.OrderType;
import com.moko.support.event.ConnectStatusEvent;
import com.moko.support.log.LogModule;
import com.moko.support.task.OrderTask;
import com.moko.support.task.OrderTaskResponse;
import com.moko.support.utils.MokoUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class DeviceInfoActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    public static final int REQUEST_CODE_SELECT_FIRMWARE = 0x10;

    @Bind(R.id.frame_container)
    FrameLayout frameContainer;
    @Bind(R.id.radioBtn_adv)
    RadioButton radioBtnAdv;
    @Bind(R.id.radioBtn_scanner)
    RadioButton radioBtnScanner;
    @Bind(R.id.radioBtn_setting)
    RadioButton radioBtnSetting;
    @Bind(R.id.radioBtn_device)
    RadioButton radioBtnDevice;
    @Bind(R.id.rg_options)
    RadioGroup rgOptions;
    public MokoService mMokoService;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.iv_save)
    ImageView ivSave;
    private FragmentManager fragmentManager;
    private AdvFragment advFragment;
    //    private SettingFragment settingFragment;
//    private DeviceFragment deviceFragment;
//    public String mPassword;
//    public String mDeviceMac;
//    public String mDeviceName;
//    private boolean mIsClose;
//    private ValidParams validParams;
//    private int validCount;
//    private int lockState;
    private boolean mReceiverTag = false;
    private boolean noDataExchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        ButterKnife.bind(this);
//        validParams = new ValidParams();
//        mPassword = getIntent().getStringExtra(AppConstants.EXTRA_KEY_PASSWORD);
        fragmentManager = getFragmentManager();
        initFragment();

        Intent intent = new Intent(this, MokoService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

//        showDeviceFragment();
//        showSettingFragment();
//        showSlotFragment();
        radioBtnAdv.setChecked(true);
        tvTitle.setText(R.string.title_advertiser);
        rgOptions.setOnCheckedChangeListener(this);
        EventBus.getDefault().register(this);
    }

    private void initFragment() {
        advFragment = AdvFragment.newInstance();
        fragmentManager.beginTransaction()
                .add(R.id.frame_container, advFragment)
                .show(advFragment)
                .commit();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMokoService = ((MokoService.LocalBinder) service).getService();
            // 注册广播接收器
            IntentFilter filter = new IntentFilter();
            filter.addAction(MokoConstants.ACTION_ORDER_RESULT);
            filter.addAction(MokoConstants.ACTION_ORDER_TIMEOUT);
            filter.addAction(MokoConstants.ACTION_ORDER_FINISH);
            filter.addAction(MokoConstants.ACTION_CURRENT_DATA);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.setPriority(200);
            registerReceiver(mReceiver, filter);
            mReceiverTag = true;
            if (!MokoSupport.getInstance().isBluetoothOpen()) {
                MokoSupport.getInstance().enableBluetooth();
            } else {
                if (mMokoService == null) {
                    finish();
                    return;
                }
                showSyncingProgressDialog();
                List<OrderTask> orderTasks = new ArrayList<>();
                // sync time after connect success;
                orderTasks.add(mMokoService.setTime());
                orderTasks.add(mMokoService.openDisconnectedNotify());
                orderTasks.add(mMokoService.openWriteConfigNotify());
                // get adv params
                orderTasks.add(mMokoService.getDeviceName());
                orderTasks.add(mMokoService.getUUID());
                orderTasks.add(mMokoService.getMajor());
                orderTasks.add(mMokoService.getMinor());
                orderTasks.add(mMokoService.getAdvInterval());
                orderTasks.add(mMokoService.getTransmission());
                orderTasks.add(mMokoService.getMeasurePower());
                orderTasks.add(mMokoService.getAdvTrigger());
                MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 100)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        EventBus.getDefault().cancelEventDelivery(event);
        final String action = event.getAction();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MokoConstants.ACTION_CONN_STATUS_DISCONNECTED.equals(action)) {
                    // 设备断开，通知页面更新
//                    if (mIsClose) {
//                        return;
//                    }
                    showDisconnectDialog();
                }
                if (MokoConstants.ACTION_DISCOVER_SUCCESS.equals(action)) {
                    // 设备连接成功，通知页面更新
//                    showSyncingProgressDialog();
//                    mMokoService.mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            MokoSupport.getInstance().sendOrder(mMokoService.getLockState());
//                        }
//                    }, 1000);
                }
            }
        });

    }

    private void showDisconnectDialog() {
        if (!noDataExchange) {
            if (MokoSupport.getInstance().isBluetoothOpen() && !isUpgrade) {
                AlertMessageDialog dialog = new AlertMessageDialog();
                dialog.setTitle("Dismiss");
                dialog.setMessage("The device disconnected!");
                dialog.setConfirm("Exit");
                dialog.setCancelGone();
                dialog.setOnAlertConfirmListener(() -> {
                    setResult(RESULT_OK);
                    finish();
                });
                dialog.show(getSupportFragmentManager());
            }
        } else {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setMessage("No data communication for 2 minutes, the device is disconnected.");
            dialog.setConfirm("OK");
            dialog.setCancelGone();
            dialog.setOnAlertConfirmListener(() -> {
                setResult(RESULT_OK);
                finish();
            });
            dialog.show(getSupportFragmentManager());
        }
    }

//    public void getSlotType() {
//        if (mMokoService == null) {
//            return;
//        }
//        showSyncingProgressDialog();
//        tvTitle.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                MokoSupport.getInstance().sendOrder(mMokoService.getSlotType());
//            }
//        }, 2000);
//    }
//
//    private void getDeviceInfo() {
//        if (mMokoService == null) {
//            return;
//        }
//        showSyncingProgressDialog();
//        validParams.reset();
//        MokoSupport.getInstance().sendOrder(mMokoService.getDeviceMac(),
//                mMokoService.getConnectable(),
//                mMokoService.getManufacturer(), mMokoService.getDeviceModel(),
//                mMokoService.getProductDate(), mMokoService.getHardwareVersion(),
//                mMokoService.getFirmwareVersion(), mMokoService.getSoftwareVersion(),
//                mMokoService.getBattery(), mMokoService.getLockState());
//    }

    private String unLockResponse;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                String action = intent.getAction();
                if (!BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    abortBroadcast();
                }
                if (MokoConstants.ACTION_CURRENT_DATA.equals(action)) {
                    OrderType orderType = (OrderType) intent.getSerializableExtra(MokoConstants.EXTRA_KEY_CURRENT_DATA_TYPE);
                    byte[] value = intent.getByteArrayExtra(MokoConstants.EXTRA_KEY_RESPONSE_VALUE);
                    switch (orderType) {
                        case DISCONNECTED_NOTIFY:
                            int type = value[0] & 0xFF;
                            if (type == 0) {
                                // valid password timeout
                            } else if (type == 1) {
                                // change password success
                            } else if (type == 2) {
                                // reset success
                            } else if (type == 3) {
                                // no data exchange timeout
                                noDataExchange = true;
                            }
                            break;
                    }
                }
                if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
                }
                if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                    dismissSyncProgressDialog();
//                    if (validParams.isEmpty() && validCount < 2) {
//                        validCount++;
//                        getDeviceInfo();
//                    } else {
//                        validCount = 0;
//                    }
                }
                if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                    OrderTaskResponse response = (OrderTaskResponse) intent.getSerializableExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TASK);
                    OrderType orderType = response.orderType;
                    int responseType = response.responseType;
                    byte[] value = response.responseValue;
                    switch (orderType) {
                        case DEVICE_NAME:
                            final String deviceName = new String(value);
                            advFragment.setDeviceName(deviceName);
                            break;
                        case UUID:
                            final String uuid = MokoUtils.bytesToHexString(value);
                            advFragment.setUUID(uuid);
                            break;
                        case MAJOR:
                            final int major = MokoUtils.toInt(value);
                            advFragment.setMajor(major);
                            break;
                        case MINOR:
                            final int minor = MokoUtils.toInt(value);
                            advFragment.setMinor(minor);
                            break;
                        case ADV_INTERVAL:
                            final int advInterval = MokoUtils.toInt(value);
                            advFragment.setAdvInterval(advInterval);
                            break;
                        case MEASURE_POWER:
                            int rssi_1m = value[0];
                            advFragment.setMeasurePower(rssi_1m);
                            break;
                        case TRANSMISSION:
                            int txPower = value[0];
                            advFragment.setTransmission(txPower);
                            break;
//                        case connectable:
//                            if (responseType == OrderTask.RESPONSE_TYPE_READ) {
//                                if (value.length >= 1) {
//                                    settingFragment.setConnectable(value);
//                                    validParams.connectable = MokoUtils.byte2HexString(value[0]);
//                                }
//                            }
//                            if (responseType == OrderTask.RESPONSE_TYPE_WRITE) {
//                                ToastUtils.showToast(DeviceInfoActivity.this, "Success!");
//                            }
//                            break;
                        case WRITE_CONFIG:
                            if (value.length >= 2) {
                                int key = value[1] & 0xff;
                                ConfigKeyEnum configKeyEnum = ConfigKeyEnum.fromConfigKey(key);
                                if (configKeyEnum == null) {
                                    return;
                                }
                                int length = value[3] & 0xff;
                                switch (configKeyEnum) {
                                    case GET_ADV_MOVE_CONDITION:
                                        if (length == 1) {
                                            advFragment.setAdvTriggerClose();
                                        }
                                        if (length == 2) {
                                            final int duration = MokoUtils.toInt(Arrays.copyOfRange(value, 4, 6));
                                            advFragment.setAdvTrigger(duration);
                                        }
                                        break;
                                    case SET_ADV_MOVE_CONDITION:
                                        // EB 31 00 00
                                        if (length == 0) {
                                            ToastUtils.showToast(DeviceInfoActivity.this, "Success");
                                        }
                                        break;
//                                    case GET_DEVICE_MAC:
//                                        if (value.length >= 10) {
//                                            String valueStr = MokoUtils.bytesToHexString(value);
//                                            String mac = valueStr.substring(valueStr.length() - 12).toUpperCase();
//                                            String macShow = String.format("%s:%s:%s:%s:%s:%s", mac.substring(0, 2), mac.substring(2, 4), mac.substring(4, 6), mac.substring(6, 8), mac.substring(8, 10), mac.substring(10, 12));
//                                            deviceFragment.setDeviceMac(macShow);
//                                            mDeviceMac = macShow;
//                                            validParams.mac = macShow;
//                                        }
//                                        break;
//                                    case GET_CONNECTABLE:
//                                        if (value.length >= 5) {
//                                            settingFragment.setConnectable(value);
//                                            validParams.connectable = MokoUtils.byte2HexString(value[4]);
//                                        }
//                                        break;
//                                    case GET_IBEACON_UUID:
//                                        if (value.length >= 20) {
//                                            slotFragment.setiBeaconUUID(value);
//                                        }
//                                        break;
//                                    case GET_IBEACON_INFO:
//                                        if (value.length >= 9) {
//                                            slotFragment.setiBeaconInfo(value);
//                                        }
//                                        break;
//                                    case SET_CLOSE:
//                                        if ("eb260000".equals(MokoUtils.bytesToHexString(value).toLowerCase())) {
//                                            ToastUtils.showToast(DeviceInfoActivity.this, "Success!");
//                                            settingFragment.setClose();
//                                            back();
//                                        }
//                                        break;
//                                    case GET_TRIGGER_DATA:
//                                        if (value.length >= 4) {
//                                            slotFragment.setTriggerData(value);
//                                        }
//                                        break;
                                }
                            }
                            break;
//                        case manufacturer:
//                            deviceFragment.setManufacturer(value);
//                            validParams.manufacture = "1";
//                            break;
//                        case deviceModel:
//                            deviceFragment.setDeviceModel(value);
//                            validParams.productModel = "1";
//                            break;
//                        case productDate:
//                            deviceFragment.setProductDate(value);
//                            validParams.manufactureDate = "1";
//                            break;
//                        case hardwareVersion:
//                            deviceFragment.setHardwareVersion(value);
//                            validParams.hardwareVersion = "1";
//                            break;
//                        case firmwareVersion:
//                            deviceFragment.setFirmwareVersion(value);
//                            validParams.firmwareVersion = "1";
//                            break;
//                        case softwareVersion:
//                            deviceFragment.setSoftwareVersion(value);
//                            validParams.softwareVersion = "1";
//                            break;
//                        case battery:
//                            deviceFragment.setBattery(value);
//                            validParams.battery = "1";
//                            break;
//                        case advSlotData:
//                            if (value.length >= 1) {
//                                slotFragment.setSlotData(value);
//                            }
//                            break;
//                        case radioTxPower:
//                            if (value.length >= 1) {
//                                slotFragment.setTxPower(value);
//                            }
//                            break;
//                        case advInterval:
//                            if (value.length >= 2) {
//                                slotFragment.setAdvInterval(value);
//                            }
//                            break;
//                        case advTxPower:
//                            if (value.length >= 1) {
//                                slotFragment.setAdvTxPower(value);
//                            }
//                            break;
//                        case lockState:
//                            String valueStr = MokoUtils.bytesToHexString(value);
//                            if (responseType == OrderTask.RESPONSE_TYPE_WRITE) {
//                                if ("eb63000100".equals(valueStr.toLowerCase())) {
//                                    // 设备上锁
//                                    if (isModifyPassword) {
//                                        isModifyPassword = false;
//                                        dismissSyncProgressDialog();
//                                        AlertMessageDialog dialog = new AlertMessageDialog();
//                                        dialog.setMessage("Password changed successfully! Please reconnect the Device.");
//                                        dialog.setCancelGone();
//                                        dialog.setOnAlertConfirmListener(new AlertMessageDialog.OnAlertConfirmListener() {
//                                            @Override
//                                            public void onClick() {
//                                                DeviceInfoActivity.this.setResult(DeviceInfoActivity.this.RESULT_OK);
//                                                back();
//                                            }
//                                        });
//                                        dialog.show(getSupportFragmentManager());
//                                    }
//                                }
//                            }
//                            if (responseType == OrderTask.RESPONSE_TYPE_READ) {
//                                if ("01".equals(valueStr.toLowerCase())) {
//                                    lockState = 1;
//                                    settingFragment.setNoPassword(false);
//                                } else if ("02".equals(valueStr.toLowerCase())) {
//                                    lockState = 2;
//                                    settingFragment.setNoPassword(true);
//                                }
//                                settingFragment.setModifyPasswordVisiable(!TextUtils.isEmpty(mPassword));
//                            }
//                            break;
//                        case unLock:
//                            if (responseType == OrderTask.RESPONSE_TYPE_READ) {
//                                unLockResponse = MokoUtils.bytesToHexString(value);
//                                LogModule.i("返回的随机数：" + unLockResponse);
//                                MokoSupport.getInstance().sendOrder(mMokoService.setConfigNotify(), mMokoService.setUnLock(mPassword, value));
//                            }
//                            if (responseType == OrderTask.RESPONSE_TYPE_WRITE) {
//                                MokoSupport.getInstance().sendOrder(mMokoService.getLockState());
//                            }
//                            break;
//                        case resetDevice:
//                            if (lockState == 2) {
//                                ToastUtils.showToast(DeviceInfoActivity.this, "Communication Timeout!");
//                            } else {
//                                ToastUtils.showToast(DeviceInfoActivity.this, "Reset successfully!");
//                            }
//                            break;
                    }
                }

//                if (MokoConstants.ACTION_RESPONSE_NOTIFY.equals(action)) {
//                    OrderType orderType = (OrderType) intent.getSerializableExtra(MokoConstants.EXTRA_KEY_RESPONSE_ORDER_TYPE);
//                    byte[] value = intent.getByteArrayExtra(MokoConstants.EXTRA_KEY_RESPONSE_VALUE);
//                    switch (orderType) {
//                        case notifyConfig:
//                            String valueHexStr = MokoUtils.bytesToHexString(value);
//                            if ("eb63000100".equals(valueHexStr.toLowerCase())) {
//                                ToastUtils.showToast(DeviceInfoActivity.this, "Device Locked!");
//                                back();
//                            }
//                            break;
//                    }
//                }
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            dismissSyncProgressDialog();
                            AlertDialog.Builder builder = new AlertDialog.Builder(DeviceInfoActivity.this);
                            builder.setTitle("Dismiss");
                            builder.setCancelable(false);
                            builder.setMessage("The current system of bluetooth is not available!");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    back();
                                }
                            });
                            builder.show();
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            if (mMokoService == null) {
                                return;
                            }
                            showSyncingProgressDialog();
//                            MokoSupport.getInstance().sendOrder(mMokoService.getSlotType(),
//                                    mMokoService.getDeviceMac(), mMokoService.getConnectable(),
//                                    mMokoService.getManufacturer(), mMokoService.getDeviceModel(),
//                                    mMokoService.getProductDate(), mMokoService.getHardwareVersion(),
//                                    mMokoService.getFirmwareVersion(), mMokoService.getSoftwareVersion(),
//                                    mMokoService.getBattery());
                            break;

                    }
                }
            }
        }
    };

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_SELECT_FIRMWARE) {
//            if (resultCode == RESULT_OK) {
//                //得到uri，后面就是将uri转化成file的过程。
//                Uri uri = data.getData();
//                String firmwareFilePath = FileUtils.getPath(this, uri);
//                //
//                final File firmwareFile = new File(firmwareFilePath);
//                if (firmwareFile.exists()) {
//                    final DfuServiceInitiator starter = new DfuServiceInitiator(mDeviceMac)
//                            .setDeviceName(mDeviceName)
//                            .setKeepBond(false)
//                            .setDisableNotification(true);
//                    starter.setZip(null, firmwareFilePath);
//                    starter.start(this, DfuService.class);
//                    showDFUProgressDialog("Waiting...");
//                } else {
//                    Toast.makeText(this, "file is not exists!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {
            mReceiverTag = false;
            // 注销广播
            unregisterReceiver(mReceiver);
        }
        unbindService(mServiceConnection);
        EventBus.getDefault().unregister(this);
    }

    private LoadingMessageDialog mLoadingMessageDialog;

    public void showSyncingProgressDialog() {
        mLoadingMessageDialog = new LoadingMessageDialog();
        mLoadingMessageDialog.setMessage("Syncing..");
        mLoadingMessageDialog.show(getSupportFragmentManager());

    }

    public void dismissSyncProgressDialog() {
        if (mLoadingMessageDialog != null)
            mLoadingMessageDialog.dismissAllowingStateLoss();
    }

    @OnClick({R.id.tv_back, R.id.iv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                back();
                break;
            case R.id.iv_save:
                if (radioBtnAdv.isChecked()) {
                    if (advFragment.isValid()) {
                        showSyncingProgressDialog();
                        advFragment.saveParams(mMokoService);
                    } else {
                        ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
                    }
                }
                if (radioBtnScanner.isChecked()) {

                }
                break;
        }
    }

    private void back() {
        MokoSupport.getInstance().disConnectBle();
//        mIsClose = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

//    private void showSlotFragment() {
//        if (slotFragment == null) {
//            slotFragment = SlotFragment.newInstance();
//            fragmentManager.beginTransaction().add(R.id.frame_container, slotFragment).commit();
//        } else {
//            fragmentManager.beginTransaction().hide(settingFragment).hide(deviceFragment).show(slotFragment).commit();
//        }
//        tvTitle.setText(getString(R.string.slot_title));
//    }
//
//    private void showSettingFragment() {
//        if (settingFragment == null) {
//            settingFragment = SettingFragment.newInstance();
//            fragmentManager.beginTransaction().add(R.id.frame_container, settingFragment).commit();
//        } else {
//            fragmentManager.beginTransaction().hide(slotFragment).hide(deviceFragment).show(settingFragment).commit();
//        }
//        tvTitle.setText(getString(R.string.setting_title));
//    }
//
//    private void showDeviceFragment() {
//        if (deviceFragment == null) {
//            deviceFragment = DeviceFragment.newInstance();
//            fragmentManager.beginTransaction().add(R.id.frame_container, deviceFragment).commit();
//        } else {
//            fragmentManager.beginTransaction().hide(slotFragment).hide(settingFragment).show(deviceFragment).commit();
//        }
//        tvTitle.setText(getString(R.string.device_title));
//    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.radioBtn_adv:
                tvTitle.setText(R.string.title_advertiser);
                ivSave.setVisibility(View.VISIBLE);
//                showSlotFragment();
//                getSlotType();
                break;
            case R.id.radioBtn_scanner:
                tvTitle.setText(R.string.title_scanner);
                ivSave.setVisibility(View.VISIBLE);
//                showSlotFragment();
//                getSlotType();
                break;
            case R.id.radioBtn_setting:
                tvTitle.setText(R.string.title_setting);
                ivSave.setVisibility(View.GONE);
//                showSettingFragment();
//                getDeviceInfo();
                break;
            case R.id.radioBtn_device:
                tvTitle.setText(R.string.title_device);
                ivSave.setVisibility(View.GONE);
//                showDeviceFragment();
//                getDeviceInfo();
                break;
        }
    }

//    private boolean isModifyPassword;
//
//    public void modifyPassword(String password) {
//        isModifyPassword = true;
//        showSyncingProgressDialog();
//        MokoSupport.getInstance().sendOrder(mMokoService.setLockState(password));
//    }
//
//    public void resetDevice() {
//        showSyncingProgressDialog();
//        MokoSupport.getInstance().sendOrder(mMokoService.resetDevice());
//    }
//
//
//    public void setConnectable(boolean isConneacted) {
//        showSyncingProgressDialog();
//        MokoSupport.getInstance().sendOrder(mMokoService.setConnectable(isConneacted), mMokoService.getConnectable());
//    }
//
//    public void setDirectedConnectable(boolean noPassword) {
//        showSyncingProgressDialog();
//        MokoSupport.getInstance().sendOrder(mMokoService.setLockStateDirected(noPassword), mMokoService.getLockState());
//    }
//
//    public void setClose() {
//        mIsClose = true;
//        showSyncingProgressDialog();
//        MokoSupport.getInstance().sendOrder(mMokoService.setClose());
//    }

    public void chooseFirmwareFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "select file first!"), REQUEST_CODE_SELECT_FIRMWARE);
        } catch (ActivityNotFoundException ex) {
            ToastUtils.showToast(this, "install file manager app");
        }
    }

    private ProgressDialog mDFUDialog;

    private void showDFUProgressDialog(String tips) {
        mDFUDialog = new ProgressDialog(DeviceInfoActivity.this);
        mDFUDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDFUDialog.setCanceledOnTouchOutside(false);
        mDFUDialog.setCancelable(false);
        mDFUDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDFUDialog.setMessage(tips);
        if (!isFinishing() && mDFUDialog != null && !mDFUDialog.isShowing()) {
            mDFUDialog.show();
        }
    }

    private void dismissDFUProgressDialog() {
        mDeviceConnectCount = 0;
        if (!isFinishing() && mDFUDialog != null && mDFUDialog.isShowing()) {
            mDFUDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceInfoActivity.this);
        builder.setTitle("Dismiss");
        builder.setCancelable(false);
        builder.setMessage("The device disconnected!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isUpgrade = false;
                back();
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
    }

    private int mDeviceConnectCount;
    private boolean isUpgrade;

    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
            LogModule.w("onDeviceConnecting...");
            mDeviceConnectCount++;
            if (mDeviceConnectCount > 3) {
                Toast.makeText(DeviceInfoActivity.this, "Error:DFU Failed", Toast.LENGTH_SHORT).show();
                dismissDFUProgressDialog();
                final LocalBroadcastManager manager = LocalBroadcastManager.getInstance(DeviceInfoActivity.this);
                final Intent abortAction = new Intent(DfuService.BROADCAST_ACTION);
                abortAction.putExtra(DfuService.EXTRA_ACTION, DfuService.ACTION_ABORT);
                manager.sendBroadcast(abortAction);
            }
        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            LogModule.w("onDeviceDisconnecting...");
        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
            isUpgrade = true;
            mDFUDialog.setMessage("DfuProcessStarting...");
        }


        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            mDFUDialog.setMessage("EnablingDfuMode...");
        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            mDFUDialog.setMessage("FirmwareValidating...");
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            ToastUtils.showToast(DeviceInfoActivity.this, "DFU Successfully!");
            dismissDFUProgressDialog();
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            mDFUDialog.setMessage("DfuAborted...");
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            mDFUDialog.setMessage("Progress:" + percent + "%");
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            ToastUtils.showToast(DeviceInfoActivity.this, "Opps!DFU Failed. Please try again!");
            LogModule.i("Error:" + message);
            dismissDFUProgressDialog();
        }
    };
}
