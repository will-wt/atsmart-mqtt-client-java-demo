package com.atsmart.mqtt;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关
 * @author willwt
 * @date 2025/09/25 09:47
 */
public class Gateway {

    private final String productKey;
    private final String deviceName;
    private final String deviceSecret;

    // 子设备
    private List<Device> subDevices;


    public Gateway(String productKey, String deviceName) {
        this.productKey = productKey;
        this.deviceName = deviceName;
        this.deviceSecret = null;
        this.subDevices = new ArrayList<>();
    }

    public Gateway(String productKey, String deviceName, String deviceSecret) {
        this.productKey = productKey;
        this.deviceName = deviceName;
        this.deviceSecret = deviceSecret;
        this.subDevices = new ArrayList<>();
    }

    public static Gateway of(String productKey, String deviceName) {
        return new Gateway(productKey, deviceName);
    }

    public static Gateway of(String productKey, String deviceName, String deviceSecret) {
        return new Gateway(productKey, deviceName, deviceSecret);
    }

    public void addSubDevice(Device device) {
        subDevices.add(device);
    }

    public String getProductKey() {
        return productKey;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceSecret() {
        return deviceSecret;
    }

    public List<Device> getSubDevices() {
        return subDevices;
    }

    @Override
    public String toString() {
        return "Gateway{" +
                "productKey='" + productKey + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceSecret='" + deviceSecret + '\'' +
                ", subDevices=" + subDevices +
                '}';
    }
}
