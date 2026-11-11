package cn.iocoder.yudao.module.fish.service.zkintelligentdiagnosis;

import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.common.util.spring.SpringUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicine.ZkUseMedicineDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkusemedicine.ZkUseMedicineMapper;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.workflow.api.dto.TaskParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkintelligentdiagnosis.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkintelligentdiagnosis.ZkIntelligentDiagnosisDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkintelligentdiagnosis.ZkIntelligentDiagnosisConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkintelligentdiagnosis.ZkIntelligentDiagnosisMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 智能诊断 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class ZkIntelligentDiagnosisServiceImpl implements ZkIntelligentDiagnosisService, JavaDelegate {

    @Resource
    private ZkIntelligentDiagnosisMapper zkIntelligentDiagnosisMapper;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public Long createZkIntelligentDiagnosis(ZkIntelligentDiagnosisCreateReqVO createReqVO) {
        AdminUserRespDTO adminUserRespDTO = adminUserApi.getUser(SecurityFrameworkUtils.getLoginUserId());
        // 插入
        ZkIntelligentDiagnosisDO zkIntelligentDiagnosis = BeanUtils.toBean(createReqVO, ZkIntelligentDiagnosisDO.class);
        zkIntelligentDiagnosisMapper.insert(zkIntelligentDiagnosis);

        // 发起 BPM 流程
        TaskParam wfTaskParam = createReqVO.getTaskParam();
        Map<String, Object> processInstanceVariables = new HashMap<>();
        // processInstanceVariables.put("day", day);
        String processInstanceId = processInstanceApi.createProcessInstance(adminUserRespDTO.getId(),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(wfTaskParam.getProcessKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(zkIntelligentDiagnosis.getId())));
        //.setStartUserSelectAssignees(zkAskforLeave.getApproveUserId()+""));

        // 将工作流的编号，更新到 OA 请假单中
        zkIntelligentDiagnosisMapper.updateById(new ZkIntelligentDiagnosisDO().setId(zkIntelligentDiagnosis.getId()).setProcessInstanceId(processInstanceId));
        return zkIntelligentDiagnosis.getId();
    }

    @Override
    public void updateZkIntelligentDiagnosis(ZkIntelligentDiagnosisUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkIntelligentDiagnosisExists(updateReqVO.getId());
        // 更新
        ZkIntelligentDiagnosisDO updateObj = BeanUtils.toBean(updateReqVO, ZkIntelligentDiagnosisDO.class);
        zkIntelligentDiagnosisMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkIntelligentDiagnosis(Long id) {
        // 校验存在
        validateZkIntelligentDiagnosisExists(id);
        // 删除
        zkIntelligentDiagnosisMapper.deleteById(id);
    }

    private void validateZkIntelligentDiagnosisExists(Long id) {
        if (zkIntelligentDiagnosisMapper.selectById(id) == null) {
            throw exception(ZK_INTELLIGENT_DIAGNOSIS_NOT_EXISTS);
        }
    }

    @Override
    public ZkIntelligentDiagnosisDO getZkIntelligentDiagnosis(Long id) {
        return zkIntelligentDiagnosisMapper.selectById(id);
    }

    @Override
    public List<ZkIntelligentDiagnosisDO> getZkIntelligentDiagnosisList(Collection<Long> ids) {
        return zkIntelligentDiagnosisMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkIntelligentDiagnosisDO> getZkIntelligentDiagnosisPage(ZkIntelligentDiagnosisPageReqVO pageReqVO) {
        return zkIntelligentDiagnosisMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkIntelligentDiagnosisDO> getZkIntelligentDiagnosisList(ZkIntelligentDiagnosisExportReqVO exportReqVO) {
        return zkIntelligentDiagnosisMapper.selectList(exportReqVO);
    }


    @Override
    public void execute(DelegateExecution execution) {
        try {
            String resStr = JSON.toJSONString(execution);
            JSONObject parent = JSON.parseObject(resStr);
            Long businessKey = parent.getLong("businessKey");
            JSONObject variablesLocal = parent.getJSONObject("variablesLocal");
            Integer processStatus = variablesLocal.getInteger("PROCESS_STATUS");
            ZkIntelligentDiagnosisMapper bean = SpringUtils.getBean(ZkIntelligentDiagnosisMapper.class);
            // String processReason = variablesLocal.getString("PROCESS_REASON"); // 拒绝理由
            if(Objects.equals(2, processStatus)) {
                ZkIntelligentDiagnosisDO domain = new ZkIntelligentDiagnosisDO().setId(businessKey).setStatus("1");
                bean.updateById(domain);
            } else if(Objects.equals(3, processStatus)) {
                ZkIntelligentDiagnosisDO domain = new ZkIntelligentDiagnosisDO().setId(businessKey).setStatus("-1");
                bean.updateById(domain);
            }
            System.out.printf("ZkIntelligentDiagnosisDO=========id:%s===processStatus:%s========>execute %n", businessKey, processStatus);
        } catch (Exception e) {
            String subject = String.format("智能诊断申请异常%s", e.getMessage());
            String msg = String.format("智能诊断申请异常:%s,内容:%s", ExceptionUtil.getStackTrace(e), JSON.toJSONString(execution));
            log.error(msg);
            EmailUtil.send(subject, msg);
        }
    }
}
