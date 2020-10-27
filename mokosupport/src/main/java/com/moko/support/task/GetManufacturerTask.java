package com.moko.support.task;

import com.moko.support.entity.OrderType;

public class GetManufacturerTask extends OrderTask {

    public byte[] data;

    public GetManufacturerTask() {
        super(OrderType.MANUFACTURER, OrderTask.RESPONSE_TYPE_READ);
    }

    @Override
    public byte[] assemble() {
        return data;
    }
}
