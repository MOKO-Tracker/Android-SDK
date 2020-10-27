package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetDeviceNameTask extends OrderTask {
    public byte[] data;

    public GetDeviceNameTask() {
        super(OrderType.DEVICE_NAME, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
