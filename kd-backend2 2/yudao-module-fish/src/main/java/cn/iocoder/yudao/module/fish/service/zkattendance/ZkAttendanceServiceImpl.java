package cn.iocoder.yudao.module.fish.service.zkattendance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.fish.controller.admin.zkattendance.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkattendance.ZkAttendanceDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.oaattendancesetting.OaAttendanceSettingDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkattendance.ZkAttendanceMapper;
import cn.iocoder.yudao.module.fish.service.oaattendancesetting.OaAttendanceSettingService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 考勤 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class ZkAttendanceServiceImpl implements ZkAttendanceService {

  // 默认上班时间：9:00（如果考勤设置中没有配置，使用此默认值）
  private static final LocalTime DEFAULT_WORK_START_TIME = LocalTime.of(9, 0);
  // 默认下班时间：18:00（如果考勤设置中没有配置，使用此默认值）
  private static final LocalTime DEFAULT_WORK_END_TIME = LocalTime.of(18, 0);
  // 下班打卡截止时间：第二天06:00（凌晨6点之前可以补打前一天的下班打卡）
  private static final LocalTime PUNCH_OUT_DEADLINE = LocalTime.of(6, 0);
  // 地球半径（米）
  private static final double EARTH_RADIUS = 6371000;

  @Resource
  private ZkAttendanceMapper attendanceMapper;

  @Resource
  private cn.iocoder.yudao.module.fish.service.holiday.HolidayService holidayService;

  @Resource
  private OaAttendanceSettingService oaAttendanceSettingService;

  /**
   * 打卡方法（智能判断打卡类型）
   * 
   * 逻辑说明：
   * 1. 每个用户每天只会产生一条考勤记录（通过 user_id + attendance_date 唯一索引保证）
   * 2. 如果当天已有记录，则更新该记录；如果没有，则创建新记录
   * 3. 上班打卡和下班打卡都会更新同一条记录的不同字段
   * 
   * 跨天打卡处理规则：
   * - 如果当前时间在00:00-06:00之间（凌晨），需要智能判断：
   * a) 检查前一天是否有上班打卡但未下班打卡的记录
   * b) 如果有，则自动补打前一天的下班打卡（使用前一天23:59:59作为打卡时间）
   * c) 如果没有，则算今天的上班打卡（夜班或早班情况）
   * - 如果当前时间在06:00之后，正常处理当天的打卡
   * 
   * 迟到、早退判断规则：
   * - 标准上班时间：9:00
   * - 标准下班时间：18:00
   * - 下班打卡截止时间：第二天06:00（凌晨6点之前可以补打前一天的下班打卡）
   * 
   * 上班打卡时：
   * - 如果打卡时间 > 9:00，状态设为"1"（迟到）
   * - 如果打卡时间 <= 9:00，状态设为"0"（正常）
   * 
   * 下班打卡时：
   * - 如果打卡时间 < 18:00（早退）：
   * - 如果之前状态是"1"（迟到），则改为"4"（迟到+早退）
   * - 否则改为"2"（早退）
   * - 如果打卡时间 >= 18:00（正常下班）：
   * - 如果之前状态是"1"（迟到），保持"1"（仅迟到）
   * - 如果之前状态是"0"或null，改为"0"（正常）
   * - 其他状态保持不变
   */
  @Override
  public Long punch(ZkAttendanceCreateReqVO createReqVO) {
    Long userId = SecurityFrameworkUtils.getLoginUserId();
    LocalDate today = LocalDate.now();
    LocalTime now = LocalTime.now();

    // ✅ 解析经纬度：优先使用独立的 latitude/longitude 字段，如果没有则尝试从 location 解析
    BigDecimal latitude = createReqVO.getLatitude();
    BigDecimal longitude = createReqVO.getLongitude();

    // 如果经纬度为空，尝试从 location 字段解析（格式：纬度,经度）
    if ((latitude == null || longitude == null) && createReqVO.getLocation() != null) {
      String[] locationParts = createReqVO.getLocation().split(",");
      if (locationParts.length == 2) {
        try {
          // 尝试解析为数字格式的经纬度
          double lat = Double.parseDouble(locationParts[0].trim());
          double lon = Double.parseDouble(locationParts[1].trim());
          // 验证经纬度范围
          if (lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180) {
            latitude = BigDecimal.valueOf(lat);
            longitude = BigDecimal.valueOf(lon);
            log.info("从 location 字段解析经纬度: latitude={}, longitude={}", latitude, longitude);
          }
        } catch (NumberFormatException e) {
          log.warn("无法从 location 字段解析经纬度: {}", createReqVO.getLocation());
        }
      }
    }

    // ✅ 验证位置是否在允许范围内
    if (latitude == null || longitude == null) {
      throw exception(ATTENDANCE_LOCATION_REQUIRED);
    }

    OaAttendanceSettingDO validSetting = validateLocationInRange(latitude, longitude);

    // ✅ 从考勤设置表获取上下班时间
    LocalTime workStartTime = validSetting.getWorkStartTime() != null
        ? validSetting.getWorkStartTime()
        : DEFAULT_WORK_START_TIME;
    LocalTime workEndTime = validSetting.getWorkEndTime() != null
        ? validSetting.getWorkEndTime()
        : DEFAULT_WORK_END_TIME;

    // 智能判断打卡类型和目标日期（处理跨天情况）
    Object[] punchInfo = determinePunchTypeAndDate(userId, today, now, createReqVO.getPunchType());
    Integer actualPunchType = (Integer) punchInfo[0];
    LocalDate targetDate = (LocalDate) punchInfo[1];

    // 如果是补打前一天的下班打卡，使用前一天23:59:59作为打卡时间
    if (actualPunchType == 2 && targetDate.isBefore(today)) {
      now = LocalTime.of(23, 59, 59);
      log.info("补打前一天({})的下班打卡，打卡时间记录为23:59:59", targetDate);
    }

    // 查询目标日期的考勤记录（每个用户每天只有一条记录）
    ZkAttendanceDO attendance = attendanceMapper.selectByUserIdAndDate(userId, targetDate);

    if (attendance == null) {
      // 创建新记录
      attendance = ZkAttendanceDO.builder()
          .userId(userId)
          .userName(SecurityFrameworkUtils.getLoginUserNickname())
          .deptId(SecurityFrameworkUtils.getLoginUserDeptId())
          .attendanceDate(targetDate)
          .location(createReqVO.getLocation() != null ? createReqVO.getLocation() : validSetting.getName())
          .longitude(longitude)
          .latitude(latitude)
          .build();
    } else {
      // ✅ 更新位置信息（保存位置名称和经纬度）
      if (createReqVO.getLocation() != null) {
        attendance.setLocation(createReqVO.getLocation());
      } else {
        attendance.setLocation(validSetting.getName());
      }
      attendance.setLongitude(longitude);
      attendance.setLatitude(latitude);
    }

    // 根据实际打卡类型设置时间和状态（使用从考勤设置表获取的时间）
    if (actualPunchType == 1) {
      // 上班打卡
      attendance.setClockInTime(now);
      // 判断是否迟到：打卡时间 > workStartTime 则为迟到
      if (now.isAfter(workStartTime)) {
        attendance.setStatus("1"); // 迟到
      } else {
        attendance.setStatus("0"); // 正常
      }
    } else if (actualPunchType == 2) {
      // 下班打卡
      attendance.setClockOutTime(now);
      String currentStatus = attendance.getStatus();

      // 判断是否早退：打卡时间 < workEndTime 则为早退
      if (now.isBefore(workEndTime)) {
        // 早退情况
        if ("1".equals(currentStatus)) {
          // 之前是迟到，现在是早退，所以是迟到+早退
          attendance.setStatus("4"); // 迟到+早退
        } else {
          // 之前是正常或其他状态，现在是早退
          attendance.setStatus("2"); // 早退
        }
      } else {
        // 正常下班（打卡时间 >= workEndTime）
        if ("1".equals(currentStatus)) {
          // 之前是迟到，现在正常下班，保持迟到状态
          attendance.setStatus("1"); // 仅迟到
        } else if (currentStatus == null || "0".equals(currentStatus)) {
          // 之前是正常或未设置，现在正常下班
          attendance.setStatus("0"); // 正常
        }
        // 其他状态保持不变
      }
    }

    // 保存或更新记录（唯一索引保证不会重复）
    if (attendance.getId() == null) {
      attendanceMapper.insert(attendance);
    } else {
      attendanceMapper.updateById(attendance);
    }

    return attendance.getId();
  }

  /**
   * 智能判断打卡类型和日期
   * 
   * 跨天打卡处理规则：
   * 1. 如果当前时间在00:00-06:00之间（凌晨）：
   * - 如果前端传的是下班打卡(2)，检查前一天是否有未完成的下班打卡
   * * 如果有上班打卡但没有下班打卡，返回(2, yesterday)用于补打前一天的下班打卡
   * * 如果没有或已完成，返回(1, today)算今天的上班打卡
   * - 如果前端传的是上班打卡(1)：
   * * 检查前一天是否有未完成的下班打卡
   * * 如果有，返回(2, yesterday)优先补打前一天的下班打卡
   * * 如果没有，返回(1, today)算今天的上班打卡
   * 2. 如果当前时间在06:00之后：
   * - 直接使用前端传递的打卡类型和今天日期
   * 
   * @param userId            用户ID
   * @param today             今天日期
   * @param now               当前时间
   * @param frontendPunchType 前端传递的打卡类型
   * @return 数组[实际打卡类型, 目标日期]
   */
  private Object[] determinePunchTypeAndDate(Long userId, LocalDate today, LocalTime now, Integer frontendPunchType) {
    // 如果当前时间在凌晨00:00-06:00之间
    if (now.isBefore(PUNCH_OUT_DEADLINE)) {
      LocalDate yesterday = today.minusDays(1);
      ZkAttendanceDO yesterdayAttendance = attendanceMapper.selectByUserIdAndDate(userId, yesterday);

      // 检查前一天是否有未完成的下班打卡
      boolean hasUnfinishedPunch = yesterdayAttendance != null
          && yesterdayAttendance.getClockInTime() != null
          && yesterdayAttendance.getClockOutTime() == null;

      if (hasUnfinishedPunch) {
        // 前一天有未完成的下班打卡，优先补打前一天的下班打卡
        log.info("检测到前一天({})有未完成的下班打卡，自动补打下班打卡", yesterday);
        return new Object[] { 2, yesterday };
      } else {
        // 前一天已完成或没有记录，算今天的上班打卡
        if (frontendPunchType == 2) {
          log.info("凌晨时间({})点击下班打卡，但前一天已完成打卡，自动转为今天的上班打卡", now);
        }
        return new Object[] { 1, today };
      }
    }

    // 06:00之后，直接使用前端传递的打卡类型和今天日期
    return new Object[] { frontendPunchType, today };
  }

  @Override
  public ZkAttendanceTodayVO getTodayAttendance() {
    Long userId = SecurityFrameworkUtils.getLoginUserId();
    LocalDate today = LocalDate.now();

    ZkAttendanceDO attendance = attendanceMapper.selectByUserIdAndDate(userId, today);

    if (attendance == null) {
      return ZkAttendanceTodayVO.builder()
          .hasClockIn(false)
          .hasClockOut(false)
          .build();
    }

    return ZkAttendanceTodayVO.builder()
        .hasClockIn(attendance.getClockInTime() != null)
        .hasClockOut(attendance.getClockOutTime() != null)
        .clockInTime(attendance.getClockInTime())
        .clockOutTime(attendance.getClockOutTime())
        .location(attendance.getLocation())
        .build();
  }

  @Override
  public PageResult<ZkAttendanceRespVO> getAttendancePage(ZkAttendancePageReqVO pageReqVO) {
    // 如果未指定用户ID，则查询当前用户
    if (pageReqVO.getUserId() == null) {
      pageReqVO.setUserId(SecurityFrameworkUtils.getLoginUserId());
    }

    PageResult<ZkAttendanceDO> pageResult = attendanceMapper.selectPage(pageReqVO);
    return BeanUtils.toBean(pageResult, ZkAttendanceRespVO.class);
  }

  @Override
  public ZkAttendanceStatisticsVO getAttendanceStatistics(String month) {
    Long userId = SecurityFrameworkUtils.getLoginUserId();
    YearMonth yearMonth;

    if (month == null || month.isEmpty()) {
      yearMonth = YearMonth.now();
    } else {
      yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    LocalDate startDate = yearMonth.atDay(1);
    LocalDate endDate = yearMonth.atEndOfMonth();

    // 查询该月所有考勤记录
    List<ZkAttendanceDO> attendances = attendanceMapper.selectList(
        new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<ZkAttendanceDO>()
            .eq(ZkAttendanceDO::getUserId, userId)
            .ge(ZkAttendanceDO::getAttendanceDate, startDate)
            .le(ZkAttendanceDO::getAttendanceDate, endDate));

    // 将考勤记录转换为Map，以日期为key，方便快速查找
    Map<LocalDate, ZkAttendanceDO> attendanceMap = new HashMap<>();
    for (ZkAttendanceDO attendance : attendances) {
      attendanceMap.put(attendance.getAttendanceDate(), attendance);
    }

    int attendanceDays = 0;
    int lateCount = 0;
    int earlyLeaveCount = 0;

    // 计算到今天为止的应出勤天数（用于计算缺勤）
    LocalDate today = LocalDate.now();
    LocalDate todayEndDate = today.isAfter(endDate) ? endDate : today;

    // 遍历整个月的每一天进行统计（出勤天数、迟到、早退）
    LocalDate currentDate = startDate;
    while (!currentDate.isAfter(endDate)) {
      // 只统计工作日（排除周末和法定节假日，包含调休工作日）
      if (!holidayService.isWorkday(currentDate)) {
        currentDate = currentDate.plusDays(1);
        continue;
      }

      ZkAttendanceDO attendance = attendanceMap.get(currentDate);

      if (attendance != null) {
        // 判断是否出勤：有上班打卡或下班打卡就算出勤
        if (attendance.getClockInTime() != null || attendance.getClockOutTime() != null) {
          attendanceDays++;

          // 统计迟到次数：状态为"1"（迟到）或"4"（迟到+早退）的都算迟到
          if ("1".equals(attendance.getStatus()) || "4".equals(attendance.getStatus())) {
            lateCount++;
          }
          // 统计早退次数：状态为"2"（早退）或"4"（迟到+早退）的都算早退
          if ("2".equals(attendance.getStatus()) || "4".equals(attendance.getStatus())) {
            earlyLeaveCount++;
          }
        }
      }

      currentDate = currentDate.plusDays(1);
    }

    // 计算应出勤天数（排除周末和法定节假日，包含调休工作日）
    // 整个月的应出勤天数
    int shouldAttendanceDays = holidayService.countWorkdays(startDate, endDate);

    // 到今天为止的应出勤天数（用于计算缺勤）
    int shouldDaysToToday = holidayService.countWorkdays(startDate, todayEndDate);

    // 计算到今天为止的实际出勤天数（用于计算缺勤）
    int attendanceDaysToToday = 0;
    currentDate = startDate;
    while (!currentDate.isAfter(todayEndDate)) {
      if (!holidayService.isWorkday(currentDate)) {
        currentDate = currentDate.plusDays(1);
        continue;
      }
      ZkAttendanceDO attendance = attendanceMap.get(currentDate);
      if (attendance != null &&
          (attendance.getClockInTime() != null || attendance.getClockOutTime() != null)) {
        attendanceDaysToToday++;
      }
      currentDate = currentDate.plusDays(1);
    }

    // 计算缺勤天数：到今天为止的应出勤天数 - 到今天为止的实际出勤天数
    int absentCount = Math.max(0, shouldDaysToToday - attendanceDaysToToday);

    return ZkAttendanceStatisticsVO.builder()
        .attendanceDays(attendanceDays)
        .shouldAttendanceDays(shouldAttendanceDays) // 整个月的应出勤天数
        .lateCount(lateCount)
        .earlyLeaveCount(earlyLeaveCount)
        .absentCount(absentCount) // 使用重新计算的缺勤天数
        .build();
  }

  /**
   * 验证位置是否在允许的打卡范围内
   * 
   * @param latitude  用户当前位置纬度
   * @param longitude 用户当前位置经度
   * @return 匹配的考勤设置（如果位置在允许范围内）
   * @throws ServiceException 如果位置不在任何允许的打卡范围内
   */
  private OaAttendanceSettingDO validateLocationInRange(BigDecimal latitude, BigDecimal longitude) {
    // 获取所有启用的考勤设置
    List<OaAttendanceSettingDO> settings = oaAttendanceSettingService.getEnabledOaAttendanceSettingList();

    if (settings == null || settings.isEmpty()) {
      log.warn("没有启用的考勤设置，允许打卡");
      // 如果没有配置考勤设置，允许打卡（兼容旧逻辑）
      return OaAttendanceSettingDO.builder()
          .name("未配置")
          .workStartTime(DEFAULT_WORK_START_TIME)
          .workEndTime(DEFAULT_WORK_END_TIME)
          .build();
    }

    // 遍历所有考勤设置，检查用户位置是否在任何一个允许范围内
    for (OaAttendanceSettingDO setting : settings) {
      if (setting.getLatitude() == null || setting.getLongitude() == null
          || setting.getAllowRange() == null) {
        continue; // 跳过配置不完整的设置
      }

      // 计算用户位置与考勤设置位置的距离（米）
      double distance = calculateDistance(
          latitude.doubleValue(),
          longitude.doubleValue(),
          setting.getLatitude().doubleValue(),
          setting.getLongitude().doubleValue());

      // 如果距离在允许范围内，返回该设置
      if (distance <= setting.getAllowRange()) {
        log.info("用户位置在考勤设置[{}]范围内，距离: {}米，允许范围: {}米",
            setting.getName(), Math.round(distance), setting.getAllowRange());
        return setting;
      }
    }

    // 如果所有设置都不匹配，抛出异常
    log.warn("用户位置不在任何允许的打卡范围内。纬度: {}, 经度: {}", latitude, longitude);
    throw exception(ATTENDANCE_LOCATION_OUT_OF_RANGE);
  }

  /**
   * 使用 Haversine 公式计算两点之间的距离（米）
   * 
   * @param lat1 第一个点的纬度
   * @param lon1 第一个点的经度
   * @param lat2 第二个点的纬度
   * @param lon2 第二个点的经度
   * @return 两点之间的距离（米）
   */
  private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    // 将角度转换为弧度
    double lat1Rad = Math.toRadians(lat1);
    double lat2Rad = Math.toRadians(lat2);
    double deltaLatRad = Math.toRadians(lat2 - lat1);
    double deltaLonRad = Math.toRadians(lon2 - lon1);

    // Haversine 公式
    double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2)
        + Math.cos(lat1Rad) * Math.cos(lat2Rad)
            * Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    // 返回距离（米）
    return EARTH_RADIUS * c;
  }
}
