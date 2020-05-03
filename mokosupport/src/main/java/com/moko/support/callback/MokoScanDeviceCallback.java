package com.moko.support.callback;

import com.moko.support.entity.DeviceInfo;

/**
 * @Date 2020/4/20
 * @Author wenzheng.liu
 * @Description 
 * @ClassPath com.moko.support.callback.MokoScanDeviceCallback
 */
public interface MokoScanDeviceCallback {
    void onStartScan();

    void onScanDevice(DeviceInfo device);

    void onStopScan();
}
