package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetDeviceTypeTask extends OrderTask {

    public byte[] data;

    public GetDeviceTypeTask() {
        super(OrderType.DEVICE_TYPE, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
