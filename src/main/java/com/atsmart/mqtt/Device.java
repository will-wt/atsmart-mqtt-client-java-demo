package com.atsmart.mqtt;

/**
 * 设备（直连设备，或网关子设备）
 * @author willwt
 * @date 2025/09/25 09:45
 */
public class Device {

    private final String productKey;
    private final String deviceName;
    private final String deviceSecret;

    public Device(String productKey, String deviceName) {
        this.productKey = productKey;
        this.deviceName = deviceName;
        this.deviceSecret = null;
    }

    public Device(String productKey, String deviceName, String deviceSecret) {
        this.productKey = productKey;
        this.deviceName = deviceName;
        this.deviceSecret = deviceSecret;
    }

    public static Device of(String productKey, String deviceName) {
        return new Device(productKey, deviceName);
    }

    public static Device of(String productKey, String deviceName, String deviceSecret) {
        return new Device(productKey, deviceName, deviceSecret);
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

    @Override
    public String toString() {
        return "Device{" +
                "productKey='" + productKey + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceSecret='" + deviceSecret + '\'' +
                '}';
    }

}
