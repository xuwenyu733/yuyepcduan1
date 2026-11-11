package cn.iocoder.yudao.module.fish.service.zkinspectionreport;

import cn.hutool.http.HttpUtil;
import cn.iocoder.yudao.framework.common.base_api.BaseDeptService;
import cn.iocoder.yudao.framework.common.util.easy.EasyBeanUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyMap3;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ReceiveOperateVo;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.to.Task2StrHandler;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zktaskdetail.TaskDetailMapper;
import cn.iocoder.yudao.module.fish.utils.DeptScopeSupport;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkinspectionreport.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkinspectionreport.ZkInspectionReportDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkinspectionreport.ZkInspectionReportConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkinspectionreport.ZkInspectionReportMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 巡检智能分析报告 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class ZkInspectionReportServiceImpl implements ZkInspectionReportService {

    @Resource
    private ZkInspectionReportMapper zkInspectionReportMapper;
    @Resource
    private TaskDetailMapper taskDetailMapper;
    @Resource
    private BaseDeptService baseDeptService;
    @Resource
    private DeptScopeSupport deptScopeSupport;

    @Value("${KEDE_COMMON_API_PREUUID}")
    private String KEDE_COMMON_API_PREUUID;

    // @Value("${KEDE_COMMON_API_URI}")
    @Value("${KEDE_COMMON_API_URL}")
    private String KEDE_COMMON_API_URI;

    @Autowired
    private AdminUserApi adminUserApi;

    @Autowired
    @Lazy
    private Task2StrHandler task2StrHandler;

    @Override
    public Long createZkInspectionReport(ZkInspectionReportCreateReqVO createReqVO) {
        // 插入
        ZkInspectionReportDO zkInspectionReport = BeanUtils.toBean(createReqVO, ZkInspectionReportDO.class);
        zkInspectionReportMapper.insert(zkInspectionReport);
        // 返回
        return zkInspectionReport.getId();
    }

    @Override
    public void updateZkInspectionReport(ZkInspectionReportUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkInspectionReportExists(updateReqVO.getId());
        // 更新
        ZkInspectionReportDO updateObj = BeanUtils.toBean(updateReqVO, ZkInspectionReportDO.class);
        zkInspectionReportMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkInspectionReport(Long id) {
        // 校验存在
        validateZkInspectionReportExists(id);
        // 删除
        zkInspectionReportMapper.deleteById(id);
    }

    private void validateZkInspectionReportExists(Long id) {
        if (zkInspectionReportMapper.selectById(id) == null) {
            throw exception(ZK_INSPECTION_REPORT_NOT_EXISTS);
        }
    }

    @Override
    public ZkInspectionReportDO getZkInspectionReport(Long id) {
        return zkInspectionReportMapper.selectById(id);
    }

    @Override
    public List<ZkInspectionReportDO> getZkInspectionReportList(Collection<Long> ids) {
        return zkInspectionReportMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkInspectionReportDO> getZkInspectionReportPage(ZkInspectionReportPageReqVO pageReqVO) {
        applyDeptScope(pageReqVO);
        return zkInspectionReportMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkInspectionReportDO> getZkInspectionReportList(ZkInspectionReportExportReqVO exportReqVO) {
        applyDeptScope(exportReqVO);
        return zkInspectionReportMapper.selectList(exportReqVO);
    }

    /** 请求未带 deptId 时回落到登录用户部门，并展开为本部门+子部门 */
    private void applyDeptScope(Object reqVO) {
        try {
            Long reqDeptId = cn.iocoder.yudao.framework.common.util.obj.CommonUtil
                    .getValByProperty(reqVO, "deptId", Long.class);
            Long deptId = deptScopeSupport.resolveRequestOrLoginDeptId(reqDeptId);
            if (deptId != null && deptId > 0) {
                cn.iocoder.yudao.framework.common.util.obj.CommonUtil.setValByProperty(reqVO, "deptId", deptId);
            }
            baseDeptService.enhancerDeptIds(reqVO, deptId, "deptIds");
        } catch (Exception ex) {
            log.warn("[zk-inspection-report] applyDeptScope {}", ex.getMessage());
        }
    }

    @Override
    public Integer receiveTaskDetailReport(ReceiveOperateVo vo) {
        // 1. 判断参数
        if(vo == null || StringUtils.isEmpty(vo.getOtherParams())) return 0;
        String taskDetailIdStr = EasyBeanUtils.getValByJsonStr(vo.getOtherParams(), "taskDetailId");
        if(StringUtils.isEmpty(taskDetailIdStr)) return 0;
        // 2. 获取 历史 巡检报告 和 任务明细
        long currTaskDetailId = Long.parseLong(taskDetailIdStr);
        TaskDetailDO taskDetailDO = taskDetailMapper.selectById(currTaskDetailId);
        if(taskDetailDO == null) return 0;
        ZkInspectionReportDO zkInspectionReportDO = selectByTaskDetailId(currTaskDetailId);

        // 4. 添加数据
        Integer res = 0;
        if(zkInspectionReportDO != null) {
            zkInspectionReportDO.build_info(taskDetailDO.getWorkshopId(), taskDetailDO.getWorkshopName(), taskDetailDO.getPondSystemId(), taskDetailDO.getPondNum(),
                    taskDetailDO.getPondSystemName(), taskDetailDO.getPondId(), taskDetailDO.getPondDetailNum(), taskDetailDO.getPondName(), vo.getContent(),
                    taskDetailDO.getUserId(), taskDetailDO.getDeptId(), currTaskDetailId);
            // 生成文件报告
            enhancerReportUrl(vo, taskDetailDO, zkInspectionReportDO);
            res = zkInspectionReportMapper.updateById(zkInspectionReportDO);
        } else {
            zkInspectionReportDO = new ZkInspectionReportDO(taskDetailDO.getWorkshopId(), taskDetailDO.getWorkshopName(), taskDetailDO.getPondSystemId(), taskDetailDO.getPondNum(),
                    taskDetailDO.getPondSystemName(), taskDetailDO.getPondId(), taskDetailDO.getPondDetailNum(), taskDetailDO.getPondName(), vo.getContent(),
                    taskDetailDO.getUserId(), taskDetailDO.getDeptId(), currTaskDetailId);
            // 生成文件报告
            enhancerReportUrl(vo, taskDetailDO, zkInspectionReportDO);
            res = zkInspectionReportMapper.insert(zkInspectionReportDO);
        }

        return res;
    }

    public void enhancerReportUrl(ReceiveOperateVo vo, TaskDetailDO taskDetailDO, ZkInspectionReportDO zkInspectionReportDO) {
        AdminUserRespDTO user = adminUserApi.getUser(taskDetailDO.getUserId());
        // 1. 任务转 map
        Map<String, Object> map = task2StrHandler.parseTaskDetail3(taskDetailDO);
        // 2. 添加 agent建议
        String comprehensiveEvaluation = EasyBeanUtils.getValByJsonStr(vo.getContent(), "comprehensiveEvaluation"); // 综合评价
        String subItemSuggestions = EasyBeanUtils.getValByJsonStr(vo.getContent(), "subItemSuggestions"); // 分项建议
        String longTermOptimization = EasyBeanUtils.getValByJsonStr(vo.getContent(), "longTermOptimization"); // 长期优化
        map.put("comprehensiveEvaluation", comprehensiveEvaluation);
        map.put("subItemSuggestions", subItemSuggestions);
        map.put("longTermOptimization", longTermOptimization);
        map.put("userName", user.getNickname());
        map.put("pondName", String.format("科德门头沟基地%s", taskDetailDO.getPondDetailNum()));
        map.put("createTime", EasyDateUtil.format(taskDetailDO.getCreateTime(), "yyyy年MM月dd日"));
        // 3. 生成 html和pdf的文件
        Map<String, Object> params = new EasyMap3<Object>().put("preUUID", KEDE_COMMON_API_PREUUID)
                .put("ftlName", "InspectionReport.ftl")
                .put("map", map).value();
        String url = String.format("%s/%s", KEDE_COMMON_API_URI, "genHtml/genFile");
        String postRes = HttpUtil.post(url, JSON.toJSONString(params));
        log.warn("【巡检生成巡检报告-{}】middle,请求common_api({})参数:{}，结果:{}", taskDetailDO.getId(), url, JSON.toJSONString(params), postRes);
        String code = EasyBeanUtils.getValByJsonStr(postRes, "code"); // 综合评价
        if(!"200".equals(code)) return;
        JSONObject data = JSON.parseObject(postRes).getJSONObject("data");
        String htmlUrl = data.getString("htmlUrl");
        String pdfUrl = data.getString("pdfUrl");
        zkInspectionReportDO.setHtmlUrl(htmlUrl).setPdfUrl(pdfUrl);
    }

    @Override
    public ZkInspectionReportDO selectByTaskDetailId(Long taskDetailId) {
        if(taskDetailId == null) return null;
        // 1. 获取 报告
        QueryWrapper<ZkInspectionReportDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_detail_id", taskDetailId).orderByDesc("id");
        List<ZkInspectionReportDO> zkInspectionReportDOS = zkInspectionReportMapper.selectList(queryWrapper);
        if(EasyListUtil.isEmpty(zkInspectionReportDOS)) return null;
        // 2. 避免重复，删除错误数据
        if(zkInspectionReportDOS.size() > 1) {
            Set<Long> needDelIds = new HashSet<>();
            for(int i=1;i<zkInspectionReportDOS.size();i++) needDelIds.add(zkInspectionReportDOS.get(i).getId());
            zkInspectionReportMapper.deleteBatchIds(needDelIds);
        }
        return zkInspectionReportDOS.get(0);
    }

}
