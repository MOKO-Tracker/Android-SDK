package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class GetScanModeTask extends OrderTask {
    public byte[] data;

    public GetScanModeTask(MokoOrderTaskCallback callback) {
        super(OrderType.SCAN_MODE, callback, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
