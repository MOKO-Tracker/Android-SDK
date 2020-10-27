package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetSoftwareVersionTask extends OrderTask {

    public byte[] data;

    public GetSoftwareVersionTask() {
        super(OrderType.SOFTWARE_VERSION, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
