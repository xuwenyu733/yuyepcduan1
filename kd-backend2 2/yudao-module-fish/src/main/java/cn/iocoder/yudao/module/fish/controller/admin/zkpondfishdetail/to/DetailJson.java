package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @创建人 lr
 * @创建时间 2025-03-10
 * @描述
 */
@Data
@NoArgsConstructor
public class DetailJson {

    private List<DetailJsonItem> detailJson;

    public DetailJson(List<DetailJsonItem> detailJson) {
        this.detailJson = detailJson;
    }

    public static DetailJson build(DetailJsonItem detailJsonItem) {
        List<DetailJsonItem> detailJson = new ArrayList<>();
        detailJson.add(detailJsonItem);
        DetailJson json = new DetailJson(detailJson);
        return json;
    }

    public void update(DetailJsonItem detailJsonItem, Integer addType) {
        if(EasyListUtil.isEmpty(detailJson)) {
            // detailJson 为空
            detailJson = new ArrayList<>();
            detailJson.add(detailJsonItem);
        }else {
            // 1. detailJson 不为空，需要更新 当前鱼苗数量
            DetailJsonItem currItem = getCurrItem(detailJsonItem);
            if(currItem == null) {
                // 1.1 之前没有投放过该类型，直接添加
                detailJson.add(detailJsonItem);
            } else {
                // 1.1 投放过，直接修改
                Long resNum = addType>=0? currItem.getFishNum()+detailJsonItem.getFishNum():currItem.getFishNum()-detailJsonItem.getFishNum();
                currItem.setFishName(detailJsonItem.getFishName());
                currItem.setFishNum(resNum);
            }
        }
    }

    public DetailJsonItem getCurrItem(DetailJsonItem detailJsonItem) {
        for(DetailJsonItem curr : detailJson) {
            if(StringUtils.equals(curr.getProductNo(), detailJsonItem.getProductNo())) { // curr.getFishType(), detailJsonItem.getFishType()
                return curr;
            }
        }
        return null;
    }

}
