package com.moko.support.task;

import com.moko.support.entity.OrderType;

import androidx.annotation.IntRange;

public class SetScanModeTask extends OrderTask {
    public byte[] data;

    public SetScanModeTask() {
        super(OrderType.SCAN_MODE, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(@IntRange(from = 0, to = 1) int scanMode) {
        this.data = new byte[1];
        data[0] = (byte) scanMode;
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
