package cn.iocoder.yudao.module.fish.controller.admin.zktask.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.to.params.ProofingParam;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishproofing.ZkFishProofingDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @创建人 lr
 * @创建时间 2025-08-06
 * @描述
 */
public class TaskDetail2FishProofing {


    /**
     * 将 巡检 明细数据 转换成 打样记录
     * @param obj
     * @return
     */
    public static ZkFishProofingDO transformer(TaskDetailDO obj) {
        List<ProofingParam> proofingParamList = transformer2proofing(obj);
        // 3. 将 proofingParamList 转换成 ZkFishProofingDO
        if(EasyListUtil.isEmpty(proofingParamList)) return null;
        BigDecimal totalSpecs = new BigDecimal(0);
        BigDecimal totalLength = new BigDecimal(0);
        for(ProofingParam item : proofingParamList) {
            totalSpecs = EasyBigDecimalUtil.add(item.getTableBodyWeight(), totalSpecs);
            totalLength = EasyBigDecimalUtil.add(item.getTableBodyLength(), totalLength);
        }
        BigDecimal avgSpecs = EasyBigDecimalUtil.divide(totalSpecs, new BigDecimal(proofingParamList.size()));
        BigDecimal avgLength = EasyBigDecimalUtil.divide(totalLength, new BigDecimal(proofingParamList.size()));
        ZkFishProofingDO zkFishProofingDO = new ZkFishProofingDO(obj.getBatchNumber(), obj.getWorkshopId(), obj.getWorkshopName(), obj.getPondSystemName(),
                obj.getPondSystemId(), obj.getPondNum(), obj.getPondName(), obj.getPondId(), obj.getPondDetailNum(), avgLength, avgSpecs,
                Long.parseLong(proofingParamList.size()+""), obj.getId());
        zkFishProofingDO.setCreateTime(obj.getFinishTime());
        return zkFishProofingDO;
    }


    public static List<ProofingParam> transformer2proofing(TaskDetailDO obj) {
        // 1. 找到打样的数据
        String checkContent = obj.getCheckContent();
        if(StringUtils.isEmpty(checkContent)) return null;
        JSONArray objects = JSON.parseArray(checkContent);
        JSONObject proofing = null;
        for(int i=0;i<objects.size();i++) {
            JSONObject temp = objects.getJSONObject(i);
            String typeValue = temp.getString("typeValue");
            if(StringUtils.equals(typeValue, TaskDetailTypeValueEnum.FORM.getValue())) {
                proofing = temp;
                break;
            }
        }
        if(proofing == null) return null;
        // 2. 转换成ProofingParam 列表
        List<ProofingParam> proofingParamList = new ArrayList<>();
        JSONArray detailList = proofing.getJSONArray("detailList");
        for(int i=0;i<detailList.size();i++) {
            JSONObject temp = detailList.getJSONObject(i);
            JSONArray tableValue = temp.getJSONArray("tableValue");
            for(int j=0;j<tableValue.size();j++) {
                JSONObject tempJ = tableValue.getJSONObject(j);
                if(tempJ == null) continue;
                ProofingParam tempParam = JSON.parseObject(tempJ.toJSONString(), ProofingParam.class);
                if(tempParam == null || tempParam.getTableBodyLength() == null || tempParam.getTableBodyWeight() == null)
                    continue;
                proofingParamList.add(tempParam);
            }
        }

        return proofingParamList;
    }

}
