package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class GetSoftwareVersionTask extends OrderTask {

    public byte[] data;

    public GetSoftwareVersionTask(MokoOrderTaskCallback callback) {
        super(OrderType.SOFTWARE_VERSION, callback, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
