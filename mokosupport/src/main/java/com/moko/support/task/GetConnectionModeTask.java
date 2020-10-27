package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetConnectionModeTask extends OrderTask {
    public byte[] data;

    public GetConnectionModeTask() {
        super(OrderType.CONNECTION_MODE, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
