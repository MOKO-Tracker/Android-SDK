package com.moko.support.task;

import com.moko.support.callback.MokoOrderTaskCallback;
import com.moko.support.entity.OrderType;

public class SetScanModeTask extends OrderTask {
    public byte[] data;

    public SetScanModeTask(MokoOrderTaskCallback callback) {
        super(OrderType.SCAN_MODE, callback, OrderTask.RESPONSE_TYPE_WRITE);
    }

    public void setData(int scanMode) {
        this.data = new byte[1];
        data[0] = (byte) scanMode;
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
