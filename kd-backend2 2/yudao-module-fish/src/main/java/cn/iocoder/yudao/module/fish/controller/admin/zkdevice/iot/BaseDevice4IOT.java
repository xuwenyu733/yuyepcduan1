package cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备基础信息
 * @创建人 lr
 * @创建时间 2025-09-10
 * @描述
 */
@Data
@NoArgsConstructor
public class BaseDevice4IOT {

    private String imei;
    private String iccid;
    private String firmware;
    private String creg; // CREG
    private String latitude;
    private String longitude;

    public BaseDevice4IOT(String imei, String iccid, String firmware, String creg, String latitude, String longitude) {
        this.imei = imei;
        this.iccid = iccid;
        this.firmware = firmware;
        this.creg = creg;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static BaseDevice4IOT buildByJson(JSONObject jsonObject) {
        String imei = jsonObject.getString("imei");
        String iccid = jsonObject.getString("iccid");
        String firmware = jsonObject.getString("firmware");
        String creg = jsonObject.getString("CREG");
        String latitude = jsonObject.getString("latitude");
        String longitude = jsonObject.getString("longitude");
        BaseDevice4IOT base = new BaseDevice4IOT(imei, iccid, firmware, creg, latitude, longitude);
        return base;
    }
}
