package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class GetAdvIntervalTask extends OrderTask {
    public byte[] data;

    public GetAdvIntervalTask(MokoOrderTaskCallback callback) {
        super(OrderType.ADV_INTERVAL, callback, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
