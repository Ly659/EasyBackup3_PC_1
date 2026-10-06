package com.ly659.easybackup3.connect_helper.packages;

import java.io.Serializable;

/**
 * 此类用于PC/App之间的连接握手。
 */
public class HandShake implements Serializable {
    private final String deviceName;      // App端设备名称


    public HandShake(String deviceName) {
        if (deviceName == null) {
            throw new IllegalArgumentException("deviceName cannot be null");
        }
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

}
