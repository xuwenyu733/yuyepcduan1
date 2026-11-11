package cn.iocoder.yudao.module.fish.dal.mysql.zkdayrecord;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdayrecord.ZkDayRecordDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkdayrecord.vo.*;

/**
 * 时间字典 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface ZkDayRecordMapper extends BaseMapperX<ZkDayRecordDO> {

    default PageResult<ZkDayRecordDO> selectPage(ZkDayRecordPageReqVO reqVO) {
        LambdaQueryWrapperX<ZkDayRecordDO> queryWrapper = new LambdaQueryWrapperX<ZkDayRecordDO>()
                .eqIfPresent(ZkDayRecordDO::getSimpleFormat, reqVO.getSimpleFormat())
                .likeIfPresent(ZkDayRecordDO::getHolidayName, reqVO.getHolidayName())
                .likeIfPresent(ZkDayRecordDO::getWeekName, reqVO.getWeekName())
                .eqIfPresent(ZkDayRecordDO::getDateType, reqVO.getDateType())
                .eqIfPresent(ZkDayRecordDO::getRemark, reqVO.getRemark())
                .geIfPresent(ZkDayRecordDO::getDateValue, reqVO.getGeDateValue())
                .eqIfPresent(ZkDayRecordDO::getDateMonth, reqVO.getDateMonth())
                .eqIfPresent(ZkDayRecordDO::getDateYear, reqVO.getDateYear())
                .eqIfPresent(ZkDayRecordDO::getDateValue, reqVO.getDateValue())
                .betweenIfPresent(ZkDayRecordDO::getCreateTime, reqVO.getCreateTime())
                ;
        if(reqVO.getIdIsAsc()) {
            queryWrapper.orderByAsc(ZkDayRecordDO::getId);
        } else {
            queryWrapper.orderByDesc(ZkDayRecordDO::getId);
        }
        return selectPage(reqVO, queryWrapper);
    }


    default List<ZkDayRecordDO> selectList(ZkDayRecordExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ZkDayRecordDO>()
                .eqIfPresent(ZkDayRecordDO::getSimpleFormat, reqVO.getSimpleFormat())
                .likeIfPresent(ZkDayRecordDO::getHolidayName, reqVO.getHolidayName())
                .likeIfPresent(ZkDayRecordDO::getWeekName, reqVO.getWeekName())
                .eqIfPresent(ZkDayRecordDO::getDateType, reqVO.getDateType())
                .eqIfPresent(ZkDayRecordDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(ZkDayRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ZkDayRecordDO::getId));
    }

}
