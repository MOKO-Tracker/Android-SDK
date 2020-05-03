package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class GetDeviceModelTask extends OrderTask {

    public byte[] data;

    public GetDeviceModelTask(MokoOrderTaskCallback callback) {
        super(OrderType.DEVICE_MODEL,  callback, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
