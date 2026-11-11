package cn.iocoder.yudao.module.fish.service.zkaskforleave;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.spring.SpringUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.iocoder.yudao.module.fish.controller.admin.zkaskforleave.vo.ZkAskforLeaveCreateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkaskforleave.vo.ZkAskforLeaveExportReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkaskforleave.vo.ZkAskforLeavePageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkaskforleave.vo.ZkAskforLeaveUpdateReqVO;
import cn.iocoder.yudao.module.fish.controller.base_component.EmailNoticeComponent;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkaskforleave.ZkAskforLeaveDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkaskforleave.ZkAskforLeaveMapper;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.workflow.api.dto.TaskParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.ZK_ASKFOR_LEAVE_NOT_EXISTS;

/**
 * 请假 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class ZkAskforLeaveServiceImpl implements ZkAskforLeaveService, JavaDelegate {

    @Resource
    private ZkAskforLeaveMapper zkAskforLeaveMapper;
    @Autowired
    private AdminUserApi adminUserApi;
    @Resource
    private EmailNoticeComponent emailNoticeComponent;
    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Override
    public Long createZkAskforLeave(ZkAskforLeaveCreateReqVO createReqVO) {
        AdminUserRespDTO adminUserRespDTO = adminUserApi.getUser(SecurityFrameworkUtils.getLoginUserId());
        // 插入
        ZkAskforLeaveDO zkAskforLeave = BeanUtils.toBean(createReqVO, ZkAskforLeaveDO.class).setStatus("0");
        zkAskforLeaveMapper.insert(zkAskforLeave);

        // 发起 BPM 流程
        TaskParam wfTaskParam = createReqVO.getTaskParam();
        Map<String, Object> processInstanceVariables = new HashMap<>();
        // processInstanceVariables.put("day", day);
        String processInstanceId = processInstanceApi.createProcessInstance(adminUserRespDTO.getId(),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(wfTaskParam.getProcessKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(zkAskforLeave.getId())));
                        //.setStartUserSelectAssignees(zkAskforLeave.getApproveUserId()+""));

        // 将工作流的编号，更新到 OA 请假单中
        zkAskforLeaveMapper.updateById(new ZkAskforLeaveDO().setId(zkAskforLeave.getId()).setProcessInstanceId(processInstanceId));
        // 抄送 请假
        String sub = String.format("请假抄送-%s-%s", adminUserRespDTO.getUsername(), zkAskforLeave.getLeaveName());
        emailNoticeComponent.noticeUser(zkAskforLeave.getNoticeUserId(), sub, zkAskforLeave.transformerMsg());

        return zkAskforLeave.getId();
    }

    @Override
    public void updateZkAskforLeave(ZkAskforLeaveUpdateReqVO updateReqVO) {
//        AdminUserRespDTO user = adminUserApi.getUser();
        // 校验存在
        validateZkAskforLeaveExists(updateReqVO.getId());
        // 更新
        ZkAskforLeaveDO updateObj = BeanUtils.toBean(updateReqVO, ZkAskforLeaveDO.class);
        zkAskforLeaveMapper.updateById(updateObj);
//        TaskParam taskParam = updateReqVO.getTaskParam();
//        Map<String, Object> variables = taskParam.getVariables();//组装流程参数
//        taskParam.setVariables(variables);
//        taskParam.setUser(user);
//        // wfTaskParam.setSubmitToPrev("y"); //是否提交到上次退回的节点
//        if(StringUtils.isNotBlank(taskParam.getTaskId())) taskParam = taskApi.submitTask(taskParam);
//        log.warn("【收到请求-{}】middle,2 申请请假审批结果:{}", TokenUtil.userInfo(), JSON.toJSONString(taskParam));
    }

    @Override
    public void deleteZkAskforLeave(Long id) {
        // 校验存在
        validateZkAskforLeaveExists(id);
        // 删除
        zkAskforLeaveMapper.deleteById(id);
    }

    private void validateZkAskforLeaveExists(Long id) {
        if (zkAskforLeaveMapper.selectById(id) == null) {
            throw exception(ZK_ASKFOR_LEAVE_NOT_EXISTS);
        }
    }

    @Override
    public ZkAskforLeaveDO getZkAskforLeave(Long id) {
        return zkAskforLeaveMapper.selectById(id);
    }

    @Override
    public List<ZkAskforLeaveDO> getZkAskforLeaveList(Collection<Long> ids) {
        return zkAskforLeaveMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkAskforLeaveDO> getZkAskforLeavePage(ZkAskforLeavePageReqVO pageReqVO) {
        return zkAskforLeaveMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkAskforLeaveDO> getZkAskforLeaveList(ZkAskforLeaveExportReqVO exportReqVO) {
        return zkAskforLeaveMapper.selectList(exportReqVO);
    }

    @Override
    public void execute(DelegateExecution execution) {
        try {
            String resStr = JSON.toJSONString(execution);
            JSONObject parent = JSON.parseObject(resStr);
            Long businessKey = parent.getLong("businessKey");
            JSONObject variablesLocal = parent.getJSONObject("variablesLocal");
            Integer processStatus = variablesLocal.getInteger("PROCESS_STATUS");
            ZkAskforLeaveMapper bean = SpringUtils.getBean(ZkAskforLeaveMapper.class);
            // String processReason = variablesLocal.getString("PROCESS_REASON"); // 拒绝理由
            if(Objects.equals(2, processStatus)) {
                ZkAskforLeaveDO zkAskforLeaveDO = new ZkAskforLeaveDO(businessKey, "1");
                bean.updateById(zkAskforLeaveDO);
            } else if(Objects.equals(3, processStatus)) {
                ZkAskforLeaveDO zkAskforLeaveDO = new ZkAskforLeaveDO(businessKey, "-2");
                bean.updateById(zkAskforLeaveDO);
            }
            System.out.println(String.format("AskforLeave=========id:%s===processStatus:%s========>execute ", businessKey, processStatus));
        } catch (Exception e) {
            String subject = String.format("请假流程异常%s", e.getMessage());
            String msg = String.format("请假流程异常:%s,内容:%s", ExceptionUtil.getStackTrace(e), JSON.toJSONString(execution));
            log.error(msg);
            EmailUtil.send(subject, msg);
        }
    }

//    @Override
//    public void handleProcess(PlWorkflowMessage message) {
//        if (StringUtils.isBlank(message.getBusinessKey())) {
//            return;
//        }
//        ZkAskforLeaveDO item = this.getZkAskforLeave(Long.parseLong(message.getBusinessKey()));
//        if (message.getEventName().equals(WorkflowContants.EVENTNAME_PROCESS_END)) { //流程正常结束
//            item.setStatus("1");// 已批准
//            zkAskforLeaveMapper.updateById(item);
//        } else if (message.getEventName().equals("")) {
//
//        }
//    }

}
