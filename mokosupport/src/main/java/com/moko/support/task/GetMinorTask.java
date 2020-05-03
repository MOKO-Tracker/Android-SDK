package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class GetMinorTask extends OrderTask {
    public byte[] data;

    public GetMinorTask(MokoOrderTaskCallback callback) {
        super(OrderType.MINOR, callback, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
