package com.moko.support.handler;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import com.moko.support.callback.MokoScanDeviceCallback;
import com.moko.support.entity.DeviceInfo;
import com.moko.support.utils.MokoUtils;

import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

/**
 * @Date 2020/4/20
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.support.handler.MokoLeScanHandler
 */
public class MokoLeScanHandler extends ScanCallback {
    private MokoScanDeviceCallback callback;

    public MokoLeScanHandler(MokoScanDeviceCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        if (result != null) {
            BluetoothDevice device = result.getDevice();
            byte[] scanRecord = result.getScanRecord().getBytes();
            int rssi = result.getRssi();
            String name = result.getScanRecord().getDeviceName();
            if (TextUtils.isEmpty(name) || scanRecord.length == 0 || rssi == 127) {
                return;
            }
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.name = name;
            deviceInfo.rssi = rssi;
            deviceInfo.mac = device.getAddress();
            String scanRecordStr = MokoUtils.bytesToHexString(scanRecord);
            deviceInfo.scanRecord = scanRecordStr;
            deviceInfo.scanResult = result;
            callback.onScanDevice(deviceInfo);
        }
    }
}