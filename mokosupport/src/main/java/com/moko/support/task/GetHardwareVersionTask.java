package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class GetHardwareVersionTask extends OrderTask {

    public byte[] data;

    public GetHardwareVersionTask(MokoOrderTaskCallback callback) {
        super(OrderType.HARDWARE_VERSION, callback, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
