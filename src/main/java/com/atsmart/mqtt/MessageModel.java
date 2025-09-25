package com.atsmart.mqtt;

import java.util.Map;

/**
 * 数据格式参照：https://help.aliyun.com/zh/iot/user-guide/device-properties-events-and-services
 * @author willwt
 * @date 2025/09/24 16:57
 */
public class MessageModel {

    // 版本号，默认为 1.0
    private String version = "1.0";

    // 消息ID，取值范围0~4294967295，且每个消息ID在当前设备中具有唯一性
    private String id;

    // 请求方法，上报设备属性消息的 method 为：thing.event.property.post
    private String method;

    // 请求参数
    private Map<String, Object> params;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

}
