package com.moko.support.entity;


import java.io.Serializable;

public enum ConfigKeyEnum implements Serializable {
    GET_DEVICE_MAC(0x20),
    SET_DEVICE_MAC(0x30),

    GET_ADV_MOVE_CONDITION(0x21),
    SET_ADV_MOVE_CONDITION(0x31),

    GET_SCAN_MOVE_CONDITION(0x22),
    SET_SCAN_MOVE_CONDITION(0x32),

    GET_STORE_RSSI_CONDITION(0x23),
    SET_STORE_RSSI_CONDITION(0x33),

    GET_STORE_TIME_CONDITION(0x24),
    SET_STORE_TIME_CONDITION(0x34),

    GET_TIME(0x25),
    SET_TIME(0x35),

    CLOSE_DEVICE(0x26),
    SET_DEFAULT(0x27),

    GET_TRIGGER_ENABLE(0x28),
    SET_TRIGGER_ENABLE(0x38),

    DELETE_STORE_DATA(0x29),

    GET_MOVE_SENSITIVE(0x40),
    SET_MOVE_SENSITIVE(0x50),

    GET_FILTER_MAC(0x41),
    SET_FILTER_MAC(0x51),

    GET_FILTER_NAME(0x42),
    SET_FILTER_NAME(0x52),

    GET_FILTER_ADV(0x43),
    SET_FILTER_ADV(0x53),

    GET_FILTER_IBEACON(0x44),
    SET_FILTER_IBEACON(0x54),

    GET_FILTER_UID(0x45),
    SET_FILTER_UID(0x55),

    GET_FILTER_RAW_DATA(0x46),
    SET_FILTER_RAW_DATA(0x56),
    ;

    private int configKey;

    ConfigKeyEnum(int configKey) {
        this.configKey = configKey;
    }


    public int getConfigKey() {
        return configKey;
    }

    public static ConfigKeyEnum fromConfigKey(int configKey) {
        for (ConfigKeyEnum configKeyEnum : ConfigKeyEnum.values()) {
            if (configKeyEnum.getConfigKey() == configKey) {
                return configKeyEnum;
            }
        }
        return null;
    }
}
