package cn.iocoder.yudao.module.fish.service.zkspecslog;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkspecslog.to.CalculateFishRadioTool;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.service.zkpondfish.ZkPondFishService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkspecslog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkspecslog.ZkSpecsLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkspecslog.ZkSpecsLogConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkspecslog.ZkSpecsLogMapper;
import cn.iocoder.yudao.module.fish.utils.DeptScopeSupport;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 规格记录 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkSpecsLogServiceImpl implements ZkSpecsLogService {

    @Resource
    private ZkSpecsLogMapper zkSpecsLogMapper;
    @Resource
    private DeptScopeSupport deptScopeSupport;
    @Autowired
    @Lazy
    private CalculateFishRadioTool calculateFishRadioTool;
    @Autowired
    @Lazy
    private ZkPondFishService zkPondFishService;

    @Override
    public Long createZkSpecsLog(ZkSpecsLogCreateReqVO createReqVO) {
        // 插入
        ZkSpecsLogDO zkSpecsLog = BeanUtils.toBean(createReqVO, ZkSpecsLogDO.class);
        zkSpecsLogMapper.insert(zkSpecsLog);
        // 返回
        return zkSpecsLog.getId();
    }

    @Override
    public void updateZkSpecsLog(ZkSpecsLogUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkSpecsLogExists(updateReqVO.getId());
        // 更新
        ZkSpecsLogDO updateObj = BeanUtils.toBean(updateReqVO, ZkSpecsLogDO.class);
        zkSpecsLogMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkSpecsLog(Long id) {
        // 校验存在
        validateZkSpecsLogExists(id);
        // 删除
        zkSpecsLogMapper.deleteById(id);
    }

    private void validateZkSpecsLogExists(Long id) {
        if (zkSpecsLogMapper.selectById(id) == null) {
            throw exception(ZK_SPECS_LOG_NOT_EXISTS);
        }
    }

    @Override
    public ZkSpecsLogDO getZkSpecsLog(Long id) {
        return zkSpecsLogMapper.selectById(id);
    }

    @Override
    public List<ZkSpecsLogDO> getZkSpecsLogList(Collection<Long> ids) {
        return zkSpecsLogMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkSpecsLogDO> getZkSpecsLogPage(ZkSpecsLogPageReqVO pageReqVO) {
        return zkSpecsLogMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkSpecsLogDO> getZkSpecsLogList(ZkSpecsLogExportReqVO exportReqVO) {
        return zkSpecsLogMapper.selectList(exportReqVO);
    }

    @Override
    public List<ZkSpecsLogDO> getList() {
        QueryWrapper<ZkSpecsLogDO> queryWrapper = new QueryWrapper<>();
        List<ZkSpecsLogDO> list = zkSpecsLogMapper.selectList(queryWrapper);

        return list;
    }

    @Override
    public void customBatchInsert(List<ZkSpecsLogDO> needAdd) {
        if(EasyListUtil.isEmpty(needAdd)) return;
        zkSpecsLogMapper.insertBatch(needAdd);
    }

    @Override
    public void customBatchUpdate(List<ZkSpecsLogDO> needUpdate) {
        if(EasyListUtil.isEmpty(needUpdate)) return;
        zkSpecsLogMapper.updateBatch(needUpdate, needUpdate.size());
    }

    @Override
    public ZkSpecsLogDO getByPondId(Long pondId) {
        if(pondId == null) return null;
        // 1. 获取数据
        QueryWrapper<ZkSpecsLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pond_id", pondId).orderByDesc("id");
        List<ZkSpecsLogDO> zkSpecsLogDOS = zkSpecsLogMapper.selectList(queryWrapper);
        // 2. 删除多余
        if(EasyListUtil.isEmpty(zkSpecsLogDOS)) return null;
        if(zkSpecsLogDOS.size() == 1) return zkSpecsLogDOS.get(0);
        if(zkSpecsLogDOS.size() > 1) {
            Set<Long> needDel = new HashSet<>();
            for(int i=1;i<zkSpecsLogDOS.size();i++) needDel.add(zkSpecsLogDOS.get(i).getId());
            zkSpecsLogMapper.deleteBatchIds(needDel);
        }
        return zkSpecsLogDOS.get(0);
    }

    @Override
    public List<ZkSpecsLogDO> updateLogBySpecs(ZkPondFishDetailDO detailDO) {
        // 1. 找到  表: zk_specs_log 数据
        List<ZkSpecsLogDO> list = new ArrayList<>();
        ZkSpecsLogDO specsLogDO = getByPondId(detailDO.getPondId());
        ZkSpecsLogDO targetSpecsLogDO = getByPondId(detailDO.getTargetPondId());
        if(specsLogDO != null) list.add(specsLogDO);
        if(targetSpecsLogDO != null) list.add(targetSpecsLogDO);
        if(EasyListUtil.isEmpty(list)) return null;
        // 获取zk_pond_fish数据
        List<ZkPondFishDO> zkPondFishDOS = zkPondFishService.selectByPondIdSet(list.stream().map(ZkSpecsLogDO::getPondId).collect(Collectors.toSet()));
        // 2. 执行更新
        list.forEach(obj -> obj.setActuralSpecs(EasyBigDecimalUtil.toNumber(detailDO.getTargetFishSpec())));
        List<ZkSpecsLogDO> needUpdateList = calculateFishRadioTool.calculateLog(list, zkPondFishDOS);
        zkSpecsLogMapper.updateBatch(needUpdateList, needUpdateList.size());
        return list;
    }

    @Override
    public List<ZkFcrModelRespVO> getFcrModelList(Long deptId) {
        List<ZkFcrModelRespVO> fcrModelList = zkSpecsLogMapper.getFcrModelList(deptScopeSupport.resolveDeptIds(deptId));
        if(EasyListUtil.isEmpty(fcrModelList)) return fcrModelList;
        // 1. 如果初始重量和当前重量相同，则根据投喂量 / 饵料系数反推净增重
        for(ZkFcrModelRespVO item : fcrModelList) {
            BigDecimal totalWeight = item.getTotalWeight() == null ? BigDecimal.ZERO : item.getTotalWeight();
            BigDecimal initWeight = item.getInitWeight() == null ? BigDecimal.ZERO : item.getInitWeight();
            BigDecimal totalCount = item.getTotalCount() == null ? BigDecimal.ZERO : item.getTotalCount();
            item.setTotalWeight(totalWeight);
            item.setInitWeight(initWeight);
            item.setTotalCount(totalCount);
            if (item.getAccumulatedWeightGain() == null) {
                item.setAccumulatedWeightGain(totalWeight.subtract(initWeight).max(BigDecimal.ZERO));
            }
            BigDecimal acturalRadio = item.getActuralRadio();
            if (totalWeight.compareTo(initWeight) == 0
                    && acturalRadio != null
                    && acturalRadio.compareTo(BigDecimal.ZERO) > 0
                    && totalCount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal addWeight = EasyBigDecimalUtil.divide(totalCount, acturalRadio);
                item.setTotalWeight(EasyBigDecimalUtil.add(initWeight, addWeight));
                item.setAccumulatedWeightGain(addWeight);
            }
        }
        return fcrModelList;
    }

}
