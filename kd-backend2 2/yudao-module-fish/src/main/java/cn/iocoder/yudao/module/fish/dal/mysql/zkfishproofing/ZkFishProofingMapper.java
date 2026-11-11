package cn.iocoder.yudao.module.fish.dal.mysql.zkfishproofing;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishproofing.ZkFishProofingDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishproofing.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 打样记录 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkFishProofingMapper extends BaseMapperX<ZkFishProofingDO> {

    default PageResult<ZkFishProofingDO> selectPage(ZkFishProofingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkFishProofingDO>()
                .eqIfPresent(ZkFishProofingDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkFishProofingDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkFishProofingDO::getWorkshopName, reqVO.getWorkshopName())
                .likeIfPresent(ZkFishProofingDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkFishProofingDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkFishProofingDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkFishProofingDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkFishProofingDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkFishProofingDO::getPondDetailNum, reqVO.getPondDetailNum())
                .eqIfPresent(ZkFishProofingDO::getInitSpecs, reqVO.getInitSpecs())
                .eqIfPresent(ZkFishProofingDO::getAvgBodyLength, reqVO.getAvgBodyLength())
                .eqIfPresent(ZkFishProofingDO::getAvgSpecs, reqVO.getAvgSpecs())
                .eqIfPresent(ZkFishProofingDO::getCurrNum, reqVO.getCurrNum())
                .eqIfPresent(ZkFishProofingDO::getTaskDetailId, reqVO.getTaskDetailId())
                .betweenIfPresent(ZkFishProofingDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkFishProofingDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkFishProofingDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkFishProofingDO::getFid, reqVO.getFid())
                .orderByDesc(ZkFishProofingDO::getId));
    }

    default List<ZkFishProofingDO> selectList(ZkFishProofingExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkFishProofingDO>()
                .eqIfPresent(ZkFishProofingDO::getBatchNumber, reqVO.getBatchNumber())
                .eqIfPresent(ZkFishProofingDO::getWorkshopId, reqVO.getWorkshopId())
                .likeIfPresent(ZkFishProofingDO::getWorkshopName, reqVO.getWorkshopName())
                .likeIfPresent(ZkFishProofingDO::getPondSystemName, reqVO.getPondSystemName())
                .eqIfPresent(ZkFishProofingDO::getPondSystemId, reqVO.getPondSystemId())
                .eqIfPresent(ZkFishProofingDO::getPondNum, reqVO.getPondNum())
                .likeIfPresent(ZkFishProofingDO::getPondName, reqVO.getPondName())
                .eqIfPresent(ZkFishProofingDO::getPondId, reqVO.getPondId())
                .eqIfPresent(ZkFishProofingDO::getPondDetailNum, reqVO.getPondDetailNum())
                .eqIfPresent(ZkFishProofingDO::getInitSpecs, reqVO.getInitSpecs())
                .eqIfPresent(ZkFishProofingDO::getAvgBodyLength, reqVO.getAvgBodyLength())
                .eqIfPresent(ZkFishProofingDO::getAvgSpecs, reqVO.getAvgSpecs())
                .eqIfPresent(ZkFishProofingDO::getCurrNum, reqVO.getCurrNum())
                .eqIfPresent(ZkFishProofingDO::getTaskDetailId, reqVO.getTaskDetailId())
                .betweenIfPresent(ZkFishProofingDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkFishProofingDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ZkFishProofingDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkFishProofingDO::getFid, reqVO.getFid())
                .orderByDesc(ZkFishProofingDO::getId));
    }

    @Select({
            "SELECT * FROM zk_fish_proofing zfp \n" +
                    "WHERE id IN ( \n" +
                    "\tSELECT MAX(id) FROM  zk_fish_proofing \n" +
                    "\tGROUP BY pond_id \n" +
                    ")"
    })
    List<ZkFishProofingDO> getCustomList(@Param("ew") QueryWrapper<ZkFishProofingDO> queryWrapper);
}
