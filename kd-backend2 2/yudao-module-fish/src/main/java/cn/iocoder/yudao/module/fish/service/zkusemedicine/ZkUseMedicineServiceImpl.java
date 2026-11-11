package cn.iocoder.yudao.module.fish.service.zkusemedicine;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.spring.SpringUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicine.vo.ZkUseMedicineCreateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicine.vo.ZkUseMedicineExportReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicine.vo.ZkUseMedicinePageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicine.vo.ZkUseMedicineUpdateReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkaskforleave.ZkAskforLeaveDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicine.ZkUseMedicineDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkaskforleave.ZkAskforLeaveMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkusemedicine.ZkUseMedicineMapper;
import cn.iocoder.yudao.module.fish.service.zkbiologycapital.ZkBiologyCapitalService;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicinelog.vo.ZkUseMedicineLogCreateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicinelog.vo.ZkUseMedicineLogUpdateReqVO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkusemedicinelog.ZkUseMedicineLogMapper;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicinelog.ZkUseMedicineLogDO;
import cn.iocoder.yudao.module.fish.service.zkmedicinelogpond.ZkMedicineLogPondService;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmedicinelogpond.ZkMedicineLogPondDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.iocoder.yudao.module.fish.service.zkfeedinglog.ZkFeedingLogService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.workflow.api.dto.TaskParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.ZK_USE_MEDICINE_NOT_EXISTS;

/**
 * 用药记录 Service 实现类
 * TODO 框架升级待完成
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class ZkUseMedicineServiceImpl implements ZkUseMedicineService, JavaDelegate {

    @Resource
    private ZkUseMedicineMapper zkUseMedicineMapper;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private ZkFeedingLogService zkFeedingLogService;
    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Resource
    private ZkUseMedicineLogMapper zkUseMedicineLogMapper;
    @Resource
    private ZkMedicineLogPondService zkMedicineLogPondService;
    @Resource
    private ZkBiologyCapitalService zkBiologyCapitalService;

    @Override
    public Long createZkUseMedicine(ZkUseMedicineCreateReqVO createReqVO) {
        AdminUserRespDTO adminUserRespDTO = adminUserApi.getUser(SecurityFrameworkUtils.getLoginUserId());
        // 插入
        ZkUseMedicineDO zkUseMedicine = BeanUtils.toBean(createReqVO, ZkUseMedicineDO.class);
        zkUseMedicineMapper.insert(enhancerOther(zkUseMedicine));

        // 插入用药明细
        if (createReqVO.getUseMedicineLogList() != null && !createReqVO.getUseMedicineLogList().isEmpty()) {
            for (ZkUseMedicineLogDO logDO : createReqVO.getUseMedicineLogList()) {
                logDO.setZkUseMedicineId(zkUseMedicine.getId()).setDeptId(zkUseMedicine.getDeptId());
                if (logDO.getPondId() == null) {
                    logDO.setPondId(zkUseMedicine.getPondId());
                    logDO.setPondNum(zkUseMedicine.getPondNum());
                    logDO.setPondDetailNum(zkUseMedicine.getPondDetailNum());
                    logDO.setPondName(zkUseMedicine.getPondName());
                    logDO.setPondScale(zkUseMedicine.getPondScale());
                    try {
                        if (zkUseMedicine.getFishNum() != null) {
                            logDO.setFishNum(Integer.valueOf(zkUseMedicine.getFishNum()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                zkUseMedicineLogMapper.insert(logDO);
                if (logDO.getPondIdArr() != null && !logDO.getPondIdArr().isEmpty()) {
                    List<ZkMedicineLogPondDO> collect = logDO.getPondIdArr().stream().map(i -> new ZkMedicineLogPondDO(i, logDO.getId())).toList();
                    zkMedicineLogPondService.customSaveBatch(logDO.getId(), collect);
                }
                sync_biologyCapital(logDO);
            }
        }

        // 发起 BPM 流程
        TaskParam wfTaskParam = createReqVO.getTaskParam();
        Map<String, Object> processInstanceVariables = new HashMap<>();
        // processInstanceVariables.put("day", day);
        String processInstanceId = processInstanceApi.createProcessInstance(adminUserRespDTO.getId(),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(wfTaskParam.getProcessKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(zkUseMedicine.getId())));
        //.setStartUserSelectAssignees(zkAskforLeave.getApproveUserId()+""));

        // 将工作流的编号，更新到 数据库中
        zkUseMedicineMapper.updateById(new ZkUseMedicineDO().setId(zkUseMedicine.getId()).setProcessInstanceId(processInstanceId));
        // 返回
        return zkUseMedicine.getId();
    }

    @Override
    public void updateZkUseMedicine(ZkUseMedicineUpdateReqVO updateReqVO) {
//        AdminUserRespDTO user = adminUserApi.getUser();
        // 校验存在
        validateZkUseMedicineExists(updateReqVO.getId());
        // 更新
        ZkUseMedicineDO updateObj = BeanUtils.toBean(updateReqVO, ZkUseMedicineDO.class);
        enhancerOther(updateObj).setCreateTime(updateReqVO.getCreateTime());
        zkUseMedicineMapper.updateById(updateObj);

        // 更新用药明细
        if (updateReqVO.getUseMedicineLogList() != null) {
            // 先删除旧的
            QueryWrapper<ZkUseMedicineLogDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("zk_use_medicine_id", updateReqVO.getId());
            List<ZkUseMedicineLogDO> oldLogs = zkUseMedicineLogMapper.selectList(queryWrapper);
            if (oldLogs != null && !oldLogs.isEmpty()) {
                for (ZkUseMedicineLogDO oldLog : oldLogs) {
                    zkMedicineLogPondService.deleteByMedicineLogId(oldLog.getId());
                    if (EasyBigDecimalUtil.gt0(oldLog.getMedicineCost())) {
                        zkBiologyCapitalService.medicineCostUpdate(oldLog.getPondId(), oldLog.getMedicineCost().negate());
                    }
                }
            }
            zkUseMedicineLogMapper.delete(queryWrapper);
            // 插入新的
            for (ZkUseMedicineLogDO logDO : updateReqVO.getUseMedicineLogList()) {
                logDO.setId(null); // 清空ID，重新插入
                logDO.setZkUseMedicineId(updateReqVO.getId()).setDeptId(updateReqVO.getDeptId());
                if (logDO.getPondId() == null) {
                    logDO.setPondId(updateReqVO.getPondId());
                    logDO.setPondNum(updateReqVO.getPondNum());
                    logDO.setPondDetailNum(updateReqVO.getPondDetailNum());
                    logDO.setPondName(updateReqVO.getPondName());
                    logDO.setPondScale(updateReqVO.getPondScale());
                    try {
                        if (updateReqVO.getFishNum() != null) {
                            logDO.setFishNum(Integer.valueOf(updateReqVO.getFishNum()));
                        }
                    } catch (Exception e) {}
                }
                zkUseMedicineLogMapper.insert(logDO);
                if (logDO.getPondIdArr() != null && !logDO.getPondIdArr().isEmpty()) {
                    List<ZkMedicineLogPondDO> collect = logDO.getPondIdArr().stream().map(i -> new ZkMedicineLogPondDO(i, logDO.getId())).toList();
                    zkMedicineLogPondService.customSaveBatch(logDO.getId(), collect);
                }
                sync_biologyCapital(logDO);
            }
        }

//        TaskParam taskParam = updateReqVO.getTaskParam();
//        Map<String, Object> variables = taskParam.getVariables();//组装流程参数
//        if (StringUtils.isNotBlank(updateReqVO.getAction())) {
//        }
//        taskParam.setVariables(variables);
//        taskParam.setUser(user);
//        //wfTaskParam.setSubmitToPrev("y");//是否提交到上次退回的节点
//        if(StringUtils.isNotBlank(taskParam.getTaskId())) taskParam = taskApi.submitTask(taskParam);
//        log.warn("【收到请求-{}】middle,2 申请用药审批结果:{}", TokenUtil.userInfo(), JSON.toJSONString(taskParam));
    }


    public ZkUseMedicineDO enhancerOther(ZkUseMedicineDO obj) {
        // 1. 补充水质情况 TODO 后续需要根据 传感器 数据来获取
        if(StringUtils.isEmpty(obj.getWaterQuality())) obj.setWaterQuality("良好");
        if(StringUtils.isEmpty(obj.getSystemState())) obj.setSystemState("良好");
        // 2. 补充 投喂信息
        if(StringUtils.isEmpty(obj.getOneBeforeFeed())) {
            // 2.1 获取最近一天的投喂信息
            List<ZkFeedingLogDO> zkFeedingLogDOArr = zkFeedingLogService.getByPondId(obj.getPondId(), 3);
            if (zkFeedingLogDOArr != null && zkFeedingLogDOArr.size() > 0) {
                ZkFeedingLogDO oneBefore = zkFeedingLogDOArr.get(0);
                ZkFeedingLogDO twoBefore = zkFeedingLogDOArr.size() > 1 ? zkFeedingLogDOArr.get(1) : zkFeedingLogDOArr.get(0);
                ZkFeedingLogDO threeBefore = zkFeedingLogDOArr.size() > 2 ? zkFeedingLogDOArr.get(2) : zkFeedingLogDOArr.get(0);
                obj.build_feedInfo(oneBefore.getFeedBrand(), oneBefore.getFeedCount(), twoBefore.getFeedCount(), threeBefore.getFeedCount());
            }
        }
        return obj;
    }

    public void sync_biologyCapital(ZkUseMedicineLogDO item) {
        // 1. 成本为空 则 无需同步
        if(!EasyBigDecimalUtil.gt0(item.getMedicineCost())) {
            return;
        }
        // 2. 执行同步
        zkBiologyCapitalService.medicineCostUpdate(item.getPondId(), item.getMedicineCost());
    }

    @Override
    public void deleteZkUseMedicine(Long id) {
        // 校验存在
        validateZkUseMedicineExists(id);
        // 删除
        zkUseMedicineMapper.deleteById(id);
    }

    private void validateZkUseMedicineExists(Long id) {
        if (zkUseMedicineMapper.selectById(id) == null) {
            throw exception(ZK_USE_MEDICINE_NOT_EXISTS);
        }
    }

    @Override
    public ZkUseMedicineDO getZkUseMedicine(Long id) {
        return zkUseMedicineMapper.selectById(id);
    }

    @Override
    public List<ZkUseMedicineDO> getZkUseMedicineList(Collection<Long> ids) {
        return zkUseMedicineMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkUseMedicineDO> getZkUseMedicinePage(ZkUseMedicinePageReqVO pageReqVO) {
        return zkUseMedicineMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkUseMedicineDO> getZkUseMedicineList(ZkUseMedicineExportReqVO exportReqVO) {
        return zkUseMedicineMapper.selectList(exportReqVO);
    }

    @Override
    public void execute(DelegateExecution execution) {
        try {
            String resStr = JSON.toJSONString(execution);
            JSONObject parent = JSON.parseObject(resStr);
            Long businessKey = parent.getLong("businessKey");
            JSONObject variablesLocal = parent.getJSONObject("variablesLocal");
            Integer processStatus = variablesLocal.getInteger("PROCESS_STATUS");
            ZkUseMedicineMapper bean = SpringUtils.getBean(ZkUseMedicineMapper.class);
            // String processReason = variablesLocal.getString("PROCESS_REASON"); // 拒绝理由
            if(Objects.equals(2, processStatus)) {
                ZkUseMedicineDO zkAskforLeaveDO = new ZkUseMedicineDO().setId(businessKey).setStatus("1");
                bean.updateById(zkAskforLeaveDO);
            } else if(Objects.equals(3, processStatus)) {
                ZkUseMedicineDO zkAskforLeaveDO = new ZkUseMedicineDO().setId(businessKey).setStatus("-1");
                bean.updateById(zkAskforLeaveDO);
            }
            System.out.println(String.format("AskforLeave=========id:%s===processStatus:%s========>execute ", businessKey, processStatus));
        } catch (Exception e) {
            String subject = String.format("用药申请异常%s", e.getMessage());
            String msg = String.format("用药申请异常:%s,内容:%s", ExceptionUtil.getStackTrace(e), JSON.toJSONString(execution));
            log.error(msg);
            EmailUtil.send(subject, msg);
        }
    }

//    @Override
//    public void handleProcess(PlWorkflowMessage message) {
//        if (StringUtils.isBlank(message.getBusinessKey())) {
//            return;
//        }
//        ZkUseMedicineDO zkUseMedicine = this.getZkUseMedicine(Long.parseLong(message.getBusinessKey()));
//        if (message.getEventName().equals(WorkflowContants.EVENTNAME_PROCESS_END)) { //流程正常结束
//            zkUseMedicine.setStatus("1");// 已批准
//            this.zkUseMedicineMapper.updateById(zkUseMedicine);
//        } else if (message.getEventName().equals("")) {
//
//        }
//    }

}
