package cn.iocoder.yudao.module.fish.service.zkbiologycapital;

import cn.iocoder.yudao.framework.common.base_api.BaseDeptService;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmedicinelogpond.ZkMedicineLogPondDO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologycapital.ZkBiologyCapitalDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkbiologycapital.ZkBiologyCapitalConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkbiologycapital.ZkBiologyCapitalMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 生物性资产 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkBiologyCapitalServiceImpl implements ZkBiologyCapitalService {

    @Resource
    private ZkBiologyCapitalMapper zkBiologyCapitalMapper;
    @Resource
    private BaseDeptService baseDeptService;
    @Autowired
    private AdminUserApi adminUserApi;

    @Override
    public Long createZkBiologyCapital(ZkBiologyCapitalCreateReqVO createReqVO) {
        // 插入
        ZkBiologyCapitalDO zkBiologyCapital = BeanUtils.toBean(createReqVO, ZkBiologyCapitalDO.class);
        zkBiologyCapitalMapper.insert(zkBiologyCapital);
        // 返回
        return zkBiologyCapital.getId();
    }

    @Override
    public void updateZkBiologyCapital(ZkBiologyCapitalUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkBiologyCapitalExists(updateReqVO.getId());
        // 更新
        ZkBiologyCapitalDO updateObj = BeanUtils.toBean(updateReqVO, ZkBiologyCapitalDO.class);
        zkBiologyCapitalMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkBiologyCapital(Long id) {
        // 校验存在
        validateZkBiologyCapitalExists(id);
        // 删除
        zkBiologyCapitalMapper.deleteById(id);
    }

    private void validateZkBiologyCapitalExists(Long id) {
        if (zkBiologyCapitalMapper.selectById(id) == null) {
            throw exception(ZK_BIOLOGY_CAPITAL_NOT_EXISTS);
        }
    }

    @Override
    public ZkBiologyCapitalDO getZkBiologyCapital(Long id) {
        return zkBiologyCapitalMapper.selectById(id);
    }

    @Override
    public List<ZkBiologyCapitalDO> getZkBiologyCapitalList(Collection<Long> ids) {
        return zkBiologyCapitalMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkBiologyCapitalDO> getZkBiologyCapitalPage(ZkBiologyCapitalPageReqVO pageReqVO) {
        return zkBiologyCapitalMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkBiologyCapitalDO> getZkBiologyCapitalList(ZkBiologyCapitalExportReqVO exportReqVO) {
        return zkBiologyCapitalMapper.selectList(exportReqVO);
    }

    @Override
    public List<ZkBiologyCapitalDO> getList() {
        QueryWrapper<ZkBiologyCapitalDO> queryWrapper = new QueryWrapper<>();

        return zkBiologyCapitalMapper.selectList(queryWrapper);
    }

    @Override
    public Integer saveBatch(List<ZkBiologyCapitalDO> list) {
        if(EasyListUtil.isEmpty(list)) return 0;
        zkBiologyCapitalMapper.insertBatch(list);
        return list.size();
    }

    @Override
    public Integer updateBatch(List<ZkBiologyCapitalDO> list) {
        if(EasyListUtil.isEmpty(list)) return 0;
        zkBiologyCapitalMapper.updateBatch(list, list.size());
        return list.size();
    }

    @Override
    public ZkBiologyCapitalDO feedingCostUpdate(ZkFeedingLogDO zkFeedingLog) {
        // 1. 获取数据
        ZkBiologyCapitalDO byPondId = getByPondId(zkFeedingLog.getPondId());
        if(byPondId == null) return null; // 投喂之前会触发统计
        // 2. 更新成本
        byPondId.build_feedCost(zkFeedingLog.getFeedingCost(), zkFeedingLog.getFeedCount(), zkFeedingLog.getFeedingPrice());
        zkBiologyCapitalMapper.updateById(byPondId);
        return byPondId;
    }

    @Override
    public ZkBiologyCapitalDO getByPondId(Long pondId) {
        QueryWrapper<ZkBiologyCapitalDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pond_id", pondId).last("limit 1");
        return zkBiologyCapitalMapper.selectOne(queryWrapper);
    }

    @Override
    public void medicineCostUpdate(Long pondId, BigDecimal medicineCost) {
        // 1. 获取数据
        ZkBiologyCapitalDO byPondId = getByPondId(pondId);
        if(byPondId == null) return; // TODO 后续有时间可以先同步数据，然后 再 更新成本
        // 2. 更新成本
        byPondId.build_medicineCost(medicineCost);
        zkBiologyCapitalMapper.updateById(byPondId);
    }

    @Override
    public ZkBiologyCapitalMainHomeRespVO getMainHomeData() {
        ZkBiologyCapitalMainHomeRespVO respVO = new ZkBiologyCapitalMainHomeRespVO();
        respVO.setBiologyCapitals(zkBiologyCapitalMapper.selectBiologyCapital());
        respVO.setSurvivalRates(zkBiologyCapitalMapper.selectSurvivalRate());
        respVO.setPrecisionFeedings(zkBiologyCapitalMapper.selectPrecisionFeeding());
        respVO.setFishGrowths(zkBiologyCapitalMapper.selectFishGrowth());
        respVO.setBaitCoefficients(zkBiologyCapitalMapper.selectBaitCoefficient());
        return respVO;
    }

    @Override
    public ZkBiologyCapitalDO sqlStatic() {
        // 1. 在 zk_product_cost 中 统计
        ZkBiologyCapitalDO zkBiologyCapitalDO = zkBiologyCapitalMapper.sqlStatic();
        if(zkBiologyCapitalDO == null) return zkBiologyCapitalDO;
        // 2. zk_product_cost中统计的会遗漏，使用erp_purchase_in_items采购的进行补充
        ZkBiologyCapitalDO byPurchase = zkBiologyCapitalMapper.sqlStaticByPurchase();
        // CommonUtil.copyPropertiesIgnoreNull(byPurchase, zkBiologyCapitalDO);
        if(EasyBigDecimalUtil.gt0(byPurchase.getFeedCost())) zkBiologyCapitalDO.setFeedCost(byPurchase.getFeedCost());
        if(EasyBigDecimalUtil.gt0(byPurchase.getBiologyCost())) zkBiologyCapitalDO.setBiologyCost(byPurchase.getBiologyCost());
        if(EasyBigDecimalUtil.gt0(byPurchase.getMedicineCost())) zkBiologyCapitalDO.setMedicineCost(byPurchase.getMedicineCost());
        if(EasyBigDecimalUtil.gt0(byPurchase.getDeviceCost())) zkBiologyCapitalDO.setDeviceCost(byPurchase.getDeviceCost());
        if(EasyBigDecimalUtil.gt0(byPurchase.getEnergyCost())) zkBiologyCapitalDO.setEnergyCost(byPurchase.getEnergyCost());
        if(EasyBigDecimalUtil.gt0(byPurchase.getExtraCost())) zkBiologyCapitalDO.setExtraCost(byPurchase.getExtraCost());
        return zkBiologyCapitalDO;
    }

    @Resource
    private cn.iocoder.yudao.module.fish.service.zkprecisionfeeding.ZkPrecisionFeedingBizService zkPrecisionFeedingBizService;

    @Override
    public PrecisionFeedingStatsRespVO getPrecisionFeedingStats() {
        // 仅用聚合 SQL，禁止再拉全量鱼池并 enrich（否则与列表接口重复且极慢）
        PrecisionFeedingStatsRespVO stats = zkBiologyCapitalMapper.selectPrecisionFeedingStats();
        if (stats == null) {
            stats = new PrecisionFeedingStatsRespVO();
        }
        return stats;
    }

    @Override
    public List<PrecisionFeedingPondRespVO> getPrecisionFeedingPonds() {
        AdminUserRespDTO adminUserRespDTO = adminUserApi.getUser(SecurityFrameworkUtils.getLoginUserId());
        List<Long> subDeptIds = baseDeptService.getSubDeptIds(adminUserRespDTO.getDeptId());

        QueryWrapper<ZkBiologyCapitalDO> queryWrapper = new QueryWrapper<>();
        // 无下级部门时仍按本部门过滤，禁止回退全量（防串部门）
        if (subDeptIds == null || subDeptIds.isEmpty()) {
            Long selfDeptId = adminUserRespDTO.getDeptId();
            if (selfDeptId != null && selfDeptId > 0) {
                queryWrapper.in("a1.dept_id", Collections.singletonList(selfDeptId));
            } else {
                // 无归属部门：返回空，避免越权看到全站鱼池
                return Collections.emptyList();
            }
        } else {
            queryWrapper.in("a1.dept_id", subDeptIds);
        }
        List<PrecisionFeedingPondRespVO> ponds = zkBiologyCapitalMapper.selectPrecisionFeedingPonds(queryWrapper);
        if (ponds == null) {
            ponds = new ArrayList<>();
        }

        List<cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo.PrecisionFeedingSpecStatVO> specStats;
        try {
            specStats = zkBiologyCapitalMapper.selectPrecisionFeedingSpecStats();
        } catch (Exception e) {
            specStats = Collections.emptyList();
        }
        if (specStats == null) {
            specStats = Collections.emptyList();
        }
        java.util.Map<Long, List<cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo.PrecisionFeedingSpecStatVO>> specStatMap = specStats.stream()
                .filter(stat -> stat.getPondId() != null)
                .collect(java.util.stream.Collectors.groupingBy(cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo.PrecisionFeedingSpecStatVO::getPondId));
        
        for (PrecisionFeedingPondRespVO pond : ponds) {
            pond.setFeedSpecStats(specStatMap.getOrDefault(pond.getId(), new java.util.ArrayList<>()));
        }
        try {
            zkPrecisionFeedingBizService.enrichPondList(ponds);
        } catch (Exception e) {
            // 扩展字段失败不影响基础列表
        }
        return ponds;
    }

    @Override
    public List<PrecisionFeedingTodayLogRespVO> getPrecisionFeedingTodayLogs() {
        return zkBiologyCapitalMapper.selectPrecisionFeedingTodayLogs();
    }

}
