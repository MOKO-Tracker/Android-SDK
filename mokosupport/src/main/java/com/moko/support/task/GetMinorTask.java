package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetMinorTask extends OrderTask {
    public byte[] data;

    public GetMinorTask() {
        super(OrderType.MINOR, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
