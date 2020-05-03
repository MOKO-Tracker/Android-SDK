package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class GetTransmissionTask extends OrderTask {
    public byte[] data;

    public GetTransmissionTask(MokoOrderTaskCallback callback) {
        super(OrderType.TRANSMISSION, callback, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
