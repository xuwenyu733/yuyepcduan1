package cn.iocoder.yudao.module.fish.service.zkmedicinelogpond;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicinelog.ZkUseMedicineLogDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkmedicinelogpond.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmedicinelogpond.ZkMedicineLogPondDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.dal.mysql.zkmedicinelogpond.ZkMedicineLogPondMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 用药存塘关联 Service 实现类
 *
 * @author admin2
 */
@Service
@Validated
public class ZkMedicineLogPondServiceImpl implements ZkMedicineLogPondService {

    @Resource
    private ZkMedicineLogPondMapper zkMedicineLogPondMapper;

    @Override
    public Long createZkMedicineLogPond(ZkMedicineLogPondSaveReqVO createReqVO) {
        // 插入
        ZkMedicineLogPondDO zkMedicineLogPond = BeanUtils.toBean(createReqVO, ZkMedicineLogPondDO.class);
        zkMedicineLogPondMapper.insert(zkMedicineLogPond);

        // 返回
        return zkMedicineLogPond.getId();
    }

    @Override
    public void updateZkMedicineLogPond(ZkMedicineLogPondSaveReqVO updateReqVO) {
        // 校验存在
        validateZkMedicineLogPondExists(updateReqVO.getId());
        // 更新
        ZkMedicineLogPondDO updateObj = BeanUtils.toBean(updateReqVO, ZkMedicineLogPondDO.class);
        zkMedicineLogPondMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkMedicineLogPond(Long id) {
        // 校验存在
        validateZkMedicineLogPondExists(id);
        // 删除
        zkMedicineLogPondMapper.deleteById(id);
    }

    @Override
        public void deleteZkMedicineLogPondListByIds(List<Long> ids) {
        // 删除
        zkMedicineLogPondMapper.deleteByIds(ids);
        }


    private void validateZkMedicineLogPondExists(Long id) {
        if (zkMedicineLogPondMapper.selectById(id) == null) {
            throw exception(ZK_MEDICINE_LOG_POND_NOT_EXISTS);
        }
    }

    @Override
    public ZkMedicineLogPondDO getZkMedicineLogPond(Long id) {
        return zkMedicineLogPondMapper.selectById(id);
    }

    @Override
    public PageResult<ZkMedicineLogPondDO> getZkMedicineLogPondPage(ZkMedicineLogPondPageReqVO pageReqVO) {
        return zkMedicineLogPondMapper.selectPage(pageReqVO);
    }

    @Override
    public void customSaveBatch(Long useMedicineLogId, List<ZkMedicineLogPondDO> collect) {
        // 1. 删除历史
        QueryWrapper<ZkMedicineLogPondDO> query = new QueryWrapper<>(new ZkMedicineLogPondDO().setMedicineLogId(useMedicineLogId));
        zkMedicineLogPondMapper.delete(query);
        // 2. 重新添加
        zkMedicineLogPondMapper.insertBatch(collect);
    }

    @Override
    public void deleteByMedicineLogId(Long medicineLogId) {
        QueryWrapper<ZkMedicineLogPondDO> queryWrapper = new QueryWrapper<>(new ZkMedicineLogPondDO().setMedicineLogId(medicineLogId));
        zkMedicineLogPondMapper.delete(queryWrapper);
    }

    @Override
    public List<ZkMedicineLogPondDO> selectByMedicineLogIds(Set<Long> medicineLogIds) {
        LambdaQueryWrapper<ZkMedicineLogPondDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ZkMedicineLogPondDO::getMedicineLogId, medicineLogIds);
        return zkMedicineLogPondMapper.selectList(queryWrapper);
    }

}