package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetDeviceModelTask extends OrderTask {

    public byte[] data;

    public GetDeviceModelTask() {
        super(OrderType.DEVICE_MODEL, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
