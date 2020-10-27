package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetMajorTask extends OrderTask {
    public byte[] data;

    public GetMajorTask() {
        super(OrderType.MAJOR, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
