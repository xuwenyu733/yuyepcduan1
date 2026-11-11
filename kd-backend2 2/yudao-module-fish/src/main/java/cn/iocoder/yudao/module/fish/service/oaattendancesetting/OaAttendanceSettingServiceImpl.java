package cn.iocoder.yudao.module.fish.service.oaattendancesetting;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.fish.controller.admin.oaattendancesetting.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.oaattendancesetting.OaAttendanceSettingDO;
import cn.iocoder.yudao.module.fish.dal.mysql.oaattendancesetting.OaAttendanceSettingMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.OA_ATTENDANCE_SETTING_NOT_EXISTS;

/**
 * OA考勤设置 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class OaAttendanceSettingServiceImpl implements OaAttendanceSettingService {

    @Resource
    private OaAttendanceSettingMapper oaAttendanceSettingMapper;

    @Override
    public Long createOaAttendanceSetting(OaAttendanceSettingCreateReqVO createReqVO) {
        // 插入
        OaAttendanceSettingDO setting = BeanUtils.toBean(createReqVO, OaAttendanceSettingDO.class);
        if (setting.getStatus() == null) {
            setting.setStatus(1); // 默认启用
        }
        if (setting.getSortOrder() == null) {
            setting.setSortOrder(0);
        }
        oaAttendanceSettingMapper.insert(setting);
        // 返回
        return setting.getId();
    }

    @Override
    public void updateOaAttendanceSetting(OaAttendanceSettingUpdateReqVO updateReqVO) {
        // 校验存在
        validateOaAttendanceSettingExists(updateReqVO.getId());
        // 更新
        OaAttendanceSettingDO updateObj = BeanUtils.toBean(updateReqVO, OaAttendanceSettingDO.class);
        oaAttendanceSettingMapper.updateById(updateObj);
    }

    @Override
    public void deleteOaAttendanceSetting(Long id) {
        // 校验存在
        validateOaAttendanceSettingExists(id);
        // 删除
        oaAttendanceSettingMapper.deleteById(id);
    }

    private OaAttendanceSettingDO validateOaAttendanceSettingExists(Long id) {
        OaAttendanceSettingDO setting = oaAttendanceSettingMapper.selectById(id);
        if (setting == null) {
            throw exception(OA_ATTENDANCE_SETTING_NOT_EXISTS);
        }
        return setting;
    }

    @Override
    public OaAttendanceSettingDO getOaAttendanceSetting(Long id) {
        return oaAttendanceSettingMapper.selectById(id);
    }

    @Override
    public List<OaAttendanceSettingDO> getOaAttendanceSettingList(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return oaAttendanceSettingMapper.selectList(new LambdaQueryWrapperX<OaAttendanceSettingDO>()
                .in(OaAttendanceSettingDO::getId, ids));
    }

    @Override
    public PageResult<OaAttendanceSettingDO> getOaAttendanceSettingPage(OaAttendanceSettingPageReqVO pageReqVO) {
        return oaAttendanceSettingMapper.selectPage(pageReqVO);
    }

    @Override
    public List<OaAttendanceSettingDO> getOaAttendanceSettingList(OaAttendanceSettingExportReqVO exportReqVO) {
        return oaAttendanceSettingMapper.selectList(exportReqVO);
    }
    
    @Override
    public List<OaAttendanceSettingDO> getEnabledOaAttendanceSettingList() {
        return oaAttendanceSettingMapper.selectEnabledList();
    }
}

