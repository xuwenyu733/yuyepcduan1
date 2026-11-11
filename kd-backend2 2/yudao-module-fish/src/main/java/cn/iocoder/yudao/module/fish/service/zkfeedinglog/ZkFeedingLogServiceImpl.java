package cn.iocoder.yudao.module.fish.service.zkfeedinglog;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishproofing.ZkFishProofingDO;
import cn.iocoder.yudao.module.fish.service.zkbiologycapital.ZkBiologyCapitalService;
import cn.iocoder.yudao.module.fish.service.zkfishoperationlog.ZkFishOperationLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkfeedinglog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkfeedinglog.ZkFeedingLogConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkfeedinglog.ZkFeedingLogMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 投喂记录 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkFeedingLogServiceImpl implements ZkFeedingLogService {

    @Resource
    private ZkFeedingLogMapper zkFeedingLogMapper;
    @Resource
    private ZkBiologyCapitalService zkBiologyCapitalService;
    @Resource
    private ZkFishOperationLogService zkFishOperationLogService;

    @Override
    public ZkFeedingLogDO createZkFeedingLog(ZkFeedingLogCreateReqVO createReqVO) {
        // 插入
        ZkFeedingLogDO zkFeedingLog = BeanUtils.toBean(createReqVO, ZkFeedingLogDO.class);
        zkFeedingLogMapper.insert(zkFeedingLog);
        // 返回
        return zkFeedingLog;
    }

    @Override
    public List<ZkFeedingLogDO> updateZkFeedingLog(ZkFeedingLogUpdateReqVO updateReqVO) {
        ZkFeedingLogDO updateObj = BeanUtils.toBean(updateReqVO, ZkFeedingLogDO.class);
        return customUpdate(updateObj);
    }

    public void sync_biologyCapital(ZkFeedingLogDO curr, ZkFeedingLogDO before) {
        if(EasyBigDecimalUtil.valueEquals(curr.getFeedCount(), before.getFeedCount())) {
            // 不 需要同步数据
            return;
        }
        // 需要同步数据 1:增加count，difference正，最终加成本，正确; 2:减少count，difference负，最终减成本，正确;
        BigDecimal difference = EasyBigDecimalUtil.subtract(curr.getFeedCount(), before.getFeedCount());
        BigDecimal diffCost = EasyBigDecimalUtil.multiply(difference, before.getFeedingPrice());
        ZkFeedingLogDO temp = new ZkFeedingLogDO();
        CommonUtil.copyPropertiesIgnoreNull(curr, temp);
        temp.setFeedingCost(diffCost);
        temp.setFeedCount(difference).setFeedTime(curr.getFeedTime());
        zkBiologyCapitalService.feedingCostUpdate(temp);
        // 同步数据 到 zk_fish_operation_log
        zkFishOperationLogService.sync_currFeedCount(temp);
    }

    @Override
    public List<ZkFeedingLogDO> getByTaskDetailIds(Set<Long> taskDetailIds) {

        if(CollectionUtils.isAnyEmpty(taskDetailIds)) return new ArrayList<>();
        QueryWrapper<ZkFeedingLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("task_detail_id", taskDetailIds);
        return zkFeedingLogMapper.selectList(queryWrapper);
    }

    @Override
    public Integer customSaveBatch(List<ZkFeedingLogDO> needAdd) {
        if(EasyListUtil.isNotEmpty(needAdd)) return 0;
        Boolean insertBatch = zkFeedingLogMapper.insertBatch(needAdd);

        return insertBatch?needAdd.size():0;
    }

    @Override
    public void customUpdateBatch(List<ZkFeedingLogDO> finalNeedUpdate) {
        if(EasyListUtil.isNotEmpty(finalNeedUpdate)) return;
        zkFeedingLogMapper.updateBatch(finalNeedUpdate);
    }

    @Override
    @DataPermission(enable = false)
    public List<Map<String, Object>> staticFeedInfo() {
        return zkFeedingLogMapper.staticFeedInfo();
    }

    @Override
    @DataPermission(enable = false)
    public List<VariousFryStagesCostRespVO> getVariousFryStagesCost() {
        return zkFeedingLogMapper.getVariousFryStagesCost();
    }

    @Override
    public ZkFeedingLogDO getById(Long id) {
        return zkFeedingLogMapper.selectById(id);
    }

    @Override
    public ZkFeedingLogDO deleteZkFeedingLog(Long id) {
        // 校验存在
        ZkFeedingLogDO zkFeedingLogDO = validateZkFeedingLogExists(id);
        // 删除
        zkFeedingLogMapper.deleteById(id);
        return zkFeedingLogDO;
    }

    private ZkFeedingLogDO validateZkFeedingLogExists(Long id) {
        ZkFeedingLogDO zkFeedingLogDO = zkFeedingLogMapper.selectById(id);
        if (zkFeedingLogDO == null) {
            throw exception(ZK_FEEDING_LOG_NOT_EXISTS);
        }
        return zkFeedingLogDO;
    }

    @Override
    public ZkFeedingLogDO getZkFeedingLog(Long id) {
        return zkFeedingLogMapper.selectById(id);
    }

    @Override
    public List<ZkFeedingLogDO> getZkFeedingLogList(Collection<Long> ids) {
        return zkFeedingLogMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkFeedingLogDO> getZkFeedingLogPage(ZkFeedingLogPageReqVO pageReqVO) {
        return zkFeedingLogMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkFeedingLogDO> getZkFeedingLogList(ZkFeedingLogExportReqVO exportReqVO) {
        return zkFeedingLogMapper.selectList(exportReqVO);
    }

    @Override
    public BigDecimal getAvg(Long pondId) {
        // 1. 获取历史值
        Date date = EasyDateUtil.plusDay2(LocalDateTime.now(), -5);
        QueryWrapper<ZkFeedingLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("feed_time", date).eq("pond_id", pondId);
        List<ZkFeedingLogDO> feedList = zkFeedingLogMapper.selectList(queryWrapper);
        if(EasyListUtil.isEmpty(feedList)) return new BigDecimal(0);
        // 2. 获取平均值
        BigDecimal sum = new BigDecimal(0);
        for(ZkFeedingLogDO item : feedList) {
            sum = EasyBigDecimalUtil.add(item.getFeedCount(), sum);
        }
        BigDecimal avg = EasyBigDecimalUtil.divide(sum, new BigDecimal(feedList.size()));
        return avg;
    }

    @Override
    public List<ZkFeedingLogDO> getByPondId(Long pondId, Integer days) {
        List<ZkFeedingLogDO> res = new ArrayList<>();
        for(int i=1;i<days;i++) {
            List<Date> dates = EasyDateUtil.plusDayBetween(LocalDateTime.now(), -i);
            ZkFeedingLogDO byDay = getByDay(pondId, dates);
            res.add(byDay);
        }

        return res;
    }

    @Override
    public List<ZkFeedingLogDO> customUpdate(ZkFeedingLogDO updateObj) {
        // 1. 校验存在
        ZkFeedingLogDO before = validateZkFeedingLogExists(updateObj.getId());
        // 2. 执行同步数据 到 表: zk_biology_capital
        sync_biologyCapital(updateObj, before);
        // 3. 更新
        zkFeedingLogMapper.updateById(updateObj);
        return List.of(before, updateObj);
    }

    public ZkFeedingLogDO getByDay(Long pondId, List<Date> timeArr) {
        ZkFeedingLogDO zkFeedingLogDO = new ZkFeedingLogDO("不详", new BigDecimal(0));
        // 1. 获取最近三次投喂记录
        QueryWrapper<ZkFeedingLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pond_id", pondId).between("feed_time", timeArr.get(0), timeArr.get(1));
        queryWrapper.orderByDesc("id");
        List<ZkFeedingLogDO> feedList = zkFeedingLogMapper.selectList(queryWrapper);
        if(EasyListUtil.isEmpty(feedList)) return zkFeedingLogDO;
        // 2. 计算总量
        for(ZkFeedingLogDO item : feedList) {
            zkFeedingLogDO.build_info(item.getPondId(), item.getFishNum(), item.getFeedBrand(), item.getFeedName(), item.getFeedSpec(), item.getFeedCount());
        }

        return zkFeedingLogDO;

    }

}
