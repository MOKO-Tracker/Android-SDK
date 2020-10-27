package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetScanModeTask extends OrderTask {
    public byte[] data;

    public GetScanModeTask() {
        super(OrderType.SCAN_MODE, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
