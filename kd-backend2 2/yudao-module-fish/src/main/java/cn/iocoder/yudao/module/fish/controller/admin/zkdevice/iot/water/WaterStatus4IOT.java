package cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot.water;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyRegexMatcher;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot.BaseDevice4IOT;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot.Property4IOTEnum;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @创建人 lr
 * @创建时间 2025-09-10
 * @描述
 */
@Data
@NoArgsConstructor
public class WaterStatus4IOT {

    private String pondIndex; // 池号

    private String doOxygen; // 溶氧
    private String doTemp; // 温度
    private String doOxyper; // 饱和度
    private String phPh; // PH
    private BaseDevice4IOT baseDevice4IOT;

    public WaterStatus4IOT build_baseDevice4IOT(BaseDevice4IOT baseDevice4IOT) {
        this.baseDevice4IOT = baseDevice4IOT;
        return this;
    }

    public WaterStatus4IOT(String doOxygen, String doTemp, String doOxyper, String phPh) {
        this.doOxygen = doOxygen;
        this.doTemp = doTemp;
        this.doOxyper = doOxyper;
        this.phPh = phPh;
    }

    public WaterStatus4IOT(String pondIndex, String doOxygen, String doTemp, String doOxyper, String phPh) {
        this.pondIndex = pondIndex;
        this.doOxygen = doOxygen;
        this.doTemp = doTemp;
        this.doOxyper = doOxyper;
        this.phPh = phPh;
    }

    public static Map<String, WaterStatus4IOT> buildByJson(JSONObject jsonObject) {
        Set<String> pondIndexSet = new HashSet<>();
        // 1. 获取池号
        for(String key : jsonObject.keySet()) {
            List<String> tempInfo = EasyRegexMatcher.getInfo(Property4IOTEnum.OXYGEN.getReg(), key, 1);
            if(EasyListUtil.isEmpty(tempInfo) && tempInfo.size()<1) continue;
            pondIndexSet.add(tempInfo.get(0));
        }
        // 2. 获取明细信息
        Map<String, WaterStatus4IOT> map = new HashMap<>();
        for(String pondIndex : pondIndexSet) {
            WaterStatus4IOT waterStatus4IOT = selectInfoByPondIndex(pondIndex, jsonObject);
            map.put(pondIndex, waterStatus4IOT);
        }
        return map;
    }
    public static List<WaterStatus4IOT> buildByJson4List(JSONObject jsonObject) {
        Map<String, WaterStatus4IOT> stringWaterStatus4IOTMap = buildByJson(jsonObject);
        List<WaterStatus4IOT> collect = stringWaterStatus4IOTMap.keySet().stream().map(key -> stringWaterStatus4IOTMap.get(key)).collect(Collectors.toList());
        return collect;
    }

    public static WaterStatus4IOT selectInfoByPondIndex(String pondIndex, JSONObject jsonObject) {
        String oxygenKey = String.format("%s%s", Property4IOTEnum.OXYGEN.getPropertyEn(), pondIndex);
        String temperature = String.format("%s%s", Property4IOTEnum.TEMPERATURE.getPropertyEn(), pondIndex);
        String saturation = String.format("%s%s", Property4IOTEnum.SATURATION.getPropertyEn(), pondIndex);
        String ph = String.format("%s%s", Property4IOTEnum.PH.getPropertyEn(), pondIndex);

        String oxygenVal = jsonObject.getString(oxygenKey);
        String temperatureVal = jsonObject.getString(temperature);
        String saturationVal = jsonObject.getString(saturation);
        String phVal = jsonObject.getString(ph);
        WaterStatus4IOT waterStatus4IOT = new WaterStatus4IOT(pondIndex, oxygenVal, temperatureVal, saturationVal, phVal);
        return waterStatus4IOT;
    }
}
