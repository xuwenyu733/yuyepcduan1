package cn.iocoder.yudao.module.fish.service.zkpond;

import cn.iocoder.yudao.framework.common.base_api.BaseDeptService;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyMap;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpondfish.ZkPondFishMapper;
import cn.iocoder.yudao.module.fish.service.zkbiologicalasset.ZkBiologicalAssetService;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.context.annotation.Lazy;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkpond.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkpond.ZkPondConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpond.ZkPondMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 存塘 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class ZkPondServiceImpl implements ZkPondService {

    @Resource
    private ZkPondMapper zkPondMapper;
    
    @Resource
    @Lazy
    private ZkBiologicalAssetService zkBiologicalAssetService;

    @Resource
    private BaseDeptService baseDeptService;

    @Resource
    private ZkPondFishMapper zkPondFishMapper;

    @Override
    public ZkPondDO createZkPond(ZkPondCreateReqVO createReqVO) {
        // 插入
        ZkPondDO zkPond = BeanUtils.toBean(createReqVO, ZkPondDO.class);
        zkPondMapper.insert(zkPond);
        // 返回
        return zkPond;
    }

    @Override
    public List<ZkPondDO> updateZkPond(ZkPondUpdateReqVO updateReqVO) {
        List<ZkPondDO> res = new ArrayList<>();
        // 校验存在
        ZkPondDO before = validateZkPondExists(updateReqVO.getId());
        // 更新
        ZkPondDO updateObj = BeanUtils.toBean(updateReqVO, ZkPondDO.class);
        zkPondMapper.updateById(updateObj);
        // 同步更新生物资产评估数据
        try {
            if (zkBiologicalAssetService != null) {
                zkBiologicalAssetService.refreshZkBiologicalAssetByZkPond(updateObj);
            } else {
                log.warn("ZkBiologicalAssetService 未初始化，无法同步更新生物资产评估数据，池塘ID: {}", updateObj.getId());
            }
        } catch (Exception e) {
            log.error("同步更新生物资产评估数据失败，池塘ID: {}", updateObj.getId(), e);
        }
        res.add(before);
        res.add(updateObj);
        return res;
    }

    @Override
    public void deleteZkPond(Long id) {
        // 校验存在
        ZkPondDO before = validateZkPondExists(id);
        // 删除
        zkPondMapper.deleteById(id);
    }

    private ZkPondDO validateZkPondExists(Long id) {
        ZkPondDO zkPondDO = zkPondMapper.selectById(id);
        if (zkPondDO == null) {
            throw exception(ZK_POND_NOT_EXISTS);
        }
        return zkPondDO;
    }

    @Override
    public ZkPondDO getZkPond(Long id) {
        return zkPondMapper.selectById(id);
    }

    @Override
    public List<ZkPondDO> getZkPondList(Collection<Long> ids) {
        if(CollectionUtils.isAnyEmpty(ids)) return new ArrayList<>();
        return zkPondMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkPondDO> getZkPondPage(ZkPondPageReqVO pageReqVO) {
        baseDeptService.enhancerDeptIds(pageReqVO, pageReqVO.getDeptId(), "deptIds");
        return zkPondMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkPondDO> getZkPondList(ZkPondExportReqVO exportReqVO) {
        return zkPondMapper.selectList(exportReqVO);
    }

    @Override
    public List<ZkPondDO> getByPondNum(String pondNum) {
        QueryWrapper<ZkPondDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pond_num", pondNum);
        return zkPondMapper.selectList(queryWrapper);
    }

    @Override
    public List<ZkPondDO> getByPondDetailNum(String pondDetailNum) {
        QueryWrapper<ZkPondDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pond_detail_num", pondDetailNum);
        return zkPondMapper.selectList(queryWrapper);
    }

    @Override
    public void updateById(ZkPondDO zkPondDO) {
        zkPondMapper.updateById(zkPondDO);
        // 同步更新生物资产评估数据
        try {
            if (zkBiologicalAssetService != null) {
                zkBiologicalAssetService.refreshZkBiologicalAssetByZkPond(zkPondDO);
                log.info("同步更新生物资产评估数据成功，池塘ID: {}", zkPondDO.getId());
            } else {
                log.warn("ZkBiologicalAssetService 未初始化，无法同步更新生物资产评估数据，池塘ID: {}", zkPondDO.getId());
            }
        } catch (Exception e) {
            log.error("同步更新生物资产评估数据失败，池塘ID: {}", zkPondDO.getId(), e);
        }
    }

    @Override
    public String getPondInfo() {
        List<ZkPondDO> zkPondDOS = zkPondMapper.selectList();
        if(EasyListUtil.isEmpty(zkPondDOS)) return "";
        List<String> collect = zkPondDOS.stream().map(obj -> new EasyMap()
                .put("id", obj.getId()).put("pondName", String.format("%s(%s)",obj.getPondNum(), obj.getPondName())).toJson()
        ).collect(Collectors.toList());

        return JSON.toJSONString(collect);
    }

    @Override
    public PondSummaryVo getSummaryInfo() {
        PondSummaryVo vo = zkPondMapper.getSummaryInfo();
        return vo;
    }

    @Override
    public void batchUpdate(List<ZkPondDO> list) {
        zkPondMapper.updateBatch(list, list.size());
        // 同步更新生物资产评估数据
        for (ZkPondDO zkPondDO : list) {
            try {
                if (zkBiologicalAssetService != null) {
                    zkBiologicalAssetService.refreshZkBiologicalAssetByZkPond(zkPondDO);
                    log.info("批量更新-同步更新生物资产评估数据成功，池塘ID: {}", zkPondDO.getId());
                } else {
                    log.warn("ZkBiologicalAssetService 未初始化，无法同步更新生物资产评估数据，池塘ID: {}", zkPondDO.getId());
                }
            } catch (Exception e) {
                log.error("批量更新-同步更新生物资产评估数据失败，池塘ID: {}", zkPondDO.getId(), e);
            }
        }
    }

    @Override
    public void updateFishNum(Long pondId, Long newFishNum) {
        ZkPondDO zkPondDO = new ZkPondDO(pondId, newFishNum);
        zkPondMapper.updateById(zkPondDO);
        // 同步更新生物资产评估数据
        try {
            if (zkBiologicalAssetService != null) {
                zkBiologicalAssetService.refreshZkBiologicalAssetByZkPond(zkPondDO);
                log.info("更新鱼数量-同步更新生物资产评估数据成功，池塘ID: {}", zkPondDO.getId());
            } else {
                log.warn("ZkBiologicalAssetService 未初始化，无法同步更新生物资产评估数据，池塘ID: {}", zkPondDO.getId());
            }
        } catch (Exception e) {
            log.error("更新鱼数量-同步更新生物资产评估数据失败，池塘ID: {}", zkPondDO.getId(), e);
        }
    }

    @Override
    public ZkPondDO getOne() {
        List<ZkPondDO> zkPondDOS = zkPondMapper.selectList(new QueryWrapper<>());
        return zkPondDOS.get(0);
    }

    @Override
    public ZkPondDO customUpdateById(ZkPondDO zkPondDO) {
        // 1. 校验存在
        ZkPondDO oldPond = validateZkPondExists(zkPondDO.getId());
        zkPondDO.build_batchNumber(oldPond.getBatchNumber(), zkPondDO.getBatchNumber())
                .build_fishSpec(zkPondDO.getFishSpec());
        CommonUtil.copyPropertiesIgnoreNull(zkPondDO, oldPond);
        // 2. 执行更新
        zkPondMapper.updateById(zkPondDO);
        // 同步更新生物资产评估数据
        try {
            if (zkBiologicalAssetService != null) {
                zkBiologicalAssetService.refreshZkBiologicalAssetByZkPond(zkPondDO);
                log.info("自定义更新-同步更新生物资产评估数据成功，池塘ID: {}", zkPondDO.getId());
            } else {
                log.warn("ZkBiologicalAssetService 未初始化，无法同步更新生物资产评估数据，池塘ID: {}", zkPondDO.getId());
            }
        } catch (Exception e) {
            log.error("自定义更新-同步更新生物资产评估数据失败，池塘ID: {}", zkPondDO.getId(), e);
        }
        return zkPondDO;
    }

    @Override
    public ZkPondDO selectFirst() {
        QueryWrapper<ZkPondDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id").last("limit 1");
        return zkPondMapper.selectOne(queryWrapper);
    }

    @Override
    public List<ZkPondDO> selectByPondIds(Set<Long> pondSet) {
        if(CollectionUtils.isAnyEmpty(pondSet)) return new ArrayList<>();
        QueryWrapper<ZkPondDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("zp.id", pondSet);

        return zkPondMapper.selectByPondIds(queryWrapper);
    }

    @Override
    public List<ZkPondDO> selectList() {
        QueryWrapper<ZkPondDO> queryWrapper = new QueryWrapper<>();

        return zkPondMapper.selectList(queryWrapper);
    }

    @Override
    public Integer customUpdateBatchById(List<ZkPondDO> pondDOList) {
        if(EasyListUtil.isEmpty(pondDOList)) return 0;
        zkPondMapper.updateBatch(pondDOList);
        // 同步更新生物资产评估数据
        for (ZkPondDO zkPondDO : pondDOList) {
            try {
                if (zkBiologicalAssetService != null) {
                    zkBiologicalAssetService.refreshZkBiologicalAssetByZkPond(zkPondDO);
                    log.info("自定义批量更新-同步更新生物资产评估数据成功，池塘ID: {}", zkPondDO.getId());
                } else {
                    log.warn("ZkBiologicalAssetService 未初始化，无法同步更新生物资产评估数据，池塘ID: {}", zkPondDO.getId());
                }
            } catch (Exception e) {
                log.error("自定义批量更新-同步更新生物资产评估数据失败，池塘ID: {}", zkPondDO.getId(), e);
            }
        }
        return pondDOList.size();
    }

    @Override
    public void updateFishSpecByPondId(Long targetPondId, BigDecimal splitAvgFishSpec) {
        if(splitAvgFishSpec == null) return;
        zkPondMapper.updateById(new ZkPondDO(targetPondId, splitAvgFishSpec));
        LambdaUpdateWrapper<ZkPondFishDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(ZkPondFishDO::getCurrFishSpec, splitAvgFishSpec).set(ZkPondFishDO::getAvgFishSpec, splitAvgFishSpec)
                        .eq(ZkPondFishDO::getPondId, targetPondId);
        zkPondFishMapper.update(updateWrapper);
    }

}