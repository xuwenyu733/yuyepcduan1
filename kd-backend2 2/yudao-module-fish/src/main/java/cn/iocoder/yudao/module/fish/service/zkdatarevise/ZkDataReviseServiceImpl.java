package cn.iocoder.yudao.module.fish.service.zkdatarevise;

import cn.iocoder.yudao.framework.common.base_api.BaseUserService;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.to.DataReviseTypeEnum;
import cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.vo.ZkDataReviseCreateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.vo.ZkDataReviseExportReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.vo.ZkDataRevisePageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.vo.ZkDataReviseUpdateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkfeedinglog.vo.ZkFeedingLogUpdateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpond.vo.ZkPondUpdateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.PondFishDetailAddTypeEnum;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.vo.ZkPondFishDetailUpdateReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdatarevise.ZkDataReviseDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkdatarevise.ZkDataReviseMapper;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.ZK_DATA_REVISE_NOT_EXISTS;

/**
 * 数据修正记录 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class ZkDataReviseServiceImpl implements ZkDataReviseService {

    @Resource
    private ZkDataReviseMapper zkDataReviseMapper;
    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private BaseUserService baseUserService;
//    @Resource
//    private WfTaskApi taskApi;

    @Override
    public Long createZkDataRevise(ZkDataReviseCreateReqVO createReqVO) {
        AdminUserRespDTO adminUserRespDTO = adminUserApi.getUser(SecurityFrameworkUtils.getLoginUserId());
        // 插入
        ZkDataReviseDO zkDataRevise = BeanUtils.toBean(createReqVO, ZkDataReviseDO.class);
        zkDataReviseMapper.insert(zkDataRevise);

//        TaskParam wfTaskParam = createReqVO.getTaskParam();
//        wfTaskParam.setProcessName(new StringBuilder("-").append(adminUserRespDTO.getNickname())
//                .append("-").append(zkDataRevise.getReviseTypeName())
//                .toString());
//        wfTaskParam.setUser(adminUserRespDTO);
//        wfTaskParam.setBusinessKey(String.valueOf(zkDataRevise.getId()));//业务主键
//        wfTaskParam.setApproveNo(StringUtil.genApproveNum("ZkDataRevise", zkDataRevise.getId()));//审批编号
//        // wfTaskParam.setApproveUserId(String.valueOf(zkUseMedicine.getBackId()));//
//        Map<String, Object> variables = wfTaskParam.getBaseParam(adminUserRespDTO);//组装流程参数
//        wfTaskParam.setVariables(variables);
//        wfTaskParam = taskApi.submitTask(wfTaskParam); //
//        zkDataRevise.setStatus("0"); // 审批中
//        zkDataRevise.setProcessInstanceId(wfTaskParam.getProcessInstanceId());
        zkDataReviseMapper.updateById(zkDataRevise);
        // 返回
        return zkDataRevise.getId();
    }

    @Override
    public void updateZkDataRevise(ZkDataReviseUpdateReqVO updateReqVO) {
        // 1. 校验存在
        validateZkDataReviseExists(updateReqVO.getId());
        // 2. 更新
        ZkDataReviseDO updateObj = BeanUtils.toBean(updateReqVO, ZkDataReviseDO.class);
        zkDataReviseMapper.updateById(updateObj);
        // 3. 更新 审批流
//        TaskParam taskParam = updateReqVO.getTaskParam();
//        Map<String, Object> variables = taskParam.getVariables();//组装流程参数
//        if (StringUtils.isNotBlank(updateReqVO.getAction())) {
//        }
//        taskParam.setVariables(variables);
//        // taskParam.setUser(user);
//        // wfTaskParam.setSubmitToPrev("y");//是否提交到上次退回的节点
//        if(StringUtils.isNotBlank(taskParam.getTaskId())) taskParam = taskApi.submitTask(taskParam);
//        log.warn("【收到请求-{}】middle,2 申请数据修正 审批结果:{}", TokenUtil.userInfo(), JSON.toJSONString(taskParam));
    }

    @Override
    public void deleteZkDataRevise(Long id) {
        // 校验存在
        validateZkDataReviseExists(id);
        // 删除
        zkDataReviseMapper.deleteById(id);
    }

    private void validateZkDataReviseExists(Long id) {
        if (zkDataReviseMapper.selectById(id) == null) {
            throw exception(ZK_DATA_REVISE_NOT_EXISTS);
        }
    }

    @Override
    public ZkDataReviseDO getZkDataRevise(Long id) {
        return zkDataReviseMapper.selectById(id);
    }

    @Override
    public List<ZkDataReviseDO> getZkDataReviseList(Collection<Long> ids) {
        return zkDataReviseMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkDataReviseDO> getZkDataRevisePage(ZkDataRevisePageReqVO pageReqVO) {
        return zkDataReviseMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkDataReviseDO> getZkDataReviseList(ZkDataReviseExportReqVO exportReqVO) {
        return zkDataReviseMapper.selectList(exportReqVO);
    }

    @Override
    public void customInsert(ZkPondFishDetailUpdateReqVO updateReqVO) {
        if(!(updateReqVO.getOriginData() != null && updateReqVO.getDifferenceData()!=null &&
                updateReqVO.getChangeRatio() != null && StringUtils.isNotEmpty(updateReqVO.getReason()))) {
            return;
        }
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        Long deptId = updateReqVO.getDeptId();
        if(deptId == null) deptId = Long.parseLong(baseUserService.byKey(loginUserId.toString(), "deptId").toString());
        // 1. 涉及修改原值，则需要同步数据
        if(StringUtils.equals(PondFishDetailAddTypeEnum.ADD.getAddType(), updateReqVO.getAddType())) {
            ZkDataReviseDO zkDataRevise = new ZkDataReviseDO(updateReqVO.getWorkshopId(), updateReqVO.getWorkshopName(), updateReqVO.getPondSystemName(),
                    null, updateReqVO.getPondNum(), updateReqVO.getPondName(), updateReqVO.getPondId(), updateReqVO.getPondDetailNum(), DataReviseTypeEnum.PUSH.getReviseType(),
                    DataReviseTypeEnum.PUSH.getName(),
                    updateReqVO.getOriginData()+"", updateReqVO.getDifferenceData()+"", updateReqVO.getChangeRatio()+"", updateReqVO.getReason());
            zkDataRevise.build_busId(updateReqVO.getId()).build_userId(loginUserId)
                    .setDeptId(deptId);
            zkDataReviseMapper.insert(zkDataRevise);
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.DEAD.getAddType(), updateReqVO.getAddType())) {
            ZkDataReviseDO zkDataRevise = new ZkDataReviseDO(updateReqVO.getWorkshopId(), updateReqVO.getWorkshopName(), updateReqVO.getPondSystemName(),
                    null, updateReqVO.getPondNum(), updateReqVO.getPondName(), updateReqVO.getPondId(), updateReqVO.getPondDetailNum(), DataReviseTypeEnum.DEAD.getReviseType(),
                    DataReviseTypeEnum.DEAD.getName(),
                    updateReqVO.getOriginData()+"", updateReqVO.getDifferenceData()+"", updateReqVO.getChangeRatio()+"", updateReqVO.getReason());
            zkDataRevise.build_busId(updateReqVO.getId()).build_userId(loginUserId)
                    .setDeptId(deptId);
            zkDataReviseMapper.insert(zkDataRevise);
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.SPLIT.getAddType(), updateReqVO.getAddType())) {
            ZkDataReviseDO zkDataRevise = new ZkDataReviseDO(updateReqVO.getWorkshopId(), updateReqVO.getWorkshopName(), updateReqVO.getPondSystemName(),
                    null, updateReqVO.getPondNum(), updateReqVO.getPondName(), updateReqVO.getPondId(), updateReqVO.getPondDetailNum(), DataReviseTypeEnum.SPLIT.getReviseType(),
                    DataReviseTypeEnum.SPLIT.getName(),
                    updateReqVO.getOriginData()+"", updateReqVO.getDifferenceData()+"", updateReqVO.getChangeRatio()+"", updateReqVO.getReason());
            zkDataRevise.build_busId(updateReqVO.getId()).build_userId(loginUserId)
                    .setDeptId(deptId);
            zkDataReviseMapper.insert(zkDataRevise);
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.SALE.getAddType(), updateReqVO.getAddType())) {

        }
    }

    @Override
    public void customInsertPond(ZkPondUpdateReqVO updateReqVO) {
        if(!(updateReqVO.getOriginData() != null && updateReqVO.getDifferenceData()!=null &&
                updateReqVO.getChangeRatio() != null && StringUtils.isNotEmpty(updateReqVO.getReason()))) {
            return;
        }
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        Long deptId = updateReqVO.getDeptId();
        if(deptId == null) deptId = Long.parseLong(baseUserService.byKey(loginUserId.toString(), "deptId").toString());
        ZkDataReviseDO zkDataRevise = new ZkDataReviseDO(updateReqVO.getWorkshopId(), updateReqVO.getWorkshopName(), updateReqVO.getPondSystemName(),
                null, updateReqVO.getPondNum(), updateReqVO.getPondName(), updateReqVO.getId(), updateReqVO.getPondDetailNum(), DataReviseTypeEnum.POND.getReviseType(),
                DataReviseTypeEnum.POND.getName(),
                updateReqVO.getOriginData()+"", updateReqVO.getDifferenceData()+"", updateReqVO.getChangeRatio()+"", updateReqVO.getReason());
        zkDataRevise.build_busId(updateReqVO.getId()).build_userId(loginUserId)
                .setDeptId(deptId);
        zkDataReviseMapper.insert(zkDataRevise);
    }

    @Override
    public void customInsertFeedingLog(ZkFeedingLogUpdateReqVO updateReqVO) {
        if(!(updateReqVO.getOriginData() != null && updateReqVO.getDifferenceData()!=null &&
                updateReqVO.getChangeRatio() != null && StringUtils.isNotEmpty(updateReqVO.getReason()))) {
            return;
        }
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        Long deptId = updateReqVO.getDeptId();
        if(deptId == null) deptId = Long.parseLong(baseUserService.byKey(loginUserId.toString(), "deptId").toString());
        ZkDataReviseDO zkDataRevise = new ZkDataReviseDO(updateReqVO.getWorkshopId(), updateReqVO.getWorkshopName(), updateReqVO.getPondSystemName(),
                null, updateReqVO.getPondNum(), updateReqVO.getPondName(), updateReqVO.getPondId(), updateReqVO.getPondDetailNum(), DataReviseTypeEnum.FEED.getReviseType(),
                DataReviseTypeEnum.FEED.getName(),
                updateReqVO.getOriginData()+"", updateReqVO.getDifferenceData()+"", updateReqVO.getChangeRatio()+"", updateReqVO.getReason());
        zkDataRevise.build_busId(updateReqVO.getId()).build_userId(loginUserId)
                .setDeptId(deptId);
        zkDataReviseMapper.insert(zkDataRevise);
    }

    public void filterInfo(Long userId, Long deptId, ZkDataReviseDO zkDataRevise) {

    }

}
