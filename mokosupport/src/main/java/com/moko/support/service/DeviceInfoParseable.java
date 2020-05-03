package com.moko.support.service;

import com.moko.support.entity.DeviceInfo;

/**
 * @Date 2020/4/20
 * @Author wenzheng.liu
 * @Description 
 * @ClassPath com.moko.support.service.DeviceInfoParseable
 */
public interface DeviceInfoParseable<T> {
    T parseDeviceInfo(DeviceInfo deviceInfo);
}
