package cn.iocoder.yudao.module.fish.service.oaattendancesetting;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.oaattendancesetting.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.oaattendancesetting.OaAttendanceSettingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * OA考勤设置 Service 接口
 *
 * @author 系统管理员
 */
public interface OaAttendanceSettingService {

    /**
     * 创建OA考勤设置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOaAttendanceSetting(@Valid OaAttendanceSettingCreateReqVO createReqVO);

    /**
     * 更新OA考勤设置
     *
     * @param updateReqVO 更新信息
     */
    void updateOaAttendanceSetting(@Valid OaAttendanceSettingUpdateReqVO updateReqVO);

    /**
     * 删除OA考勤设置
     *
     * @param id 编号
     */
    void deleteOaAttendanceSetting(Long id);

    /**
     * 获得OA考勤设置
     *
     * @param id 编号
     * @return OA考勤设置
     */
    OaAttendanceSettingDO getOaAttendanceSetting(Long id);

    /**
     * 获得OA考勤设置列表
     *
     * @param ids 编号
     * @return OA考勤设置列表
     */
    List<OaAttendanceSettingDO> getOaAttendanceSettingList(Collection<Long> ids);

    /**
     * 获得OA考勤设置分页
     *
     * @param pageReqVO 分页查询
     * @return OA考勤设置分页
     */
    PageResult<OaAttendanceSettingDO> getOaAttendanceSettingPage(OaAttendanceSettingPageReqVO pageReqVO);

    /**
     * 获得OA考勤设置列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return OA考勤设置列表
     */
    List<OaAttendanceSettingDO> getOaAttendanceSettingList(OaAttendanceSettingExportReqVO exportReqVO);
    
    /**
     * 获得启用的OA考勤设置列表
     *
     * @return OA考勤设置列表
     */
    List<OaAttendanceSettingDO> getEnabledOaAttendanceSettingList();
}



