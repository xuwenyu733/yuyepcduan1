package cn.iocoder.yudao.module.fish.service.zkdevicerecord;

import cn.iocoder.yudao.framework.common.base_api.BaseDeptService;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkintelligentdiagnosis.ZkIntelligentDiagnosisDO;
import cn.iocoder.yudao.module.fish.utils.DeptScopeSupport;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkdevicerecord.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicerecord.ZkDeviceRecordDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkdevicerecord.ZkDeviceRecordConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkdevicerecord.ZkDeviceRecordMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 设备数据记录 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkDeviceRecordServiceImpl implements ZkDeviceRecordService {

    @Resource
    private ZkDeviceRecordMapper zkDeviceRecordMapper;
    @Resource
    private BaseDeptService baseDeptService;
    @Resource
    private DeptScopeSupport deptScopeSupport;

    @Override
    public Long createZkDeviceRecord(ZkDeviceRecordCreateReqVO createReqVO) {
        // 插入
        ZkDeviceRecordDO zkDeviceRecord = BeanUtils.toBean(createReqVO, ZkDeviceRecordDO.class);
        zkDeviceRecordMapper.insert(zkDeviceRecord);
        // 返回
        return zkDeviceRecord.getId();
    }

    @Override
    public void updateZkDeviceRecord(ZkDeviceRecordUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkDeviceRecordExists(updateReqVO.getId());
        // 更新
        ZkDeviceRecordDO updateObj = BeanUtils.toBean(updateReqVO, ZkDeviceRecordDO.class);
        zkDeviceRecordMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkDeviceRecord(Long id) {
        // 校验存在
        validateZkDeviceRecordExists(id);
        // 删除
        zkDeviceRecordMapper.deleteById(id);
    }

    private void validateZkDeviceRecordExists(Long id) {
        if (zkDeviceRecordMapper.selectById(id) == null) {
            throw exception(ZK_DEVICE_RECORD_NOT_EXISTS);
        }
    }

    @Override
    public ZkDeviceRecordDO getZkDeviceRecord(Long id) {
        return zkDeviceRecordMapper.selectById(id);
    }

    @Override
    public List<ZkDeviceRecordDO> getZkDeviceRecordList(Collection<Long> ids) {
        return zkDeviceRecordMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkDeviceRecordDO> getZkDeviceRecordPage(ZkDeviceRecordPageReqVO pageReqVO) {
        if (pageReqVO.getDeptId() == null) {
            pageReqVO.setDeptId(deptScopeSupport.resolveRequestOrLoginDeptId(null));
        }
        baseDeptService.enhancerDeptIds(pageReqVO, pageReqVO.getDeptId(), "deptIds");
        return zkDeviceRecordMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkDeviceRecordDO> getZkDeviceRecordList(ZkDeviceRecordExportReqVO exportReqVO) {
        return zkDeviceRecordMapper.selectList(exportReqVO);
    }

    @Override
    public List<ZkDeviceRecordDO> selectNeedDel(int recentDays) {
        return zkDeviceRecordMapper.selectNeedDel(recentDays);
    }

    @Override
    public void customeDelete(List<ZkDeviceRecordDO> recordDOS) {
        if (EasyListUtil.isEmpty(recordDOS)) return;
        Set<Long> collect = recordDOS.stream().map(ZkDeviceRecordDO::getId).collect(Collectors.toSet());
        QueryWrapper<ZkDeviceRecordDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(ZkDeviceRecordDO::getId, collect);
        zkDeviceRecordMapper.delete(queryWrapper);
    }

    @Override
    public List<ZkDeviceRecordDO> selectRecent() {
        return zkDeviceRecordMapper.selectRecent();
    }

    @Override
    public List<ZkDeviceRecordDO> selectRecentByQuery(Long pondId) {
        QueryWrapper<ZkDeviceRecordDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("zd.pond_id", pondId);
        return zkDeviceRecordMapper.selectRecentByQuery(queryWrapper);
    }

}
