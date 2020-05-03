package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class GetProductDateTask extends OrderTask {

    public byte[] data;

    public GetProductDateTask(MokoOrderTaskCallback callback) {
        super(OrderType.PRODUCT_DATE, callback, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
