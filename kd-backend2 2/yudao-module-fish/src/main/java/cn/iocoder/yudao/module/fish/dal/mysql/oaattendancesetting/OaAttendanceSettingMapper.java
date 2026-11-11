package cn.iocoder.yudao.module.fish.dal.mysql.oaattendancesetting;

import java.util.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.oaattendancesetting.OaAttendanceSettingDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.oaattendancesetting.vo.*;

/**
 * OA考勤设置 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface OaAttendanceSettingMapper extends BaseMapperX<OaAttendanceSettingDO> {

    default PageResult<OaAttendanceSettingDO> selectPage(OaAttendanceSettingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OaAttendanceSettingDO>()
                .likeIfPresent(OaAttendanceSettingDO::getName, reqVO.getName())
                .eqIfPresent(OaAttendanceSettingDO::getStatus, reqVO.getStatus())
                .orderByAsc(OaAttendanceSettingDO::getSortOrder)
                .orderByDesc(OaAttendanceSettingDO::getCreateTime));
    }

    default List<OaAttendanceSettingDO> selectList(OaAttendanceSettingExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<OaAttendanceSettingDO>()
                .likeIfPresent(OaAttendanceSettingDO::getName, reqVO.getName())
                .eqIfPresent(OaAttendanceSettingDO::getStatus, reqVO.getStatus())
                .orderByAsc(OaAttendanceSettingDO::getSortOrder)
                .orderByDesc(OaAttendanceSettingDO::getCreateTime));
    }
    
    default List<OaAttendanceSettingDO> selectEnabledList() {
        return selectList(new LambdaQueryWrapperX<OaAttendanceSettingDO>()
                .eq(OaAttendanceSettingDO::getStatus, 1)
                .orderByAsc(OaAttendanceSettingDO::getSortOrder));
    }
}



