package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;
import com.moko.support.utils.MokoUtils;

public class SetMeasurePowerTask extends OrderTask {
    public byte[] data;

    public SetMeasurePowerTask(MokoOrderTaskCallback callback) {
        super(OrderType.MEASURE_POWER, callback, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(int measurePower) {
        this.data = new byte[1];
        data[0] = (byte) measurePower;
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
