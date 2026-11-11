package cn.iocoder.yudao.module.fish.service.fishsystemlog;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.vo.FishSystemLogCreateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.vo.FishSystemLogExportReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.vo.FishSystemLogPageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.vo.FishSystemLogUpdateReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.fishsystemlog.FishSystemLogDO;
import cn.iocoder.yudao.module.fish.dal.mysql.fishsystemlog.FishSystemLogMapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import java.util.*;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.FISH_SYSTEM_LOG_NOT_EXISTS;

/**
 * fish系统日志 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class FishSystemLogServiceImpl implements FishSystemLogService {

    @Resource
    private FishSystemLogMapper fishSystemLogMapper;

    @Override
    public Long createFishSystemLog(FishSystemLogCreateReqVO createReqVO) {
        // 插入
        FishSystemLogDO fishSystemLog = BeanUtils.toBean(createReqVO, FishSystemLogDO.class);
        fishSystemLogMapper.insert(fishSystemLog);
        // 返回
        return fishSystemLog.getId();
    }

    @Override
    public void updateFishSystemLog(FishSystemLogUpdateReqVO updateReqVO) {
        // 校验存在
        validateFishSystemLogExists(updateReqVO.getId());
        // 更新
        FishSystemLogDO updateObj = BeanUtils.toBean(updateReqVO, FishSystemLogDO.class);
        fishSystemLogMapper.updateById(updateObj);
    }

    @Override
    public void deleteFishSystemLog(Long id) {
        // 校验存在
        validateFishSystemLogExists(id);
        // 删除
        fishSystemLogMapper.deleteById(id);
    }

    private void validateFishSystemLogExists(Long id) {
        if (fishSystemLogMapper.selectById(id) == null) {
            throw exception(FISH_SYSTEM_LOG_NOT_EXISTS);
        }
    }

    @Override
    public FishSystemLogDO getFishSystemLog(Long id) {
        return fishSystemLogMapper.selectById(id);
    }

    @Override
    public List<FishSystemLogDO> getFishSystemLogList(Collection<Long> ids) {
        return fishSystemLogMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<FishSystemLogDO> getFishSystemLogPage(FishSystemLogPageReqVO pageReqVO) {
        return fishSystemLogMapper.selectPage(pageReqVO);
    }

    @Override
    public List<FishSystemLogDO> getFishSystemLogList(FishSystemLogExportReqVO exportReqVO) {
        return fishSystemLogMapper.selectList(exportReqVO);
    }

}
