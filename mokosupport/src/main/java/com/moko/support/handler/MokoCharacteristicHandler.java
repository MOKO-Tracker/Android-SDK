package com.moko.support.handler;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.text.TextUtils;

import com.moko.support.entity.MokoCharacteristic;
import com.moko.support.entity.OrderType;

import java.util.HashMap;
import java.util.List;

/**
 * @Date 2020/4/20
 * @Author wenzheng.liu
 * @Description 
 * @ClassPath com.moko.support.handler.MokoCharacteristicHandler
 */
public class MokoCharacteristicHandler {
    private static MokoCharacteristicHandler INSTANCE;

    public static final String SERVICE_UUID_HEADER_SYSTEM = "0000180a";
    public static final String SERVICE_UUID_HEADER_PARAMS = "0000ff00";

    public HashMap<OrderType, MokoCharacteristic> mokoCharacteristicMap;

    private MokoCharacteristicHandler() {
        //no instance
        mokoCharacteristicMap = new HashMap<>();
    }

    public static MokoCharacteristicHandler getInstance() {
        if (INSTANCE == null) {
            synchronized (MokoCharacteristicHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MokoCharacteristicHandler();
                }
            }
        }
        return INSTANCE;
    }

    public HashMap<OrderType, MokoCharacteristic> getCharacteristics(final BluetoothGatt gatt) {
        if (mokoCharacteristicMap != null && !mokoCharacteristicMap.isEmpty()) {
            mokoCharacteristicMap.clear();
        }
        List<BluetoothGattService> services = gatt.getServices();
        for (BluetoothGattService service : services) {
            String serviceUuid = service.getUuid().toString();
            if (TextUtils.isEmpty(serviceUuid)) {
                continue;
            }
            if (serviceUuid.startsWith("00001800")||serviceUuid.startsWith("00001801")) {
                continue;
            }
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            if (service.getUuid().toString().startsWith(SERVICE_UUID_HEADER_SYSTEM)) {
                for (BluetoothGattCharacteristic characteristic : characteristics) {
                    String characteristicUuid = characteristic.getUuid().toString();
                    if (TextUtils.isEmpty(characteristicUuid)) {
                        continue;
                    }

                    if (characteristicUuid.equals(OrderType.MANUFACTURER.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.MANUFACTURER, new MokoCharacteristic(characteristic, OrderType.MANUFACTURER));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.DEVICE_MODEL.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.DEVICE_MODEL, new MokoCharacteristic(characteristic, OrderType.DEVICE_MODEL));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.PRODUCT_DATE.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.PRODUCT_DATE, new MokoCharacteristic(characteristic, OrderType.PRODUCT_DATE));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.HARDWARE_VERSION.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.HARDWARE_VERSION, new MokoCharacteristic(characteristic, OrderType.HARDWARE_VERSION));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.FIRMWARE_VERSION.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.FIRMWARE_VERSION, new MokoCharacteristic(characteristic, OrderType.FIRMWARE_VERSION));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.SOFTWARE_VERSION.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.SOFTWARE_VERSION, new MokoCharacteristic(characteristic, OrderType.SOFTWARE_VERSION));
                        continue;
                    }
                }
            }
            if (service.getUuid().toString().startsWith(SERVICE_UUID_HEADER_PARAMS)) {
                for (BluetoothGattCharacteristic characteristic : characteristics) {
                    String characteristicUuid = characteristic.getUuid().toString();
                    if (TextUtils.isEmpty(characteristicUuid)) {
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.WRITE_CONFIG.getUuid())) {
                        gatt.setCharacteristicNotification(characteristic, true);
                        mokoCharacteristicMap.put(OrderType.WRITE_CONFIG, new MokoCharacteristic(characteristic, OrderType.WRITE_CONFIG));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.PASSWORD.getUuid())) {
                        gatt.setCharacteristicNotification(characteristic, true);
                        mokoCharacteristicMap.put(OrderType.PASSWORD, new MokoCharacteristic(characteristic, OrderType.PASSWORD));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.DISCONNECTED_NOTIFY.getUuid())) {
                        gatt.setCharacteristicNotification(characteristic, true);
                        mokoCharacteristicMap.put(OrderType.DISCONNECTED_NOTIFY, new MokoCharacteristic(characteristic, OrderType.DISCONNECTED_NOTIFY));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.STORE_DATA_NOTIFY.getUuid())) {
                        gatt.setCharacteristicNotification(characteristic, true);
                        mokoCharacteristicMap.put(OrderType.STORE_DATA_NOTIFY, new MokoCharacteristic(characteristic, OrderType.STORE_DATA_NOTIFY));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.DEVICE_TYPE.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.DEVICE_TYPE, new MokoCharacteristic(characteristic, OrderType.DEVICE_TYPE));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.UUID.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.UUID, new MokoCharacteristic(characteristic, OrderType.UUID));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.MAJOR.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.MAJOR, new MokoCharacteristic(characteristic, OrderType.MAJOR));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.MINOR.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.MINOR, new MokoCharacteristic(characteristic, OrderType.MINOR));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.MEASURE_POWER.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.MEASURE_POWER, new MokoCharacteristic(characteristic, OrderType.MEASURE_POWER));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.TRANSMISSION.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.TRANSMISSION, new MokoCharacteristic(characteristic, OrderType.TRANSMISSION));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.ADV_INTERVAL.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.ADV_INTERVAL, new MokoCharacteristic(characteristic, OrderType.ADV_INTERVAL));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.DEVICE_NAME.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.DEVICE_NAME, new MokoCharacteristic(characteristic, OrderType.DEVICE_NAME));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.BATTERY.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.BATTERY, new MokoCharacteristic(characteristic, OrderType.BATTERY));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.SCAN_MODE.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.SCAN_MODE, new MokoCharacteristic(characteristic, OrderType.SCAN_MODE));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.CONNECTION_MODE.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.CONNECTION_MODE, new MokoCharacteristic(characteristic, OrderType.CONNECTION_MODE));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.STORE_ALERT.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.STORE_ALERT, new MokoCharacteristic(characteristic, OrderType.STORE_ALERT));
                        continue;
                    }
                    if (characteristicUuid.equals(OrderType.RESET.getUuid())) {
                        mokoCharacteristicMap.put(OrderType.RESET, new MokoCharacteristic(characteristic, OrderType.RESET));
                        continue;
                    }
                }
            }
        }
        return mokoCharacteristicMap;
    }
}
