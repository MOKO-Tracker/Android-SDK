package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class GetBatteryTask extends OrderTask {

    public byte[] data;

    public GetBatteryTask(MokoOrderTaskCallback callback) {
        super(OrderType.BATTERY, callback, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
