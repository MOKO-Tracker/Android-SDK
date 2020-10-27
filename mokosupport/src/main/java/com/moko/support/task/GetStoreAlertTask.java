package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetStoreAlertTask extends OrderTask {
    public byte[] data;

    public GetStoreAlertTask() {
        super(OrderType.STORE_ALERT, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
