package cn.iocoder.yudao.module.fish.dal.mysql.holiday;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.fish.dal.dataobject.holiday.HolidayDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 节假日 Mapper
 *
 * @author 系统管理员
 */
@Mapper
public interface HolidayMapper extends BaseMapperX<HolidayDO> {

    /**
     * 查询指定年份的所有节假日数据
     */
    default List<HolidayDO> selectByYear(Integer year) {
        return selectList(new LambdaQueryWrapperX<HolidayDO>()
                .eq(HolidayDO::getYear, year));
    }

    /**
     * 查询指定日期范围内的节假日数据
     */
    default List<HolidayDO> selectByDateRange(LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<HolidayDO>()
                .ge(HolidayDO::getHolidayDate, startDate)
                .le(HolidayDO::getHolidayDate, endDate));
    }

    /**
     * 根据日期查询
     */
    default HolidayDO selectByDate(LocalDate date) {
        return selectOne(new LambdaQueryWrapperX<HolidayDO>()
                .eq(HolidayDO::getHolidayDate, date));
    }
}

