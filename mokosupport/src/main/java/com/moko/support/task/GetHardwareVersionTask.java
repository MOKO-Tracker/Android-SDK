package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetHardwareVersionTask extends OrderTask {

    public byte[] data;

    public GetHardwareVersionTask() {
        super(OrderType.HARDWARE_VERSION, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
