package cn.iocoder.yudao.module.fish.service.zkdevicealarm;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevicealarm.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicealarm.ZkDeviceAlarmDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkdevicealarm.ZkDeviceAlarmConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkdevicealarm.ZkDeviceAlarmMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

import cn.iocoder.yudao.module.fish.service.zkdevicealarm.ZkDeviceAlarmWebSocketService;
import cn.iocoder.yudao.framework.common.base_api.BaseDeptService;
import cn.iocoder.yudao.module.fish.utils.DeptScopeSupport;

/**
 * 设备报警 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkDeviceAlarmServiceImpl implements ZkDeviceAlarmService {

    public static final String PROCESS_KEY = "alarm_handle";

    @Resource
    private ZkDeviceAlarmMapper zkDeviceAlarmMapper;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;
    
    @Resource
    private ZkDeviceAlarmWebSocketService zkDeviceAlarmWebSocketService;

    @Resource
    private BaseDeptService baseDeptService;

    @Resource
    private DeptScopeSupport deptScopeSupport;

    @Override
    public Long createZkDeviceAlarm(ZkDeviceAlarmCreateReqVO createReqVO) {
        // 插入
        ZkDeviceAlarmDO zkDeviceAlarm = BeanUtils.toBean(createReqVO, ZkDeviceAlarmDO.class);
        zkDeviceAlarmMapper.insert(zkDeviceAlarm);
        
        // 通过WebSocket推送告警信息到前端
        zkDeviceAlarmWebSocketService.sendDeviceAlarm(zkDeviceAlarm);
        
        // 返回
        return zkDeviceAlarm.getId();
    }



    @Override
    public void updateZkDeviceAlarm(ZkDeviceAlarmUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkDeviceAlarmExists(updateReqVO.getId());
        // 更新业务字段（不依赖 BPM）
        ZkDeviceAlarmDO updateObj = BeanUtils.toBean(updateReqVO, ZkDeviceAlarmDO.class);
        zkDeviceAlarmMapper.updateById(updateObj);

        // BPM 流程可选：未部署 alarm_handle 时不影响报警处置闭环
        try {
            Long userId = updateReqVO.getUserId();
            if (userId == null) {
                userId = cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId();
            }
            if (userId == null) {
                return;
            }
            Map<String, Object> processInstanceVariables = new HashMap<>();
            String processInstanceId = processInstanceApi.createProcessInstance(userId,
                    new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(PROCESS_KEY)
                            .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(updateReqVO.getId())));
            ZkDeviceAlarmDO alarmDO = new ZkDeviceAlarmDO();
            alarmDO.setId(updateReqVO.getId());
            alarmDO.setProcessInstanceId(processInstanceId);
            zkDeviceAlarmMapper.updateById(alarmDO);
        } catch (Exception ex) {
            // 流程定义缺失或发起失败时，仅记录日志，保证报警状态可正常更新
            org.slf4j.LoggerFactory.getLogger(getClass())
                    .warn("[updateZkDeviceAlarm][id={}] 发起 BPM 流程失败（可忽略）: {}",
                            updateReqVO.getId(), ex.getMessage());
        }
    }



    @Override
    public void deleteZkDeviceAlarm(Long id) {
        // 校验存在
        validateZkDeviceAlarmExists(id);
        // 删除
        zkDeviceAlarmMapper.deleteById(id);
    }

    private void validateZkDeviceAlarmExists(Long id) {
        if (zkDeviceAlarmMapper.selectById(id) == null) {
            throw exception(ZK_DEVICE_ALARM_NOT_EXISTS);
        }
    }

    @Override
    public ZkDeviceAlarmDO getZkDeviceAlarm(Long id) {
        return zkDeviceAlarmMapper.selectById(id);
    }

    @Override
    public List<ZkDeviceAlarmDO> getZkDeviceAlarmList(Collection<Long> ids) {
        return zkDeviceAlarmMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkDeviceAlarmDO> getZkDeviceAlarmPage(ZkDeviceAlarmPageReqVO pageReqVO) {
        pageReqVO.setDeptIds(deptScopeSupport.resolveDeptIds(pageReqVO.getDeptId()));
        if (deptScopeSupport.isDeniedScope(pageReqVO.getDeptIds())) {
            return PageResult.empty();
        }
        PageResult<ZkDeviceAlarmDO> pageResult = zkDeviceAlarmMapper.selectPage(pageReqVO);

        // 对结果列表按照 warnType 进行升序排序
        if (pageResult.getList() != null && !pageResult.getList().isEmpty()) {
            pageResult.getList().sort(Comparator.comparing(ZkDeviceAlarmDO::getWarnType,
                    Comparator.nullsLast(Comparator.naturalOrder())));
        }

        return pageResult;

    }

    @Override
    public List<ZkDeviceAlarmDO> getZkDeviceAlarmList(ZkDeviceAlarmExportReqVO exportReqVO) {
        exportReqVO.setDeptIds(deptScopeSupport.resolveDeptIds(exportReqVO.getDeptId()));
        if (deptScopeSupport.isDeniedScope(exportReqVO.getDeptIds())) {
            return Collections.emptyList();
        }
        return zkDeviceAlarmMapper.selectList(exportReqVO);
    }

    @Override
    public List<ZkDeviceAlarmDashboardRespVO> getZkDeviceAlarmDashboardList(Long deptId) {
        List<Long> deptIds = deptScopeSupport.resolveDeptIds(deptId);
        if (deptScopeSupport.isDeniedScope(deptIds)) {
            return Collections.emptyList();
        }
        return zkDeviceAlarmMapper.selectDashboardList(deptIds);
    }

}
