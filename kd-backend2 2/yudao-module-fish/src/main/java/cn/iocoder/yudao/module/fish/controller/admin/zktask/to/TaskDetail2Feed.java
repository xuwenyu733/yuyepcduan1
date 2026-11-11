package cn.iocoder.yudao.module.fish.controller.admin.zktask.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpWarehouseDO;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.stock.ErpWarehouseService;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.to.params.ProofingParam;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishproofing.ZkFishProofingDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @创建人 lr
 * @创建时间 2025-08-06
 * @描述
 */
@Component
@Slf4j
public class TaskDetail2Feed {

    @Resource
    private ZkPondService zkPondService;

    @Resource
    private ErpProductService erpProductService;

    @Resource
    private ErpWarehouseService erpWarehouseService;

    /**
     * 将 巡检 明细数据 转换成 打样记录
     * @param obj
     * @return
     */
    public List<ZkFeedingLogDO> transformer(TaskDetailDO obj) {
        List<ZkFeedingLogDO> list = transformer2target(obj);

        return list;
    }


    public List<ZkFeedingLogDO> transformer2target(TaskDetailDO obj) {
        // 1. 找到 巡检对应 的明细数据
        String checkContent = obj.getCheckContent();
        if(StringUtils.isEmpty(checkContent)) return null;
        JSONArray objects = JSON.parseArray(checkContent);
        JSONObject item = null;
        for(int i=0;i<objects.size();i++) {
            JSONObject temp = objects.getJSONObject(i);
            String typeValue = temp.getString("typeValue");
            if(StringUtils.equals(typeValue, TaskDetailCheckTypeEnum.DAY_AREA1_FEED_TW.getValue())) {
                item = temp;
                break;
            }
        }
        if(item == null) return null;
        // 1.1 获取 默认饲料 信息
        ErpProductDO erpProductDO = erpProductService.getDefaultFeedProduct(obj.getTenantId(), obj.getDeptId());
        if(erpProductDO == null) {
            EmailUtil.send("巡检投喂记录没找到默认商品", "对应巡检内容:"+JSON.toJSONString(obj));
            return new ArrayList<>();
        }
        // 1.2 获取 默认仓库 信息
        ErpWarehouseDO erpWarehouseDO = erpWarehouseService.getDefaultWarehouse(obj.getTenantId(), obj.getDeptId());
        if(erpWarehouseDO == null) {
            EmailUtil.send("巡检投喂记录没找到默认仓库", "对应巡检内容:"+JSON.toJSONString(obj));
            return new ArrayList<>();
        }
        // 2. 转换成 投喂数据
        List<ZkFeedingLogDO> list = new ArrayList<>();
        JSONArray detailList = item.getJSONArray("detailList");
        for(int i=0;i<detailList.size();i++) {
            JSONObject temp = detailList.getJSONObject(i);
            String twFeedCount = temp.getString("twFeedCount_content");
            // String twFeedName = temp.getString("twFeedName_content");
            String twPersonScore = temp.getString("twPersonScore_content");
            BigDecimal twFeedCount_ = EasyBigDecimalUtil.toNumber(twFeedCount);
            BigDecimal twPersonScore_ = EasyBigDecimalUtil.toNumber(twPersonScore);
            if(!EasyBigDecimalUtil.gt0(twFeedCount_)) continue;

            ZkFeedingLogDO feedingLogDO = new ZkFeedingLogDO(obj.getWorkshopId(), obj.getWorkshopName(), obj.getPondSystemName(),
                    obj.getPondNum(), obj.getPondDetailNum(), obj.getPondName(), obj.getPondId(), twFeedCount_, twPersonScore_);
            BigDecimal feedingCost = EasyBigDecimalUtil.multiply(erpProductDO.getPurchasePrice(), twFeedCount_);
            feedingLogDO.setFeedBrand(erpProductDO.getProductBrand())
                            .setFeedSpec(erpProductDO.getStandard())
                    .setProductId(erpProductDO.getId()).setWarehouseId(erpWarehouseDO.getId())
                            .setFeedTime(LocalDateTime.now())
                            .setUserId(obj.getUserId()).setDeptId(obj.getDeptId()).setTenantId(obj.getTenantId())
                            .setProductNo(erpProductDO.getProductNo())
                            .setFeedingPrice(erpProductDO.getPurchasePrice())
                            .setFeedingCost(feedingCost)
                            .setFeedName(erpProductDO.getName());
            list.add(feedingLogDO);
        }

        return list;
    }

}
