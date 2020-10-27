package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetTransmissionTask extends OrderTask {
    public byte[] data;

    public GetTransmissionTask() {
        super(OrderType.TRANSMISSION, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
