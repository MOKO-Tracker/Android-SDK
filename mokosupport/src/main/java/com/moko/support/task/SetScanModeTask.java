package com.moko.support.task;

import android.support.annotation.IntRange;

import com.moko.support.entity.OrderType;

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
