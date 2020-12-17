package com.moko.support;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.moko.support.callback.MokoResponseCallback;
import com.moko.support.log.LogModule;
import com.moko.support.utils.MokoUtils;

import java.util.UUID;

import androidx.annotation.NonNull;
import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.BleManagerCallbacks;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;

public class MokoBleManager extends BleManager<BleManagerCallbacks> {

    private MokoResponseCallback mMokoResponseCallback;
    private static MokoBleManager managerInstance = null;
    private final static UUID SERVICE_UUID = UUID.fromString("0000FF00-0000-1000-8000-00805F9B34FB");
    private final static UUID STORE_DATA_UUID = UUID.fromString("0000FF0E-0000-1000-8000-00805F9B34FB");
    private final static UUID PASSWORD_UUID = UUID.fromString("0000FF08-0000-1000-8000-00805F9B34FB");
    private final static UUID WRITE_CONFIG_UUID = UUID.fromString("0000FF10-0000-1000-8000-00805F9B34FB");
    private final static UUID DISCONNECTED_UUID = UUID.fromString("0000FF0D-0000-1000-8000-00805F9B34FB");

    private BluetoothGattCharacteristic storeDataCharacteristic;
    private BluetoothGattCharacteristic passwordCharacteristic;
    private BluetoothGattCharacteristic writeConfigCharacteristic;
    private BluetoothGattCharacteristic disconnectedCharacteristic;

    public static synchronized MokoBleManager getMokoBleManager(final Context context) {
        if (managerInstance == null) {
            managerInstance = new MokoBleManager(context);
        }
        return managerInstance;
    }

    @Override
    public void log(int priority, @NonNull String message) {
        LogModule.v(message);
    }

    public MokoBleManager(@NonNull Context context) {
        super(context);
    }

    public void setBeaconResponseCallback(MokoResponseCallback mMokoResponseCallback) {
        this.mMokoResponseCallback = mMokoResponseCallback;
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return new MokoBleManagerGattCallback();
    }

    public class MokoBleManagerGattCallback extends BleManagerGattCallback {

        @Override
        protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(SERVICE_UUID);
            if (service != null) {
                storeDataCharacteristic = service.getCharacteristic(STORE_DATA_UUID);
                passwordCharacteristic = service.getCharacteristic(PASSWORD_UUID);
                writeConfigCharacteristic = service.getCharacteristic(WRITE_CONFIG_UUID);
                disconnectedCharacteristic = service.getCharacteristic(DISCONNECTED_UUID);
                enableWriteConfigNotify();
                enableDisconnectedNotify();
                enablePasswordNotify();
                return true;
            }
            return false;
        }

        @Override
        protected void onDeviceDisconnected() {

        }

        @Override
        protected void onCharacteristicWrite(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
            LogModule.e("onCharacteristicWrite");
            LogModule.e("device to app : " + MokoUtils.bytesToHexString(characteristic.getValue()));
            mMokoResponseCallback.onCharacteristicWrite(characteristic.getValue());
        }

        @Override
        protected void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
            LogModule.e("onCharacteristicRead");
            LogModule.e("device to app : " + MokoUtils.bytesToHexString(characteristic.getValue()));
            mMokoResponseCallback.onCharacteristicRead(characteristic.getValue());
        }

        @Override
        protected void onDescriptorWrite(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor) {
            LogModule.e("onDescriptorWrite");
            String characteristicUUIDStr = descriptor.getCharacteristic().getUuid().toString().toLowerCase();
            if (passwordCharacteristic.getUuid().toString().toLowerCase().equals(characteristicUUIDStr))
                mMokoResponseCallback.onServicesDiscovered(gatt);
        }
    }

    public void enableStoreDataNotify() {
        setIndicationCallback(storeDataCharacteristic).with(new DataReceivedCallback() {
            @Override
            public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
                final byte[] value = data.getValue();
                LogModule.e("onDataReceived");
                LogModule.e("device to app : " + MokoUtils.bytesToHexString(value));
                mMokoResponseCallback.onCharacteristicChanged(storeDataCharacteristic, value);
            }
        });
        enableNotifications(storeDataCharacteristic).enqueue();
    }

    public void disableStoreDataNotify() {
        disableNotifications(storeDataCharacteristic).enqueue();
    }

    public void enablePasswordNotify() {
        setIndicationCallback(passwordCharacteristic).with(new DataReceivedCallback() {
            @Override
            public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
                final byte[] value = data.getValue();
                LogModule.e("onDataReceived");
                LogModule.e("device to app : " + MokoUtils.bytesToHexString(value));
                mMokoResponseCallback.onCharacteristicChanged(passwordCharacteristic, value);
            }
        });
        enableNotifications(passwordCharacteristic).enqueue();
    }

    public void disablePasswordNotify() {
        disableNotifications(passwordCharacteristic).enqueue();
    }

    public void enableWriteConfigNotify() {
        setIndicationCallback(writeConfigCharacteristic).with(new DataReceivedCallback() {
            @Override
            public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
                final byte[] value = data.getValue();
                LogModule.e("onDataReceived");
                LogModule.e("device to app : " + MokoUtils.bytesToHexString(value));
                mMokoResponseCallback.onCharacteristicChanged(writeConfigCharacteristic, value);
            }
        });
        enableNotifications(writeConfigCharacteristic).enqueue();
    }

    public void disableWriteConfigNotify() {
        disableNotifications(writeConfigCharacteristic).enqueue();
    }

    public void enableDisconnectedNotify() {
        setIndicationCallback(disconnectedCharacteristic).with(new DataReceivedCallback() {
            @Override
            public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
                final byte[] value = data.getValue();
                LogModule.e("onDataReceived");
                LogModule.e("device to app : " + MokoUtils.bytesToHexString(value));
                mMokoResponseCallback.onCharacteristicChanged(disconnectedCharacteristic, value);
            }
        });
        enableNotifications(disconnectedCharacteristic).enqueue();
    }

    public void disableDisconnectedNotify() {
        disableNotifications(disconnectedCharacteristic).enqueue();
    }
}
