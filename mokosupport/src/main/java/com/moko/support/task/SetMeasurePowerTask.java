package com.moko.support.task;

import android.support.annotation.IntRange;

import com.moko.support.entity.OrderType;

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
