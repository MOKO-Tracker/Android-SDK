package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetProductDateTask extends OrderTask {

    public byte[] data;

    public GetProductDateTask() {
        super(OrderType.PRODUCT_DATE, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
