package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class GetUUIDTask extends OrderTask {
    public byte[] data;

    public GetUUIDTask(MokoOrderTaskCallback callback) {
        super(OrderType.UUID, callback, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
