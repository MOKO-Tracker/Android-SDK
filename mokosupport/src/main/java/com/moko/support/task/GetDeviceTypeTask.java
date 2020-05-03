package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class GetDeviceTypeTask extends OrderTask {

    public byte[] data;

    public GetDeviceTypeTask(MokoOrderTaskCallback callback) {
        super(OrderType.DEVICE_TYPE,  callback, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
