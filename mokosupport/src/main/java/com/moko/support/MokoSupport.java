package com.moko.support;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.callback.MokoResponseCallback;
import com.moko.support.callback.MokoScanDeviceCallback;
import com.moko.support.entity.MokoCharacteristic;
import com.moko.support.entity.OrderType;
import com.moko.support.event.ConnectStatusEvent;
import com.moko.support.handler.MokoCharacteristicHandler;
import com.moko.support.handler.MokoLeScanHandler;
import com.moko.support.log.LogModule;
import com.moko.support.task.OrderTask;
import com.moko.support.utils.MokoUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import no.nordicsemi.android.ble.BleManagerCallbacks;
import no.nordicsemi.android.ble.callback.FailCallback;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

/**
 * @Date 2020/4/20
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.support.MokoSupport
 */
public class MokoSupport implements MokoResponseCallback {
    public static final UUID DESCRIPTOR_UUID_NOTIFY = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private MokoLeScanHandler mMokoLeScanHandler;
    private HashMap<OrderType, MokoCharacteristic> mCharacteristicMap;
    private BlockingQueue<OrderTask> mQueue;
    private MokoScanDeviceCallback mMokoScanDeviceCallback;

    private static volatile MokoSupport INSTANCE;

    private Context mContext;

    private MokoBleManager mokoBleManager;

    private Handler mHandler;

    private MokoSupport() {
        //no instance
        mQueue = new LinkedBlockingQueue<>();
    }

    public static MokoSupport getInstance() {
        if (INSTANCE == null) {
            synchronized (MokoSupport.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MokoSupport();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Context context) {
        LogModule.init(context);
        mContext = context;
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mHandler = new Handler(Looper.getMainLooper());
        mokoBleManager = MokoBleManager.getMokoBleManager(context);
        mokoBleManager.setBeaconResponseCallback(this);
        mokoBleManager.setGattCallbacks(new BleManagerCallbacks() {
            @Override
            public void onDeviceConnecting(@NonNull BluetoothDevice device) {

            }

            @Override
            public void onDeviceConnected(@NonNull BluetoothDevice device) {
            }

            @Override
            public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
                if (isSyncData()) {
                    mQueue.clear();
                }
            }

            @Override
            public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
                ConnectStatusEvent connectStatusEvent = new ConnectStatusEvent();
                connectStatusEvent.setAction(MokoConstants.ACTION_CONN_STATUS_DISCONNECTED);
                EventBus.getDefault().post(connectStatusEvent);
            }

            @Override
            public void onLinkLossOccurred(@NonNull BluetoothDevice device) {

            }

            @Override
            public void onServicesDiscovered(@NonNull BluetoothDevice device, boolean optionalServicesFound) {

            }

            @Override
            public void onDeviceReady(@NonNull BluetoothDevice device) {

            }

            @Override
            public void onBondingRequired(@NonNull BluetoothDevice device) {

            }

            @Override
            public void onBonded(@NonNull BluetoothDevice device) {

            }

            @Override
            public void onBondingFailed(@NonNull BluetoothDevice device) {

            }

            @Override
            public void onError(@NonNull BluetoothDevice device, @NonNull String message, int errorCode) {

            }

            @Override
            public void onDeviceNotSupported(@NonNull BluetoothDevice device) {

            }
        });
    }

    public boolean isBluetoothOpen() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    public boolean isConnDevice(Context context, String address) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        int connState = bluetoothManager.getConnectionState(mBluetoothAdapter.getRemoteDevice(address), BluetoothProfile.GATT);
        return connState == BluetoothProfile.STATE_CONNECTED;
    }

    public void startScanDevice(MokoScanDeviceCallback mokoScanDeviceCallback) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            LogModule.i("Start scan");
        }
        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        List<ScanFilter> scanFilterList = Collections.singletonList(new ScanFilter.Builder().build());
        mMokoLeScanHandler = new MokoLeScanHandler(mokoScanDeviceCallback);
        scanner.startScan(scanFilterList, settings, mMokoLeScanHandler);
        mMokoScanDeviceCallback = mokoScanDeviceCallback;
        mokoScanDeviceCallback.onStartScan();
    }

    public void stopScanDevice() {
        if (mMokoLeScanHandler != null && mMokoScanDeviceCallback != null) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                LogModule.i("End scan");
            }
            final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(mMokoLeScanHandler);
            mMokoScanDeviceCallback.onStopScan();
            mMokoLeScanHandler = null;
            mMokoScanDeviceCallback = null;
        }
    }

    public void connDevice(final Context context, final String address) {
        if (TextUtils.isEmpty(address)) {
            LogModule.i("connDevice: address null");
            return;
        }
        if (!isBluetoothOpen()) {
            LogModule.i("connDevice: blutooth close");
            return;
        }
        if (isConnDevice(context, address)) {
            LogModule.i("connDevice: device connected");
            disConnectBle();
            return;
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    LogModule.i("start connect");
                    mokoBleManager.connect(device)
                            .retry(3, 100)
                            .fail(new FailCallback() {
                                @Override
                                public void onRequestFailed(@NonNull BluetoothDevice device, int status) {
                                    ConnectStatusEvent connectStatusEvent = new ConnectStatusEvent();
                                    connectStatusEvent.setAction(MokoConstants.ACTION_CONN_STATUS_DISCONNECTED);
                                    EventBus.getDefault().post(connectStatusEvent);
                                }
                            })
                            .enqueue();
                }
            });
        } else {
            LogModule.i("the device is null");
        }
    }

    public void sendOrder(OrderTask... orderTasks) {
        if (orderTasks.length == 0) {
            return;
        }
        if (!isSyncData()) {
            for (OrderTask ordertask : orderTasks) {
                if (ordertask == null) {
                    continue;
                }
                mQueue.offer(ordertask);
            }
            executeTask(null);
        } else {
            for (OrderTask ordertask : orderTasks) {
                if (ordertask == null) {
                    continue;
                }
                mQueue.offer(ordertask);
            }
        }
    }

    public void executeTask(MokoOrderTaskCallback callback) {
        if (callback != null && !isSyncData()) {
            callback.onOrderFinish();
            return;
        }
        if (mQueue.isEmpty()) {
            return;
        }
        final OrderTask orderTask = mQueue.peek();
        if (mBluetoothGatt == null) {
            LogModule.i("executeTask : BluetoothGatt is null");
            return;
        }
        if (orderTask == null) {
            LogModule.i("executeTask : orderTask is null");
            return;
        }
        if (mCharacteristicMap == null || mCharacteristicMap.isEmpty()) {
            LogModule.i("executeTask : characteristicMap is null");
            disConnectBle();
            return;
        }
        final MokoCharacteristic mokoCharacteristic = mCharacteristicMap.get(orderTask.orderType);
        if (mokoCharacteristic == null) {
            LogModule.i("executeTask : mokoCharacteristic is null");
            return;
        }
        if (orderTask.response.responseType == OrderTask.RESPONSE_TYPE_READ) {
            sendReadOrder(orderTask, mokoCharacteristic);
        }
        if (orderTask.response.responseType == OrderTask.RESPONSE_TYPE_WRITE) {
            sendWriteOrder(orderTask, mokoCharacteristic);
        }
        if (orderTask.response.responseType == OrderTask.RESPONSE_TYPE_WRITE_NO_RESPONSE) {
            sendWriteNoResponseOrder(orderTask, mokoCharacteristic);
        }
        if (orderTask.response.responseType == OrderTask.RESPONSE_TYPE_NOTIFY) {
            sendNotifyOrder(orderTask, mokoCharacteristic);
        }
        if (orderTask.response.responseType == OrderTask.RESPONSE_TYPE_DISABLE_NOTIFY) {
            sendDisableNotifyOrder(orderTask, mokoCharacteristic);
        }
        timeoutHandler(orderTask);
    }

    public synchronized boolean isSyncData() {
        return mQueue != null && !mQueue.isEmpty();
    }

    public void disConnectBle() {
        mokoBleManager.disconnect().enqueue();
    }

    public void enableBluetooth() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.enable();
        }
    }

    public void disableBluetooth() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.disable();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////

    private void sendNotifyOrder(OrderTask orderTask, final MokoCharacteristic mokoCharacteristic) {
        LogModule.i("app set device notify : " + orderTask.orderType.getName());
        mokoCharacteristic.characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        final BluetoothGattDescriptor descriptor = mokoCharacteristic.characteristic.getDescriptor(DESCRIPTOR_UUID_NOTIFY);
        if (descriptor == null) {
            return;
        }
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        });
    }

    private void sendDisableNotifyOrder(OrderTask orderTask, final MokoCharacteristic mokoCharacteristic) {
        LogModule.i("app set device notify : " + orderTask.orderType.getName());
        mokoCharacteristic.characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        final BluetoothGattDescriptor descriptor = mokoCharacteristic.characteristic.getDescriptor(DESCRIPTOR_UUID_NOTIFY);
        if (descriptor == null) {
            return;
        }
        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        });
    }

    private void sendWriteOrder(OrderTask orderTask, final MokoCharacteristic mokoCharacteristic) {
        LogModule.i("app to device write : " + orderTask.orderType.getName());
        LogModule.i(MokoUtils.bytesToHexString(orderTask.assemble()));
        mokoCharacteristic.characteristic.setValue(orderTask.assemble());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt.writeCharacteristic(mokoCharacteristic.characteristic);
            }
        });
    }

    private void sendWriteNoResponseOrder(OrderTask orderTask, final MokoCharacteristic mokoCharacteristic) {
        LogModule.i("app to device write no response : " + orderTask.orderType.getName());
        LogModule.i(MokoUtils.bytesToHexString(orderTask.assemble()));
        mokoCharacteristic.characteristic.setValue(orderTask.assemble());
        mokoCharacteristic.characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt.writeCharacteristic(mokoCharacteristic.characteristic);
            }
        });
    }


    private void sendReadOrder(OrderTask orderTask, final MokoCharacteristic mokoCharacteristic) {
        LogModule.i("app to device read : " + orderTask.orderType.getName());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt.readCharacteristic(mokoCharacteristic.characteristic);
            }
        });
    }

    public void sendDirectOrder(OrderTask orderTask) {
        final MokoCharacteristic mokoCharacteristic = mCharacteristicMap.get(orderTask.orderType);
        if (mokoCharacteristic == null) {
            LogModule.i("executeTask : mokoCharacteristic is null");
            return;
        }
        LogModule.i("app to device write no response : " + orderTask.orderType.getName());
        LogModule.i(MokoUtils.bytesToHexString(orderTask.assemble()));
        mokoCharacteristic.characteristic.setValue(orderTask.assemble());
        mokoCharacteristic.characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mBluetoothGatt.writeCharacteristic(mokoCharacteristic.characteristic);
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic, byte[] value) {
        if (!isSyncData()) {
            OrderType orderType = null;
            if (characteristic.getUuid().toString().equals(OrderType.WRITE_CONFIG.getUuid())) {
                // 写通知命令
                orderType = OrderType.WRITE_CONFIG;
            }
            // 延时应答
            if (orderType != null) {
                LogModule.i(orderType.getName());
                Intent intent = new Intent(MokoConstants.ACTION_CURRENT_DATA);
                intent.putExtra(MokoConstants.EXTRA_KEY_CURRENT_DATA_TYPE, orderType);
                intent.putExtra(MokoConstants.EXTRA_KEY_RESPONSE_VALUE, value);
                mContext.sendBroadcast(intent);
            }
        } else {
            OrderTask orderTask = mQueue.peek();
            if (value != null && value.length > 0 && orderTask != null) {
                switch (orderTask.orderType) {
                    case WRITE_CONFIG:
                    case PASSWORD:
                        formatCommonOrder(orderTask, value);
                        break;
                }
            }
        }

    }

    @Override
    public void onCharacteristicWrite(byte[] value) {
        if (!isSyncData()) {
            return;
        }
        OrderTask orderTask = mQueue.peek();
        if (value != null && value.length > 0) {
            switch (orderTask.orderType) {
                case WRITE_CONFIG:
                case UUID:
                case MAJOR:
                case MINOR:
                case MEASURE_POWER:
                case TRANSMISSION:
                case ADV_INTERVAL:
                case DEVICE_NAME:
                case SCAN_MODE:
                case CONNECTION_MODE:
                case STORE_ALERT:
                case RESET:
                    formatCommonOrder(orderTask, value);
                    break;
            }
        }
    }

    @Override
    public void onCharacteristicRead(byte[] value) {
        if (!isSyncData()) {
            return;
        }
        OrderTask orderTask = mQueue.peek();
        if (value != null && value.length > 0) {
            switch (orderTask.orderType) {
                case DEVICE_MODEL:
                case PRODUCT_DATE:
                case FIRMWARE_VERSION:
                case HARDWARE_VERSION:
                case SOFTWARE_VERSION:
                case MANUFACTURER:
                case DEVICE_TYPE:
                case UUID:
                case MAJOR:
                case MINOR:
                case MEASURE_POWER:
                case TRANSMISSION:
                case ADV_INTERVAL:
                case DEVICE_NAME:
                case BATTERY:
                case SCAN_MODE:
                case CONNECTION_MODE:
                case STORE_ALERT:
                    formatCommonOrder(orderTask, value);
                    break;
            }
        }
    }

    @Override
    public void onDescriptorWrite() {
        if (!isSyncData()) {
            return;
        }
        OrderTask orderTask = mQueue.peek();
        LogModule.i("device to app notify : " + orderTask.orderType.getName());
        orderTask.orderStatus = OrderTask.ORDER_STATUS_SUCCESS;
        mQueue.poll();
        executeTask(orderTask.callback);
    }

    @Override
    public void onBatteryValueReceived(BluetoothGatt gatt) {
        mBluetoothGatt = gatt;
        mCharacteristicMap = MokoCharacteristicHandler.getInstance().getCharacteristics(gatt);
        ConnectStatusEvent connectStatusEvent = new ConnectStatusEvent();
        connectStatusEvent.setAction(MokoConstants.ACTION_DISCOVER_SUCCESS);
        EventBus.getDefault().post(connectStatusEvent);
    }

    private void formatCommonOrder(OrderTask task, byte[] value) {
        task.orderStatus = OrderTask.ORDER_STATUS_SUCCESS;
        task.response.responseValue = value;
        mQueue.poll();
        task.callback.onOrderResult(task.response);
        executeTask(task.callback);
    }

    public void onOpenNotifyTimeout() {
        if (!mQueue.isEmpty()) {
            mQueue.clear();
        }
        disConnectBle();
    }

    public void pollTask() {
        if (mQueue != null && !mQueue.isEmpty()) {
            OrderTask orderTask = mQueue.peek();
            LogModule.i("remove " + orderTask.orderType.getName());
            mQueue.poll();
        }
    }

    public void timeoutHandler(OrderTask orderTask) {
        mHandler.postDelayed(orderTask.timeoutRunner, orderTask.delayTime);
    }
}
