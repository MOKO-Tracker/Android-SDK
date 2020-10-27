package com.moko.support.callback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * @Date 2020/4/20
 * @Author wenzheng.liu
 * @Description 
 * @ClassPath com.moko.support.callback.MokoResponseCallback
 */
public interface MokoResponseCallback {

    void onCharacteristicChanged(BluetoothGattCharacteristic characteristic, byte[] value);

    void onCharacteristicWrite(byte[] value);

    void onCharacteristicRead(byte[] value);

    void onServicesDiscovered(BluetoothGatt gatt);
}
