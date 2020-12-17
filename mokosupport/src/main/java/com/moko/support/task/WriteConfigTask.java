package com.moko.support.task;

import android.text.TextUtils;

import com.moko.support.entity.ConfigKeyEnum;
import com.moko.support.entity.OrderType;
import com.moko.support.utils.MokoUtils;

import java.util.Calendar;

import androidx.annotation.IntRange;

/**
 * @Date 2018/1/20
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.support.task.WriteConfigTask
 */
public class WriteConfigTask extends OrderTask {
    public byte[] data;

    public WriteConfigTask() {
        super(OrderType.WRITE_CONFIG, OrderTask.RESPONSE_TYPE_WRITE);
    }

    @Override
    public byte[] assemble() {
        return data;
    }

    public void setData(ConfigKeyEnum key) {
        switch (key) {
            case GET_DEVICE_MAC:
            case GET_ADV_MOVE_CONDITION:
            case GET_SCAN_MOVE_CONDITION:
            case GET_STORE_RSSI_CONDITION:
            case GET_STORE_TIME_CONDITION:
            case GET_TIME:
            case CLOSE_DEVICE:
            case SET_DEFAULT:
            case GET_TRIGGER_ENABLE:
            case DELETE_STORE_DATA:
            case GET_MOVE_SENSITIVE:
            case GET_FILTER_MAC:
            case GET_FILTER_NAME:
            case GET_FILTER_UUID:
            case GET_FILTER_MAJOR:
            case GET_FILTER_MINOR:
            case GET_FILTER_ADV_RAW_DATA:
            case GET_FILTER_ENABLE:
            case GET_SCAN_START_TIME:
            case GET_VIBRATIONS_NUMBER:
            case GET_FILTER_MAJOR_RANGE:
            case GET_FILTER_MINOR_RANGE:
            case SHAKE:
                createGetConfigData(key.getConfigKey());
                break;
        }
    }

    private void createGetConfigData(int configKey) {
        data = new byte[]{(byte) 0xEA, (byte) configKey, (byte) 0x00, (byte) 0x00};
    }

    public void setDeviceMac(String mac) {
        byte[] macBytes = MokoUtils.hex2bytes(mac);
        data = new byte[10];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_DEVICE_MAC.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x06;
        data[4] = macBytes[0];
        data[5] = macBytes[1];
        data[6] = macBytes[2];
        data[7] = macBytes[3];
        data[8] = macBytes[4];
        data[9] = macBytes[5];
    }

    public void setAdvMoveCondition(@IntRange(from = 0, to = 65535) int seconds) {
        if (seconds == 0) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_ADV_MOVE_CONDITION.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] secondBytes = MokoUtils.toByteArray(seconds, 2);
            data = new byte[6];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_ADV_MOVE_CONDITION.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x02;
            data[4] = secondBytes[0];
            data[5] = secondBytes[1];
        }
    }

    public void setScanMoveCondition(@IntRange(from = 0, to = 65535) int second) {
        if (second == 0) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_SCAN_MOVE_CONDITION.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] secondBytes = MokoUtils.toByteArray(second, 2);
            data = new byte[6];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_SCAN_MOVE_CONDITION.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x02;
            data[4] = secondBytes[0];
            data[5] = secondBytes[1];
        }
    }

    public void setStoreRssiCondition(@IntRange(from = -127, to = 0) int rssi) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_STORE_RSSI_CONDITION.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) rssi;
    }

    public void setStoreTimeCondition(@IntRange(from = 0, to = 255) int minute) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_STORE_TIME_CONDITION.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) minute;
    }

    public void setTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 2000;
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        data = new byte[10];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_TIME.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x06;
        data[4] = (byte) year;
        data[5] = (byte) month;
        data[6] = (byte) date;
        data[7] = (byte) hour;
        data[8] = (byte) minute;
        data[9] = (byte) second;
    }

    public void setTriggerEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_TRIGGER_ENABLE.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) enable;
    }

    public void setMoveSensitive(@IntRange(from = 7, to = 255) int sensitive) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_MOVE_SENSITIVE.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) sensitive;
    }

    public void setScanStartTime(@IntRange(from = 1, to = 4) int startTime) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_SCAN_START_TIME.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) startTime;
    }

    public void setFilterMac(String mac) {
        if (TextUtils.isEmpty(mac)) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MAC.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] macBytes = MokoUtils.hex2bytes(mac);
            int length = macBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MAC.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) length;
            data[4] = (byte) 0x01;
            for (int i = 0; i < macBytes.length; i++) {
                data[5 + i] = macBytes[i];
            }
        }
    }

    public void setFilterName(String name) {
        if (TextUtils.isEmpty(name)) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_NAME.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] nameBytes = name.getBytes();
            int length = nameBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_NAME.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) length;
            data[4] = (byte) 0x01;
            for (int i = 0; i < nameBytes.length; i++) {
                data[5 + i] = nameBytes[i];
            }
        }
    }

    public void setFilterAdvRawData(String adv) {
        if (TextUtils.isEmpty(adv)) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_ADV_RAW_DATA.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] advBytes = MokoUtils.hex2bytes(adv);
            int length = advBytes.length + 1;
            data = new byte[4 + length];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_ADV_RAW_DATA.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) length;
            data[4] = (byte) 0x01;
            for (int i = 0; i < advBytes.length; i++) {
                data[5 + i] = advBytes[i];
            }
        }
    }

//    public void setFilteriBeacon(String uuid, String major, String minor) {
//        data = new byte[27];
//        data[0] = (byte) 0xEA;
//        data[1] = (byte) ConfigKeyEnum.SET_FILTER_IBEACON.getConfigKey();
//        data[2] = (byte) 0x00;
//        data[3] = (byte) 0x17;
//        if (!TextUtils.isEmpty(uuid)) {
//            byte[] uuidBytes = MokoUtils.hex2bytes(uuid);
//            int length = uuidBytes.length;
//            data[4] = (byte) 0x01;
//            for (int i = 0; i < length; i++) {
//                data[5 + i] = uuidBytes[i];
//            }
//        } else {
//            for (int i = 0; i < 17; i++) {
//                data[4 + i] = (byte) 0x00;
//            }
//        }
//        if (!TextUtils.isEmpty(major)) {
//            byte[] majorBytes = MokoUtils.hex2bytes(major);
//            int length = majorBytes.length;
//            data[21] = (byte) 0x01;
//            for (int i = 0; i < length; i++) {
//                data[22 + i] = majorBytes[i];
//            }
//        } else {
//            for (int i = 0; i < 3; i++) {
//                data[21 + i] = (byte) 0x00;
//            }
//        }
//        if (!TextUtils.isEmpty(minor)) {
//            byte[] minorBytes = MokoUtils.hex2bytes(minor);
//            int length = minorBytes.length;
//            data[24] = (byte) 0x01;
//            for (int i = 0; i < length; i++) {
//                data[25 + i] = minorBytes[i];
//            }
//        } else {
//            for (int i = 0; i < 3; i++) {
//                data[24 + i] = (byte) 0x00;
//            }
//        }
//    }

//    public void setFilterUid(String instanceId, String namespace) {
//        data = new byte[22];
//        data[0] = (byte) 0xEA;
//        data[1] = (byte) ConfigKeyEnum.SET_FILTER_UID.getConfigKey();
//        data[2] = (byte) 0x00;
//        data[3] = (byte) 0x12;
//        if (!TextUtils.isEmpty(instanceId)) {
//            byte[] instanceIdBytes = MokoUtils.hex2bytes(instanceId);
//            int length = instanceIdBytes.length;
//            data[4] = (byte) 0x01;
//            for (int i = 0; i < length; i++) {
//                data[5 + i] = instanceIdBytes[i];
//            }
//        } else {
//            for (int i = 0; i < 11; i++) {
//                data[4 + i] = (byte) 0x00;
//            }
//        }
//        if (!TextUtils.isEmpty(namespace)) {
//            byte[] namespaceBytes = MokoUtils.hex2bytes(namespace);
//            int length = namespaceBytes.length;
//            data[15] = (byte) 0x01;
//            for (int i = 0; i < length; i++) {
//                data[16 + i] = namespaceBytes[i];
//            }
//        } else {
//            for (int i = 0; i < 7; i++) {
//                data[15 + i] = (byte) 0x00;
//            }
//        }
//    }

    public void setFilterEnable(@IntRange(from = 0, to = 1) int enable) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_FILTER_ENABLE.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) enable;
    }

    public void setFilterUUID(String uuid) {
        if (TextUtils.isEmpty(uuid)) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_UUID.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] uuidBytes = MokoUtils.hex2bytes(uuid);
            int length = uuidBytes.length;
            data = new byte[4 + length];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_UUID.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) length;
            for (int i = 0; i < uuidBytes.length; i++) {
                data[4 + i] = uuidBytes[i];
            }
        }
    }

    public void setFilterMajor(String major) {
        if (TextUtils.isEmpty(major)) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MAJOR.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] majorBytes = MokoUtils.hex2bytes(major);
            int length = majorBytes.length;
            data = new byte[4 + length];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MAJOR.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) length;
            for (int i = 0; i < majorBytes.length; i++) {
                data[4 + i] = majorBytes[i];
            }
        }
    }

    public void setFilterMinor(String minor) {
        if (TextUtils.isEmpty(minor)) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MINOR.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] minorBytes = MokoUtils.hex2bytes(minor);
            int length = minorBytes.length;
            data = new byte[4 + length];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MINOR.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) length;
            for (int i = 0; i < minorBytes.length; i++) {
                data[4 + i] = minorBytes[i];
            }
        }
    }

    public void setVibrationNumber(@IntRange(from = 1, to = 10) int vibrationNumber) {
        data = new byte[5];
        data[0] = (byte) 0xEA;
        data[1] = (byte) ConfigKeyEnum.SET_VIBRATIONS_NUMBER.getConfigKey();
        data[2] = (byte) 0x00;
        data[3] = (byte) 0x01;
        data[4] = (byte) vibrationNumber;
    }

    public void setFilterMajorRange(@IntRange(from = 0, to = 1) int enable,
                                    @IntRange(from = 0, to = 65535) int majorMin,
                                    @IntRange(from = 0, to = 65535) int majorMax) {
        if (enable == 0) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MAJOR_RANGE.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] majorMinBytes = MokoUtils.toByteArray(majorMin, 2);
            byte[] majorMaxBytes = MokoUtils.toByteArray(majorMax, 2);
            data = new byte[8];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MAJOR_RANGE.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x04;
            data[4] = majorMinBytes[0];
            data[5] = majorMinBytes[1];
            data[6] = majorMaxBytes[0];
            data[7] = majorMaxBytes[1];
        }
    }

    public void setFilterMinorRange(@IntRange(from = 0, to = 1) int enable,
                                    @IntRange(from = 0, to = 65535) int minorMin,
                                    @IntRange(from = 0, to = 65535) int minorMax) {
        if (enable == 0) {
            data = new byte[5];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MINOR_RANGE.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x01;
            data[4] = (byte) 0x00;
        } else {
            byte[] minorMinBytes = MokoUtils.toByteArray(minorMin, 2);
            byte[] minorMaxBytes = MokoUtils.toByteArray(minorMax, 2);
            data = new byte[8];
            data[0] = (byte) 0xEA;
            data[1] = (byte) ConfigKeyEnum.SET_FILTER_MINOR_RANGE.getConfigKey();
            data[2] = (byte) 0x00;
            data[3] = (byte) 0x04;
            data[4] = minorMinBytes[0];
            data[5] = minorMinBytes[1];
            data[6] = minorMaxBytes[0];
            data[7] = minorMaxBytes[1];
        }
    }
}
