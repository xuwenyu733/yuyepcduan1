package cn.iocoder.yudao.module.fish.service.holiday;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 节假日 Service 接口
 *
 * @author 系统管理员
 */
public interface HolidayService {

    /**
     * 获取指定年份的节假日数据（如果数据库没有则从API获取并保存）
     *
     * @param year 年份
     * @return 节假日数据，key为日期字符串（YYYY-MM-DD），value为是否工作日
     */
    Set<String> getHolidays(Integer year);

    /**
     * 获取指定年份的调休工作日数据
     *
     * @param year 年份
     * @return 调休工作日日期集合
     */
    Set<String> getWorkdays(Integer year);

    /**
     * 判断指定日期是否为工作日（排除周末和法定节假日，包含调休工作日）
     *
     * @param date 日期
     * @return 是否为工作日
     */
    boolean isWorkday(LocalDate date);

    /**
     * 计算指定日期范围内的工作日数量
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 工作日数量
     */
    int countWorkdays(LocalDate startDate, LocalDate endDate);
}


