package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetUUIDTask extends OrderTask {
    public byte[] data;

    public GetUUIDTask() {
        super(OrderType.UUID, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
