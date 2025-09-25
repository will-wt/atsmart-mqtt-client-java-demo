package com.atsmart.mqtt;

/**
 * MQTT 签名
 * @author wentao
 */
public class MqttSign {

    private String clientid;
    private String username;
    private String password;

    public String getClientid() {
        return this.clientid;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }


    public boolean isValid() {
        return this.clientid != null && this.username != null && this.password != null;
    }


    // 计算认证使用的签名
    public void calculate(String productKey, String deviceName, String deviceSecret) {
        if (productKey == null || deviceName == null || deviceSecret == null) {
            return;
        }

        try {
            long timestamp = System.currentTimeMillis();

            // MQTT 用户名
            this.username = deviceName + "&" + productKey;

            // MQTT 密码
            String plainPasswd = "clientId" + productKey + "." + deviceName + "deviceName" + deviceName
                                 + "productKey" + productKey + "timestamp" + timestamp;
            this.password = CryptoUtil.hmacSha256(plainPasswd, deviceSecret);

            // MQTT ClientId
            this.clientid = productKey + "." + deviceName + "|" + "timestamp=" + timestamp +
                            ",_v=paho-java-1.0.0,securemode=2,signmethod=hmacsha256|";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
