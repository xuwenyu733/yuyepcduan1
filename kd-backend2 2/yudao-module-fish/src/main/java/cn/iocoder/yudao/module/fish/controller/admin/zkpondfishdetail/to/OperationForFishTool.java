package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * 鱼苗、分池、销售、死亡 由 zk_pond_fish_detail 汇总到 zk_pond_fish
 * @创建人 lr
 * @创建时间 2025-07-18
 * @描述
 */
public class OperationForFishTool {


    /**
     * 投放 由 zk_pond_fish_detail 汇总到 zk_pond_fish
     * @param zkPondFishDetail
     * @return
     */
    public static ZkPondFishDO parseAdd(ZkPondFishDetailDO zkPondFishDetail) {
        // String workshopName, Long workshopId, String pondName, Long pondId, String fishTypes, Long fishNum, Long userId, Long deptId
        ZkPondFishDO zkPondFishDO = new ZkPondFishDO();
        CommonUtil.copyPropertiesIgnoreNull(zkPondFishDetail, zkPondFishDO);
        enhancerFishNum(zkPondFishDetail, zkPondFishDO);

        return zkPondFishDO;
    }

    /**
     * 分池到目标塘 由 zk_pond_fish_detail 汇总到 zk_pond_fish
     * @param zkPondFishDetail
     * @return
     */
    public static ZkPondFishDO parseSplit(ZkPondFishDetailDO zkPondFishDetail) {
        // String workshopName, Long workshopId, String pondName, Long pondId, String fishTypes, Long fishNum, Long userId, Long deptId
        ZkPondFishDO zkPondFishDO = new ZkPondFishDO();
        CommonUtil.copyPropertiesIgnoreNull(zkPondFishDetail, zkPondFishDO);
        zkPondFishDO.build_for_fenzhi(zkPondFishDetail); // 这里的pondId为 目标塘 的，所以需要重新设置
        zkPondFishDO.build_splitAddNum(zkPondFishDetail.getFishNum()).build_specInfo(zkPondFishDetail.getTargetFishSpec());
        return zkPondFishDO;
    }

    /**
     * 分池到目标塘 由 zk_pond_fish_detail 汇总到 zk_pond_fish ， 自己给自己分池
     * @param zkPondFishDetail
     * @return
     */
    public static ZkPondFishDO parseSplit4Safe(ZkPondFishDetailDO zkPondFishDetail) {
        // String workshopName, Long workshopId, String pondName, Long pondId, String fishTypes, Long fishNum, Long userId, Long deptId
        ZkPondFishDO zkPondFishDO = new ZkPondFishDO();
        CommonUtil.copyPropertiesIgnoreNull(zkPondFishDetail, zkPondFishDO);
        zkPondFishDO.build_for_fenzhi(zkPondFishDetail); // 这里的pondId为 目标塘 的，所以需要重新设置
        zkPondFishDO.setFishNum(zkPondFishDetail.getFishNum()).build_specInfo(zkPondFishDetail.getTargetFishSpec());
        return zkPondFishDO;
    }

    /**
     * 执行更新操作
     * @param zkPondFishDetail
     * @param zkPondFishDO
     */
    public static void parseUpdate(ZkPondFishDetailDO zkPondFishDetail, ZkPondFishDO zkPondFishDO) {
        String fishTypes = zkPondFishDO.getFishTypes();
        if(StringUtils.isNotBlank(zkPondFishDetail.getFishTypes())) {
            if(StringUtils.isNotBlank(fishTypes) && !fishTypes.contains(zkPondFishDetail.getFishTypes())) {
                String newFishTypes = fishTypes + "," + zkPondFishDetail.getFishTypes();
                zkPondFishDO.setFishTypes(newFishTypes);
            } else if(StringUtils.isEmpty(fishTypes)) {
                zkPondFishDO.setFishTypes(zkPondFishDetail.getFishTypes());
            }
        }
        Long currFishNum = zkPondFishDO.getFishNum();
        if(Integer.parseInt(zkPondFishDetail.getAddType()) >= 0) {
            currFishNum += zkPondFishDetail.getFishNum();
        } else {
            currFishNum -= zkPondFishDetail.getFishNum();
        }
        enhancerFishNum(zkPondFishDetail, zkPondFishDO);

        zkPondFishDO.setFishNum(currFishNum);
    }

    public static void enhancerFishNum(ZkPondFishDetailDO zkPondFishDetail, ZkPondFishDO zkPondFishDO) {
        BigDecimal fishSpec = EasyBigDecimalUtil.toNumber(zkPondFishDetail.getFishSpec());
        if(StringUtils.equals(PondFishDetailAddTypeEnum.ADD.getAddType(), zkPondFishDetail.getAddType())) {
            zkPondFishDO.build_pushFishNum(zkPondFishDetail.getFishNum())
                    .build_specInfo(zkPondFishDetail.getFishSpec());
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.DEAD.getAddType(), zkPondFishDetail.getAddType())) {
            zkPondFishDO.build_deadFishNum(zkPondFishDetail.getFishNum())
                    .build_currFishSpec(fishSpec);
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.SALE.getAddType(), zkPondFishDetail.getAddType())) {
            zkPondFishDO.build_saleFishNum(zkPondFishDetail.getFishNum())
                    .build_currFishSpec(fishSpec)
                    .setSaleWeight(zkPondFishDetail.getSaleWeight());
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.SPLIT.getAddType(), zkPondFishDetail.getAddType())) {
            BigDecimal targetFishSpec = EasyBigDecimalUtil.toNumber(zkPondFishDetail.getTargetFishSpec());
            zkPondFishDO.build_splitReduceNum(zkPondFishDetail.getFishNum())
                    .build_currFishSpec(targetFishSpec);
        }
    }

    /**
     * 之前有 zk_pond_fish , 则执行更新逻辑
     * @param zkPondFishDetail
     * @param zkPondFishDO
     */
    public static void parseUpdateForSplit(ZkPondFishDetailDO zkPondFishDetail, ZkPondFishDO zkPondFishDO) {
        String fishTypes = zkPondFishDO.getFishTypes();
        if(StringUtils.isNotBlank(fishTypes) && !fishTypes.contains(zkPondFishDetail.getFishTypes())) {
            String newFishTypes = new StringBuilder(fishTypes).append(",").append(fishTypes).toString();
            zkPondFishDO.setFishTypes(newFishTypes);
        }
        Long currFishNum = zkPondFishDO.getFishNum();
        currFishNum += zkPondFishDetail.getFishNum();
        zkPondFishDO.setFishNum(currFishNum);
        zkPondFishDO.build_splitAddNum(zkPondFishDetail.getFishNum())
                .build_currFishSpec(EasyBigDecimalUtil.toNumber(zkPondFishDetail.getTargetFishSpec()));
    }

    /**
     * 之前有 zk_pond_fish , 则执行更新逻辑, 自己给自己分池
     * @param zkPondFishDetail
     * @param zkPondFishDO
     */
    public static void parseUpdateForSplit4Safe(ZkPondFishDetailDO zkPondFishDetail, ZkPondFishDO zkPondFishDO) {
        String fishTypes = zkPondFishDO.getFishTypes();
        if(StringUtils.isNotBlank(fishTypes) && !fishTypes.contains(zkPondFishDetail.getFishTypes())) {
            String newFishTypes = new StringBuilder(fishTypes).append(",").append(fishTypes).toString();
            zkPondFishDO.setFishTypes(newFishTypes);
        }
        Long currFishNum = zkPondFishDO.getFishNum();
        // currFishNum += zkPondFishDetail.getFishNum();
        currFishNum = zkPondFishDetail.getFishNum();
        zkPondFishDO.setFishNum(currFishNum);
        zkPondFishDO // .build_splitAddNum(zkPondFishDetail.getFishNum())
                .build_currFishSpec(EasyBigDecimalUtil.toNumber(zkPondFishDetail.getTargetFishSpec()));
    }

}
