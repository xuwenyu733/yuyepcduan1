package cn.iocoder.yudao.module.fish.controller.admin.zkdevice.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @创建人 lr
 * @创建时间 2025-09-29
 * @描述
 */
@Slf4j
@Component
public class WaterChecker {

    public static List<String> waterMetric = Arrays.asList(
            "溶氧", "PH", "温度", "ORP", "浊度", "盐度", "液位", "视觉识别_水位液面", "视觉识别_水位泡沫", "视觉识别_水体透明度", "水体二氧化碳"
    );

    /**
     * 水质传感器检测
     * TODO 目前没有对接，后续对接好了需要联调，先默认询问用户，后续对应基地有多少传感器就检测多少传感器
     * @return
     */
    public String selectWaterCheck(WaterCheckParam param) {
        String str = String.format("目前当前用户对应基地暂未对接传感器可以询问用户对应指标是否正常，水质常见检测指标有：%s", waterMetric.stream().collect(Collectors.joining("、")));
        return str;
    }


}
