package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class SetDeviceNameTask extends OrderTask {
    public byte[] data;

    public SetDeviceNameTask() {
        super(OrderType.DEVICE_NAME, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(String deviceName) {
        this.data = deviceName.getBytes();
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
