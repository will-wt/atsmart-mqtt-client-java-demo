package com.atsmart.mqtt;

import com.alibaba.fastjson2.JSON;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

/**
 * org.eclipse.paho.client.mqttv3 客户端
 * @author willwt
 * @date 2025/09/24 17:03
 */
public class PahoMqtt3Client {

    protected String endpoint;
    protected int port;
    protected String clientId;
    protected String username;
    protected String password;
    protected String productKey;
    protected String serverCertPath;

    private MqttClient mqttClient;


    public void createMqttClient() {
        String serverURI = null;
        if (port == 8883){
            serverURI = "ssl://" + productKey + "." + endpoint + ":" + port;
        }else if (port == 1883){
            serverURI = "tcp://" + productKey + "." + endpoint + ":" + port;
        }

        // 设置连接参数
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
        connOpts.setCleanSession(true);
        connOpts.setAutomaticReconnect(true);
        connOpts.setConnectionTimeout(10);
        connOpts.setKeepAliveInterval(120);

        try {
            if (port == 8883){
                SSLContext sslContext = createSSLContext(serverCertPath);
                // 设置SSL套接字工厂
                connOpts.setSocketFactory(sslContext.getSocketFactory());
            }

            mqttClient = new MqttClient(serverURI, clientId, new MemoryPersistence());
            mqttClient.setTimeToWait(3000L);
            // 设置回调
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("连接丢失: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println(String.format("messageArrived, topic=%s, messageId=%s", topic, message.getId()));

                    // 根据topic，选择不同的消息处理器
                    handleMessage(topic, message.getPayload());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // 消息发送完成回调
                    System.out.println("deliveryComplete, msgId=" + token.getMessageId());
                    System.out.println();
                }
            });

            mqttClient.connect(connOpts);
            System.out.println("Connect broker success, endpoint=" + endpoint + ", clientId=" + clientId);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private SSLContext createSSLContext(String serverCertPath) throws Exception {
        // 创建一个临时的空的密钥库
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);

        // 读取服务端证书
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(serverCertPath)) {
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inputStream);
            // 将服务端证书添加到信任库
            String alias = "iot-stack-mqtt-server-cert";
            keyStore.setCertificateEntry(alias, cert);
        }

        // 初始化信任管理工厂，使用我们的信任库
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        // 创建并初始化SSL上下文
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
        return sslContext;
    }

    private void handleMessage(String topic, byte[] payload) {
        // 处理接收的云端消息
        System.out.println("start handle message, topic: " + topic + ", payload: " + new String(payload));

    }

    public void subscribeTopics(List<String> topics) {
        try {
            String[] topicFilters = topics.toArray(new String[0]);
            IMqttToken mqttToken = mqttClient.subscribeWithResponse(topicFilters);
            mqttToken.waitForCompletion(3000L);
            System.out.println("Topics subscribed");
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }


    public void publishMessage(String topic, long msgId, Map<String, Object> data) {
        if (mqttClient == null || !mqttClient.isConnected()) {
            System.out.println("未连接到服务器，无法发送消息");
            return;
        }

        String method = null;
        // topic 和 method 的映射关系
        if (topic.endsWith("/thing/event/property/post")) {
            method = "thing.event.property.post";
        }

        if (method == null) {
            throw new IllegalArgumentException("topic not supported");
        }

        MessageModel message = new MessageModel();
        message.setId(String.valueOf(msgId));
        message.setMethod(method);
        message.setParams(data);

        String payloadStr = JSON.toJSONString(message);
        System.out.println("publishMessage: " + payloadStr);

        try {
            byte[] payload = payloadStr.getBytes(StandardCharsets.UTF_8);
            MqttMessage mqttMessage = new MqttMessage(payload);
            mqttMessage.setQos(1);
            mqttMessage.setId((int) (msgId % 65535));
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            System.out.println("publish error: " + e);
        }
    }

}
