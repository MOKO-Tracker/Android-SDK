package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class GetDeviceNameTask extends OrderTask {
    public byte[] data;

    public GetDeviceNameTask(MokoOrderTaskCallback callback) {
        super(OrderType.DEVICE_NAME, callback, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
