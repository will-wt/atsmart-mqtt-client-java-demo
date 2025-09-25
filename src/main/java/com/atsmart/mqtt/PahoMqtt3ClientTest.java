package com.atsmart.mqtt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * org.eclipse.paho.client.mqttv3 客户端DEMO
 * @author willwt
 * @date 2025/09/24 10:30
 */
public class PahoMqtt3ClientTest {

    public static void main(String[] args) {
        PahoMqtt3Client mqttClient = new PahoMqtt3Client();
        mqttClient.endpoint = "mqtt.iot.ataiot.com";
        mqttClient.port = 8883;
        mqttClient.clientId = "dev003@QKWpocicMQf";
        // 使用mqtt的 username/password 的认证方式
        mqttClient.username = "******";
        mqttClient.password = "******";
        mqttClient.productKey = "QKWpocicMQf";
        // 从物联平台下载的证书
        mqttClient.serverCertPath = "cert/ataiot-mqtt-ca.crt";

        // 1. 创建 MQTT 客户端
        mqttClient.createMqttClient();

        // 2. 订阅topic，接收下行消息
        String productKey = "QKWpocicMQf";
        String deviceName = "dev003";
        subscribeTopics(mqttClient, productKey, deviceName);


        // 3. 模拟发布消息到云平台
        AtomicLong msgId = new AtomicLong(0);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        // 上行topic格式：$aiot/sys/${productKey}/${deviceName}/thing/event/property/post
        String topicUp = "$aiot/sys/"+ productKey +"/"+ deviceName +"/thing/event/property/post";

        executorService.scheduleAtFixedRate(() -> {
            mockPublishMessage(mqttClient, topicUp, msgId.getAndIncrement());
        }, 10, 20, TimeUnit.SECONDS);

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void subscribeTopics(PahoMqtt3Client mqttClient, String productKey, String deviceName) {
        // 下行topic格式：$aiot/sys/${productKey}/${deviceName}/thing/event/property/set
        String topic1 = "$aiot/sys/"+ productKey +"/"+ deviceName +"/thing/event/property/set";
        // 通配符订阅
        String topic2 = "$aiot/sys/67KW0fRavxQ/+/thing/event/property/set";

        String topic3 = "post/TempHumi/property";

        mqttClient.subscribeTopics(List.of(topic1, topic2, topic3));
    }

    private static void mockPublishMessage(PahoMqtt3Client mqttClient, String topic, long msgId) {
        // 模拟温度 和 湿度 数值
        // 26-38度
        int temperature = 26 + (int) (Math.random() * 12);
        // 50-90%
        int humidity = 50 + (int) (Math.random() * 40);

        Map<String, Object> data = new HashMap<>();
        data.put("Temperature", temperature);
        data.put("Humidity", humidity);

        if (msgId % 10 == 0){
            data.put("PowerSwitch", 1);
        }

        mqttClient.publishMessage(topic, msgId, data);
    }

}
