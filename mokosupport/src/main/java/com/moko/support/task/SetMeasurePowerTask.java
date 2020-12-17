package com.moko.support.task;

import com.moko.support.entity.OrderType;

import androidx.annotation.IntRange;

public class SetMeasurePowerTask extends OrderTask {
    public byte[] data;

    public SetMeasurePowerTask() {
        super(OrderType.MEASURE_POWER, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(@IntRange(from = -127, to = 0) int measurePower) {
        this.data = new byte[1];
        data[0] = (byte) measurePower;
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
