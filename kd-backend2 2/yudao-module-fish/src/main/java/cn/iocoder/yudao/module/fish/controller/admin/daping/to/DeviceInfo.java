package cn.iocoder.yudao.module.fish.controller.admin.daping.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @创建人 lr
 * @创建时间 2025-03-21
 * @描述
 */
@Data
@NoArgsConstructor
public class DeviceInfo {

    private Long workshopId;
    private List<ItemVo> items = new ArrayList<>();

    public DeviceInfo(Long workshopId) {
        this.workshopId = workshopId;
    }

    public DeviceInfo build(ItemVo item) {
        items.add(item);
        return this;
    }

    public DeviceInfo gen() {
        Random random = new Random();
        this.workshopId = new Long(random.nextInt(5)+1);
        List<String> shuiWei = Arrays.asList("2.68", "3.13", "3.81", "3.68", "3.72");
        List<String> shuiWen = Arrays.asList("21", "23", "24", "29", "27");
        List<String> suanJianDu = Arrays.asList("7.3", "6.9", "8.2", "8.7", "7.9");
        List<String> rongYangNongDu = Arrays.asList("3", "4", "3.2", "4.3", "2.8");
        List<String> anDan = Arrays.asList("1", "1.2", "1.5", "0.8", "0.7");
        List<String> upDownLIst = Arrays.asList("up", "down", "up", "down", "up");

        ItemVo itemVo1 = new ItemVo("水位", shuiWei.get(random.nextInt(shuiWei.size())), "m", upDownLIst.get(random.nextInt(upDownLIst.size())));
        ItemVo itemVo2 = new ItemVo("水温", shuiWen.get(random.nextInt(shuiWei.size())), "℃", upDownLIst.get(random.nextInt(upDownLIst.size())));
        ItemVo itemVo3 = new ItemVo("酸碱度", suanJianDu.get(random.nextInt(shuiWei.size())), "PH", upDownLIst.get(random.nextInt(upDownLIst.size())));
        ItemVo itemVo4 = new ItemVo("溶氧浓度", rongYangNongDu.get(random.nextInt(shuiWei.size())), "mg/l", upDownLIst.get(random.nextInt(upDownLIst.size())));
        ItemVo itemVo5 = new ItemVo("氨氮", anDan.get(random.nextInt(shuiWei.size())), "mg/l", upDownLIst.get(random.nextInt(upDownLIst.size())));
        items.addAll(Arrays.asList(itemVo1.setKey("shuiWei"), itemVo2.setKey("shuiWen"),
                itemVo3.setKey("suanJianDu"), itemVo4.setKey("rongYangNongDu"), itemVo5.setKey("anDan")));
        return this;
    }

    @Data
    @NoArgsConstructor
    public static class ItemVo {
        private String name;
        private String value;
        private String unit;
        private String upOrDown;
        private String key;

        public ItemVo(String name, String value, String unit, String upOrDown) {
            this.name = name;
            this.value = value;
            this.unit = unit;
            this.upOrDown = upOrDown;
        }

        public ItemVo build_upOrDown(String last) {
            if(StringUtil.isEmpty(last)) last = "0";
            if(StringUtil.isEmpty(this.getValue())) this.value = "0";
            BigDecimal lastV = new BigDecimal(last);
            BigDecimal currV = new BigDecimal(this.value);
            BigDecimal subtract = EasyBigDecimalUtil.subtract(currV, lastV);
            Boolean isUp = EasyBigDecimalUtil.gt0(subtract);
            String upOrDown = "down";
            if(isUp) upOrDown = "up";
            this.setUpOrDown(upOrDown);
            return this;
        }
    }

    public DeviceInfo build_info(String rongYangNongDu, String shuiwen, String anDan, String suanJianDu) {
        ItemVo itemVo1 = this.items.get(0);
        Random random = new Random();
        List<String> upDownLIst = Arrays.asList("up", "down", "up", "down", "up");
        ItemVo itemVo2 = new ItemVo("水温", shuiwen, "℃", upDownLIst.get(random.nextInt(upDownLIst.size())));
        ItemVo itemVo3 = new ItemVo("酸碱度", suanJianDu, "PH", upDownLIst.get(random.nextInt(upDownLIst.size())));
        ItemVo itemVo4 = new ItemVo("溶氧浓度", rongYangNongDu, "mg/l", upDownLIst.get(random.nextInt(upDownLIst.size())));
        ItemVo itemVo5 = new ItemVo("氨氮", anDan, "mg/l", upDownLIst.get(random.nextInt(upDownLIst.size())));
        this.items = Arrays.asList(itemVo1, itemVo2, itemVo3, itemVo4, itemVo5);
        return this;
    }
    public DeviceInfo build_upOrDown(DeviceInfo last) {
        if(last == null) return this;
        List<ItemVo> lastItems = last.getItems();
        if(EasyListUtil.isEmpty(lastItems) || lastItems.size() < 5) return this;

        ItemVo last_itemVo1 = lastItems.get(0);
        ItemVo last_itemVo2 = lastItems.get(1);
        ItemVo last_itemVo3 = lastItems.get(2);
        ItemVo last_itemVo4 = lastItems.get(3);
        ItemVo last_itemVo5 = lastItems.get(4);

        ItemVo itemVo1 = this.items.get(0);
        ItemVo itemVo2 = this.items.get(1);
        ItemVo itemVo3 = this.items.get(2);
        ItemVo itemVo4 = this.items.get(3);
        ItemVo itemVo5 = this.items.get(4);
        this.items = Arrays.asList(itemVo1.build_upOrDown(last_itemVo1.getValue()), itemVo2.build_upOrDown(last_itemVo2.getValue()),
                itemVo3.build_upOrDown(last_itemVo3.getValue()),
                itemVo4.build_upOrDown(last_itemVo4.getValue()),
                itemVo5.build_upOrDown(last_itemVo5.getValue()));
        return this;
    }
}
