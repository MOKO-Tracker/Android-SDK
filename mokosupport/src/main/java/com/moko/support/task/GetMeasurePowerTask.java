package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetMeasurePowerTask extends OrderTask {
    public byte[] data;

    public GetMeasurePowerTask() {
        super(OrderType.MEASURE_POWER, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
