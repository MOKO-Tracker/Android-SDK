package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetAdvIntervalTask extends OrderTask {
    public byte[] data;

    public GetAdvIntervalTask() {
        super(OrderType.ADV_INTERVAL, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
