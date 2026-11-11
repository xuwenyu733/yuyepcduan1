package cn.iocoder.yudao.module.fish.dal.mysql.zkattendance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkattendance.ZkAttendanceDO;
import cn.iocoder.yudao.module.fish.controller.admin.zkattendance.vo.ZkAttendancePageReqVO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;

/**
 * 考勤 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkAttendanceMapper extends BaseMapperX<ZkAttendanceDO> {

    default PageResult<ZkAttendanceDO> selectPage(ZkAttendancePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ZkAttendanceDO>()
                .eqIfPresent(ZkAttendanceDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ZkAttendanceDO::getUserName, reqVO.getUserName())
                .betweenIfPresent(ZkAttendanceDO::getAttendanceDate, reqVO.getAttendanceDate())
                .eqIfPresent(ZkAttendanceDO::getStatus, reqVO.getStatus())
                .orderByDesc(ZkAttendanceDO::getAttendanceDate)
                .orderByDesc(ZkAttendanceDO::getId));
    }

    default ZkAttendanceDO selectByUserIdAndDate(Long userId, LocalDate date) {
        return selectOne(new LambdaQueryWrapperX<ZkAttendanceDO>()
                .eq(ZkAttendanceDO::getUserId, userId)
                .eq(ZkAttendanceDO::getAttendanceDate, date));
    }
}





