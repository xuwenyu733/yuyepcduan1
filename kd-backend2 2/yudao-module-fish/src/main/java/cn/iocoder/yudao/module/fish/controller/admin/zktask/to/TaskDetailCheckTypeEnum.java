package cn.iocoder.yudao.module.fish.controller.admin.zktask.to;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @创建人 lr
 * @创建时间 2025-04-02
 * @描述
 */
@Getter
@AllArgsConstructor
public enum TaskDetailCheckTypeEnum {


    DAY_AREA1_FISH("日巡检_养殖区_鱼", "11", "")
    ,DAY_AREA1_WATER("日巡检_养殖区_水", "12", "")
    ,DAY_AREA1_DEVICE("日巡检_养殖区_设备", "13", "")
    ,DAY_AREA1_DEAD_SW("日巡检_养殖区_死鱼", "14", "")
    ,DAY_AREA1_FEED_TW("日巡检_养殖区_投喂", "15", "")

    ,DAY_AREA2_1("日巡检_设备区_水泵微滤机", "21", "")
    ,DAY_AREA2_2("日巡检_设备区_移动床固定床脱气床", "22", "")
    ,DAY_AREA2_3("日巡检_设备区_氧锥风机", "23", "")
    ,DAY_AREA2_4("日巡检_设备区_中央控制柜液氧罐", "24", "")
    ,DAY_AREA2_5("日巡检_设备区_碱度添加系统补水源水投饵机", "25", "")

    ,REGULAR_AREA1_1("定期巡检_养殖区_鱼", "31", "")
    ,REGULAR_AREA1_2("定期巡检_养殖区_水", "32", "")
    ,REGULAR_AREA1_3("定期巡检_养殖区_打样", "33", "")

    ,REGULAR_AREA2_1("定期巡检_设备区_水泵微滤机", "41", "")
    ,REGULAR_AREA2_2("定期巡检_设备区_移动床固定床脱气床", "42", "")
    ,REGULAR_AREA2_3("定期巡检_设备区_紫外杀菌器氧锥", "43", "")
    ,REGULAR_AREA2_4("定期巡检_设备区_臭氧发生器增氧控制柜液氧罐", "44", "")
    ,REGULAR_AREA2_5("定期巡检_设备区_中央控制柜风机", "45", "")
    ,REGULAR_AREA2_6("定期巡检_设备区_水源热泵发电机补水系统", "46", "")

    ,REGULAR_AREA3_1("定期巡检_仓库区_饲料", "51", "")
    ,REGULAR_AREA3_2("定期巡检_仓库区_动保", "52", "")
    ,REGULAR_AREA3_3("定期巡检_仓库区_设备", "53", "")
    ,REGULAR_AREA3_4("定期巡检_仓库区_工具", "54", "")
    ;

    private final String name;
    private final String value;
    private final String remark;

}
