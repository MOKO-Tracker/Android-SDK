package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class GetStoreAlertTask extends OrderTask {
    public byte[] data;

    public GetStoreAlertTask(MokoOrderTaskCallback callback) {
        super(OrderType.STORE_ALERT, callback, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
