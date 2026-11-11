package cn.iocoder.yudao.module.fish.service.holiday;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.holiday.HolidayDO;
import cn.iocoder.yudao.module.fish.dal.mysql.holiday.HolidayMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 节假日 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Slf4j
public class HolidayServiceImpl implements HolidayService {

    private static final String TIAN_API_KEY = "fb386bfacd6b89e06daa2f672ebd1237";
    private static final String TIAN_API_URL = "https://apis.tianapi.com/jiejiari/index";

    @Resource
    private HolidayMapper holidayMapper;

    @Override
    public Set<String> getHolidays(Integer year) {
        // 确保数据库中有该年份的数据
        ensureHolidayData(year);

        // 从数据库查询节假日（daycode=1或2且isnotwork=1）
        List<HolidayDO> holidays = holidayMapper.selectByYear(year)
                .stream()
                .filter(h -> (h.getDaycode() == 1 || h.getDaycode() == 2) && h.getIsnotwork() == 1)
                .collect(Collectors.toList());

        return holidays.stream()
                .map(h -> h.getHolidayDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getWorkdays(Integer year) {
        // 确保数据库中有该年份的数据
        ensureHolidayData(year);

        // 从数据库查询调休工作日（daycode=3且isnotwork=0，或daycode=2且isnotwork=0）
        List<HolidayDO> workdays = holidayMapper.selectByYear(year)
                .stream()
                .filter(h -> (h.getDaycode() == 3 && h.getIsnotwork() == 0) ||
                        (h.getDaycode() == 2 && h.getIsnotwork() == 0))
                .collect(Collectors.toList());

        return workdays.stream()
                .map(h -> h.getHolidayDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isWorkday(LocalDate date) {
        // 确保该年份的节假日数据存在
        ensureHolidayData(date.getYear());

        HolidayDO holiday = holidayMapper.selectByDate(date);

        if (holiday != null) {
            // 如果是调休工作日，即使原本是周末也算工作日
            if ((holiday.getDaycode() == 3 || holiday.getDaycode() == 2) && holiday.getIsnotwork() == 0) {
                return true;
            }
            // 如果是法定节假日，即使原本是工作日也不算工作日
            if ((holiday.getDaycode() == 1 || holiday.getDaycode() == 2) && holiday.getIsnotwork() == 1) {
                return false;
            }
        }

        // 排除周末
        int dayOfWeek = date.getDayOfWeek().getValue();
        return dayOfWeek != 6 && dayOfWeek != 7; // 6=周六，7=周日
    }

    @Override
    public int countWorkdays(LocalDate startDate, LocalDate endDate) {
        // 确保相关年份的节假日数据存在（可能跨年）
        int startYear = startDate.getYear();
        int endYear = endDate.getYear();
        for (int year = startYear; year <= endYear; year++) {
            ensureHolidayData(year);
        }

        int count = 0;
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            if (isWorkday(current)) {
                count++;
            }
            current = current.plusDays(1);
        }

        return count;
    }

    /**
     * 确保数据库中有指定年份的节假日数据，如果没有则从API获取并保存
     */
    @Transactional(rollbackFor = Exception.class)
    public void ensureHolidayData(Integer year) {
        // 检查数据库中是否已有该年份的数据
        List<HolidayDO> existingData = holidayMapper.selectByYear(year);
        if (CollUtil.isNotEmpty(existingData)) {
            log.debug("数据库中已有{}年的节假日数据，共{}条", year, existingData.size());
            return;
        }

        // 从API获取数据
        log.info("数据库中无{}年的节假日数据，开始从天行API获取", year);
        try {
            String url = TIAN_API_URL + "?key=" + TIAN_API_KEY + "&type=1&date=" + year;
            String response = HttpRequest.get(url).execute().body();

            JSONObject jsonObject = JSONUtil.parseObj(response);
            if (jsonObject.getInt("code") != 200) {
                log.error("天行API返回错误: {}", jsonObject.getStr("msg"));
                return;
            }

            JSONObject result = jsonObject.getJSONObject("result");
            if (result == null) {
                log.warn("天行API返回的result为空");
                return;
            }

            JSONArray list = result.getJSONArray("list");
            if (list == null || list.isEmpty()) {
                log.warn("天行API返回的数据为空");
                return;
            }

            // 用于存储所有需要保存的日期，避免重复
            Set<LocalDate> processedDates = new HashSet<>();
            // 用于存储调休工作日日期（remark字符串中的日期）
            Set<LocalDate> remarkWorkdays = new HashSet<>();
            // 用于存储法定节假日日期（wage字符串中的日期，需要3倍工资）
            Set<LocalDate> wageHolidays = new HashSet<>();
            int savedCount = 0;

            // 第一遍：遍历list数组，收集所有数据
            for (int i = 0; i < list.size(); i++) {
                JSONObject item = list.getJSONObject(i);
                String dateStr = item.getStr("date");

                // 如果date字段不存在，尝试从其他字段推断
                if (StrUtil.isBlank(dateStr)) {
                    // 尝试从vacation字段获取第一个日期
                    String vacationStr = item.getStr("vacation");
                    if (StrUtil.isNotBlank(vacationStr)) {
                        String[] vacationDates = vacationStr.split("\\|");
                        if (vacationDates.length > 0) {
                            dateStr = vacationDates[0].trim();
                        }
                    }
                }

                if (StrUtil.isBlank(dateStr)) {
                    log.warn("跳过无日期的数据项: {}", item);
                    continue;
                }

                LocalDate holidayDate;
                try {
                    holidayDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (Exception e) {
                    log.warn("解析日期失败: {}", dateStr, e);
                    continue;
                }

                // 如果已经处理过，跳过（避免重复）
                if (processedDates.contains(holidayDate)) {
                    continue;
                }

                Integer daycode = item.getInt("daycode");
                Integer isnotwork = item.getInt("isnotwork");

                // 如果daycode和isnotwork都不存在，根据日期类型推断
                if (daycode == null && isnotwork == null) {
                    // 默认根据是否为周末判断
                    int dayOfWeek = holidayDate.getDayOfWeek().getValue();
                    if (dayOfWeek == 6 || dayOfWeek == 7) {
                        daycode = 2; // 双休日
                        isnotwork = 1; // 休息日
                    } else {
                        daycode = 0; // 工作日
                        isnotwork = 0; // 工作日
                    }
                }

                // 保存当前日期的数据
                HolidayDO holiday = HolidayDO.builder()
                        .holidayDate(holidayDate)
                        .daycode(daycode != null ? daycode : 0)
                        .isnotwork(isnotwork != null ? isnotwork : 0)
                        .year(year)
                        .build();

                holidayMapper.insert(holiday);
                processedDates.add(holidayDate);
                savedCount++;

                // 处理vacation字符串：节假日期间的所有日期（用|分隔）
                String vacationStr = item.getStr("vacation");
                if (StrUtil.isNotBlank(vacationStr)) {
                    String[] vacationDates = vacationStr.split("\\|");
                    for (String vacationDateStr : vacationDates) {
                        vacationDateStr = vacationDateStr.trim();
                        if (StrUtil.isNotBlank(vacationDateStr)) {
                            try {
                                LocalDate vacationDate = LocalDate.parse(vacationDateStr,
                                        DateTimeFormatter.ISO_LOCAL_DATE);

                                // 如果还没处理过，保存为节假日
                                if (!processedDates.contains(vacationDate)) {
                                    HolidayDO vacationHoliday = HolidayDO.builder()
                                            .holidayDate(vacationDate)
                                            .daycode(1) // 节假日
                                            .isnotwork(1) // 休息日
                                            .year(year)
                                            .build();

                                    holidayMapper.insert(vacationHoliday);
                                    processedDates.add(vacationDate);
                                    savedCount++;
                                }
                            } catch (Exception e) {
                                log.warn("解析vacation日期失败: {}", vacationDateStr, e);
                            }
                        }
                    }
                }

                // 处理wage字符串：法定节假日（用|分隔，需要3倍工资）
                String wageStr = item.getStr("wage");
                if (StrUtil.isNotBlank(wageStr)) {
                    String[] wageDates = wageStr.split("\\|");
                    for (String wageDateStr : wageDates) {
                        wageDateStr = wageDateStr.trim();
                        if (StrUtil.isNotBlank(wageDateStr)) {
                            try {
                                LocalDate wageDate = LocalDate.parse(wageDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                                wageHolidays.add(wageDate);
                            } catch (Exception e) {
                                log.warn("解析wage日期失败: {}", wageDateStr, e);
                            }
                        }
                    }
                }

                // 收集remark字符串：调休工作日（需要上班的周末，用|分隔）
                String remarkStr = item.getStr("remark");
                if (StrUtil.isNotBlank(remarkStr)) {
                    String[] remarkDates = remarkStr.split("\\|");
                    for (String remarkDateStr : remarkDates) {
                        remarkDateStr = remarkDateStr.trim();
                        if (StrUtil.isNotBlank(remarkDateStr)) {
                            try {
                                LocalDate remarkDate = LocalDate.parse(remarkDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                                remarkWorkdays.add(remarkDate);
                            } catch (Exception e) {
                                log.warn("解析remark日期失败: {}", remarkDateStr, e);
                            }
                        }
                    }
                }
            }

            // 第二遍：处理调休工作日（remark字符串中的日期）
            for (LocalDate remarkDate : remarkWorkdays) {
                HolidayDO existing = holidayMapper.selectByDate(remarkDate);
                if (existing != null && existing.getYear().equals(year)) {
                    // 如果已存在，更新为调休工作日
                    existing.setDaycode(3); // 调休日
                    existing.setIsnotwork(0); // 工作日
                    holidayMapper.updateById(existing);
                } else if (!processedDates.contains(remarkDate)) {
                    // 如果不存在，新建为调休工作日
                    HolidayDO remarkHoliday = HolidayDO.builder()
                            .holidayDate(remarkDate)
                            .daycode(3) // 调休日
                            .isnotwork(0) // 工作日
                            .year(year)
                            .build();

                    holidayMapper.insert(remarkHoliday);
                    processedDates.add(remarkDate);
                    savedCount++;
                }
            }

            // 第三遍：处理法定节假日（wage字符串中的日期，确保标记为法定节假日）
            for (LocalDate wageDate : wageHolidays) {
                HolidayDO existing = holidayMapper.selectByDate(wageDate);
                if (existing != null && existing.getYear().equals(year)) {
                    // 如果已存在，确保标记为法定节假日
                    if (existing.getDaycode() != 1 || existing.getIsnotwork() != 1) {
                        existing.setDaycode(1); // 法定节假日
                        existing.setIsnotwork(1); // 休息日
                        holidayMapper.updateById(existing);
                    }
                } else if (!processedDates.contains(wageDate)) {
                    // 如果不存在，新建为法定节假日
                    HolidayDO wageHoliday = HolidayDO.builder()
                            .holidayDate(wageDate)
                            .daycode(1) // 法定节假日
                            .isnotwork(1) // 休息日
                            .year(year)
                            .build();

                    holidayMapper.insert(wageHoliday);
                    processedDates.add(wageDate);
                    savedCount++;
                }
            }

            log.info("成功保存{}年的节假日数据，共{}条", year, savedCount);
        } catch (Exception e) {
            log.error("从天行API获取{}年节假日数据失败", year, e);
        }
    }
}
