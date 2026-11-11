package cn.iocoder.yudao.module.fish.dal.mysql.zktask;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import cn.iocoder.yudao.module.fish.utils.QueryUtil;
import cn.iocoder.yudao.module.fish.utils.QueryUtil2;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.vo.*;

/**
 * 任务 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkTaskMapper extends BaseMapperX<ZkTaskDO> {

    default PageResult<ZkTaskDO> selectPage(ZkTaskPageReqVO reqVO) {
        LambdaQueryWrapperX<ZkTaskDO> queryWrapper = new LambdaQueryWrapperX<ZkTaskDO>()
                .eqIfPresent(ZkTaskDO::getBusId, reqVO.getBusId())
                .likeIfPresent(ZkTaskDO::getTaskName, reqVO.getTaskName())
                .likeIfPresent(ZkTaskDO::getTaskContent, reqVO.getTaskContent())
                .eqIfPresent(ZkTaskDO::getTaskPoint, reqVO.getTaskPoint())
                .eqIfPresent(ZkTaskDO::getTaskSummary, reqVO.getTaskSummary())
                // .eqIfPresent(ZkTaskDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkTaskDO::getUserName, reqVO.getUserName())
                // .eqIfPresent(ZkTaskDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkTaskDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkTaskDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkTaskDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkTaskDO::getProcessInstanceId, reqVO.getProcessInstanceId());
        AdminUserRespDTO adminUserRespDTO = reqVO.getAdminUserRespDTO();

        if(Objects.equals(reqVO.getSearchType(), 2)) {
            queryWrapper.eqIfPresent(ZkTaskDO::getCreator, reqVO.getAdminUserRespDTO().getId());
            if(reqVO.getUserId() != null) {
                queryWrapper.eqIfPresent(ZkTaskDO::getUserId, reqVO.getUserId());
            }
        } else {
            if(reqVO.getUserId() != null) {
                queryWrapper.eqIfPresent(ZkTaskDO::getUserId, reqVO.getUserId());
            } else {
                new QueryUtil2<ZkTaskDO>().filterQuery(queryWrapper, adminUserRespDTO, ZkTaskDO::getUserId, ZkTaskDO::getDeptId);
            }
        }
        List<String> statusArr = reqVO.selectStatusArr();
        if(EasyListUtil.isNotEmpty(statusArr)) {
            queryWrapper.in(ZkTaskDO::getStatus, statusArr);
        }
        queryWrapper.orderByAsc(ZkTaskDO::getTaskType).orderByDesc(ZkTaskDO::getId);
        return selectPage(reqVO, queryWrapper);
    }

    default List<ZkTaskDO> selectList(ZkTaskExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkTaskDO>()
                .eqIfPresent(ZkTaskDO::getBusId, reqVO.getBusId())
                .likeIfPresent(ZkTaskDO::getTaskName, reqVO.getTaskName())
                .eqIfPresent(ZkTaskDO::getTaskContent, reqVO.getTaskContent())
                .eqIfPresent(ZkTaskDO::getTaskPoint, reqVO.getTaskPoint())
                .eqIfPresent(ZkTaskDO::getTaskSummary, reqVO.getTaskSummary())
                .eqIfPresent(ZkTaskDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkTaskDO::getUserName, reqVO.getUserName())
                .eqIfPresent(ZkTaskDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(ZkTaskDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ZkTaskDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ZkTaskDO::getFid, reqVO.getFid())
                .eqIfPresent(ZkTaskDO::getProcessInstanceId, reqVO.getProcessInstanceId())
                .orderByDesc(ZkTaskDO::getId));
    }

}
