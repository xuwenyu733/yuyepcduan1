package cn.iocoder.yudao.module.fish.controller.admin.zktask.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.in.ErpPurchaseInPageReqVO;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.purchase.ErpPurchaseInItemDO;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.purchase.ErpPurchaseInService;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.PondFishDetailAddTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @创建人 lr
 * @创建时间 2025-08-06
 * @描述
 */
@Component
@Slf4j
public class TaskDetail2Dead {

    @Resource
    private ZkPondService zkPondService;

    @Resource
    private ErpProductService erpProductService;

    @Resource
    private ErpPurchaseInService erpPurchaseInService;

    /**
     * 将 巡检 明细数据 转换成 打样记录
     * @param obj
     * @return
     */
    public List<ZkPondFishDetailDO> transformer(TaskDetailDO obj) {
        List<ZkPondFishDetailDO> list = transformer2target(obj);

        return list;
    }


    public List<ZkPondFishDetailDO> transformer2target(TaskDetailDO obj) {
        // 1. 找到 巡检对应 的明细数据
        String checkContent = obj.getCheckContent();
        if(StringUtils.isEmpty(checkContent)) return null;
        JSONArray objects = JSON.parseArray(checkContent);
        JSONObject item = null;
        for(int i=0;i<objects.size();i++) {
            JSONObject temp = objects.getJSONObject(i);
            String typeValue = temp.getString("typeValue");
            if(StringUtils.equals(typeValue, TaskDetailCheckTypeEnum.DAY_AREA1_DEAD_SW.getValue())) {
                item = temp;
                break;
            }
        }
        if(item == null) return null;
        // 1.1 获取 默认饲料 信息
        List<ErpPurchaseInItemDO> erpPurchaseInItemDOS = erpPurchaseInService.listForBatch(new ErpPurchaseInPageReqVO(obj.getBatchNumber()));
        if(EasyListUtil.isEmpty(erpPurchaseInItemDOS)) {
            EmailUtil.send("巡检死亡记录没找到对应批次信息", "对应巡检内容:"+JSON.toJSONString(obj));
            return new ArrayList<>();
        }
        ErpPurchaseInItemDO erpPurchaseInItemDO = erpPurchaseInItemDOS.get(0); // 一般只有一个，取第一个
        // 1.2 获取 存塘记录
        ZkPondDO zkPond = zkPondService.getZkPond(obj.getPondId());
        // 2. 转换成 死亡数据
        List<ZkPondFishDetailDO> list = new ArrayList<>();
        JSONArray detailList = item.getJSONArray("detailList");
        for(int i=0;i<detailList.size();i++) {
            JSONObject temp = detailList.getJSONObject(i);
            String swFishNum = temp.getString("swFishNum_content");
            String swFishName = temp.getString("swFishName_content");
            String swFishSpec = temp.getString("swFishSpec_content");
            Long swFishNum_ = EasyBigDecimalUtil.toLong(swFishNum);
            BigDecimal swFishSpec_ = EasyBigDecimalUtil.toNumber(swFishSpec);
            if(swFishNum_ <= 0) continue;
            if(!EasyBigDecimalUtil.gt0(swFishSpec_)) swFishSpec_ = zkPond.getFishSpec();
            if(zkPond == null || zkPond.getFishNum() == null || zkPond.getFishNum() <= 0) {
                String sub = String.format("【App提交死亡记录但是该鱼池鱼数为0】pondId:%s", obj.getPondId());
                String error_msg = String.format("操作人:%s, 对应明细:%s，对应存塘:%s", TokenUtil.userInfo(), JSON.toJSONString(temp), JSON.toJSONString(zkPond));
                EmailUtil.send(sub, error_msg);
                log.error(sub, error_msg);
                continue;
            }
            Long currFishNum = zkPond.getFishNum() - swFishNum_;

            ZkPondFishDetailDO detailDO = new ZkPondFishDetailDO(null, obj.getWorkshopId(), obj.getWorkshopName(), obj.getPondName(),
                    obj.getPondNum(), obj.getPondId(), zkPond.getFishTypes(), erpPurchaseInItemDO.getProductNo(), erpPurchaseInItemDO.getProductName(),
                    swFishNum_, PondFishDetailAddTypeEnum.DEAD.getAddType(), obj.getUserId(), "", obj.getDeptId(), zkPond.getPondScale());
            detailDO.setFid(UUID.randomUUID().toString().replaceAll("-", ""))
                            .setBatchNumber(obj.getBatchNumber())
                            .setPondSystemName(obj.getPondSystemName())
                            .setPondDetailNum(obj.getPondDetailNum())
                            .setSupplierId(erpPurchaseInItemDO.getSupplierId())
                            .setSupplier(erpPurchaseInItemDO.getSupplierName())
                            .setSupplierAddress(erpPurchaseInItemDO.getSupplierAddress())
                            .setCurrFishNum(Integer.parseInt(currFishNum+""))
                            .setFishTypes(zkPond.getFishTypes())
                            .setFishSpec(swFishSpec_+"")
                            .setHistoryFishNum(Integer.parseInt(zkPond.getFishNum()+""));
            list.add(detailDO);
        }

        return list;
    }

}
